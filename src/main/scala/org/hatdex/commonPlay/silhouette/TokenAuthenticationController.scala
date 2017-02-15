/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

//package utils.silhouette
//
//import com.mohiva.play.silhouette.api.Silhouette
//import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
//import org.hatdex.commonPlay.models.auth.User
//import play.api.i18n.I18nSupport
//
//trait TokenAuthenticationController extends Silhouette[User, JWTAuthenticator] with I18nSupport {
//  val env: TokenAuthenticationEnvironment
//  implicit def securedRequest2User[A](implicit request: SecuredRequest[A]): User = request.identity
//  implicit def userAwareRequest2UserOpt[A](implicit request: UserAwareRequest[A]): Option[User] = request.identity
//}