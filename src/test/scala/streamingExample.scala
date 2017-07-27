import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json.JSONObject

/**
  * Created by cluster on 2017/6/11.
  */
object streamingExample {
  //Logger.getLogger("org").setLevel(Level.ERROR)
  //  logInfo("test log")
  def main(args: Array[String]) {
    /**
      * Director
      */
    val conf = new SparkConf().setMaster("local[2]").setAppName("test")
    //    val ssc = new StreamingContext(conf, Durations.seconds(5))
    val ssc = new StreamingContext(conf,Seconds(2))
    //    val topics = Set("skurawdataonline_new01")
    val topics = Set("test04")
    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"

    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder","auto.offset.reset" -> "smallest")
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
    /**
      * 方法二
      */
    kafkaStream.map(pair => replaceContext(pair._2)).foreachRDD(rdd => {
      rdd.foreach( msg=>{
        println(msg)
      })
    })





    ssc.start()
    //等待
    ssc.awaitTermination()
    //ssc.stop()
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
