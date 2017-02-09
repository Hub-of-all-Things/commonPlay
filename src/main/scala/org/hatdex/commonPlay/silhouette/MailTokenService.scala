/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.silhouette

import javax.inject.Inject

import anorm.JodaParameterMetaData._
import anorm.SqlParser._
import anorm._
import org.hatdex.commonPlay.models.auth.MailTokenUser
import org.joda.time.DateTime
import play.api.db.DBApi
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent._

trait MailTokenService[T <: MailToken] {
  def create(token: T): Future[Option[T]]
  def retrieve(id: String): Future[Option[T]]
  def consume(id: String): Unit
}

@javax.inject.Singleton
class MailTokenUserService @Inject() (dbapi: DBApi) extends MailTokenService[MailTokenUser] {
  def create(token: MailTokenUser): Future[Option[MailTokenUser]] = {
    save(token).map(Some(_))
  }
  def retrieve(id: String): Future[Option[MailTokenUser]] = {
    findById(id)
  }
  def consume(id: String): Unit = {
    delete(id)
  }

  private val db = dbapi.database("default")

  private def findById(id: String): Future[Option[MailTokenUser]] = {
    Future {
      blocking {
        db.withConnection { implicit conn =>
          SQL("SELECT * FROM user_mail_tokens WHERE id = {tokenId}")
            .on('tokenId -> id)
            .as(mailTokenParser.singleOpt)
        }
      }
    }
  }

  private def mailTokenParser: RowParser[MailTokenUser] = {
    get[String]("user_mail_tokens.id") ~
      get[String]("user_mail_tokens.email") ~
      get[DateTime]("user_mail_tokens.expiration_time") ~
      get[Boolean]("user_mail_tokens.is_signup") map {
        case id ~ email ~ expirationTime ~ isSignup =>
          MailTokenUser(id, email, expirationTime, isSignup)
      }
  }

  private def save(token: MailTokenUser): Future[MailTokenUser] = {
    Future {
      blocking {
        db.withConnection { implicit conn =>
          SQL(
            """
              | INSERT INTO user_mail_tokens (id, email, expiration_time, is_signup)
              | VALUES ({id}, {email}, {expirationTime}, {isSignup})
            """.stripMargin)
            .on(
              'id -> token.id,
              'email -> token.email,
              'expirationTime -> token.expirationTime,
              'isSignup -> token.isSignUp)
            .executeInsert(scalar[String].single)
          token
        }
      }
    }
  }

  private def delete(id: String): Unit = {
    Future {
      blocking {
        db.withConnection { implicit conn =>
          SQL("DELETE FROM user_mail_tokens WHERE id = {tokenId}")
            .on('tokenId -> id)
            .executeUpdate()
        }
      }
    }
  }
}