package org.hatdex.commonPlay.models.auth.json

import java.util.UUID

import org.hatdex.commonPlay.models.auth.{ Hat, User, UserMarketProfile }
import play.api.libs.json.{ JsPath, Json, Writes }
import play.api.libs.json._
import play.api.libs.functional.syntax._

trait UserMarshalling extends UuidMarshalling {
  implicit val hatJsonReads = Json.reads[Hat]
  implicit val hatJsonWrites = Json.writes[Hat]
  implicit val userMarketProfileJsonReads = Json.reads[UserMarketProfile]
  implicit val userMarketProfileJsonWrites = Json.writes[UserMarketProfile]

  implicit val userJsonWrites: Writes[User] = (
    (JsPath \ "uuid").write[Option[UUID]] and
    (JsPath \ "email").write[String] and
    (JsPath \ "emailConfirmed").write[Boolean] and
    (JsPath \ "termsAgreed").write[Boolean] and
    (JsPath \ "nick").write[String] and
    (JsPath \ "firstName").write[String] and
    (JsPath \ "lastName").write[String] and
    (JsPath \ "hatOwned").write[Option[Hat]] and
    (JsPath \ "marketProfile").write[Option[UserMarketProfile]])({ user =>
      (user.id, user.email, user.emailConfirmed, user.termsAgreed, user.nick,
        user.firstName, user.lastName, user.hatOwned, user.marketProfile)
    })

  implicit val userJsonReads: Reads[User] = (
    (JsPath \ "uuid").readNullable[UUID] and
    (JsPath \ "email").read[String] and
    (JsPath \ "emailConfirmed").read[Boolean] and
    (JsPath \ "termsAgreed").read[Boolean] and
    (JsPath \ "nick").read[String] and
    (JsPath \ "firstName").read[String] and
    (JsPath \ "lastName").read[String] and
    (JsPath \ "hatOwned").readNullable[Hat] and
    (JsPath \ "marketProfile").readNullable[UserMarketProfile])((id, email, emailConfirmed, termsAgreed, nick, firstName, lastName, hatOwned, marketProfile) =>
      User(id, email, emailConfirmed, termsAgreed, "", nick, firstName, lastName, hatOwned, marketProfile, List(), List()))
}
