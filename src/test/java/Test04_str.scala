/**
  * Created by cluster on 2017/6/20.
  */
object Test04_str {
  def main(args: Array[String]) {
    val str = "[街道, 纪念, 名字, 小镇, 活动, 注明, 营销, http, 链接, 标注]"
//  tolist
    val list = str.toList
//  replace
    val reStr = str.replaceAll("[\\[\\]]","").split(",")

    println(reStr)
  }
}
