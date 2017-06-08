import kafka.serializer.StringDecoder

import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.json.JSONObject

/**
  * Created by cluster on 2017/6/6.
  */
object Test01 {

  def main(args: Array[String]) {
//    if (args.length < 2) {
//           |  <brokers> is a list of one or more Kafka brokers
//           |  <topics> is a list of one or more kafka topics to consume from
//    }

    //    Test01.setStreamingLogLevels()

//    val Array(brokers, topics) = args

    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
    val topics = "test02"

    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("simhash_keyword").setMaster("local[2]")
    //加入解决序列化问题
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "5")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.sparkContext.setLogLevel("ERROR")


    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"serializer.class" -> "kafka.serializer.StringEncoder", "auto.offset.reset" -> "smallest")
    val kafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    val dataDStream = kafkaDStream.foreachRDD(rdd =>{
      rdd.foreachPartition(part =>{
        part.foreach(line =>{
          //val dataAlias = addFieldAlias.FieldAlias(JSONObject.fromObject(line._2)) //第一步：对原始数据中的color,print,brand,material添加别名
//          val dataAlias = addFieldAlias.FieldAlias(new JSONObject(line._2))

          //simhash去重
          //HanLp提取关键词
          println(line)

        })
      })

    })
    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }

}
