import java.sql.{DriverManager, PreparedStatement, Connection}

import com.hankcs.hanlp.HanLP
import kafka.serializer.StringDecoder

import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.json.JSONObject

/**
  * Created by cluster on 2017/6/6.
  *
  * 统计关键词做wordcount 但是这种方式不行，最终结果不能累加
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
    val topics = "test04"

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
    val kafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)

    val lines = kafkaDStream.map(line =>new JSONObject(line._2)).filter(jsonObj => (jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null && jsonObj.get("content_text") != ""))
    val keywordStr = lines.map(jsonObj => HanLP.extractKeyword(jsonObj.getString("content_text"), 10).toString.replaceAll("[\\[\\]]",""))

    val words = keywordStr.flatMap(_.split(","))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordCounts.foreachRDD(rdd =>{
      def func(records: Iterator[(String,Int)]) {
        //Connect the mysql
        var conn: Connection = null
        var stmt: PreparedStatement = null
        try {
          val url = "jdbc:mysql://192.168.39.18:3306/test?useUnicode=true&characterEncoding=UTF-8"
          val user = "root"
          val password = "123456"
          conn = DriverManager.getConnection(url, user, password)
          records.foreach(word => {
            val sql = "insert into keyword (word,num) values (?,?)"
            stmt = conn.prepareStatement(sql)
            stmt.setString(1, word._1)
            stmt.setInt(2, word._2)
            stmt.executeUpdate()
          })
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          if (stmt != null) {
            stmt.close()
          }
          if (conn != null) {
            conn.close()
          }
        }
      }
      val repartitionedRDD = rdd.repartition(3)
      repartitionedRDD.foreachPartition(func)
    })

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }

}
