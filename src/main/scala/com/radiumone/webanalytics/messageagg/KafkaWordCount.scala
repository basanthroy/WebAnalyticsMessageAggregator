// scalastyle:off println
package com.radiumone.webanalytics.messageagg

import java.util.HashMap

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.streaming.kafka010.ConsumerStrategies._
import org.apache.spark.streaming.kafka010.LocationStrategies._
import org.apache.spark.streaming.kafka010.{ConsumerStrategy, KafkaUtils, LocationStrategy}





/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>
 *   <zkQuorum> is a list of one or more zookeeper servers that make quorum
 *   <group> is the name of kafka consumer group
 *   <topics> is a list of one or more kafka topics to consume from
 *   <numThreads> is the number of threads the kafka consumer should use
 *
 * Example:
 *    `$ bin/run-example \
 *      org.apache.spark.examples.streaming.KafkaWordCount zoo01,zoo02,zoo03 \
 *      my-consumer-group topic1,topic2 1`
 */
//object KafkaWordCount {
//  def main(args: Array[String]) {
//    if (args.length < 4) {
//      System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>")
//      System.exit(1)
//    }
//
//    StreamingExamples.setStreamingLogLevels()
//
//    val Array(zkQuorum, group, topics, numThreads) = args
//    System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads> BASANTH -----")
//    System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads> BASANTH -----")
//    System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads> BASANTH -----")
//    System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads> BASANTH -----")
//    val sparkConf = new SparkConf()
//      .setMaster("local[*]")
//      .setAppName("KafkaWordCount")
//      .set("spark.driver.memory","1024mb")
//      .set("spark.executor.memory","1024mb")
//    System.err.println(sparkConf.get("spark.driver.memory"))
//    System.err.println(sparkConf.get("spark.executor.memory"))
//    val ssc = new StreamingContext(sparkConf, Seconds(2))
//    ssc.checkpoint("checkpoint")
//
//    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
//    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
//    val words = lines.flatMap(_.split(" "))
//    val wordCounts = words.map(x => (x, 1L))
//      .reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2)
//    wordCounts.print()
//
//    ssc.start()
//    ssc.awaitTermination()
//  }
//}

// Produces some random words between 1 and 100.
object KafkaWordCountProducer {

  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: KafkaWordCountProducer <metadataBrokerList> <topic> " +
        "<messagesPerSec> <wordsPerMessage>")
      System.exit(1)
    }

    val Array(brokers, topic, messagesPerSec, wordsPerMessage) = args

    // Zookeeper connection properties
    val props = new HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    // Send some messages
    while(true) {
      (1 to messagesPerSec.toInt).foreach { messageNum =>
//        val str = (1 to wordsPerMessage.toInt).map(x => scala.util.Random.nextInt(10).toString)
//          .mkString(" ")

        val str = requestJsonTemplate format sampleTrackingIds(scala.util.Random.nextInt(sampleTrackingIds.size))

//        val message = new ProducerRecord[String, String](topic, null, this.js)
        val message = new ProducerRecord[String, String](topic, null, str)
        producer.send(message)
      }

      Thread.sleep(1000)
    }
  }

  val sampleTrackingIds = List("B80854A8-7B56-4041-8DED-F353890C7976", "C11DC874-7582-4F82-9DAD-94244FAFC999", "AEA4F0B2-D876-4E0E-AF01-A7C8010BBE33",
  "C813F76D-F79F-4F1C-BEE8-CA5CF3A7B71E", "4ED10645-5D2E-476D-9C90-A29B5C12E365")

  val requestJsonTemplate = """{
    "tracking_id": "%s",
    "application_name": null,
    "application_user_id": null,
    "application_version": "11.8.2.2",
    "application_build": "2",
    "conn_type": "WIFI",
    "timezone": "America/Los_Angeles",
    "user_language": "en",
    "sdk_version": "3.3.0",
    "source": "ADVERTISER_SDK",
    "page": {
      "page": {
      "referrer": "http://www.radiumone.com",
      "title": "AdTech Ninjas",
      "url": "www.cnn.com",
      "scroll_position": "30x40"
    }
    },
    "device_info": {
      "device_info": {
      "id_info": {
      "post_cookie": "32charactersneeded",
      "ob_login": "ob_login_value",
      "opt_out": false
    },
      "ip_v4": "92.117.48.48.99.48.92.117.48.48.97.56.92.117.48.48.48.49.92.117.48.48.52.54",
      "user_agent": "Mozilla/5.0 (iPad; CPU OS 10_1_1 like Mac OS X) AppleWebKit/602.2.14 (KHTML, like Gecko) Mobile/14B100",
      "screen": {
      "width": 320,
      "height": 568,
      "density": 2,
      "viewport_size": "768x1024"
    }
    }
    },
    "event_info": {
      "event_info": [
    {
      "event_name": "Key-SearchBegan",
      "key_value": {
      "network": "RealZeit - Android",
      "source": "RealiZeit > RealZeit - Android",
      "id": "123",
      "name": "Shoes",
      "currency": "USD",
      "receipt_status": "no_receipt"
    },
      "lat": null,
      "lon": null,
      "session_id": "D8A7BBF6-DCF2-40AA-9E66-B78C44B816E5",
      "timestamp": 1481329407375,
      "transaction_id": "92.117.48.48.52.102.92.117.48.48.48.51.92.117.48.48.99.51.92.117.48.48.52.56.92.117.48.48.100.55.92.117.48.48.48.56.92.117.48.48.52.54.92.117.48.48.98.57.92.117.48.48.56.50.92.117.48.48.100.54.92.117.48.48.98.54.92.117.48.48.55.50.92.117.48.48.48.97.92.117.48.48.102.97.92.117.48.48.52.57.92.117.48.48.102.102"
    }
      ]
    }
  }"""

}
// scalastyle:on println
