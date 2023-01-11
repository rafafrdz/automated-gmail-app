package io.github.rafafrdz.app

import cats.data.NonEmptyList
import cats.effect.{ExitCode, IO, IOApp}
import io.github.rafafrdz.contact.{ReceiverMail, SenderMail}
import io.github.rafafrdz.gmail.client.GMailClient
import io.github.rafafrdz.gmail.email.GMail
import io.github.rafafrdz.gmail.functions.gmail._
import io.github.rafafrdz.property.{CustomLogger, Properties}
import io.github.rafafrdz.table.Table

import scala.annotation.tailrec
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object App extends IOApp with CustomLogger {

  override def run(args: List[String]): IO[ExitCode] = for {
    properties <- IO {
      require(args.nonEmpty, "It requires configuration path. Example: path1/path2/application.conf")
      logger(s"Getting configuration from ${args.head}")
      Properties.from(args.head)
    }
    _ <- IO.println(s"Connecting to ${properties.account.user}")
    gmail =
      GMailClient
        .build
        .user(properties.account.user)
        .password(properties.account.password)
        .ssl
        .create

    _ <- IO.println(s"Succeeded connection with ${properties.account.user}")
    d <- programm(properties, gmail).as(ExitCode.Success)
    _ <- IO.println("Succeeded sending mails")
  } yield d


  def programm(properties: Properties, gmail: GMailClient): IO[NonEmptyList[String]] =
    for {
      receivers <- Table(properties.resource).receivers
      mails = receivers.map(rec => createMail(properties.sender, rec))
      _ <- IO.println("Sending mails...")
      xs = mails.map(mail => retrySending(gmail, mail, properties.retryNumber))
      nonEmptyList <- IO.parSequenceN(10)(xs).onCancel(IO.canceled)
    } yield flatten(nonEmptyList)


  /** Methods */
  private def createMail(sender: SenderMail, receiver: ReceiverMail): GMail = {
    sender.from match {
      case Some(value) => from(value, sender.email)
        .to(receiver.to)
        .subject(sender.subject)
        .html(receiver.body)
        .end
      case None => from(sender.email)
        .to(receiver.to)
        .subject(sender.subject)
        .html(receiver.body)
        .end
    }

  }

  private def flatten[T](xs: List[NonEmptyList[T]]): NonEmptyList[T] = {
    @tailrec
    def aux(acc: NonEmptyList[T], rest: List[NonEmptyList[T]]): NonEmptyList[T] = {
      rest match {
        case Nil => acc
        case ::(head, tl) => aux(acc ::: head, tl)
      }
    }

    aux(xs.head, xs.tail)
  }

  def retrySending(gmail: GMailClient, mail: GMail, retry: Int): IO[NonEmptyList[String]] = {
    lazy val mssgError: String = s"Error sending mail to: ${mail.mail.header.recipients.to.head.address}"
    lazy val errorResult = IO(NonEmptyList.one(mssgError))

    if (retry <= 0) gmail.send(mail).handleErrorWith { _ => println(mssgError); errorResult }
    else gmail.send(mail).andWait(2 seconds).handleErrorWith { _ => retrySending(gmail, mail, retry - 1) }
  }

}
