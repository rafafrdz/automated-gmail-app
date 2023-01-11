package io.github.rafafrdz.gmail.client

import emil.MailConfig.UrlParts
import emil.{MailConfig, SSLType}
import io.github.rafafrdz.gmail.client.GMailClientBuilder.ImplicitURLPart

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

case class GMailClientBuilder private(mc: MailConfig = GMailClientBuilder.configEmpty) {
  self =>

  def user(name: String): GMailClientBuilder = {
    require(name.contains("@"), "It must be a valid gmail account. Ej: myemail@gmail.com or myemail@domain.io (created on gmail)")
    GMailClientBuilder(mc.copy(user = name))
  }

  def password(pass: String): GMailClientBuilder =
    GMailClientBuilder(mc.copy(password = pass))

  def port(p: Int): GMailClientBuilder =
    GMailClientBuilder(mc.copy(url = mc.urlParts.copy(port = Option(p)).show()))

  def port25: GMailClientBuilder = port(25)

  def port465: GMailClientBuilder = port(465)

  def port587: GMailClientBuilder = port(587)

  def ssl: GMailClientBuilder =
    ssl(SSLType.SSL)

  def startTLS: GMailClientBuilder =
    ssl(SSLType.StartTLS)

  def noEncryption: GMailClientBuilder =
    ssl(SSLType.NoEncryption)

  def ssl(mode: SSLType): GMailClientBuilder =
    GMailClientBuilder(mc.copy(sslType = mode))

  def ssl(mode: String): GMailClientBuilder =
    ssl(SSLType.fromString(mode).getOrElse(SSLType.NoEncryption))

  def OAuth2(enable: Boolean): GMailClientBuilder =
    GMailClientBuilder(mc.copy(enableXOAuth2 = enable))

  def withOAuth2: GMailClientBuilder =
    OAuth2(true)

  def certificateCheck(enable: Boolean): GMailClientBuilder =
    GMailClientBuilder(mc.copy(disableCertificateCheck = !enable))

  def enableCertificateCheck: GMailClientBuilder =
    certificateCheck(true)

  def disableCertificateCheck: GMailClientBuilder =
    certificateCheck(false)

  def timetout(time: Long, unit: TimeUnit): GMailClientBuilder =
    GMailClientBuilder(mc.copy(timeout = FiniteDuration(time, unit)))

  def timetout(time: Long): GMailClientBuilder =
    GMailClientBuilder(mc.copy(timeout = FiniteDuration(time, TimeUnit.SECONDS)))

  def smtp(email: String, password: String): GMailClientBuilder = GMailClientBuilder(MailConfig.gmailSmtp(email, password))

  def imap(email: String, password: String): GMailClientBuilder = GMailClientBuilder(MailConfig.gmailImap(email, password))

  def create: GMailClient = new GMailClient {
    override protected val config: MailConfig = mc
  }

}

object GMailClientBuilder {

  private def configEmpty: MailConfig = smtp("", "").mc

  private[client] def build: GMailClientBuilder = GMailClientBuilder()

  def smtp(email: String, password: String): GMailClientBuilder = GMailClientBuilder(MailConfig.gmailSmtp(email, password)).port465

  def imap(email: String, password: String): GMailClientBuilder = GMailClientBuilder(MailConfig.gmailImap(email, password))

  implicit class ImplicitURLPart(urlPart: UrlParts) {
    def show(): String = s"${urlPart.protocol}://${urlPart.host}:${urlPart.port.get}"
  }
}
