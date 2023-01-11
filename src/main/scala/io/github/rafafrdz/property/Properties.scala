package io.github.rafafrdz.property

import com.typesafe.config.{Config, ConfigFactory}
import io.github.rafafrdz.contact.{AccountGmail, SenderMail}
import io.github.rafafrdz.table.Resources

import java.io.File
import scala.util.Try

trait Properties {

  val conf: Config

  def resource: Resources = Resources(conf.getString("path.table"), conf.getString("path.txt"))

  def account: AccountGmail = AccountGmail(conf.getString("gmail.user.account"), conf.getString("gmail.user.password"))

  def sender: SenderMail = SenderMail(account.user, conf.getString("gmail.subject"), Try(conf.getString("gmail.from")).toOption)

  def retryNumber: Int = Try(conf.getString("gmail.retries").toInt).getOrElse(Properties.RetriesDefault)
}

object Properties {
  val RetriesDefault: Int = 5

  def from(path: String): Properties = new Properties {
    override val conf: Config = ConfigFactory.parseFile(new File(path)).resolve()
  }
}
