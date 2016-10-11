/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.silhouette

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.{ Environment, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import net.ceedubs.ficus.Ficus._
import org.hatdex.commonPlay.models.auth.User
import org.joda.time.DateTime
import play.api.i18n.I18nSupport
import play.api.mvc.Controller
import play.api.{ Configuration, Logger }

import scala.concurrent.duration.FiniteDuration

trait AuthenticationController extends Controller with I18nSupport {
  def silhouette: Silhouette[AuthenticationEnvironment]
  def env: Environment[AuthenticationEnvironment] = silhouette.env

  def SecuredAction = silhouette.SecuredAction
  def UnsecuredAction = silhouette.UnsecuredAction
  def UserAwareAction = silhouette.UserAwareAction

  implicit def securedRequest2User[A](implicit request: SecuredRequest[AuthenticationEnvironment, A]): User = request.identity
  implicit def userAwareRequest2UserOpt[A](implicit request: UserAwareRequest[AuthenticationEnvironment, A]): Option[User] = request.identity

  val clock: Clock
  val configuration: Configuration

  def authenticatorWithRememberMe(authenticator: CookieAuthenticator, rememberMe: Boolean): CookieAuthenticator = {
    if (rememberMe) {
      //      val rememberMeDuration: Duration = rememberMeParams._1.asInstanceOf[Duration]
      Logger.info(s"Remember me params: $rememberMeParams")
      val expirationTime: DateTime = clock.now + rememberMeParams._1
      authenticator.copy(
        expirationDateTime = expirationTime,
        idleTimeout = rememberMeParams._2,
        cookieMaxAge = rememberMeParams._3)
    }
    else {
      authenticator
    }
  }

  private lazy val rememberMeParams: (FiniteDuration, Option[FiniteDuration], Option[FiniteDuration]) = {
    val cfg = configuration.getConfig("silhouette.authenticator.rememberMe").get.underlying
    (
      cfg.as[FiniteDuration]("authenticatorExpiry"),
      cfg.getAs[FiniteDuration]("authenticatorIdleTimeout"),
      cfg.getAs[FiniteDuration]("cookieMaxAge"))
  }
}