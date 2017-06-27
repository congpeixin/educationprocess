import java.sql.{PreparedStatement, Connection, DriverManager}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.sql.SparkSession

/**
  * Created by cluster on 2017/6/21.
  */
object GetKW {
  Logger.getLogger("org").setLevel(Level.ERROR)
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder()
      .appName("DatasetOps")
      .master("local")
      .config("spark.sql.warehouse.dir", "/spark-warehouse")
      .getOrCreate()

    val url="jdbc:mysql://192.168.39.18:3306/datapark?useUnicode=true&characterEncoding=UTF-8"
    val uname = "root"
    val password = "123456"
    val prop = new java.util.Properties
    prop.setProperty("user",uname)
    prop.setProperty("password",password)

//    #指定读取条件,这里 Array("gender=1") 是where过滤条件
    val keyWordRDD = spark.sqlContext.read.jdbc(url,"commerce",prop).select("keywords")
    import spark.implicits._
    val keyWordStrRDD = keyWordRDD.map(line => line.toString.replaceAll("[\\[\\]]",""))
//    val keyWordPairRDD = keyWordStrRDD.flatMap(_.split(",")).map(x => (x, 1))
//   1. val keyWordPairRDD = keyWordStrRDD.flatMap(_.split(",")).groupByKey(_.toLowerCase()).count().toJavaRDD
    val keyWordPairRDD = keyWordStrRDD.flatMap(_.split(",")).map(word => word.replace(" ","")).groupByKey(_.toLowerCase()).count()
    var conn: Connection = null
    var ps: PreparedStatement = null
    val sql: String = "INSERT INTO keyword (word,num) VALUES (?,?)"

    //这个方法不能将相同的合并
    keyWordPairRDD.foreachPartition(iter =>{
      conn = DriverManager.getConnection(url, uname, password)
      iter.foreach(pair  => {
        ps = conn.prepareStatement(sql)
        ps.setString(1,pair._1)
        ps.setLong(2,pair._2)
        ps.executeUpdate()
      })
    })

//写入外部文件
//keyWordPairRDD.repartition(1).saveAsTextFile("data/test/kw.txt")



  }
}
