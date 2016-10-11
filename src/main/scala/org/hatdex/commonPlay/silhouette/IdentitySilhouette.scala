/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.silhouette

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import org.hatdex.commonPlay.silhouette.Implicits._

trait IdentitySilhouette extends Identity {
  def key: String
  def loginInfo: LoginInfo = key
}