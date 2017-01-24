/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.services

import java.sql.{ Connection, SQLException }
import java.util.UUID
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import org.hatdex.commonPlay.models.auth.roles.UserRole
import org.hatdex.commonPlay.models.auth.{ Hat, User, UserMarketProfile }
import org.hatdex.commonPlay.silhouette.Implicits._
import play.api.{ Configuration, Logger }
import play.api.db.DBApi

import scala.concurrent._
import scala.util.{ Success, Try }

@javax.inject.Singleton
class UserService @Inject() (dbapi: DBApi, configuration: Configuration) extends IdentityService[User] {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = findByEmail(loginInfo)
  //  def save(user: User): Future[User] = User.save(user)

  val commonPlayDatabase = configuration.getString("commonPlayDatabase").getOrElse("default")
  private val db = dbapi.database(commonPlayDatabase)
  import UserAnomParsers._
  import play.api.libs.concurrent.Execution.Implicits._

  def save(user: User): Future[User] = {
    val theUser = if (user.id.isDefined) user else user.copy(id = Some(UUID.randomUUID()))
    Future {
      blocking {
        db.withTransaction { implicit conn =>
          val savedUser = for {
            savedUserId <- upsertUser(theUser)
            user <- updateRoles(theUser)
            _ <- user.hatOwned.map(upsertHat(_, savedUserId)).getOrElse(Success(UUID.randomUUID()))
            _ <- user.marketProfile.map(upsertMarketProfile(_, savedUserId)).getOrElse(Success(UUID.randomUUID()))
          } yield user

          val saved = savedUser recover {
            case e =>
              Logger.error(s"Failed to upsert user $user: ${e.getMessage}")
              conn.rollback()
              throw e
          }
          saved.get
        }
      }
    }
  }

  val userQuery = """
    | SELECT market_user.*,
    |   user_role.approved, user_role.extra, user_role.role,
    |   user_hat.address, user_hat.country, user_hat.public_key, user_hat.user_id FROM market_user
    | LEFT JOIN user_role ON user_role.user_id = market_user.id
    | LEFT JOIN user_hat ON user_hat.user_id = market_user.id
    """.stripMargin

  def remove(email: String): Future[Unit] = {
    Future {
      blocking {
        db.withConnection { implicit connection =>
          SQL("""DELETE FROM market_user WHERE market_user.email = {email}""").on('email -> email).executeUpdate()
        }
      }
    }
  }

  def findByEmail(email: String): Future[Option[User]] = {
    Future {
      blocking {
        db.withConnection { implicit conn =>
          val usersWithRoles = SQL(s"$userQuery WHERE market_user.email={email}")
            .on('email -> email)
            .as(userParser.*)

          val user = aggregateRoles(usersWithRoles).headOption
          user
        }
      }
    }
  }

  def findByNick(nick: String): Future[Option[User]] = {
    //    Logger.debug(s"Find users by nick: $nick")
    Future {
      blocking {
        db.withConnection { implicit conn =>
          val usersWithRoles = SQL(s"$userQuery WHERE market_user.nick={nick}")
            .on('nick -> nick)
            .as(userParser.*)

          val user = aggregateRoles(usersWithRoles).headOption
          user
        }
      }
    }
  }

  def findByHatAddress(hatAddress: String): Future[Option[User]] = {
    Future {
      blocking {
        db.withConnection { implicit conn =>
          val usersWithRoles = SQL(s"$userQuery WHERE user_hat.address={address}")
            .on('address -> hatAddress)
            .as(userParser.*)

          val user = aggregateRoles(usersWithRoles).headOption
          user
        }
      }
    }
  }

  def searchNickLike(nick: String): Future[Seq[User]] = {
    Future {
      blocking {
        db.withConnection { implicit conn =>
          val usersWithRoles = SQL(s"$userQuery WHERE market_user.nick ilike {nick}")
            .on('nick -> s"%$nick%")
            .as(userParser.*)

          val users = aggregateRoles(usersWithRoles)
          Logger.info(s"Found users with nick like $nick: $users")
          users
        }
      }
    }
  }

  def findById(userIds: UUID*): Future[Seq[User]] = {
    //    Logger.debug(s"Find users by Ids: $userIds")
    if (userIds.isEmpty) {
      Future.successful(Seq())
    }
    else {
      Future {
        blocking {
          db.withConnection { implicit conn =>
            val usersWithRoles = SQL(s"$userQuery WHERE market_user.id IN ({userIds})")
              .on('userIds -> SeqParameter(userIds, ", ", "", "::uuid"))
              .as(userParser.*)
            aggregateRoles(usersWithRoles)
          }
        }
      }
    }
  }

  def all(page: Int, usersPerPage: Int): Future[Seq[User]] = {
    Future {
      blocking {
        db.withConnection { implicit conn =>
          val usersWithRoles = SQL(s"$userQuery ORDER BY market_user.nick ASC LIMIT {count} OFFSET {start}")
            .on('start -> (page - 1) * usersPerPage, 'count -> usersPerPage)
            .as(userParser.*)
          aggregateRoles(usersWithRoles).sortBy(_.nick)
        }
      }
    }
  }

  def totalHats: Future[Long] = {
    Future {
      blocking {
        db.withConnection { implicit connection =>
          SQL("""SELECT COUNT(user_hat.user_id) FROM user_hat""").as(scalar[Long].single)
        }
      }
    }
  }

  private def upsertUser(theUser: User)(implicit connection: Connection): Try[UUID] = {
    Try {
      SQL(
        """
          | INSERT INTO market_user (id, email, email_confirmed, terms_agreed, password, nick, first_name, last_name)
          | VALUES ({id}::uuid, {email}, {emailConfirmed}, {termsAgreed}, {password}, {nick}, {firstName}, {lastName})
          | ON CONFLICT (id) DO UPDATE SET
          |   email = {email},
          |   email_confirmed = {emailConfirmed},
          |   terms_agreed = {termsAgreed},
          |   password = {password},
          |   nick = {nick},
          |   first_name = {firstName},
          |   last_name = {lastName}
        """.stripMargin)
        .on(
          'id -> theUser.id.get,
          'email -> theUser.email,
          'emailConfirmed -> theUser.emailConfirmed,
          'termsAgreed -> theUser.termsAgreed,
          'password -> theUser.password,
          'nick -> theUser.nick,
          'firstName -> theUser.firstName,
          'lastName -> theUser.lastName).executeInsert(scalar[UUID].single)
    }

  }

  private def aggregateRoles(result: List[(User, Option[(UserRole, Boolean)])]): Seq[User] = {
    val queryGrouped: Map[User, List[(User, Option[(UserRole, Boolean)])]] = result.groupBy(_._1)

    queryGrouped.map {
      case (user, userRoles) =>
        val unzippedRoles: Set[UserRole] = userRoles.unzip._2.flatten.filter(_._2).map(_._1).toSet
        val unzippedPendingRoles: Set[UserRole] = userRoles.unzip._2.flatten.filterNot(_._2).map(_._1).toSet
        user.withRoles(unzippedRoles.toSeq: _*).withPendingRoles(unzippedPendingRoles.toSeq: _*)
    }.toSeq
  }

  private def updateRoles(theUser: User)(implicit connection: Connection): Try[User] = {
    Logger.debug(s"Clearing existing roles for $theUser")

    val rolesDeleted = Try {
      SQL(""" DELETE FROM user_role WHERE user_id = {userId}::uuid """).on('userId -> theUser.id.get).executeUpdate()
    }

    val rolesSetup = rolesDeleted flatMap { deleted =>
      Logger.debug(s"Setting up roles for $theUser, $deleted")
      val insertRolesQuery = """INSERT INTO user_role (user_id, role, extra, approved) VALUES ({userId}::uuid, {role}, {extra}, {approved})"""
      Try {
        theUser.roles.map { role =>
          SQL(insertRolesQuery).on(
            'userId -> theUser.id.get,
            'role -> role.name,
            'extra -> role.extra,
            'approved -> true)
            .execute()
        }

        theUser.pendingRoles.map { role =>
          SQL(insertRolesQuery).on(
            'userId -> theUser.id.get,
            'role -> role.name,
            'extra -> role.extra,
            'approved -> false)
            .execute()
        }
      }
    }

    rolesSetup map { roles =>
      Logger.debug(s"Roles setup for $theUser: $roles")
      theUser
    } recover {
      case e: SQLException =>
        Logger.error(s"Failed to setup roles: ${e.getMessage}")
        def recursiveExceptionLog(e: SQLException): Unit = {
          Logger.debug(s"Sub-Exception ${e.getMessage}")
          Option(e.getNextException).foreach(recursiveExceptionLog)
        }
        Option(e.getNextException).foreach(recursiveExceptionLog)

        throw e
      case e =>
        Logger.error(s"Failed to setup roles: ${e.getMessage}")
        throw e
    }

  }

  def upsertMarketProfile(profile: UserMarketProfile, userId: UUID)(implicit connection: Connection): Try[UUID] = {
    Try {
      SQL(
        """
          | INSERT INTO user_market_profile (id, user_id, gender, age_group, country, city)
          | VALUES ({profileId}::uuid, {userId}::uuid, {gender}, {ageGroup}, {country}, {city})
          | ON CONFLICT (id) DO UPDATE SET
          |   gender = {gender},
          |   age_group = {ageGroup},
          |   country = {country},
          |   city = {city}
        """.stripMargin).on(
        'profileId -> profile.id,
        'userId -> userId,
        'gender -> profile.gender,
        'ageGroup -> profile.ageGroup,
        'country -> profile.country,
        'city -> profile.city)
        .executeInsert(scalar[UUID].single)
    }
  }

  def upsertHat(hat: Hat, userId: UUID)(implicit connection: Connection): Try[UUID] = {
    Try {
      Logger.debug(s"Upserting user HAT $hat for user $userId")
      SQL(
        """
          | INSERT INTO user_hat (id, user_id, country, address, public_key)
          | VALUES ({hatId}::uuid, {userId}::uuid, {country}, {address}, {publicKey})
          | ON CONFLICT (id) DO UPDATE SET
          |   user_id = {userId}::uuid,
          |   country = {country},
          |   address = {address},
          |   public_key = {publicKey}
        """.stripMargin).on(
        'hatId -> hat.id,
        'userId -> userId,
        'country -> hat.country,
        'address -> hat.address,
        'publicKey -> hat.publicKey)
        .executeInsert(scalar[UUID].single)
    } recover {
      case e =>
        Logger.error(s"Upserting user HAT $hat for user $userId failed: ${e.getMessage}")
        throw e
    }
  }
}