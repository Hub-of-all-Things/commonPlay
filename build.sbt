import Dependencies._

libraryDependencies ++= Seq(
  Library.Play.ws,
  Library.Play.cache,
  Library.Play.test,
  Library.Play.typesafeConfigExtras,
  Library.Play.mailer,
  Library.Play.specs2,
  Library.Play.Specs2.matcherExtra,
  Library.Play.Specs2.mock,
  Library.Specs2.core,
  Library.Play.Db.jdbc,
  Library.Play.Db.postgres,
  Library.Play.Db.anorm,
  Library.Play.Jwt.bouncyCastle,
  Library.Play.Jwt.bouncyCastlePkix,
  Library.Play.Jwt.nimbusDsJwt,
  Library.Play.Silhouette.passwordBcrypt,
  Library.Play.Silhouette.persistence,
  Library.Play.Silhouette.cryptoJca,
  Library.Play.Silhouette.silhouette,
  Library.akkaTestkit,
  Library.scalaGuice
)

publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(s3resolver.value("HAT Library Artifacts " + prefix, s3("library-artifacts-" + prefix + ".hubofallthings.com")) withMavenPatterns)
}