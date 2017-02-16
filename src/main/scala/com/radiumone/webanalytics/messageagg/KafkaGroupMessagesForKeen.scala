// scalastyle:off println
package com.radiumone.webanalytics.messageagg

import java.util
import java.util.concurrent.Future

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.radiumone.webanalytics.messageagg.WebAnalyticsJsonRecord
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.log4j.Level
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
//import org.apache.spark.streaming.kafka._

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._


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
object KafkaGroupMessagesForKeen {
  def main(args: Array[String]) {

    System.err.println("REACHED BEGINNING..........")

    if (args.length < 3) {
      System.err.println("Usage: KafkaWordCount <group> <topics> <destinationTopic>")
      System.exit(1)
    }

    val REST_PAYLOAD_RECORD_LIMIT:Int = 2000
//    val kafkaOpTopic:String = "SINGLEPARTITION_REST_TOPIC"
    val kafkaBrokers = "localhost:9092"


    val Array(group, topics, kafkaOpTopic) = args

    System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads> BASANTH -----")

    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("KafkaWordCount")
      .set("spark.driver.memory","1024mb")
      .set("spark.executor.memory","1024mb")
    System.err.println(sparkConf.get("spark.driver.memory"))
    System.err.println(sparkConf.get("spark.executor.memory"))
    val ssc = new StreamingContext(sparkConf, Seconds(8))
    ssc.checkpoint("checkpoint")

    StreamingExamples.setStreamingLogLevels()


    org.apache.log4j.Logger.getRootLogger.setLevel(Level.WARN)
    System.out.println("org.apache.log4j.Logger.getRootLogger.getLevel=" + org.apache.log4j.Logger.getRootLogger.getLevel)

    //    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
//      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "group.id" -> group,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

//    val topics2 = Array("topicA", "topicB")
    val topics2 = Array(topics)

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics2, kafkaParams)
    )

    val objectMapper:ObjectMapper = new ObjectMapper()

    case class restJsonRecord(f1:String)

//    var groupedRecords:Map[String, String] = Map()
//    var count:Int = 0


    System.err.println("stream = " + stream)

    stream.foreachRDD(x =>
      {

        System.err.println("x = " + x)

        // TODO : do we need to iterate over the partitions or can
        // TODO : we somehow iterate over the records in the
        // TODO : RDD directly ? I didn't see a slice() method for the 'x' RDD object
//        x.mapPartitionsWithIndex(
//            (index, part) => part.map(cr => (index, cr)), true)
//            .foreachPartition(part => {

        x.foreachPartition(part => {
//          val loopIndex = 0


          System.err.println("Partition = " + part)

//          while (!part.isEmpty) {
           while (!part.isEmpty) {

             val objectMapper: ObjectMapper = new ObjectMapper()
             objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
             objectMapper.registerModule(DefaultScalaModule)

            System.err.println("Partition is not empty = " + part)

//            part.take(2000)
//            part.drop(2000)
            // TODO : ensure that slice removes the 'taken' element so that
            // TODO : in the next iteration of the loop, the next set of elements are
            // TODO : chosen and not the same ones again
            val groupedRecords = part
                                 // .slice(loopIndex,loopIndex + REST_PAYLOAD_RECORD_LIMIT)
                                 // I removed loopIndex since I didn't want to have any global vars
                                  .slice(0,REST_PAYLOAD_RECORD_LIMIT)
//                                  .map(cr => cr.value())
                                   .map(cr => {val value:String = cr.value()
//                                               value

  val jsonObject:WebAnalyticsJsonRecord = objectMapper.readValue(value, classOf[WebAnalyticsJsonRecord])
//    .readValue(value, classOf[WebAnalyticsJsonRecord])

  //      jsonObject.doMethod()

  //      System.out.println("\n\njsonObject = " + jsonObject)

  val trackingId = jsonObject.tracking_id
  trackingId

                                     })
//                                  .map(cr => cr._1.toString)

            // TODO : 1. Need to tune batch sizes so that we don't end up with lot of tiny REST
            // TODO :    payloads due to the lack of availability of data in any one specific
            // TODO :    time interval.
            // TODO : 2. Another option is to have a wider time interval for batch size and if we end up with
            // TODO :    larger record counts, then just send the surplus records back into same kafka to be
            // TODO :    processed later

//            objectMapper.readValue(cr.value(), classOf[restJsonRecord])

            val singleValue:String  = groupedRecords.reduceLeft(_ + ", " +  _)

            System.err.println("groupedRecords.toString() = " + singleValue)

            val producer = createProducer(kafkaBrokers)

             val message = new ProducerRecord[String, String](kafkaOpTopic, null, singleValue)
             val fut:Future[RecordMetadata] = producer.send(message)

            System.err.println("Future object = " + fut + ", message=" + message)

          }
        }
        )

      }
    )

    ssc.start()
    ssc.awaitTermination()

  }

  def objmapp() {
    var value:String = """{"FName":"Mad", "LName": "Max"}"""
    val objectMapper: ObjectMapper = new ObjectMapper()
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    objectMapper.registerModule(DefaultScalaModule)
    val jsonObject:Person = objectMapper
      .readValue(value, classOf[Person])
    println("jsonobject" + jsonObject)
    println("string value" + objectMapper.writeValueAsString(jsonObject))
  }

  def createProducer(kafkaBrokers: String):KafkaProducer[String, String] = {
    val props = new util.HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
//    props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, classOf[StringSerializer].getName)
    val producer = new KafkaProducer[String, String](props)
    producer
  }


}



// scalastyle:on println
