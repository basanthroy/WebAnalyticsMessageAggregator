//val li = List(1,2,3,19)
//li
//val listr = li.toStream
//val li2 = li.map(_.==(2))
//val li3 = listr.map(_.unary_+unary_+)
//val li4 = li.map(_.unary_+unary_+)
//val tup = (1,23)
//tup._2

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.kafka.common.serialization.StringSerializer

//val spark = SparkSession.builder().appName("Spark SQL basic example").config("spark.some.config.option", "some-value").getOrCreate()

//def listWithSum(numbers: List[Int]) = numbers.foldLeft((List[Int](), 0)) {
//  (resultingTuple, currentInteger) =>
//    (currentInteger :: resultingTuple._1, currentInteger + resultingTuple._2)
//}
//
//val lis:List[Int] = 1 :: 2 :: 3 :: 4 :: Nil
//
//val fnEval = listWithSum(lis)

//case class Person(@JsonProperty("FName") FName: String, @JsonProperty("LName") LName: String) = {
//  def getFName = "a"
//
//  def getLName = {LName}
//}
//val objectMapper = new ObjectMapper() with ScalaObjectMapper
//objectMapper.registerModule(DefaultScalaModule)
//val str = """{"FName":"Mad", "LName":"Max"}"""
//val p:Person = new Person("fn", "ln")
//p.FName
//var s = objectMapper.writeValueAsString(p)
//val p1:Person = objectMapper.readValue[Person](str)
//val name:Person = objectMapper.readValue[Person](str)


//val objectMapper:ObjectMapper = new ObjectMapper() with ScalaObjectMapper
//objectMapper.registerModule(DefaultScalaModule)

//case class RestJsonRecord(@JsonProperty("f1") f1:String)
//
//val jsonString:String = """{"f1":"abc"}"""

//val restClass:RestJsonRecord = objectMapper.readValue[RestJsonRecord](jsonString)

//val name:RestJsonRecord = objectMapper.readValue[RestJsonRecord](jsonString)

//try {
//  var restClass: RestJsonRecord = objectMapper.readValue(jsonString, new Class[RestJsonRecord])
//} catch e:Exception {
//}
//restClass


//val s2:String = "s2"

case class Person(@JsonProperty("FName") FName: String, @JsonProperty("LName") LName: String)
//
//val objectMapper = new ObjectMapper() with ScalaObjectMapper
//objectMapper.registerModule(DefaultScalaModule)
//val str = """{"FName":"Mad", "LName": "Max"}"""
//val name:Person = objectMapper.readValue[Person](str)


//class Person(var FName: String, var LName: String) {
//  def getLname: String = {
//    LName
//  }
//  def getFName: String = {
//    FName
//  }
//}

//class Point(var x: Int, var y: Int) {
//  def move(dx: Int, dy: Int): Unit = {
//    x = x + dx
//    y = y + dy
//  }
//}

//var objectMapper = new ObjectMapper() with ScalaObjectMapper
//objectMapper.registerModule(DefaultScalaModule)
//objectMapper.setConfig(DeserializationConfig)
//objectMapper.setConfig(SerializationConfig)
val str = """{"FName":"Mad", "LName": "Max"}"""
//val name:Person = objectMapper.readValue[Person](str)
//val name:Person = objectMapper.readValue(str, classOf[Person])

val objectMapper: ObjectMapper = new ObjectMapper()
objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
objectMapper.registerModule(DefaultScalaModule)

//val name:Person = objectMapper.readValue(str, classOf[Person])
//name


//def createRestPayload(records: List[String]) = records.foldLeft((List[String](), 0)) {
//  (resultingTuple, currentInteger) =>
//    (currentInteger :: resultingTuple._1, currentInteger + resultingTuple._2)
//}


classOf[StringSerializer].getName
classOf[StringSerializer].getName()
