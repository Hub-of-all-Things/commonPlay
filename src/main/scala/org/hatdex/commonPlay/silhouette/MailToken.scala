/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.silhouette

import org.joda.time.DateTime

trait MailToken {
  def id: String
  def email: String
  def expirationTime: DateTime
  def isExpired = expirationTime.isBeforeNow
}