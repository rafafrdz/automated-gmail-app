package io.github.rafafrdz.back.table

case class Resources(pathTable: String, pathTxt: String) {
  self =>
  def from(path: String): Resources =
    self.copy(
      pathTable = Resources.normalizePath(path) + pathTable
      , pathTxt = Resources.normalizePath(path) + pathTxt
    )

  def from(path: Option[String]): Resources =
    path.map(p => from(p)).getOrElse(self)
}

object Resources {
  private def normalizePath(path: String): String =
    "/" +  path.split("/").filterNot(_.isEmpty).mkString("/") + "/"
}