package DB

import java.sql.{DriverManager, PreparedStatement, Connection}

import com.hankcs.hanlp.HanLP
import org.apache.log4j.{Level, Logger}
import org.json.JSONObject

/**
  * Created by cluster on 2017/6/16.
  */
object data2MySQL {
  Logger.getLogger("org").setLevel(Level.ERROR)
  var conn: Connection = null
  var ps: PreparedStatement = null
  val sql_commerce: String = "INSERT INTO commerce (site_name,post_title,post_url,content_text,content_html,crawl_time,type,module,keywords,abstract) VALUES (?,?,?,?,?,?,?,?,?,?)"
  val sql_conference: String = "INSERT INTO conference (site_name,post_title,post_url,conference_address,conference_time,crawl_time,type,module) VALUES (?,?,?,?,?,?,?,?)"
  def toMySQL_commerce(jsonObj: JSONObject): Unit ={
    if (jsonObj.getString("content_text") != null){
      conn = DriverManager.getConnection("jdbc:mysql://192.168.39.18:3306/datapark?useUnicode=true&characterEncoding=UTF-8", "root", "123456")
      ps = conn.prepareStatement(sql_commerce)
      ps.setString(1,jsonObj.get("site_name").toString)
      ps.setString(2, jsonObj.get("post_title").toString.replace(" ",""))
      ps.setString(3,jsonObj.get("post_url").toString)
      ps.setString(4,jsonObj.getString("content_text").replaceAll("[\\x{10000}-\\x{10FFFF}]", "").toString)
      ps.setString(5,jsonObj.get("content_html").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", ""))
      ps.setInt(6,jsonObj.getInt("crawl_time"))
      ps.setString(7,jsonObj.getString("type"))
      ps.setString(8,jsonObj.get("module").toString)
      ps.setString(9,HanLP.extractKeyword(jsonObj.getString("content_text"), 10).toString.replace("[","").replace("]",""))
      ps.setString(10,HanLP.extractSummary(jsonObj.getString("content_text"), 5).toString)
      ps.executeUpdate()
    }

  }
  def toMySQL_conference(jsonObj: JSONObject): Unit ={
    if (jsonObj.get("post_title") != null){
      conn = DriverManager.getConnection("jdbc:mysql://192.168.39.18:3306/datapark?useUnicode=true&characterEncoding=UTF-8", "root", "123456")
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
    }
  }
}
