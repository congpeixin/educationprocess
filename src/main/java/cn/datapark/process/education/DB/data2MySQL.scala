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
        ps.setString(4,jsonObj.getString("content_text").replaceAll("[\\x{10000}-\\x{10FFFF}]", "").toString)
        ps.setString(5,jsonObj.get("content_html").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", ""))
        ps.setInt(6,jsonObj.getInt("crawl_time"))
        ps.setString(7,jsonObj.getString("type"))
        ps.setString(8,jsonObj.get("module").toString)
        ps.setString(9,HanLP.extractKeyword(jsonObj.get("post_title").toString.replace(" ","")+jsonObj.getString("content_text"), 4).toString.replace("[","").replace("]",""))
        ps.setString(10, summary.summarize(jsonObj.getString("content_text").replaceFirst(".*本文.*转载.*?[。]","").replaceFirst("除非注明.*","").replaceFirst("更多专业报道.*", ""), "MMR"))
        ps.setInt(11,0)
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


    val str = "本文授权转载ACGdoge。伴随着智能手机的发展，国内的地铁、" +
      "天桥上出现了很多专门回收手机以及给手机贴膜的小摊位，\"贴膜大王\"、“贴膜女神”之类的宣传看板比比皆是，" +
      "“贴膜”也成为了国内智能手机的一种现象。而在日本的秋叶原最近出现了一个修手机的女子偶像组合，" +
      "这个组合的偶像本职工作就是在手机店修手机，然后还要进行偶像 Live 唱歌等活动。以后这偶像是不是要举办一场修手机握手会，" +
      "修一台手机握手一次。这个修手机偶像组合是日本的电器事业公司 GEO 搞出来的叫做 GEO 偶像部，这个组合一共有 5 个妹子，" +
      "每个妹子其实都是 GEO 手机修理店的员工，平时在店里修手机但同时也要进行和偶像一样举办 Live 活动，" +
      "还要在 GEO 旗下的手机店进行宣传活动，比如组合中的某位偶像去某个特定的店铺里修手机，" +
      "差不多就和签售会一样。GEO 的店里是专修 iPhone 手机，在昨天组合中的一位偶像还专门现场演示了自己给 iPhone 换屏的技术。" +
      "当然这个偶像的本质是给 GEO 进行换屏又好又便宜的宣传，而且偶像亲自给你修的手机想想就幸福，这个偶像组合的实用性还是很高的。" +
      "未来这个偶像组合的成员还将在 GEO 全国的手机店铺中巡回修手机，当年秋元康主打的是“面对面的偶像”，" +
      "现在 GEO 变成了“面对面修手机的偶像”，请问什么时候有快递偶像、外卖偶像？以下附赠偶像修屏全过程： " +
      "更多专业报道，请点击下载“界面新闻”APP0,公益广告 广告 除非注明，否则本站文章均为原创翻译，转载请注明出处，并标注本文链接：http://iwebad.com/video/2993.html"

    val result = summary.summarize(str.replaceFirst(".*本文.*转载.*?[。]","").replaceFirst("除非注明.*",""), "MMR")

    println(result.getClass.getName)
  }


}
