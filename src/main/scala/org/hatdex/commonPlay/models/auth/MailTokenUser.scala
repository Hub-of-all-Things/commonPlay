/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.models.auth

import java.util.UUID

import org.hatdex.commonPlay.silhouette.MailToken
import org.joda.time.DateTime

case class MailTokenUser(id: String, email: String, expirationTime: DateTime, isSignUp: Boolean) extends MailToken

object MailTokenUser {
  private val mailTokenValidityHours = 24

  def apply(email: String, isSignUp: Boolean): MailTokenUser =
    MailTokenUser(UUID.randomUUID().toString, email, new DateTime().plusHours(mailTokenValidityHours), isSignUp)
}