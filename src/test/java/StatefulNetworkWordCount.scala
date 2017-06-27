package org.apache.spark.examples.streaming

import _root_.kafka.serializer.StringDecoder
import com.hankcs.hanlp.HanLP
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.json.JSONObject

object StatefulNetworkWordCount {
  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println("Usage: StatefulNetworkWordCount <hostname> <port>")
      System.exit(1)
    }

//    StreamingExamples.setStreamingLogLevels()

    val sparkConf = new SparkConf().setAppName("StatefulNetworkWordCount")
    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sparkConf, Seconds(1))
    ssc.checkpoint("checkpoint")

    // Initial state RDD for mapWithState operation
    val brokers = "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092"
    val topics = "test04"
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers,"serializer.class" -> "kafka .serializer.StringEncoder", "auto.offset.reset" -> "smallest")
    val kafkaDStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    val lines = kafkaDStream.map(line =>new JSONObject(line._2)).filter(jsonObj => (jsonObj.get("type") == "commerce" && jsonObj.get("content_text") != null && jsonObj.get("content_text") != ""))
    val keywordStr = lines.map(jsonObj => HanLP.extractKeyword(jsonObj.getString("content_text"), 10).toString.replaceAll("[\\[\\]]",""))
    val words = keywordStr.flatMap(_.split(","))
    val wordDstream = words.map(x => (x, 1))

    // Update the cumulative count using mapWithState
    // This will give a DStream made of state (which is the cumulative count of the words)
    val mappingFunc = (word: String, one: Option[Int], state: State[Int]) => {
      val sum = one.getOrElse(0) + state.getOption.getOrElse(0)
      val output = (word, sum)
      state.update(sum)
      output
    }

    val stateDstream = wordDstream.mapWithState(
      StateSpec.function(mappingFunc))
    stateDstream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}