import java.util.HashMap

import _root_.kafka.serializer.StringDecoder
import com.hankcs.hanlp.HanLP
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.log4j.{Level, Logger}

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.json.JSONObject

/**
  * Created by cluster on 2017/6/21.
  */
object Test05_wordcount {

  def main(args: Array[String]) {
//    if (args.length < 4) {
//      System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>")
//      System.exit(1)
//    }
//创建streamingContext
val conf = new SparkConf().setAppName("test").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(2))

    //将数据进行保存（这里作为演示，生产中保存在hdfs）
    ssc.checkpoint("checkPoint")
    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
    val topics = "test04"
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"serializer.class" -> "kafka .serializer.StringEncoder", "auto.offset.reset" -> "smallest")
    val kafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    val lines = kafkaDStream.map(line =>new JSONObject(line._2)).filter(jsonObj => (jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null && jsonObj.get("content_text") != ""))
    val keywordStr = lines.map(jsonObj => HanLP.extractKeyword(jsonObj.getString("content_text"), 10).toString.replaceAll("[\\[\\]]",""))
    //（1）窗口长度（window length），即窗口的持续时间，上图中的窗口长度为3
    //（2）滑动间隔（sliding interval），窗口操作执行的时间间隔，上图中的滑动间隔为2
    // 这两个参数必须是原始DStream 批处理间隔（batch interval）的整数倍（上图中的原始DStream的batch interval为1）
    val words = keywordStr.flatMap(_.split(","))
    val wordCounts = words.map(x => (x, 1))
      .reduceByKeyAndWindow(_ + _, _ - _, Seconds(10), Seconds(2), 2)
    wordCounts.foreachRDD(a => a.foreach( b => println(b)))

    ssc.start()
    ssc.awaitTermination()
  }
}
