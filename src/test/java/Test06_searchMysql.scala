import org.apache.log4j.{Level, Logger}
import java.sql.{PreparedStatement, Connection, DriverManager}
/**
  * Created by cluster on 2017/6/26.
  */
object Test06_searchMysql {
  Logger.getLogger("org").setLevel(Level.ERROR)
  def main(args: Array[String]) {
    // connect to the database named "mysql" on the localhost
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://192.168.39.18:3306/test?useUnicode=true&characterEncoding=UTF-8"
    val username = "root"
    val password = "123456"

    var conn:Connection = null
    var ps: PreparedStatement = null
    try {

      Class.forName(driver)
      conn = DriverManager.getConnection(url, username, password)
      val conditions: String = "2017中国（上海）国际广告展"
      ps = conn.prepareStatement("select * from conference where post_title = '"+conditions+"'")
//      val statement = connection.createStatement()
      val resultSet = ps.executeQuery()
      if (resultSet.next() == false) println("此数据不存在") else println("此数据存在")

    } catch {
      case e => e.printStackTrace
      //case _: Throwable => println("ERROR")
    }
    conn.close()
  }
}
