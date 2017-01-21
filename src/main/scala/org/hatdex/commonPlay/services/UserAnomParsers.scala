/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.services

import java.util.UUID

import anorm.SqlParser._
import anorm.{ RowParser, ~ }
import org.hatdex.commonPlay.models.auth.roles.UserRole
import org.hatdex.commonPlay.models.auth.{ Hat, User, UserMarketProfile }

object UserAnomParsers {
  def hatParser: RowParser[Hat] = {
    get[UUID]("id") ~
      get[String]("country") ~
      get[String]("address") ~
      get[String]("public_key") map {
        case id ~ country ~ address ~ publicKey =>
          Hat(id, country, address, publicKey)
      }
  }

  def profileParser: RowParser[UserMarketProfile] = {
    get[UUID]("user_market_profile.id") ~
      get[Option[String]]("user_market_profile.gender") ~
      get[Option[String]]("user_market_profile.age_group") ~
      get[Option[String]]("user_market_profile.country") ~
      get[Option[String]]("user_market_profile.city") map {
        case id ~ gender ~ ageGroup ~ country ~ city =>
          UserMarketProfile(id, gender, ageGroup, country, city)
      }
  }

  def userParser: RowParser[(User, Option[(UserRole, Boolean)])] = {
    get[UUID]("id") ~
      get[String]("email") ~
      get[Boolean]("email_confirmed") ~
      get[Boolean]("terms_agreed") ~
      get[String]("password") ~
      get[String]("nick") ~
      get[String]("first_name") ~
      get[String]("last_name") ~
      hatParser.? ~
      profileParser.? ~
      UserRole.userRoleParser.? map {
        case id ~ email ~ emailConfirmed ~ termsAgreed ~ password ~ nick ~ firstName ~ lastName ~ maybeHat ~ maybeProfile ~ role =>
          (User(Some(id), email, emailConfirmed, termsAgreed, password, nick, firstName, lastName, maybeHat, maybeProfile, List(), List()), role)
      }
  }
}
