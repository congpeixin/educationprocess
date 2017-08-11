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
object streamingProcess extends Serializable  {
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("org.apache.kafka").setLevel(Level.ERROR)
  Logger.getLogger("org.apache.zookeeper").setLevel(Level.ERROR)
  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
  ConfigUtil.initConfig(streamingProcess.getClass.getClassLoader.getResourceAsStream(ConfigUtil.topoConfigfile))
  val topoConfig: ArticleExtractTopoConfig = ConfigUtil.getConfigInstance
  def main(args: Array[String]) {
    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
    val topics = "test07"

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
    val simClass = new SimHashTest
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
                simURL = simClass.checkSimilarArticle(jsonObj)
                if (simURL == null){
                  //存入MySQL
                  data2MySQL.toMySQL_commerce(conn,sql_commerce,jsonObj)
                  //存入ES
                  ES.storageArticle(jsonObj)
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
    var content_text = ""
    var content_html = ""
    val json = new JSONObject(str)
    if (json.has("content_html")){
      if (json.get("site_name").toString == "36氪"){
        content_text = json.get("content_text").toString.replaceAll("(.*本文.*转载.*?[。]|更多专业报道.*|欢迎长按下方二维码.*?[。]|电商资讯第一入口问答｜.*|除非注明.*|问答｜.*|更多案例尽在.*|SocialBeta：每日.*|文中图片来自.*|题图.*)", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll(".*36氪经授权发布。|.*授权36氪转载。|编者按：本文[来|转]自.*?，|本文来自.*?，","")

        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else{
        content_text = json.get("content_text").toString.replaceAll("(.*本文.*转载.*?[。]|更多专业报道.*|欢迎长按下方二维码.*?[。]|电商资讯第一入口问答｜.*|除非注明.*|问答｜.*|更多案例尽在.*|SocialBeta：每日.*|文中图片来自.*|题图.*)", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<span.*><b>未按照规范转载者，虎嗅保留追究相应责任的权利</b></span>|<span>html<br></span>|<span.*>\\*文章为作者独立观点，不代表虎嗅网立场<br></span>" +
          "|<span.*>.*来源[：|自|于].*</span>|<p>\\s*.*来源[：| ：|自|于|\\ |].*</p>|<span.*>.*[本文|关注].*微信公众号.*</span>|未来面前，你我还都是孩子，还不去下载|虎嗅App|猛嗅创新！|"+
          "关注大胆，拒绝一切小心翼翼|↓|虎嗅注：|<span.*>.*来自:.*</span>|<p.*>.*标签:.*</p>|除非注明.*[.html]|<div class=\"author-main\">.*</p></div>|" +
          "<div class=\"article-btn\">.*</span></div>|<div class=\"article-source\">.*</em></div>|本文.*转载.*[\\u4e00-\\u9fa5|。|.+）]|<p.*>.*更多专业报道.*</p>|" +
          "本文.*转载.*[\\u4e00-\\u9fa5|。|.+）]|关注作者|已关注|私信|H5玩法说明视频|" +
          "【品牌制片厂】|是 SocialBeta 推出的栏目，会从创意、制作、幕后等多个角度分享品牌所拍摄的可以媲美电影的视频广告，我们的口号是：「别看烂片啦！来看广告啊喂！」|" +
          "<p>.*微信.*关注.*</p>|<p nocleanhtml=\"true\">.*</p>|<p class=\"lazylood\">.*</p>|<p>.*图.*来自.*</p>|<p style=\"text-align:center;\">.*</p>|" +
          "<blockquote style=\"white-space: normal;\">.*案例一周.*</blockquote>|<p style=\"white-space: normal;\">.*案例一周.*</p>|<p>.*本文来自.*</p>" +
          "|<p>.*本文转自.*</p>|<p>\\s*雷锋网.*文章.*转载.*</p>|<p>.*雷锋网.*公众号.*</p>|<p.*>.*[本文|关注].*微信公众号.*</p>|<img class=\"aligncenter\" src=\"http://www.tmtpost.com/public/css/images/wzny_ewm.jpg\" alt=\"\">" +
          "|<p.*>.*[本文|关注].*微信号.*</p>|<p.*>.*作者：.*</p>|<p.*>.*关注.*公众号.*</p>|<img .* alt=\"钛媒体\"/>|<b>.*速途.*消息.*</b>" +
          "|<img src=.*logo_sootoo.jpg\"/>|<p>【广 告 主】.*</p>|<p>【广告代理】.*</p>|【制作公司】.*|<div class=\"article-copyright-bar\">([\\s\\S]*?)</div>|<p class=\"article-copyright-bar\">([\\s\\S]*?)</p>" +
          "|<p.*>.*猎云网.*company_code.*</p>|<p class=\"instructions\">.*</p>|<img.*机器之能图标.png\"/>|<p>.*编译[ \\| |:|：].*</p>|<p class=\"vcard author\">.*</p>" +
          "|<p>.*[微信公众号|微信号][：|:| |(|（][0-9a-zA-Z]*</p>|<p>.*扫描.*二维码.*</p>|<p.*>.*长按.*二维码.*</p>|<p.*>.*活动详情.*</p>|<p.*>.*报名[详情|请戳|请点击连接|链接|连接].*</p>" +
          "|<p.*>.*本文.*作者.*</p>|<p.*><a href=.*ad/id.*</p>|<div class=\"author\">.*?</div>|<div class=\"mesinfo cf\">.*</div>|<div class=\"copyright\">.*</div>|DoNews.*记者.*）","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
    }
    json
  }


}
