/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.utils

import javax.inject.Inject

import play.api.UsefulException
import play.api.i18n.Messages
import play.api.mvc.RequestHeader
import play.twirl.api.Html

abstract class Mailer @Inject() (configuration: play.api.Configuration, ms: MailService) {

  import scala.language.implicitConversions

  implicit def html2String(html: Html): String = html.toString

  def serverErrorNotify(request: RequestHeader, exception: UsefulException)(implicit m: Messages): Unit

  def serverExceptionNotify(request: RequestHeader, exception: Throwable)(implicit m: Messages): Unit
}

