package process

package Process

import java.sql.{DriverManager, PreparedStatement, Connection}

import Util.{List2String, CharSetUtil}
import com.hankcs.hanlp.HanLP
import kafka.serializer.StringDecoder
import org.apache.log4j.{Level, Logger}


import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.json.JSONObject

/**
  * Created by cluster on 2017/6/6.
  */
object streamingProcess {
  Logger.getLogger("org").setLevel(Level.ERROR)
  def main(args: Array[String]) {

    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
    val topics = "test01"

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


    val dataRDD = kafkaDStream.map(json => {
      val jsonObj = new JSONObject(json._2).
//      (jsonObj.get("site_name"),jsonObj.get("post_title"),jsonObj.get("post_url"),jsonObj.getString("content_text").replaceAll("[\\x{10000}-\\x{10FFFF}]", ""),jsonObj.get("content_html"),jsonObj.getInt("crawl_time"),jsonObj.getString("type"),jsonObj.getString("module"),HanLP.extractKeyword(jsonObj.getString("content_text"), 10).toString.replace("[","").replace("]",""))
    }).filter(a => a.)


//    val commerceRDD = dataRDD.filter(field => new JSONObject(field) )
//    val conferenceRDD = dataRDD.filter(file => file._7 == "conference")

    commerceRDD.foreachRDD(rdd => {
      rdd.foreachPartition(iterator => {
        var conn: Connection = null
        var ps: PreparedStatement = null
        val sql: String = "INSERT INTO commerce (site_name,post_title,post_url,content_text,content_html,crawl_time,type,module,keywords) VALUES (?,?,?,?,?,?,?,?,?)"

        try {
          conn = DriverManager.getConnection("jdbc:mysql://192.168.39.18:3306/datapark?useUnicode=true&characterEncoding=UTF-8", "root", "123456")
          iterator.foreach(line=>{
            ps = conn.prepareStatement(sql)
            ps.setString(1,line._1.toString)
            ps.setString(2, line._2.toString)
            ps.setString(3,line._3.toString)
            ps.setString(4,line._4.toString)
            ps.setString(5,line._5.toString)
            ps.setInt(6,line._6)
            ps.setString(7,line._7.toString)
            ps.setString(8,line._8.toString)
            ps.setString(9,line._9.toString)
            ps.executeUpdate()
          })
        } catch {
          case e: Exception =>  println(e.printStackTrace())
        }finally {
          if (ps != null)
            ps.close()
          if (conn != null)
            conn.close()
        }
      })
    })







    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }



}


