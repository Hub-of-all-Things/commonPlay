/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
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