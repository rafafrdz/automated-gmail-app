package io.github.rafafrdz.property

import cats.effect.IO
import org.apache.log4j._

trait CustomLogger {

  // creates pattern layout
  var layout = new PatternLayout()
  var conversionPattern = "%-7p %d [%t] %c %x - %m%n"
  layout.setConversionPattern(conversionPattern)

  // creates console appender
  var consoleAppender = new ConsoleAppender()
  consoleAppender.setLayout(layout)
  consoleAppender.activateOptions()

  // creates file appender
  var fileAppender = new FileAppender()
  fileAppender.setFile("applog3.txt")
  fileAppender.setLayout(layout)
  fileAppender.activateOptions()

  // configures the root logger
  var rootLogger = Logger.getRootLogger
  rootLogger.setLevel(Level.ERROR)
  rootLogger.addAppender(consoleAppender)
  rootLogger.addAppender(fileAppender)

  def logger(mssg: String) = println(mssg)
}
