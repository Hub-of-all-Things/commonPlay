/*
 * Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 11 2016
 */

package org.hatdex.commonPlay.utils

import scala.concurrent.{ ExecutionContext, Future }

object FutureTransformations {
  def transform[A](o: Option[Future[A]])(implicit ec: ExecutionContext): Future[Option[A]] =
    o.map(f => f.map(Option(_))).getOrElse(Future.successful(None))

  def transform[A](o: Option[Future[Option[A]]]): Future[Option[A]] =
    o.getOrElse(Future.successful(None))
}
