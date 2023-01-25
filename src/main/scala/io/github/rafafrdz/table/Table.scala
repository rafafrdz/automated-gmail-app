package io.github.rafafrdz.table

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.github.rafafrdz.contact.ReceiverMail
import io.github.rafafrdz.property.Properties
import io.github.rafafrdz.source.Resources
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

case class Table(properties: Properties) {

  implicit lazy val spark: SparkSession =
    SparkSession
      .builder()
      .master("local[*]")
      .appName("automated-email")
      .getOrCreate()

  private val sep: String = properties.sepCSV

  private lazy val df: DataFrame = Resources.getTable(properties.resource, sep).unsafeRunSync()

  private lazy val header: Array[String] = {
    val h: Array[String] = df.columns
    lazy val wordsBlank = h.filter(s => s != s.trim)
    require(h.length > 1, "Table must have more than one column")
    require(h.forall(s => s == s.trim), s"Columns' name have a blank space outside the word. ${wordsBlank.map(w => s"'$w' =/= '${w.trim}'").mkString(", ")}")
    h
  }

  private lazy val to: String = {
    val hd: Array[String] = header
    Try(hd.filter(w => w.toLowerCase == "to").head) match {
      case Failure(_) => throw new Exception("Table must have the column 'to' in its header")
      case Success(value) => value
    }

  }

  private def replace(fileReaded: String): List[ReceiverMail] =
    df.rdd.collect()
      .map { row =>
        val toSender: String = row.getAs[String](to)
        val bodySender: String =
          header.map(f => ref(f) -> row.getAs[String](f))
            .foldLeft(fileReaded) { case (acc, (param, value)) => acc.replace(param, value) }

        ReceiverMail(toSender, bodySender)
      }.toList

  def receivers: IO[List[ReceiverMail]] = for {
    bodyText <- fileTxt
    senders = replace(bodyText)
  } yield senders

  private lazy val fileTxt: IO[String] = Resources.getTXT(properties.resource)


  def ref(x: String): String = s"{{$x}}"
}
