/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.silhouette

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.{ CookieAuthenticator, JWTAuthenticator }
import org.hatdex.commonPlay.models.auth.User

trait AuthenticationEnvironment extends Env {
  type I = User
  type A = CookieAuthenticator
}

trait TokenAuthenticationEnvironment extends Env {
  type I = User
  type A = JWTAuthenticator
}
