package cn.datapark.process.education.DB

import java.sql.{DriverManager, PreparedStatement, Connection}
import java.util.Arrays

import HanLPProcess.HanLP
import cn.datapark.process.education.Summary
import cn.datapark.process.education.Summary.NewsSummary

import org.apache.log4j.{Level, Logger}

//import org.apache.log4j.spi.LoggerFactory
//import org.apache.log4j.{Level, Logger}
import org.json.JSONObject

import org.slf4j.LoggerFactory

/**
  * Created by cluster on 2017/6/16.
  */
object data2MySQL {

  Logger.getLogger("org").setLevel(Level.ERROR)
  val logger = LoggerFactory.getLogger(this.getClass)
  val summary = new NewsSummary

  def toMySQL_commerce(conn:Connection,sql:String,jsonObj:JSONObject): Unit ={
    if (jsonObj.getString("content_text") != null){
      try{
        val ps : PreparedStatement = conn.prepareStatement(sql)
        ps.setString(1,jsonObj.get("site_name").toString)
        ps.setString(2, jsonObj.get("post_title").toString.replace(" ",""))
        ps.setString(3,jsonObj.get("post_url").toString)
        ps.setString(4,jsonObj.getString("content_text"))
        ps.setString(5,jsonObj.get("content_html").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", ""))
        ps.setInt(6,jsonObj.getInt("crawl_time"))
        ps.setString(7,jsonObj.getString("type"))
        ps.setString(8,jsonObj.get("module").toString)
//        ps.setString(9,HanLP.extractKeyword(jsonObj.get("post_title").toString.replace(" ","")+jsonObj.getString("content_text"), 4).toString.replace("[","").replace("]",""))
//        ps.setString(10, summary.summarize(jsonObj.getString("content_text").replaceFirst(".*本文.*转载.*?[。]","").replaceFirst("除非注明.*","").replaceFirst("更多专业报道.*", ""), "MMR"))
        ps.setString(9,HanLP.extractKeyword(jsonObj.get("post_title").toString.replace(" ","")+jsonObj.getString("content_text"), 4).toString.replace("[","").replace("]",""))
        ps.setInt(10,0)
        ps.executeUpdate()
      }catch{
        case exception:Exception=>
          logger.warn("Error in execution of query"+exception.printStackTrace())
      }
    }
  }
  def toMySQL_conference(conn:Connection,sql:String,jsonObj:JSONObject): Unit ={
    if (jsonObj.get("post_title") != null){
      try{
        val ps : PreparedStatement = conn.prepareStatement(sql)
        ps.setString(1,jsonObj.get("site_name").toString)
        ps.setString(2, jsonObj.get("post_title").toString.replace(" ","").replaceAll ("\\\\r\\\\n", ""))
        ps.setString(3,jsonObj.get("post_url").toString)
        ps.setString(4,jsonObj.getString("conference_address").replaceAll("[\\x{10000}-\\x{10FFFF}]", "").replaceAll("乘车路线.*",""))//conference_address
        ps.setString(5,jsonObj.getString("conference_time"))//conference_time
        ps.setInt(6,jsonObj.getInt("crawl_time"))
        ps.setString(7,jsonObj.getString("type"))
        ps.setString(8,jsonObj.get("module").toString)
        ps.executeUpdate()
      }catch{
        case exception:Exception=>
          logger.warn("Error in execution of query"+exception.printStackTrace())
      }
    }
  }

  def main(args: Array[String]) {
    val title = "因为没预算金山毒霸做了一个很耿直的广告"
    val content = "ApplePay联合孙宏斌王健林的新乐视"
    val str = title + content
    val keyword = HanLP.extractKeyword(str,4)
    println(keyword)
  }


}
