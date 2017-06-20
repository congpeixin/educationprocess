import java.sql.{DriverManager, PreparedStatement, Connection}


import DB.data2MySQL
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
object Test03 {
  Logger.getLogger("org").setLevel(Level.ERROR)
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
    //    val dataRDD = kafkaDStream.map(json => {
    //      val jsonObj = json._2 })

    kafkaDStream.foreachRDD(rdd =>{
      rdd.foreachPartition(partion =>{
        partion.foreach(json =>{
          val jsonObj = new JSONObject(json._2)
          if ((jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null)||(jsonObj.get("type") == "conference" && jsonObj.get("post_title") != null)){

            //去重simhash
            //与已经抓取的文章对比,判断是否有相似文章,as为当前抓取的文章，simURL为库中存在相似文章的url
            //simURL == null 文章不重复

              if (jsonObj.get("type") == "commerce"){
//                data2MySQL.toMySQL_commerce(jsonObj)
                println("---------------------------------------------------"+jsonObj)
              }
              else {
//                data2MySQL.toMySQL_conference(jsonObj)
                println("*****************************************************"+jsonObj)
              }

          }

        })
      })
    })
    ssc.start()
    ssc.awaitTermination()

  }
}
