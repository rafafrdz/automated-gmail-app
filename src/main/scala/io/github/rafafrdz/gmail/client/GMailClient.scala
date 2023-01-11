package io.github.rafafrdz.gmail.client

import cats.data.NonEmptyList
import cats.effect.{IO, unsafe}
import emil.javamail.JavaMailEmil
import emil.{Emil, Mail, MailConfig}
import io.github.rafafrdz.gmail.client.GMailClient.{IOGmail, ioemil}
import io.github.rafafrdz.gmail.email.GMail

trait GMailClient extends IOGmail {

  protected val config: MailConfig

  private lazy val connect: IOGmail =
    GMailClient.ioemil(config)

  override def run[A](op: emil.MailOp[IO, ioemil.C, A]): IO[A] =
    connect.run(op)

  override def send(mail: Mail[IO], mails: Mail[IO]*): IO[NonEmptyList[String]] =
    connect.send(mail, mails: _*)

  def sendUnsafe(mail: Mail[IO], mails: Mail[IO]*)(implicit runtime: unsafe.IORuntime): NonEmptyList[String] =
    send(mail, mails: _*).unsafeRunSync()

  def send(mail: GMail, mails: GMail*): IO[NonEmptyList[String]] =
    send(mail.mail, mails.map(_.mail): _*)

  def sendUnsafe(mail: GMail, mails: GMail*)(implicit runtime: unsafe.IORuntime): NonEmptyList[String] =
    send(mail, mails: _*).unsafeRunSync()

}

object GMailClient {

  type IOGmail = Emil.Run[IO, ioemil.C]

  private[client] val ioemil: Emil[IO] = JavaMailEmil[IO]()

  def build: GMailClientBuilder = GMailClientBuilder.build

}
