/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.utils

import java.io.StringReader
import java.security.Security
import java.security.interfaces.RSAPublicKey
import javax.inject.Inject

import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.SignedJWT
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.hatdex.commonPlay.models.auth.User
import org.hatdex.commonPlay.services.UserService
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future
import scala.util.Try

class JwtAuthenticatedRequest[A](val identity: User, val request: Request[A])
  extends WrappedRequest[A](request)

class JwtAuthenticatedAction @Inject() (userService: UserService, configuration: play.api.Configuration) extends ActionBuilder[JwtAuthenticatedRequest] {
  def invokeBlock[A](request: Request[A], block: (JwtAuthenticatedRequest[A]) => Future[Result]): Future[Result] = {
    request.headers.get("X-Auth-Token")
      .orElse(request.getQueryString("X-Auth-Token"))
      .orElse(request.getQueryString("token"))
      .map(validateJwtToken)
      .map { eventualMaybeUser =>
        eventualMaybeUser.flatMap { maybeUser =>
          maybeUser map { user =>
            block(new JwtAuthenticatedRequest(user, request))
          } getOrElse {
            Future.successful(Results.Unauthorized)
          }

        } recover {
          case e =>
            Logger.error(s"Error while authenticating: ${e.getMessage}")
            Results.Unauthorized(Json.obj("status" -> "unauthorized", "message" -> s"No valid login information"))
        }
      }
      .getOrElse(Future.successful(Results.Unauthorized))
  }

  def validateJwtToken(token: String): Future[Option[User]] = {
    val expectedSubject = "hat"
    val expectedResource = configuration.getString("service.address").get
    val expectedAccessCope = "validate"
    val maybeSignedJWT = Try(SignedJWT.parse(token))

    val maybeFutureUser = maybeSignedJWT.map { signedJWT =>
      val claimSet = signedJWT.getJWTClaimsSet
      val fresh = claimSet.getExpirationTime.after(DateTime.now().toDate)
      val subjectMatches = claimSet.getSubject == expectedSubject
      val resource = Option(claimSet.getClaim("resource")).map(r => java.net.URLDecoder.decode(r.asInstanceOf[String], "UTF-8"))
      val resourceMatches = resource.exists(_.contains(expectedResource))
      val accessScopeMatches = Option(claimSet.getClaim("accessScope")).contains(expectedAccessCope)

      Logger.debug(s"Claimeset: $claimSet")
      Logger.debug(s"Claimset issuer: ${claimSet.getIssuer}, fresh: $fresh, subjectMatches: $subjectMatches, resourceMatches: $resourceMatches ($resource, $expectedResource)")

      if (fresh && resourceMatches && accessScopeMatches) {
        val hatAddress = claimSet.getIssuer
        val maybeUser = for {
          user <- userService.findByHatAddress(hatAddress).map(_.get)
          publicKey <- readPublicKey(user.hatOwned.get.publicKey)
        } yield {
          val verifier: JWSVerifier = new RSASSAVerifier(publicKey)
          val verified = signedJWT.verify(verifier)
          if (verified) {
            Logger.debug("JWT token signature verified")
            Some(user)
          }
          else {
            Logger.debug("JWT token signature failed")
            None
          }
        }
        maybeUser recover {
          case e =>
            Logger.error(s"Error while finding user: ${e.getMessage}")
            throw e
        }

      }
      else {
        Logger.debug("JWT token validation failed")
        Future(None)
      }
    } getOrElse {
      // JWT parse error
      Future(None)
    }
    maybeFutureUser
  }

  Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
  def readPublicKey(publicKey: String): Future[RSAPublicKey] = {
    Future {
      val reader = new PEMParser(new StringReader(publicKey))
      val temp: SubjectPublicKeyInfo = reader.readObject().asInstanceOf[SubjectPublicKeyInfo]
      val converter = new JcaPEMKeyConverter()
      converter.getPublicKey(temp).asInstanceOf[RSAPublicKey]
      //      temp.parsePublicKey().asInstanceOf[RSAPublicKey]
    }
  }
}

class JwtUserAwareRequest[A](val maybeUser: Option[User], val request: Request[A])
  extends WrappedRequest[A](request)

class JwtUserAwareAction @Inject() (
    userService: UserService,
    configuration: play.api.Configuration,
    jwtAuthenticatedAction: JwtAuthenticatedAction) extends ActionBuilder[JwtUserAwareRequest] {

  def invokeBlock[A](request: Request[A], block: (JwtUserAwareRequest[A]) => Future[Result]): Future[Result] = {
    request.headers.get("X-Auth-Token")
      .orElse(request.getQueryString("X-Auth-Token"))
      .orElse(request.getQueryString("token"))
      .map(jwtAuthenticatedAction.validateJwtToken)
      .map { eventualMaybeUser =>
        eventualMaybeUser.flatMap { maybe =>
          block(new JwtUserAwareRequest(maybe, request))
        } recoverWith {
          case e =>
            Logger.error(s"Error while authenticating: ${e.getMessage}")
            block(new JwtUserAwareRequest(None, request))
        }
      }
      .getOrElse(block(new JwtUserAwareRequest(None, request)))
  }
}
