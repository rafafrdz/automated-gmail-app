package io.github.rafafrdz.gmail.email

import cats.effect.IO
import emil.Header
import emil.builder._

import java.time.Instant

trait GMailBuilder {
  self =>

  def from(email: String): GMailBuilder = add(From[IO](email))

  def from(personal: String, email: String): GMailBuilder = add(From[IO](personal, email))

  def to(email: String): GMailBuilder = add(To[IO](email))

  def to(personal: String, email: String): GMailBuilder = add(To[IO](personal, email))

  def cc(email: String): GMailBuilder = add(Cc[IO](email))

  def cc(personal: String, email: String): GMailBuilder = add(Cc[IO](personal, email))

  def bcc(email: String): GMailBuilder = add(Bcc[IO](email))

  def bcc(personal: String, email: String): GMailBuilder = add(Bcc[IO](personal, email))

  def subject(text: String): GMailBuilder = add(Subject[IO](text))

  def date(second: Long): GMailBuilder = date(Instant.ofEpochSecond(second))

  /**
   * ISO Format
   * pattern: yyyy-MM-ddThh:mm:SS.sss
   * Example: 2018-11-30T18:35:24.00Z
   * */
  def date(value: String): GMailBuilder = date(Instant.parse(value))

  def date(instant: Instant): GMailBuilder = add(Date[IO](instant))

  def body(text: String): GMailBuilder = add(TextBody[IO](text))

  def html(text: String): GMailBuilder = add(HtmlBody[IO](text))

  def header(hd: Header): GMailBuilder = add(CustomHeader(hd))

  def header(custom: CustomHeader[IO]): GMailBuilder = add(custom)

  def header(name: String, value: String, more: String*): GMailBuilder =
    header(Header(name, value, more: _*))

  def inReplyTo(value: String, more: String*): GMailBuilder =
    header(Header.inReplyTo(value, more: _*))

  def userAgent(value: String): GMailBuilder =
    header(Header.userAgent(value))

  def xmailer(value: String): GMailBuilder =
    header(Header.xmailer(value))

  def listId(value: String): GMailBuilder =
    header(Header.listId(value))

  def end: GMail = GMail.buildGmail(parts: _*)

  protected lazy val parts: Vector[Trans[IO]] = Vector.empty

  private def add(part: Trans[IO]): GMailBuilder = new GMailBuilder {
    override protected lazy val parts: Vector[Trans[IO]] = self.parts :+ part
  }

}

object GMailBuilder {
  private[email] def empty: GMailBuilder = new GMailBuilder {}
}
