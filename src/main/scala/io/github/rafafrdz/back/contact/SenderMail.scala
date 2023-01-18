package io.github.rafafrdz.back.contact

case class SenderMail(email: String, subject: String, from: Option[String] = None)
