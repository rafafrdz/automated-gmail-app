package io.github.rafafrdz.table

import cats.effect.IO
import fs2.io.file.{Files, Path}
import io.github.rafafrdz.contact.ReceiverMail
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

case class Table(resource: Resources) {

  val spark: SparkSession =
    SparkSession
      .builder()
      .master("local[*]")
      .appName("automated-email")
      .getOrCreate()

  private val sep: String = ","

  private lazy val df: DataFrame = spark.read.option("header", "true").option("sep", sep).csv(resource.pathTable)

  private lazy val header: Array[String] = {
    val h: Array[String] = df.columns
    require(h.length > 1, "Table must have more than one column")
    h
  }

  private lazy val to: String = {
    Try(header.filter(w => w.toLowerCase == "to").head) match {
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

  private lazy val fileTxt: IO[String] = Files[IO].readUtf8(Path(resource.pathTxt)).compile.string

  def ref(x: String): String = s"{{$x}}"
}
