import HanLP.HanLP

/**
  * Created by cluster on 2017/7/14.
  */
object Test07_summary {
  def main(args: Array[String]) {

    val content = "不要以为“国名老公”王思聪说要推出的能量饮料只是个玩笑。现在这个叫“爱洛”的能量饮料已经在上海的全家便利店销售，" +
      "同时京东上也可以搜索到这款商品，售价为9.9元。王思聪对“爱洛”饮料是真的认真以待，最近他还为这款产品推出了一部以“丧尸”为题材的广告片。" +
      "想要知道为什么拍这些看起来有点莫名其妙的桥段，就需要了解爱洛饮料的定位。其实别看王思聪在微博上经常成为热点，似乎做事不太正经，" +
      "但是对于营销推广似乎他也有所洞见。他把这款饮料的消费场景设定为在电子竞技、运动健身、工作加班、夜店派对等活动中，" +
      "同时目标消费者为20至30岁的年轻人。所以这个广告的故事就发生在加班回家和在网吧打游戏两个场景中。故事的主线其实非常简单，" +
      "大家以为主角变成了丧尸，其实只不过是加班或者打游戏太久累了于是很丧。丧尸是年轻人喜欢的影视题材，美剧《行尸走肉》也拍了好多季，" +
      "但是广告中非常具有“王思聪嘲讽特色”，譬如一个穿着特警制服的人说“丧什么丧，中国不应许有丧尸，他只是加班累了”，价值观颇为端正；同时，" +
      "也会让人联想到去年一个“影视作品中的动物建国之后不允许成精”的规定。这则广告的投放渠道为B站，从满屏的弹幕中投放效果还算不错。" +
      "而且王思聪还擅长在微博上制造话题，譬如他把饮料送给电竞圈的一些网红，让他们也在微博上为自己“打广告”，要知道电竞圈粉丝效益也不熟娱乐圈。" +
      "电竞网红晒出王思聪送的饮料。至于饮料本身，似乎没有太多人深究，但是对于年轻人的洞察王思聪确实比一般品牌更就熟驾轻，他了解这群人的嗨点在哪。" +
      "原因很简单，他身于其中，而品牌通常从代理公司或咨询公司的报告中了解年轻人罢了。" +
      "————————————————欢迎长按下方二维码，关注界面营销频道微信公众号“看你卖”（kannimai）。" +
      "更多专业报道，请点击下载“界面新闻”APP0牙韩翔界面编辑关注作者取消关注私信"

    val summary_1 = HanLP.extractSummary(content,3)
    val summary_2 = HanLP.extractSummary(content,5)
    val summary_3 = HanLP.getSummary(content,10)

    println("关键句size为3 :"+summary_1)
    println("关键句size为5 :"+summary_2)
    println("摘要长度为10 :"+summary_3)
  }
}
