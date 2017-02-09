/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.models.auth.forms

import java.util.UUID

import org.hatdex.commonPlay.models.auth.User
import org.hatdex.commonPlay.models.auth.roles.UserRole
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages

object AuthForms {
  val passwordValidation = nonEmptyText(minLength = 6)

  val signInForm = Form(tuple(
    "identifier" -> email,
    "password" -> nonEmptyText,
    "rememberMe" -> boolean))

  val signinHatForm = Form("hataddress" -> nonEmptyText)
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // SIGN UP
  val emailForm = Form(single("email" -> email))

  def signUpForm(implicit messages: Messages): Form[User] = Form(
    mapping(
      "id" -> ignored(None: Option[UUID]),
      "email" -> email,
      "emailConfirmed" -> ignored(false),
      "password" -> passwordValidation,
      "nick" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "services" -> list(nonEmptyText),
      "terms" -> checked(Messages("signup.termsAgreed.mandatory")))({
        // APPLY
        case (id, userEmail, emailConfirmed, password, nick, firstName, lastName, services, termsAgreed) =>
          User(id, userEmail.toLowerCase, emailConfirmed, termsAgreed, password, nick.toLowerCase, firstName, lastName,
            hatOwned = None,
            marketProfile = None,
            userRoles = List(),
            pendingRoles = services.map(UserRole.userRoleDeserialize(_, None, approved = false)).map(_._1).intersect(User.availableServices))
      })({
        case user: User =>
          Some(
            (None, user.email, user.emailConfirmed, user.password, user.nick, user.firstName, user.lastName, user.pendingRoles.map(_.name), user.termsAgreed))
      }))

  def resetPasswordForm(implicit messages: Messages): Form[(String, String)] = Form(tuple(
    "password1" -> passwordValidation,
    "password2" -> nonEmptyText) verifying (Messages("auth.passwords.notequal"), passwords => passwords._2 == passwords._1))

  def changePasswordForm(implicit messages: Messages): Form[(String, String, String)] = Form(tuple(
    "current" -> nonEmptyText,
    "password1" -> passwordValidation,
    "password2" -> nonEmptyText) verifying (Messages("auth.passwords.notequal"), passwords => passwords._3 == passwords._2))
}
