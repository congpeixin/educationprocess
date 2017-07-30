package cn.datapark.process.education.process

import java.sql.PreparedStatement

import cn.datapark.process.education.DB.data2MySQL
import cn.datapark.process.education.Es.IndexArticle
import cn.datapark.process.education.SimHash.{SimHashTest, ArticleExtractTopoConfig, ConfigUtil}
import cn.datapark.process.education.Util.ConnectionPool
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import org.json.JSONObject

/**
  * Created by cluster on 2017/7/30.
  */
object streamingProcessSimhash {
  Logger.getLogger("org").setLevel(Level.ERROR)
  ConfigUtil.initConfig(streamingProcess.getClass.getClassLoader.getResourceAsStream(ConfigUtil.topoConfigfile))
  val topoConfig: ArticleExtractTopoConfig = ConfigUtil.getConfigInstance
  private var simURL: String = ""
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("educationProcess").setMaster("local[2]")
    //    val sparkConf = new SparkConf().setAppName("educationProcess")
    //加入解决序列化问题
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "5")
    val sc = new SparkContext(sparkConf)

    var ps: PreparedStatement = null
    val ES = new IndexArticle
    val simClass = new SimHashTest
    val sql_commerce: String = "INSERT INTO commerce (site_name,post_title,post_url,content_text,content_html,crawl_time,type,module,keywords,state) VALUES (?,?,?,?,?,?,?,?,?,?)"
    val sql_conference: String = "INSERT INTO conference (site_name,post_title,post_url,conference_address,conference_time,crawl_time,type,module) VALUES (?,?,?,?,?,?,?,?)"

    val jsonRDD = sc.textFile("data/simhashJson.json").repartition(1)

    jsonRDD.map(pair => replaceContext(pair)).foreachPartition(partion =>{

        val conn = ConnectionPool.getConnection.getOrElse(null)
        if(conn!=null){

          partion.foreach(jsonObj =>{

            if ((jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null && jsonObj.get("content_text") != "")||(jsonObj.get("type") == "conference" && jsonObj.get("post_title") != null && jsonObj.get("post_title") != "")){
              if (jsonObj.get("type") == "commerce"){
                simURL = simClass.checkSimilarArticle(jsonObj)
                if (simURL == null){
                  //存入MySQL
                  data2MySQL.toMySQL_commerce(conn,sql_commerce,jsonObj)
                  //存入ES
//                  ES.storageArticle(jsonObj)
                  println("commerce："+jsonObj.get("post_title"))
                }else{
                  println(simURL)
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

  }

  def replaceContext(str: String): JSONObject ={
    val json = new JSONObject(str)
    if (json.has("content_text")){
      val content_text = json.get("content_text").toString.replaceAll("(.*本文.*转载.*?[。]|更多专业报道.*|欢迎长按下方二维码.*?[。]|电商资讯第一入口问答｜.*|除非注明.*|问答｜.*|更多案例尽在.*|SocialBeta：每日.*|文中图片来自.*|题图.*)", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
      json.put("content_text",content_text)
    }
    json
  }
}
