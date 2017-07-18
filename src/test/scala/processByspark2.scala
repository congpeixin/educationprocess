import org.apache.spark.sql.SparkSession

/**
  * Created by cluster on 2017/7/12.
  */
object processByspark2 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .appName("Spark structured streaming Kafka example")
      .master("local[2]")
      .getOrCreate()

    val inputstream = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "process2.pd.dp:9092,process3.pd.dp:9092,process5.pd.dp:9092")
      .option("subscribe", "test04")
      .load()
    import spark.implicits._
    val query = inputstream.select($"key", $"value")
      .as[(String, String)].map(kv => kv._1 + " " + kv._2).as[String]
      .writeStream
      .outputMode("append")
      .format("console")
      .start()

    query.awaitTermination()
  }
}
