/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.utils

import akka.actor.Scheduler
import akka.pattern.after

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._
import scala.util.Random

object FutureRetries {
  def retry[T](f: => Future[T], delays: List[FiniteDuration])(implicit ec: ExecutionContext, s: Scheduler): Future[T] = {
    f recoverWith { case _ if delays.nonEmpty => after(delays.head, s)(retry(f, delays.tail)) }
  }

  def withDefault(delays: List[FiniteDuration], retries: Int, default: FiniteDuration): List[FiniteDuration] = {
    if (delays.length > retries) {
      delays take retries
    }
    else {
      delays ++ List.fill(retries - delays.length)(default)
    }
  }

  def withJitter(delays: List[FiniteDuration], maxJitter: Double, minJitter: Double): List[FiniteDuration] = {
    delays.map { delay =>
      val jitter = delay * (minJitter + (maxJitter - minJitter) * Random.nextDouble)
      jitter match {
        case d: FiniteDuration => d
        case _                 => delay
      }
    }
  }

  val fibonacci: Stream[FiniteDuration] = 0.seconds #:: 1.seconds #:: (fibonacci zip fibonacci.tail).map { t => t._1 + t._2 }
}
