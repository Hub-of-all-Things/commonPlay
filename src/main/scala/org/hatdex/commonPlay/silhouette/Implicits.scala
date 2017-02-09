/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.password.BCryptPasswordHasher

object Implicits {
  import scala.language.implicitConversions

  implicit def key2loginInfo(key: String): LoginInfo = LoginInfo(CredentialsProvider.ID, key)
  implicit def loginInfo2key(loginInfo: LoginInfo): String = loginInfo.providerKey
  implicit def pwd2passwordInfo(pwd: String): PasswordInfo = PasswordInfo(BCryptPasswordHasher.ID, pwd, salt = Some("your-salt"))
  implicit def passwordInfo2pwd(passwordInfo: PasswordInfo): String = passwordInfo.password
}