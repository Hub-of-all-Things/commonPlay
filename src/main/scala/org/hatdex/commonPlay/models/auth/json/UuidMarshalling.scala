/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.models.auth.json

import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.Try

trait UuidMarshalling {
  object UUIDReads extends Reads[java.util.UUID] {
    def parseUUID(s: String): Try[java.util.UUID] = Try(java.util.UUID.fromString(s))

    def reads(json: JsValue) = {
      json match {
        case JsString(s) =>
          parseUUID(s).map(JsSuccess(_)).getOrElse(JsError(JsPath(), ValidationError("Expected UUID string")))

        case _ =>
          JsError(Seq(JsPath() -> Seq(ValidationError("Expected UUID string"))))
      }
    }
  }

  object UUIDWrites extends Writes[java.util.UUID] {
    def writes(uuid: java.util.UUID): JsValue = JsString(uuid.toString)
  }

  implicit val uuidFormat: Format[java.util.UUID] = Format(UUIDReads, UUIDWrites)
}
