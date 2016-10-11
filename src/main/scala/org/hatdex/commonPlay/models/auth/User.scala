/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.models.auth

import java.util.UUID

import org.hatdex.commonPlay.models.auth.roles._
import org.hatdex.commonPlay.silhouette.IdentitySilhouette

/*
 * A user can register some accounts from third-party services, then it will have access to different parts of the webpage. The 'master' privilege has full access.
 * Ex: ("master") -> full access to every point of the webpage.
 * Ex: ("serviceA") -> have access only to general and serviceA areas.
 * Ex: ("serviceA", "serviceB") -> have access only to general, serviceA and serviceB areas.
 */
case class User(
    id: Option[UUID],
    email: String,
    emailConfirmed: Boolean,
    termsAgreed: Boolean,
    password: String,
    nick: String,
    firstName: String,
    lastName: String,
    hatOwned: Option[Hat],
    marketProfile: Option[UserMarketProfile],
    private val userRoles: List[UserRole],
    pendingRoles: List[UserRole]) extends IdentitySilhouette {

  def key: String = email

  def fullName: String = s"$firstName $lastName"

  def withRoles(roles: UserRole*): User = {
    this.copy(userRoles = userRoles.filterNot(r => roles.map(_.title).contains(r.title)) ++ roles)
  }

  def withPendingRoles(roles: UserRole*): User = {
    this.copy(pendingRoles = pendingRoles ++ roles)
  }

  def withoutRoles(roles: UserRole*): User = {
    val withoutRoles = userRoles.filterNot(r => roles.map(_.title).contains(r.title))
    this.copy(userRoles = withoutRoles)
  }

  def withoutPendingRoles(roles: UserRole*): User = {
    val withoutRoles = pendingRoles.filterNot(r => roles.map(_.title).contains(r.title))
    this.copy(pendingRoles = withoutRoles)
  }

  def roles: List[UserRole] = {
    userRoles
  }
}

object User {
  val availableServices = Seq(CreateDataOffers(), CreateDataPlugs())
  val defaultServices = Seq(GetDataOffers(), ViewDataStats(), CanChat(), CreateDataOffers())
}
