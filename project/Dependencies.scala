/* Copyright (C) HAT Data Exchange Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Andrius Aucinas <andrius.aucinas@hatdex.org>, September 2016
 */

import sbt._

object Dependencies {

  object Versions {
    val crossScala = Seq("2.11.8")
    val scalaVersion = crossScala.head
  }

  val resolvers = Seq(
    "Atlassian Releases" at "https://maven.atlassian.com/public/",
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "HAT Library Artifacts Snapshots" at "https://s3-eu-west-1.amazonaws.com/library-artifacts-snapshots.hubofallthings.com",
    "HAT Library Artifacts Releases" at "https://s3-eu-west-1.amazonaws.com/library-artifacts-releases.hubofallthings.com"
  )

  object Library {

    object Play {
      val version = play.core.PlayVersion.current
      val ws = "com.typesafe.play" %% "play-ws" % version
      val cache = "com.typesafe.play" %% "play-cache" % version
      val test = "com.typesafe.play" %% "play-test" % version
      val filters = "com.typesafe.play" %% "play-filters" % version
      val specs2 = "com.typesafe.play" %% "play-specs2" % version
      val jsonDerivedCodecs = "org.julienrf" % "play-json-derived-codecs_2.11" % "3.3"
      val typesafeConfigExtras = "com.iheart" %% "ficus" % "1.3.4"
      val mailer = "com.typesafe.play" %% "play-mailer" % "5.0.0"

      object Db {
        val jdbc = "com.typesafe.play" %% "play-jdbc" % version
        val postgres = "org.postgresql" % "postgresql" % "9.4-1206-jdbc4"
        val anorm = "com.typesafe.play" %% "anorm" % "2.5.2"
        val liquibase = "org.liquibase" % "liquibase-maven-plugin" % "3.5.1"
      }

      object Specs2 {
        private val version = "3.6.6"
        val matcherExtra = "org.specs2" %% "specs2-matcher-extra" % version
        val mock = "org.specs2" %% "specs2-mock" % version
      }
      object Jwt {
        private val bouncyCastleVersion = "1.55"
        val bouncyCastle = "org.bouncycastle" % "bcprov-jdk15on" % bouncyCastleVersion
        val bouncyCastlePkix = "org.bouncycastle" % "bcpkix-jdk15on" % bouncyCastleVersion
        val nimbusDsJwt = "com.nimbusds" % "nimbus-jose-jwt" % "2.16"
      }

      object Utils {
        val playBootstrap = "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3" exclude("org.webjars", "jquery")
        val jbcrypt = "org.mindrot" % "jbcrypt" % "0.3m"
        val commonsValidator = "commons-validator" % "commons-validator" % "1.5.0"
        val jsonDerivedCodecs = "org.julienrf" % "play-json-derived-codecs_2.11" % "3.2"
        val htmlCompressor = "com.mohiva" %% "play-html-compressor" % "0.6.3"
        val pegdown = "org.pegdown" % "pegdown" % "1.6.0"
      }

      object Silhouette {
        private val silhouetteVersion = "4.0.0"
        val passwordBcrypt = "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion
        val persistence = "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion
        val cryptoJca = "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVersion
        val silhouette = "com.mohiva" %% "play-silhouette" % silhouetteVersion
      }
    }

    object Apis {
      private val xmppRocksVersion = "0.7.0"
      val xmppCoreClient = "rocks.xmpp" % "xmpp-core-client" % xmppRocksVersion
      val xmppExtension = "rocks.xmpp" % "xmpp-extensions" % xmppRocksVersion
      val xmppExtensionsClient = "rocks.xmpp" % "xmpp-extensions-client" % xmppRocksVersion
      val xmppWebsocket = "rocks.xmpp" % "xmpp-websocket" % xmppRocksVersion
      val restFb = "com.restfb" % "restfb" % "1.23.0"
      val googleHttpClientJackson2 = "com.google.http-client" % "google-http-client-jackson2" % "1.22.0"
      val googleHttoClient = "com.google.http-client" % "google-http-client" % "1.22.0"
      val googleOauthClient = "com.google.oauth-client" % "google-oauth-client" % "1.22.0"
      val googleApiClient = "com.google.api-client" % "google-api-client" % "1.22.0"
      val googleApiServicesCalendar = "com.google.apis" % "google-api-services-calendar" % "v3-rev186-1.22.0"
      val snakeYaml = "org.yaml" % "snakeyaml" % "1.17"
      val awsJavaSdk = "com.amazonaws" % "aws-java-sdk" % "1.10.64"
    }

    object Specs2 {
      private val version = "3.6.6"
      val core = "org.specs2" %% "specs2-core" % version
      val matcherExtra = "org.specs2" %% "specs2-matcher-extra" % version
      val mock = "org.specs2" %% "specs2-mock" % version
    }

    object Akka {
      val clusterTools = "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.6"
      val slf4j = "com.typesafe.akka" %% "akka-slf4j" % "2.4.6"
      val httpCore = "com.typesafe.akka" %% "akka-http-core" % "2.4.6"
      val sslConfig = "com.typesafe" %% "ssl-config-akka" % "0.2.0"
    }

    object Services {
      val stripe = "com.stripe" % "stripe-java" % "2.7.0"
      val mailchimp = "com.ecwid" % "maleorang" % "3.0-0.9.2"
    }

    object HATDeX {
      private val version = "2.3.0-SNAPSHOT"
      val hatClient = "org.hatdex" %% "hat-client-scala-play" % version
    }

    val scalaGuice = "net.codingwell" %% "scala-guice" % "4.0.1"
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % "2.4.7"
  }
}
