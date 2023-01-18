package io.github.rafafrdz.source

import cats.effect.IO
import fs2.io.file.{Files, Path}

trait Resources extends TXTResource with TableResource {
  self =>
  def from(path: String): Resources =
    Resources.copy(
      table = Resources.normalizePath(path) + tableURL
      , txt = Resources.normalizePath(path) + txtURL
    )

  def from(path: Option[String]): Resources =
    path.map(p => from(p)).getOrElse(self)

  def resolve(): Resources = Resources.resolve(self)
}

object Resources extends Package[Resources] {
  private def normalizePath(path: String): String =
    "/" + path.split("/").filterNot(_.isEmpty).mkString("/") + "/"

  def getFile(url: String): IO[String] =
    Files[IO].readUtf8(Path(url)).compile.string

  def getTXT(res: Resources) = getFileResource(res.txtURL)
  def getTable(res: Resources) = getFileResource(res.tableURL)
  private def getFileResource(url: String): IO[String] = {
    val isHttp: Boolean = url.contains("http")
    val isGoogleDrive: Boolean = isHttp && url.contains("drive.google.com")
    if (isGoogleDrive) GoogleDriveSource.getFile(url)
    else if (isHttp) HTTPResource.getFile(url)
    else getFile(url)
  }


  private def resolve(res: Resources): Resources = {
    val isHttp: Boolean = res.txtURL.contains("http") || res.tableURL.contains("http")
    val isGoogleDrive: Boolean = isHttp && (res.txtURL.contains("drive.google.com") || res.tableURL.contains("drive.google.com"))
    if (isGoogleDrive) GoogleDriveSource(res.tableURL, res.txtURL)
    else if (isHttp) HTTPResource(res.tableURL, res.txtURL)
    else res
  }
}