package io.github.rafafrdz.source

trait Package[T <: Resources] {

  def unapply(arg: T): Option[(String, String)] = Option((arg.tableURL, arg.txtURL))

  def apply(table: String, txt: String): T = new Resources {
    override def tableURL: String = table

    override def txtURL: String = txt
  }.asInstanceOf[T]

  def copy(table: String, txt: String): T =
    new Resources {
      override def tableURL: String = table

      override def txtURL: String = txt
    }.asInstanceOf[T]

}
