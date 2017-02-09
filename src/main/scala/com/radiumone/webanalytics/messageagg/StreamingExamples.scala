package com.radiumone.webanalytics.messageagg

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging
import org.slf4j.LoggerFactory
import org.slf4j.Logger

/**
  * Created by broy on 2/6/17.
  */
/** Utility functions for Spark Streaming examples. */
object StreamingExamples
{

  def main(args: Array[String]) {
    System.out.println(Level.WARN)
    System.out.println(classOf[SparkContext])
  }

//  def setStreamingLogLevels(): Unit = {
//
//  }

  /** Set reasonable logging levels for streaming if the user has not configured log4j. */
  def setStreamingLogLevels() {
    val log4jInitialized = org.apache.log4j.Logger.getRootLogger.getAllAppenders.hasMoreElements
    if (!log4jInitialized) {
      // We first log something to initialize Spark's default logging, then we override the
      // logging level.
//      logInfo("Setting log level to [WARN] for streaming example." +
//        " To override add a custom log4j.properties to the classpath.")
      val logger = LoggerFactory.getLogger(this.getClass.getName)
      logger.info("Setting log level to [WARN] for streaming example." +
                " To override add a custom log4j.properties to the classpath.")
      org.apache.log4j.Logger.getRootLogger.setLevel(Level.WARN)
    }
  }
}