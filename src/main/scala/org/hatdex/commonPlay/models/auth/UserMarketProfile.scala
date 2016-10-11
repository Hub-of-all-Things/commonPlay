/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.models.auth

import java.util.UUID

case class UserMarketProfile(
  id: UUID,
  gender: Option[String],
  ageGroup: Option[String],
  country: Option[String],
  city: Option[String])

object UserMarketProfile {
  val genders = Seq(
    "male", "female"
  )
  val ageGroups = Seq(
    "18 – 24",
    "25 – 34",
    "35 – 44",
    "45 – 54",
    "55 – 64",
    "65+"
  )
}

