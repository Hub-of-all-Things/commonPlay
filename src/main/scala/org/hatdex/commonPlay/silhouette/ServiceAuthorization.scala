/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.silhouette

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import org.hatdex.commonPlay.models.auth.User
import org.hatdex.commonPlay.models.auth.roles.{ Master, UserRole }
import play.api.mvc.Request

import scala.concurrent.Future

/**
 * Only allows those users that have at least a service of the selected.
 * Master service is always allowed.
 * Ex: WithService("serviceA", "serviceB") => only users with services "serviceA" OR "serviceB" (or "master") are allowed.
 */
case class WithRole(anyOf: UserRole*) extends Authorization[User, CookieAuthenticator] {
  def isAuthorized[B](user: User, authenticator: CookieAuthenticator)(implicit r: Request[B]): Future[Boolean] = {
    Future.successful {
      WithRole.isAuthorized(user, anyOf: _*)
    }
  }

}
object WithRole {
  def isAuthorized(user: User, anyOf: UserRole*): Boolean =
    anyOf.intersect(user.roles).nonEmpty || user.roles.contains(Master())
}

/**
 * Only allows those users that have every of the selected services.
 * Master service is always allowed.
 * Ex: Restrict("serviceA", "serviceB") => only users with services "serviceA" AND "serviceB" (or "master") are allowed.
 */
case class WithRoles(allOf: UserRole*) extends Authorization[User, CookieAuthenticator] {
  def isAuthorized[A](user: User, authenticator: CookieAuthenticator)(implicit r: Request[A]): Future[Boolean] = {
    Future.successful {
      WithRoles.isAuthorized(user, allOf: _*)
    }
  }
}
object WithRoles {
  def isAuthorized(user: User, allOf: UserRole*): Boolean =
    allOf.intersect(user.roles).size == allOf.size || user.roles.contains(Master())
}