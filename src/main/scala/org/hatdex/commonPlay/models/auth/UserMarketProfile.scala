/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
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

