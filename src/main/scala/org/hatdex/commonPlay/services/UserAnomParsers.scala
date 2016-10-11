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
    get[UUID]("user_hat.id") ~
      get[UUID]("user_hat.user_id") ~
      get[String]("user_hat.country") ~
      get[String]("user_hat.address") ~
      get[String]("user_hat.public_key") map {
        case id ~ userId ~ country ~ address ~ publicKey =>
          Hat(id, country, address, publicKey)
      }
  }

  def profileParser: RowParser[UserMarketProfile] = {
    get[UUID]("user_market_profile.id") ~
      get[UUID]("user_market_profile.user_id") ~
      get[Option[String]]("user_market_profile.gender") ~
      get[Option[String]]("user_market_profile.age_group") ~
      get[Option[String]]("user_market_profile.country") ~
      get[Option[String]]("user_market_profile.city") map {
        case id ~ userId ~ gender ~ ageGroup ~ country ~ city =>
          UserMarketProfile(id, gender, ageGroup, country, city)
      }
  }

  def userParser: RowParser[(User, Option[(UserRole, Boolean)])] = {
    get[UUID]("market_user.id") ~
      get[String]("market_user.email") ~
      get[Boolean]("market_user.email_confirmed") ~
      get[Boolean]("market_user.terms_agreed") ~
      get[String]("market_user.password") ~
      get[String]("market_user.nick") ~
      get[String]("market_user.first_name") ~
      get[String]("market_user.last_name") ~
      hatParser.? ~
      profileParser.? ~
      UserRole.userRoleParser.? map {
        case id ~ email ~ emailConfirmed ~ termsAgreed ~ password ~ nick ~ firstName ~ lastName ~ maybeHat ~ maybeProfile ~ role =>
          (User(Some(id), email, emailConfirmed, termsAgreed, password, nick, firstName, lastName, maybeHat, maybeProfile, List(), List()), role)
      }
  }
}
