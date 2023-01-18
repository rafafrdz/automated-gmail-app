package io.github.rafafrdz.source

import cats.effect.IO

case class GoogleDriveSource(tableURL: String, txtURL: String) extends HTTPResource

object GoogleDriveSource extends Package[GoogleDriveSource] {


  private val DriveDownloadURLPrefix: String = "https://drive.google.com/uc?export=download&id="

  /** Files */
  private val DriveFileURLPrefix: String = "https://drive.google.com/file/d/"
  /** SpreadSheets */
  private val DriveSpreadSheetURLPrefix: String = "https://docs.google.com/spreadsheets/d/"
  /** Documents */
  private val DriveDocumentURLPrefix: String = "https://docs.google.com/document/d/"
  /** Suffix */
  private val DriveFileURLSufix: String = "/view?usp=share_link"
  private val DriveEditSharingSufix: String = "edit?usp=sharing"
  private val DriveExportFormatQuery: String = "/export?format="


  sealed trait DriveSource {

    def id: String

    def url: String
  }

  case class FileSource(url: String, id: String) extends DriveSource

  case class SpreadSheetSource(url: String, id: String) extends DriveSource

  case class DocumentSource(url: String, id: String) extends DriveSource

  def getFile(url: String): IO[String] = {
    val source: DriveSource = driveURL(url)
    HTTPResource.getFile(source.url)
  }

  def isDocument(url: String): Boolean = url.startsWith(DriveDocumentURLPrefix)

  def isSpreadSheet(url: String): Boolean = url.startsWith(DriveSpreadSheetURLPrefix)

  def isFile(url: String): Boolean = url.startsWith(DriveFileURLPrefix)

  private def whatSource(url: String): DriveSource = {
    val id: String = getID(url)
    if (isFile(url)) FileSource(url, id)
    else if (isDocument(url)) DocumentSource(url, id)
    else if (isSpreadSheet(url)) SpreadSheetSource(url, id)
    else FileSource(url, id)
  }

  /** https://drive.google.com/uc?export=download&id=SPREAD_SHEET_ID */
  val FormatFileDownloadFunction: String => String = (id: String) => DriveDownloadURLPrefix + id
  /** https://docs.google.com/spreadsheets/d/SPREAD_SHEET_ID/export?format=csv */
  val FormatSpreadSheetDownloadFunction: String => String = (id: String) => DriveSpreadSheetURLPrefix + id + DriveExportFormatQuery + "csv"
  /** "https://docs.google.com/document/d/SPREAD_SHEET_ID/export?format=txt" */
  val FormatDocumentDownloadFunction: String => String = (id: String) => DriveDocumentURLPrefix + id + DriveExportFormatQuery + "txt"

  def driveURL(url: String): DriveSource = {
    whatSource(url) match {
      case FileSource(_, id) => FileSource(FormatFileDownloadFunction(id), id)
      case SpreadSheetSource(_, id) => SpreadSheetSource(FormatSpreadSheetDownloadFunction(id), id)
      case DocumentSource(_, id) => DocumentSource(FormatDocumentDownloadFunction(id), id)
    }
  }

  private def getID(url: String): String =
    List(DriveFileURLPrefix, DriveFileURLSufix, DriveSpreadSheetURLPrefix, DriveEditSharingSufix, DriveDocumentURLPrefix, "/")
      .foldLeft(url)(drop)

  private def drop(url: String, part: String): String = url.replace(part, "")


}
