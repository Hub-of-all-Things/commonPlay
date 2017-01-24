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

trait BaseUser extends IdentitySilhouette {
  val id: Option[UUID]
  val email: String
  val emailConfirmed: Boolean
  val password: String
  val firstName: String
  val lastName: String
  protected val userRoles: List[UserRole]

  def key: String = email

  def fullName: String = s"$firstName $lastName"

  def roles: List[UserRole] = {
    userRoles
  }
}

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
    protected val userRoles: List[UserRole],
    pendingRoles: List[UserRole]) extends BaseUser {

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
}

object User {
  val availableServices = Seq(CreateDataOffers(), CreateDataPlugs())
  val defaultServices = Seq(GetDataOffers(), ViewDataStats(), CanChat(), CreateDataOffers())
}
