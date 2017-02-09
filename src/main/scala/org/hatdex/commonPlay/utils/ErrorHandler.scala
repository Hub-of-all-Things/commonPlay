/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.utils

import javax.inject._

import com.mohiva.play.silhouette.api.actions.{ SecuredErrorHandler, UnsecuredErrorHandler }
import org.hatdex.commonPlay.models.auth.User
import play.api._
import play.api.http.{ ContentTypes, DefaultHttpErrorHandler }
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent.Future

class ErrorHandler @Inject() (
  env: Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: Provider[Router],
  val messagesApi: MessagesApi,
  mailer: Mailer) extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
    with SecuredErrorHandler with UnsecuredErrorHandler with I18nSupport with ContentTypes with RequestExtractors with Rendering {

  // 401 - Unauthorized
  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = {
    Logger.debug("[Silhouette] Not authenticated")
    Future.successful {
      render {
        case Accepts.Json() => Unauthorized(Json.obj("error" -> "Not Authenticated", "message" -> s"Not Authenticated"))
        case _              => Unauthorized(views.html.defaultpages.unauthorized())
      }
    }
  }

  // 403 - Forbidden
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = {
    Future.successful {
      render {
        case Accepts.Json() => Forbidden(Json.obj("error" -> "Forbidden", "message" -> s"Access Denied"))
        case _              => Forbidden(views.html.defaultpages.unauthorized())
      }
    }
  }

  // 404 - page not found error
  override def onNotFound(request: RequestHeader, message: String): Future[Result] = Future.successful {
    implicit val _request = request
    implicit val noUser: Option[User] = None
    NotFound(env.mode match {
      case Mode.Prod => views.html.defaultpages.notFound(request.method, request.uri)
      case _         => views.html.defaultpages.devNotFound(request.method, request.uri, Some(router.get))
    })
  }

  // 500 - internal server error
  override def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    implicit val _request = request
    implicit val noUser: Option[User] = None
    mailer.serverErrorNotify(request, exception)
    Future.successful {
      render {
        case Accepts.Json() =>
          InternalServerError(Json.obj(
            "error" -> "Internal Server error",
            "message" -> s"A server error occurred, please report this error code to our admins: ${exception.id}"))
        case _ =>
          InternalServerError(views.html.defaultpages.error(exception))
      }
    }
  }

  override def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    implicit val _request = request
    implicit val noUser: Option[User] = None
    Future.successful {
      render {
        case Accepts.Json() => BadRequest(Json.obj("error" -> "Bad Request", "message" -> message))
        case _              => InternalServerError(views.html.defaultpages.badRequest(request.method, request.uri, message))
      }
    }
  }
}