import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by cluster on 2017/6/11.
  */
object streamingTest {
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
    val topics = Set("test01")
    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"

    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder","auto.offset.reset" -> "smallest")
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
    /**
      * 方法二
      */
    kafkaStream.foreachRDD(rdd => {
      rdd.foreach( msg=>{
        println(msg._2)
      })
    })
    ssc.start()
    //等待
    ssc.awaitTermination()
    //ssc.stop()
  }
}
