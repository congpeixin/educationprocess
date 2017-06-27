import java.sql.{DriverManager, PreparedStatement, Connection}


import cn.datapark.process.education.DB.data2MySQL
import cn.datapark.process.education.SimHash.{ArticleExtractTopoConfig, ConfigUtil, SimHashTest}


import com.hankcs.hanlp.HanLP
import kafka.serializer.StringDecoder
import org.apache.log4j.spi.LoggerFactory
import org.apache.log4j.{Logger, Level}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json.JSONObject

/**
  * Created by congpeixin on 2017/6/23 15:00.
  *
  * content_text和post_title都采用了simhash进行过滤
  *
  */
object Test02_simhash {
  Logger.getLogger("org").setLevel(Level.ERROR)
  //ConfigUtil.initConfig(classOf[ArticleContentExtractBolt].getClassLoader.getResourceAsStream(ConfigUtil.topoConfigfile))
  ConfigUtil.initConfig(Test02_simhash.getClass.getClassLoader.getResourceAsStream(ConfigUtil.topoConfigfile))
  val topoConfig: ArticleExtractTopoConfig = ConfigUtil.getConfigInstance
  def main(args: Array[String]) {
    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
    val topics = "test04"

    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("simhash_keyword").setMaster("local[2]")
    //加入解决序列化问题
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "5")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"serializer.class" -> "kafka.serializer.StringEncoder", "auto.offset.reset" -> "smallest")
    val kafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    //    StreamingExamples.setStreamingLogLevels()
    var conn: Connection = null
    var ps: PreparedStatement = null


    val simClass = new SimHashTest

    val sql_commerce: String = "INSERT INTO commerce (site_name,post_title,post_url,content_text,content_html,crawl_time,type,module,keywords,abstract) VALUES (?,?,?,?,?,?,?,?,?,?)"
    val sql_conference: String = "INSERT INTO conference (site_name,post_title,post_url,conference_address,conference_time,crawl_time,type,module) VALUES (?,?,?,?,?,?,?,?)"
    kafkaDStream.foreachRDD(rdd =>{
      rdd.foreachPartition(partion =>{
        conn = DriverManager.getConnection("jdbc:mysql://192.168.39.18:3306/test?useUnicode=true&characterEncoding=UTF-8", "root", "123456")
        var simURL = ""
        partion.foreach(json =>{
          val jsonObj = new JSONObject(json._2)
          if ((jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null && jsonObj.get("content_text") != "" )||(jsonObj.get("type") == "conference" && jsonObj.get("post_title") != null && jsonObj.get("post_title") != "")){


            if (jsonObj.get("type") == "commerce"){
              simURL = simClass.checkSimilarArticle(jsonObj)
              if (simURL == null){
                ps = conn.prepareStatement(sql_commerce)
                ps.setString(1,jsonObj.get("site_name").toString)
                ps.setString(2, jsonObj.get("post_title").toString.replace(" ",""))
                ps.setString(3,jsonObj.get("post_url").toString)
                ps.setString(4,jsonObj.getString("content_text").replaceAll("[\\x{10000}-\\x{10FFFF}]", "").toString)
                ps.setString(5,jsonObj.get("content_html").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", ""))
                ps.setInt(6,jsonObj.getInt("crawl_time"))
                ps.setString(7,jsonObj.getString("type"))
                ps.setString(8,jsonObj.get("module").toString)
                ps.setString(9,HanLP.extractKeyword(jsonObj.getString("content_text"), 10).toString.replaceAll("[\\[\\]]","").replace(" ",""))
                ps.setString(10,HanLP.extractSummary(jsonObj.getString("content_text"), 5).toString)
                ps.executeUpdate()
                println("---------------------------------------------------"+jsonObj)
              }else{
                println(jsonObj.get("post_title")+"type = commerce文章存在")
              }
            }
            else if (jsonObj.get("type") == "conference"){
              simURL = simClass.checkSimilarArticle(jsonObj)
              if (simURL == null){
                ps = conn.prepareStatement(sql_conference)
                ps.setString(1,jsonObj.get("site_name").toString)
                ps.setString(2, jsonObj.get("post_title").toString.replace(" ","").replaceAll ("\\\\r\\\\n", ""))
                ps.setString(3,jsonObj.get("post_url").toString)
                ps.setString(4,jsonObj.getString("conference_address").replaceAll("[\\x{10000}-\\x{10FFFF}]", ""))//conference_address
                ps.setString(5,jsonObj.getString("conference_time"))//conference_time
                ps.setInt(6,jsonObj.getInt("crawl_time"))
                ps.setString(7,jsonObj.getString("type"))
                ps.setString(8,jsonObj.get("module").toString)
                ps.executeUpdate()
                println("*****************************************************"+jsonObj)
              }else{
                println(jsonObj.get("post_title")+"type = conference文章存在")
              }
            }
          }
        })
      })
    })





    ssc.start()
    ssc.awaitTermination()

  }
}
