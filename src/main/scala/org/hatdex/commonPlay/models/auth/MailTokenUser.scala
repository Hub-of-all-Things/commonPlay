/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
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