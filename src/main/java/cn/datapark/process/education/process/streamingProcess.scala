package cn.datapark.process.education.process
import java.sql.{Statement, DriverManager, PreparedStatement, Connection}

import cn.datapark.process.education.DB.data2MySQL
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
object streamingProcess extends Serializable  {
  Logger.getLogger("org").setLevel(Level.ERROR)
  //ConfigUtil.initConfig(classOf[ArticleContentExtractBolt].getClassLoader.getResourceAsStream(ConfigUtil.topoConfigfile))
  ConfigUtil.initConfig(streamingProcess.getClass.getClassLoader.getResourceAsStream(ConfigUtil.topoConfigfile))
  val topoConfig: ArticleExtractTopoConfig = ConfigUtil.getConfigInstance
  def main(args: Array[String]) {
    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
    val topics = "test04"

    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("educationProcess").setMaster("local[2]")
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

    val simClass = new SimHashTest
    val sql_commerce: String = "INSERT INTO commerce (site_name,post_title,post_url,content_text,content_html,crawl_time,type,module,keywords,abstract,state) VALUES (?,?,?,?,?,?,?,?,?,?,?)"
    val sql_conference: String = "INSERT INTO conference (site_name,post_title,post_url,conference_address,conference_time,crawl_time,type,module) VALUES (?,?,?,?,?,?,?,?)"
    kafkaDStream.foreachRDD(rdd =>{
      rdd.foreachPartition(partion =>{
//        conn = DriverManager.getConnection("jdbc:mysql://192.168.39.18:3306/datapark?useUnicode=true&characterEncoding=UTF-8", "root", "123456")
        val conn = ConnectionPool.getConnection.getOrElse(null)
        if(conn!=null){
          var simURL = ""
          partion.foreach(json =>{
            val jsonObj = new JSONObject(json._2)
            if ((jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null && jsonObj.get("content_text") != "" )||(jsonObj.get("type") == "conference" && jsonObj.get("post_title") != null && jsonObj.get("post_title") != "")){
              if (jsonObj.get("type") == "commerce"){
                simURL = simClass.checkSimilarArticle(jsonObj)
                if (simURL == null){
                  //存入MySQL
                  data2MySQL.toMySQL_commerce(conn,sql_commerce,jsonObj)
                  println("commerce："+jsonObj.get("post_title"))
                }else{
                  println(jsonObj.get("post_title")+"type = commerce文章存在")
                }
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
}
