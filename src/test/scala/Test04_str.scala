/**
  * Created by cluster on 2017/6/20.
  */
object Test04_str {

  def judgeLenght(str: String): Boolean ={
    val  istr = str.length()
    println("str的长度是：" + istr)
    val  str1 = str.replaceAll("[!?。！？;；]", "")
    val istr1 = str1.length()
    System.out.println("str1的长度是：" + istr1)
    System.out.println("标点符号的个数是：" + (istr - istr1))
    val result = istr - istr1
    if (result <= 3) false else true
  }



  def main(args: Array[String]) {
//    val str = "[街道, 纪念, 名字, 小镇, 活动, 注明, 营销, http, 链接, 标注]"
//    val str1 = "[爱打架那家发生的你发]  aa"
//    val str2 = "广州保利世贸展览馆 广州市海珠区新港东路1000号 乘车路线 公交18路车"
//
////  tolist
//    val list = str.toList
////  replace
//    val reStr = str.replaceAll("[\\[\\]]","").split(",")
//    val reStr1 = str1.replaceAll("[\\[\\]]\\s*","")// \s表示匹配空格， \s*表示匹配多个空格
//    val reStr2 = str2.replaceAll("乘车路线.*","")// 匹配指定的字符串并且替换




    val  str = "来自英国大众汽车为旗下Gofl GTE车型推出的宣传广告，" +
      "广告一开始是一连串好莱坞电影中出现的各种坏蛋，!这些坏蛋的共同特点是用一个按钮来干坏事最后影片切换到了大众汽车上，" +
      "这款车配备了一键切换汽油动力和电力动力的功能（简单讲就是按一键可以让汽油和电力发挥最大功效）最后广告语：更有责任的使用能源；" +
      "大众Golf Gte是一辆混合动力，相对纯汽油能源车，!混合动力更节能环保，所以就和影片前面坏蛋们拿能源来做坏事形成了对比。" +
      "标签: 汽车视频广告 大众汽车广告 "

    val result = judgeLenght(str)

    println(result)

  }
}
