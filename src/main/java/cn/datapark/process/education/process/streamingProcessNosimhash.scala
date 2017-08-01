package cn.datapark.process.education.process

import java.sql.{Statement, DriverManager, PreparedStatement, Connection}

import cn.datapark.process.education.DB.data2MySQL
import cn.datapark.process.education.Es.IndexArticle
import cn.datapark.process.education.SimHash.{ConfigUtil, SimHashTest,ArticleExtractTopoConfig}
import cn.datapark.process.education.Util.ConnectionPool
import com.hankcs.hanlp.HanLP
import kafka.serializer.StringDecoder
import org.apache.log4j.{Logger, Level}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json.JSONObject

/**
  * Created by cluster on 2017/6/11.
  */
object streamingProcessNosimhash extends Serializable  {
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("org.apache.kafka").setLevel(Level.ERROR)
  Logger.getLogger("org.apache.zookeeper").setLevel(Level.ERROR)
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  ConfigUtil.initConfig(streamingProcess.getClass.getClassLoader.getResourceAsStream(ConfigUtil.topoConfigfile))
  val topoConfig: ArticleExtractTopoConfig = ConfigUtil.getConfigInstance
  def main(args: Array[String]) {
    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
    val topics = "test04"
    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("educationProcess").setMaster("local[4]")
    //    val sparkConf = new SparkConf().setAppName("educationProcess")
    //加入解决序列化问题
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "5")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"serializer.class" -> "kafka.serializer.StringEncoder", "auto.offset.reset" -> "smallest")
    val kafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    //    StreamingExamples.setStreamingLogLevels()
    //    var conn: Connection = null
    var ps: PreparedStatement = null
    val ES = new IndexArticle

    val sql_commerce: String = "INSERT INTO commerce (site_name,post_title,post_url,content_text,content_html,crawl_time,type,module,keywords,state) VALUES (?,?,?,?,?,?,?,?,?,?)"
    val sql_conference: String = "INSERT INTO conference (site_name,post_title,post_url,conference_address,conference_time,crawl_time,type,module) VALUES (?,?,?,?,?,?,?,?)"

    kafkaDStream.map(pair => replaceContext(pair._2)).foreachRDD(rdd =>{

      rdd.foreachPartition(partion =>{

        val conn = ConnectionPool.getConnection.getOrElse(null)
        if(conn!=null){
          var simURL = ""
          partion.foreach(jsonObj =>{

            if ((jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null && jsonObj.get("content_text") != "")||(jsonObj.get("type") == "conference" && jsonObj.get("post_title") != null && jsonObj.get("post_title") != "")){
              if (jsonObj.get("type") == "commerce"){
                  //存入MySQL
                  data2MySQL.toMySQL_commerce(conn,sql_commerce,jsonObj)
                  //存入ES
                  ES.storageArticle(jsonObj)
                  println("commerce："+jsonObj.get("post_title"))
              }
              //这个类别的文章不用simhash去重，直接给定 post_title和conference_address 去查是否有这条数据
              else if (jsonObj.get("type") == "conference"){
                val conditions_post_title: String = jsonObj.getString("post_title")
                val conditions_conference_address: String = jsonObj.getString("conference_address")
                val sql_conditions = "select * from conference where post_title = '"+conditions_post_title+"'"+" AND conference_address = '"+conditions_conference_address+"'"
                ps = conn.prepareStatement(sql_conditions)
                val resultSet = ps.executeQuery()
                if (resultSet.next() == false){
                  //存入MySQL
                  data2MySQL.toMySQL_conference(conn,sql_conference,jsonObj)
                  println("conference："+jsonObj.get("post_title"))
                }else{
                  println(jsonObj.get("post_title")+"type = conference文章存在")
                }
              }
            }
          })
          ConnectionPool.closeConnection(conn)
        }
      })
    })
    ssc.start()
    ssc.awaitTermination()

  }

  /**
    * 判断文章中，可以分割为摘要的标点个数，如果小于等于3，则过滤掉
    * @param str
    * @return
    */
  def judgeLenght(str: String): Boolean ={
    val  istr = str.length()
    //    println("str的长度是：" + istr)
    val  str1 = str.replaceAll("[!?。！？;；]", "")
    val istr1 = str1.length()
    //    System.out.println("str1的长度是：" + istr1)
    //    System.out.println("标点符号的个数是：" + (istr - istr1))
    val result = istr - istr1
    if (result <= 3) false else true
  }

  /**
    * 清洗文章内容
    * @param str
    * @return
    */
  def replaceContext(str: String): JSONObject ={
    val json = new JSONObject(str)
    if (json.has("content_text")){
      val content_text = json.get("content_text").toString.replaceAll("(.*本文.*转载.*?[。]|更多专业报道.*|欢迎长按下方二维码.*?[。]|电商资讯第一入口问答｜.*|除非注明.*|问答｜.*|更多案例尽在.*|SocialBeta：每日.*|文中图片来自.*|题图.*)", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
      json.put("content_text",content_text)
    }
    json
  }

}
