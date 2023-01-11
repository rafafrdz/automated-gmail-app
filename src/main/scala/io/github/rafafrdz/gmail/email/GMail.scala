package io.github.rafafrdz.gmail.email

import cats.effect.IO
import emil.Mail
import emil.builder.{MailBuilder, Trans}

trait GMail {

  protected lazy val internalMail: Mail[IO] = Mail.empty[IO]

  def mail: Mail[IO] = internalMail

}

object GMail {

  private[email] def buildGmail(m: Mail[IO]): GMail = new GMail {
    override protected lazy val internalMail: Mail[IO] = m
  }

  private[email] def buildGmail(parts: Trans[IO]*): GMail =
    buildGmail(MailBuilder.build(parts: _*))

  def build: GMailBuilder = GMailBuilder.empty

}
