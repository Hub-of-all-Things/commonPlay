/*
 * Copyright (C) 2016 HAT Data Exchange Ltd - All Rights Reserved
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, 10 2016
 */

package org.hatdex.commonPlay.utils

import scala.annotation.tailrec

//from https://github.com/julienrf/chooze/blob/master/app/util/Util.scala
object Slugs {
  def slugify(str: String): String = {
    import java.text.Normalizer
    Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\w ]", "").replace(" ", "-").toLowerCase
  }

  def slugifyUnique(str: String, existing: Seq[String]): String = generateUniqueSlug(slugify(str), existing)

  @tailrec
  private def generateUniqueSlug(slug: String, existingSlugs: Seq[String]): String = {
    if (!(existingSlugs contains slug)) {
      slug
    }
    else {
      val EndsWithNumber = "(.+-)([0-9]+)$".r
      slug match {
        case EndsWithNumber(s, n) => generateUniqueSlug(s + (n.toInt + 1), existingSlugs)
        case s                    => generateUniqueSlug(s + "-2", existingSlugs)
      }
    }
  }
}
