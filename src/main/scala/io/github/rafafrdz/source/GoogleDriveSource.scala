package io.github.rafafrdz.source

import cats.effect.IO

import scala.io.Source

case class GoogleDriveSource(tableURL: String, txtURL: String) extends HTTPResource

object GoogleDriveSource extends Package[GoogleDriveSource] {


  private val DriveDownloadURLPrefix: String = "https://drive.google.com/uc?export=download&id="
  private val DriveDownloadNameQuery: String = "&name="


  def getFile(url: String, name: Option[String] = None): IO[String] =
    HTTPResource.getFile(driveURL(url, name))

  def driveURL(url: String, name: Option[String] = None): String = {
    val id: String = getID(url)
    val drive: String = DriveDownloadURLPrefix + id
    name.map(n => drive + DriveDownloadNameQuery + n).getOrElse(drive)
  }

  private def getID(url: String): String =
    url
      .replace("https://drive.google.com/file/d/", "")
      .replace("/view?usp=share_link", "")


}
