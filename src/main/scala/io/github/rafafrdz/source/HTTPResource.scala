package io.github.rafafrdz.source

import cats.effect.IO

import scala.io.Source

trait HTTPResource extends Resources

object HTTPResource extends Package[HTTPResource] {
  def getFile(url: String): IO[String] =
    IO(Source.fromURL(url)).map(body => body.mkString)
}
