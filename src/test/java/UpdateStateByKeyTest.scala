import com.hankcs.hanlp.HanLP
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{StreamingContext, Seconds}
import org.apache.spark.{SparkContext, SparkConf}

import org.json.JSONObject

object UpdateStateByKeyTest {

  def main (args: Array[String]) {

    def functionToCreateContext(): StreamingContext = {
      //创建streamingContext
      val conf = new SparkConf().setAppName("test").setMaster("local[2]")
      val ssc = new StreamingContext(conf, Seconds(5))

      //将数据进行保存（这里作为演示，生产中保存在hdfs）
      ssc.checkpoint("checkPoint")
      val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
      val topics = "test04"
      val topicsSet = topics.split(",").toSet
      val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"serializer.class" -> "kafka .serializer.StringEncoder", "auto.offset.reset" -> "smallest")
      val user_payment = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
      val lines = user_payment.map(line =>new JSONObject(line._2)).filter(jsonObj => (jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null && jsonObj.get("content_text") != ""))
      val keywordStr = lines.map(jsonObj => HanLP.extractKeyword(jsonObj.getString("content_text"), 10).toString.replaceAll("[\\[\\]]",""))
      //从kafka读入数据并且将json串进行解析
      val words = keywordStr.flatMap(_.split(",")).map(word => (word,1))

      //对一分钟的数据进行计算
      val paymentSum = words.map(jsonLine =>{
        val user = jsonLine._1
        val payment = jsonLine._2
        (user,payment)
      }).reduceByKey(_+_)

      //输出每分钟的计算结果
      paymentSum.print()

      //将以前的数据和最新一分钟的数据进行求和
      val addFunction = (currValues : Seq[Int],preVauleState : Option[Int]) => {
        val currentSum = currValues.sum
        val previousSum = preVauleState.getOrElse(0)
        Some(currentSum + previousSum)
      }

      val totalPayment = paymentSum.updateStateByKey[Int](addFunction)

      //输出总计的结果
      totalPayment.print()

      ssc
    }

    //如果"checkPoint"中存在以前的记录，则重启streamingContext,读取以前保存的数据，否则创建新的StreamingContext
    val context = StreamingContext.getOrCreate("checkPoint", functionToCreateContext _)

    context.start()
    context.awaitTermination()
  }
}