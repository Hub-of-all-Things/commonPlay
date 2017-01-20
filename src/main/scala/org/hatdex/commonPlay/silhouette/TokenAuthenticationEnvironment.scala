/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

//package utils.silhouette
//
//import javax.inject.{ Inject, Singleton }
//
//import com.mohiva.play.silhouette.api.util.{ Clock, PasswordInfo }
//import com.mohiva.play.silhouette.api.{ Environment, EventBus, SilhouetteEvent }
//import com.mohiva.play.silhouette.impl.authenticators._
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
//import com.mohiva.play.silhouette.impl.repositories.DelegableAuthInfoRepository
//import com.mohiva.play.silhouette.impl.util.{ BCryptPasswordHasher, SecureRandomIDGenerator }
//import org.hatdex.commonPlay.models.auth.User
//import play.api.Configuration
//
//import scala.concurrent.duration._
//
//class TokenAuthenticationEnvironment @Inject() (
//  val conf: Configuration,
//  userService: UserService,
//  injectedPasswordInfoDao: PasswordInfoDAO,
//  injectedTokenService: MailTokenUserService) extends Environment[User, CookieAuthenticator] with utils.ConfigSupport {
//
//  val identityService = userService
//  val passwordInfoDAO = injectedPasswordInfoDao
//  val tokenService = injectedTokenService
//
//  override implicit val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
//  val clock = Clock()
//  val eventBus = EventBus()
//  def publish(event: SilhouetteEvent): Unit = eventBus.publish(event)
//
//  val requestProviders = Seq()
//  val passwordHasher = new BCryptPasswordHasher()
//  def authInfo(password: String): PasswordInfo = passwordHasher.hash(password)
//
//  val authenticatorService = {
//    val cfg = requiredConfig("silhouette.apiAuthenticator")
//
//    new JWTAuthenticatorService(
//      JWTAuthenticatorSettings(
//        headerName = confRequiredString("headerName", cfg),
//        issuerClaim = confRequiredString("issuerClaim", cfg),
//        encryptSubject = confRequiredBoolean("encryptSubject", cfg),
//        authenticatorIdleTimeout = confGetInt("authenticatorIdleTimeout", cfg).map(_.seconds),
//        authenticatorExpiry = Duration(confRequiredString("authenticatorExpiry", cfg)).asInstanceOf[FiniteDuration],
//        sharedSecret = confRequiredString("sharedSecret", cfg)),
//      None,
//      idGenerator = new SecureRandomIDGenerator(),
//      clock)
//  }
//
//  lazy val authInfoRepository = new DelegableAuthInfoRepository(passwordInfoDAO)
//  lazy val credentialsProvider = new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))
//}