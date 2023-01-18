package io.github.rafafrdz.source

import cats.effect.IO
import fs2.io.file.{Files, Path}
import org.apache.spark.sql.{DataFrame, SparkSession}

trait Resources extends TXTResource with TableResource {
  self =>
  def from(path: String): Resources =
    Resources.copy(
      table = if (tableURL.contains("http")) tableURL else Resources.normalizePath(path) + tableURL
      , txt = if (txtURL.contains("http")) txtURL else Resources.normalizePath(path) + txtURL
    )

  def from(path: Option[String]): Resources =
    path.map(p => from(p)).getOrElse(self)

}

object Resources extends Package[Resources] {
  def normalizePath(path: String): String =
    "/" + path.split("/").filterNot(_.isEmpty).mkString("/") + "/"

  def getFile(url: String): IO[String] =
    Files[IO].readUtf8(Path(url)).compile.string

  def getTXT(res: Resources): IO[String] = getFileResource(res.txtURL)

  def getTable(res: Resources, sep: String)(implicit spark: SparkSession): IO[DataFrame] = {
    import spark.implicits._
    for {
      csv <- getFileResource(res.tableURL)
      dst = spark.sparkContext.parallelize(csv.linesIterator.toList).toDS()
      df = spark.read.option("header", "true").option("sep", sep).csv(dst)
    } yield df
  }


  private def getFileResource(url: String): IO[String] = {
    val isHttp: Boolean = url.contains("http")
    val isGoogleDrive: Boolean = isHttp && url.contains("google.com")
    if (isGoogleDrive) GoogleDriveSource.getFile(url)
    else if (isHttp) HTTPResource.getFile(url)
    else getFile(url)
  }

}