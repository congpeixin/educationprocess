import org.json.JSONObject

import scala.util.matching.Regex

/**
  * Created by cluster on 2017/7/26.
  */
object Text11_replace {

  def replaceContext(str: String): JSONObject ={
    val json = new JSONObject(str)
    if (json.has("content_text")){
      val content_text = json.get("content_text").toString.replaceAll("(.*本文.*转载.*?[。]|更多专业报道.*|欢迎长按下方二维码.*?[。]|电商资讯第一入口问答｜.*|除非注明.*|问答｜.*|更多案例尽在.*|SocialBeta：每日.*|文中图片来自.*|题图.*)", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
      json.put("content_text",content_text)
    }
    json
  }



  def main(args: Array[String]) {
    val str = "本文授权转载ACGdoge。伴随着智能手机的发展，国内的地铁、" +
      "天桥上出现了很多专门回收手机以及给手机贴膜的小摊位，\"贴膜大王\"、“贴膜女神”之类的宣传看板比比皆是，" +
      "“贴膜”也成为了国内智能手机的一种现象。而在日本的秋叶原最近出现了一个修手机的女子偶像组合，" +
      "这个组合的偶像本职工作就是在手机店修手机，然后还要进行偶像 Live 唱歌等活动。以后这偶像是不是要举办一场修手机握手会，" +
      "修一台手机握手一次。这个修手机偶像组合是日本的电器事业公司 GEO 搞出来的叫做 GEO 偶像部，这个组合一共有 5 个妹子，" +
      "每个妹子其实都是 GEO 手机修理店的员工，平时在店里修手机但同时也要进行和偶像一样举办 Live 活动，" +
      "还要在 GEO 旗下的手机店进行宣传活动，比如组合中的某位偶像去某个特定的店铺里修手机，" +
      "差不多就和签售会一样。GEO 的店里是专修 iPhone 手机，在昨天组合中的一位偶像还专门现场演示了自己给 iPhone 换屏的技术。" +
      "当然这个偶像的本质是给 GEO 进行换屏又好又便宜的宣传，而且偶像亲自给你修的手机想想就幸福，这个偶像组合的实用性还是很高的。" +
      "未来这个偶像组合的成员还将在 GEO 全国的手机店铺中巡回修手机，当年秋元康主打的是“面对面的偶像”，" +
      "现在 GEO 变成了“面对面修手机的偶像”，请问什么时候有快递偶像、外卖偶像？以下附赠偶像修屏全过程： " +
      "更多专业报道，请点击下载“界面新闻”APP0,公益广告 广告 除非注明，否则本站文章均为原创翻译，转载请注明出处，并标注本文链接：http://iwebad.com/video/2993.html"

    val str_1 = "罹患老人痴呆症会逐渐丧失记忆，广告中的奶奶在回忆二战期间哥哥的经历，士兵闯入了敌军区，补给品已经消耗完，而且被打中...在这里老人就记不起来她哥哥到底是受伤了还是没有...最后广告语“老人痴呆症夺走的不仅仅是记忆”，意思是说，她可能永远也记不起来她哥哥了，而这对一个人来说，其实不仅仅是记忆，还有亲情，思念等等标签:  公益广告 广告 除非注明，否则本站文章均为原创翻译，转载请注明出处，并标注本文链接：http://iwebad.com/video/2993.html"



    val str_2 = "【眼科专家建议】爱眼护眼，从点击图片全屏阅读开始全屏欣赏，效果那是相当不错了其实我就是想发个广告告诉你们乘坐顺风车和开顺风车真，的，很，不，错 动动你的小手儿，快点开视频"
    //    if(str_2.contains("。")) println("包含句号") else println("不包含句号")

    val str_3 = "小孩被遗忘在车里致死的案件时有发生，虽然现在已经有相应的App可以提醒父母们，但在多米尼加共和国依然有85%的人还没有使用手机上网（资费问题吧）所以多米尼加雀巢NIDO全脂奶粉推出了一个贴心的“脐带”，绑住父母和孩子，这样父母在下车的时候就比较不会忘记车内还有小孩后面就是在销售产品的时候赠送这个安全带，以及线下推广安全知识等等标签:食品饮料营销 dominicanrepublic广告 雀巢广告 品牌营销案例 麦肯光明广告公司除非注明，否则本站文章均为原创翻译，转载请注明出处，并标注本文链接：http://iwebad.com/case/6691.html"
    val result = str_3.replaceFirst(".*本文.*转载.*?[。]","").replaceFirst("除非注明.*","").replaceFirst("更多专业报道.*", "")

    val str_4 = "图片来源：爱尔兰旅游局随着7月17日第一集的播出，《权力的游戏》第七季再次让全世界的美剧爱好者兴奋不已。" +
      "首集开播就便超越了前六季的首播收视率，创造最高实时收视人数，打破了HBO收视纪录。" +
      "而目前豆瓣9.8分也说明粉丝对这一系列有多么狂热。广告商们又怎么可能放过借着《权力的游戏》" +
      "蹭热度的机会？最近，爱尔兰旅游局和广告公司伦敦阳狮一起，把权力的游戏前六季的故事绣上了一条挂毯。" +
      "六季的故事当然不短，于是这条挂毯长达250英尺（约76米），而挂毯上则绣上了权力的游戏里的众多重要场景。" +
      "用挂毯叙述历史或神话，是一种在全世界众多民族中通行的古老艺术形式。这条《权力的游戏》挂毯的灵感来源于11世纪的" +
      "《贝叶挂毯》。这条挂毯原长70米，现存62米，用70多个场面记录了黑斯廷战役，被认为可能是人类历史上最长的连环画。" +
      "贝叶挂毯而《权力的游戏》挂毯的长度比《贝叶挂毯》更长，更不用说这部剧的故事还没完结。此外在工艺上区别也非常大，" +
      "《贝叶挂毯》本身完全是刺绣制作，因此很难称得上真正意义上的挂毯，而爱尔兰旅游局制作的这条则是在一条已经手工编织完成的挂毯上进行刺绣。" +
      "目前，这条挂毯正在北爱尔兰首府贝尔法斯特的阿尔斯特博物馆展出。图片来源：爱尔兰旅游局虽然《权力的游戏》故事发生在一个虚构的大陆，" +
      "不过剧集中许多场景都来自爱尔兰，因此这部人气剧集也成为了爱尔兰旅游局吸引游客的一个噱头。去年，爱尔兰旅游局就推出了“权力之门”的营销战役，" +
      "用木雕制作了10个门，每个门上都雕刻着一个《权力的游戏》第六季的一集。这些门现在悬挂在北爱尔兰的酒吧和其他场地，供粉丝们上门打卡朝圣。" +
      "权力之门据报道，“权力之门”视频被观看了1700万次，而整个项目覆盖达到1.26亿人，在上个月这一项目也拿下了戛纳国际创意节户外类金奖。" +
      "《权力的游戏》挂毯则是“权力之门”的后续营销活动。其实关于《权力的游戏》第七季的营销活动还不止这么一个，3月份，片方就玩起了直播，" +
      "他们弄来了一块巨大的冰块，用融化冰块的创意，来宣布第七季的开播时间。今年6月，Twitter也借势《权力的游戏》，推出了一套emoji，" +
      "把剧中角色和各种元素都做成了呆萌的emoji。HBO直播融化冰块。 图片来源：Facebook: Game of Thrones《权力的游戏》" +
      "emoji相对于国内铺天盖地的电视剧广告植入，《权力的游戏》所属的HBO依靠订阅而非广告赢利。不过对于品牌们来说，" +
      "没有植入也就意味着不能挥舞着支票簿肆无忌惮地广告了，与众不同的创意才能让更多受众注意到品牌。欢迎长按下方二维码，关注界面营销频道微信公众号“看你卖”（kannimai）。更多专业报道，请点击下载“界面新闻”APP0黄逸鹏界面运营关注作者已关注私信,"

    //    replace多个条件

    val str_5 = "中国有嘻哈电商资讯第一入口问答｜《我的前半生》内容营销大揭秘！扫码参加↑↑↑【版权提示】亿邦动力网倡导尊重与保护知识产权。如发现本站文章存在版权问题，烦请提供版权疑问、身份证明、版权证明、联系方式等发邮件至run@ebrun.com，我们将及时沟通与处理。"

    val result1 = str_5.replaceAll("(.*本文.*转载.*?[。]|更多专业报道.*|欢迎长按下方二维码.*?[。]|电商资讯第一入口问答｜.*|除非注明.*|问答｜.*|更多案例尽在.*|SocialBeta：每日.*|文中图片来自.*|题图.*)", "").replaceAll("(\\\\\\\\u.{4})","")
    //    println(result1)


    val js = "{\"site_name\": \"socialbeta\", \"post_title\": \"【案例】\u200B麦当劳是如何把「在大暑吃大薯」变成一个「传统」的？\", \"module\": \"brand\", \"post_url\": \"http://socialbeta.com/t/case-mcdonald-2017-bigfriesday-20170725\", \"content_text\": \"传统节日对年轻人来说有什么意义？大概除了会放假的节日会赢得年轻人的欢迎以外，那些没有放假功能的节日恐怕早已淡出了年轻人的视野。但，也有例外。「大暑」这天，我们看到很多年轻人都在微博和朋友圈上，晒出了自己在麦当劳吃薯条度过大暑、或者用薯条创作的有趣图案，甚至主动 @麦当劳 来表达自己的兴奋。不过，在他们的表述中，这个中国传统节气被称为「大薯日」。这已经是麦当劳第三年与粉丝共庆「大薯日」了，不搞点大事怎么对得起粉丝呢？今年的麦当劳在上海顶级酒店的露天泳池举办了一个「大薯日终极消暑泳池趴」，邀请全国潮流达人及粉丝一同吃薯消暑。在这个神秘派对中，DJ、Rapper 与麦当劳叔叔一同带来最 hot 的现场，薯条游泳圈、抓娃娃机、荧光贴纸、手环等等夏日元素也将现场打造为拍照圣地，而最令人期待的，当然是麦当劳发布的独家薯条元素人字拖、雨伞、薯条扇、懒人沙发等消暑周边啊！令人开心又难过的是，这些可爱又实用的周边都是不要钱的，因为麦当劳并没有出售，而你想要获得，只能靠上天给的运气或者你的才艺啦~当然，麦当劳也为那些无法到场的朋友们准备了「大暑免费续大薯」的福利，在 7 月 21 日至 7 月 25 日，每天日头最猛的下午 2 至 6 点，麦当劳粉丝和顾客们来到麦当劳购买大薯，就可以免费续大薯两包。而那些想要周边的粉丝，只要拿出摄影才艺参加「第二届薯条创意摄影大赛」，参与微博互动晒出脑洞作品，就有机会获得全套大薯日限定周边！根据麦当劳向 SocialBeta 最新透露的数据，到目前为止，微博上#大暑免费续大薯# 话题的阅读量已有三千多万，赢取薯条懒人沙发单条微博的转发数超过 3 万。官方微信的阅读数也创下了历史高度，仅文章分享数就已接近 30 万。自 2015 年起，麦当劳把「节日」的概念与意义，沿袭到品牌与粉丝的互动中，独创了「麦麦粉丝节」。除了「大薯日」， 「麦麦粉丝节」还包括了 3 月 14 的「派 day」，粉丝与顾客在这天会用麦当劳的经典小食派来庆祝圆周率的诞生。麦当劳通过打造一个属于自己的粉丝节，将爱好者聚拢形成品牌社区；再用年轻人喜欢的互动方式与其沟通，形成了一种独特的品牌文化——大暑这天要去麦当劳吃薯条，3 月 14 日麦当劳的派也不可少。对于麦当劳而言，经典食品一向是最好的与粉丝互动的「自有媒体」，通过经典产品，与线下各地麦当劳餐厅内的多场派对狂欢，构建起一个围绕品牌的粉丝圈层并且形成紧密的关系与黏性。小小一个派或者一包薯条，成为了麦当劳用来与年轻消费者沟通的「圈粉」利器。你看，「大薯日」并不只是免费续单这么简单，它同样折射出大品牌在营销策略上的积累与创新。如果电商造节只是一种促销手段，那么麦当劳则希望你真正乐于其中，好在一起。\", \"crawl_time\": 1501137352, \"_id\": \"http://socialbeta.com/t/case-mcdonald-2017-bigfriesday-20170725\", \"type\": \"commerce\", \"content_html\": \"<div class=\\\"content\\\"><p>传统节日对年轻人来说有什么意义？大概除了会放假的节日会赢得年轻人的欢迎以外，那些没有放假功能的节日恐怕早已淡出了年轻人的视野。<br></p><p>但，也有例外。「大暑」这天，我们看到很多年轻人都在微博和朋友圈上，晒出了自己在麦当劳吃薯条度过大暑、或者用薯条创作的有趣图案，甚至主动<a href=\\\"http://weibo.com/mcdonaldsworlds\\\" target=\\\"_self\\\"> @麦当劳 </a>来表达自己的兴奋。不过，在他们的表述中，这个中国传统节气被称为「大薯日」。</p><p style=\\\"text-align: center;\\\"><img src=\\\"http://img.socialbeta.com/upload/5-1500974800.png\\\"></p><p style=\\\"text-align: center;\\\"><br></p><p>这已经是麦当劳第三年与粉丝共庆「大薯日」了，不搞点大事怎么对得起粉丝呢？今年的麦当劳在上海顶级酒店的露天泳池举办了一个「大薯日终极消暑泳池趴」，邀请全国潮流达人及粉丝一同吃薯消暑。</p><p style=\\\"text-align: center;\\\"><img src=\\\"http://img.socialbeta.com/upload/1561-1500971950.jpg\\\" style=\\\"text-align: center; white-space: normal;\\\"></p><p>在这个神秘派对中，DJ、Rapper 与麦当劳叔叔一同带来最 hot 的现场，薯条游泳圈、抓娃娃机、荧光贴纸、手环等等夏日元素也将现场打造为拍照圣地，而最令人期待的，当然是麦当劳发布的独家薯条元素人字拖、雨伞、薯条扇、懒人沙发等消暑周边啊！</p><p style=\\\"text-align: center;\\\"><img src=\\\"http://img.socialbeta.com/upload/1561-1500971948.jpg\\\"></p><p style=\\\"text-align: center;\\\"><img src=\\\"http://img.socialbeta.com/upload/1561-1500971946.jpg\\\"></p><p style=\\\"text-align: justify;\\\">令人开心又难过的是，这些可爱又实用的周边都是不要钱的，因为麦当劳并没有出售，而你想要获得，只能靠上天给的运气或者你的才艺啦~</p><p style=\\\"text-align: center;\\\"><img src=\\\"http://wx2.sinaimg.cn/mw1024/66e6e0f8gy1fhw8fie294g20m80et7wh.gif\\\"></p><p>当然，麦当劳也为那些无法到场的朋友们准备了「大暑免费续大薯」的福利，在 7 月 21 日至 7 月 25 日，每天日头最猛的下午 2 至 6 点，麦当劳粉丝和顾客们来到麦当劳购买大薯，就可以免费续大薯两包。而那些想要周边的粉丝，只要拿出摄影才艺参加「第二届薯条创意摄影大赛」，参与微博互动晒出脑洞作品，就有机会获得全套大薯日限定周边！</p><p>根据麦当劳向 SocialBeta 最新透露的数据，<strong>到</strong><strong style=\\\"white-space: normal;\\\">目前为止，微博上#大暑免费续大薯# 话题的阅读量已有三千多万，赢取薯条懒人沙发单条微博的转发数超过 3 万。官方微信的阅读数也创下了历史高度，仅文章分享数就已接近 30 万。</strong></p><p style=\\\"text-align: center;\\\"><img src=\\\"http://img.socialbeta.com/upload/1561-1500971506.jpg\\\" style=\\\"white-space: normal;\\\"></p><p>自 2015 年起，麦当劳把「节日」的概念与意义，沿袭到品牌与粉丝的互动中，独创了「麦麦粉丝节」。除了「大薯日」， 「麦麦粉丝节」还包括了 3 月 14 的「派 day」，粉丝与顾客在这天会用麦当劳的经典小食派来庆祝圆周率的诞生。麦当劳通过打造一个属于自己的粉丝节，将爱好者聚拢形成品牌社区；再用年轻人喜欢的互动方式与其沟通，形成了一种独特的品牌文化——大暑这天要去麦当劳吃薯条，3 月 14 日麦当劳的派也不可少。</p><p style=\\\"text-align: center;\\\"><img src=\\\"http://img.socialbeta.com/upload/2131-1489982894.jpg\\\" width=\\\"320\\\" height=\\\"457\\\" border=\\\"0\\\" vspace=\\\"0\\\" title=\\\"\\\" alt=\\\"\\\" hiragino=\\\"\\\" sans=\\\"\\\" microsoft=\\\"\\\" font-size:=\\\"\\\" text-align:=\\\"\\\" white-space:=\\\"\\\" background-color:=\\\"\\\" style=\\\"border: none; vertical-align: middle; max-width: 100%; height: auto;\\\"><img src=\\\"http://img.socialbeta.com/upload/2131-1489982895.jpg\\\" width=\\\"320\\\" height=\\\"457\\\" border=\\\"0\\\" vspace=\\\"0\\\" title=\\\"\\\" alt=\\\"\\\" hiragino=\\\"\\\" sans=\\\"\\\" microsoft=\\\"\\\" font-size:=\\\"\\\" text-align:=\\\"\\\" white-space:=\\\"\\\" background-color:=\\\"\\\" style=\\\"border: none; vertical-align: middle; max-width: 100%; height: auto;\\\"></p><p>对于麦当劳而言，经典食品一向是最好的与粉丝互动的「自有媒体」，通过经典产品，与线下各地麦当劳餐厅内的多场派对狂欢，构建起一个围绕品牌的粉丝圈层并且形成紧密的关系与黏性。小小一个派或者一包薯条，成为了麦当劳用来与年轻消费者沟通的「圈粉」利器。</p><p>你看，「大薯日」并不只是免费续单这么简单，它同样折射出大品牌在营销策略上的积累与创新。如果电商造节只是一种促销手段，那么麦当劳则希望你真正乐于其中，好在一起。</p></div>\"}"
    val js1 = "{\"site_name\": \"iwebad\", \"post_title\": \"英国BBC电视台新纪录片 使用人工智能机器人代言广告\", \"module\": \"brand\", \"post_url\": \"http://iwebad.com/video/2999.html\", \"content_text\": \"随着人工智能成为全球科技界的热潮，英国BBC电视台为此拍摄了一个纪录片《Being human》，为宣传该纪录片，BBC请来了全球目前最顶尖的仿人智能机器人-索菲亚代言，这个机器人来自汉森机器人公司，代工制作据说是来自香港(深圳)随着摄影机在她的周围旋转，她开始述说，“成为人类想必是非常棒的事情，你可以在日出时看到美，你可以欣赏交响乐的力量.....虽然我可以做很多事..但我(目前)真正的梦想是成为人类”，作为最厉害的人工智能，她在某些方面已经远远超越人类，但她的梦想却是成为人类...Jon Farrar，英国广播公司全球高级副总裁，表示：“BBC Earth告诉大家这星球上最伟大的事情，但没有比成为人类更值得探讨的了；Being Human这部纪录片将重点探索人类的复杂性、奇迹；带来一个全新的视角，关于这个所有时代都面临的问题：我们是谁，我们如何生活得更好，我们未来是怎样的。“标签: 其他类视频广告BBC BBDO广告公司除非注明，否则本站文章均为原创翻译，转载请注明出处，并标注本文链接：http://iwebad.com/video/2999.html\", \"crawl_time\": 1501137352, \"_id\": \"http://iwebad.com/video/2999.html\", \"type\": \"commerce\", \"content_html\": \"<div class=\\\"news_ckkk \\\">\\r\\n \\r\\n    \\t       <p><img src=\\\"/d/file/video/other/2017-07-27/b023bc6fe18e4345e2007c9e9d1d43ff.jpg\\\" width=\\\"675\\\" alt=\\\"英国BBC电视台新纪录片 使用人工智能机器人代言广告\\\"></p>\\r\\n<p><embed src=\\\"http://player.youku.com/player.php/sid/XMjkyMzQyNDk0MA==/partnerid/8a2e111011b0aa43/v.swf\\\" allowfullscreen=\\\"true\\\" quality=\\\"high\\\" width=\\\"675\\\" height=\\\"583\\\" align=\\\"middle\\\" allowscriptaccess=\\\"always\\\" wmode=\\\"Opaque\\\" type=\\\"application/x-shockwave-flash\\\"></embed><br></p>  <p>随着人工智能成为全球科技界的热潮，英国BBC电视台为此拍摄了一个纪录片《Being human》，为宣传该纪录片，BBC请来了全球目前最顶尖的仿人智能机器人-索菲亚代言，这个机器人来自汉森机器人公司，代工制作据说是来自香港(深圳)</p>\\r\\n<p><img src=\\\"/d/file/video/other/2017-07-27/ffcca0befc749af4580f3614c8783d19.jpg\\\" alt=\\\"英国BBC电视台新纪录片 使用人工智能机器人代言广告\\\" width=\\\"675\\\" height=\\\"380\\\"></p>\\r\\n<p>随着摄影机在她的周围旋转，她开始述说，“成为人类想必是非常棒的事情，你可以在日出时看到美，你可以欣赏交响乐的力量.....虽然我可以做很多事..但我(目前)真正的梦想是成为人类”，作为最厉害的人工智能，她在某些方面已经远远超越人类，但她的梦想却是成为人类...</p>\\r\\n<p><img src=\\\"/d/file/video/other/2017-07-27/758470f4e20ac83b4af246e5d9dce2e6.jpg\\\" alt=\\\"英国BBC电视台新纪录片 使用人工智能机器人代言广告\\\" width=\\\"675\\\" height=\\\"380\\\"></p>\\r\\n<p>Jon Farrar，英国广播公司全球高级副总裁，表示：“BBC Earth告诉大家这星球上最伟大的事情，但没有比成为人类更值得探讨的了；Being Human这部纪录片将重点探索人类的复杂性、奇迹；带来一个全新的视角，关于这个所有时代都面临的问题：我们是谁，我们如何生活得更好，我们未来是怎样的。“</p>\\r\\n\\t  \\r\\n\\t  \\t  \\t   \\r\\n\\t     \\t   \\r\\n\\t     \\t   \\t   \\r\\n\\t   \\r\\n\\t  \\r\\n\\r\\n<p class=\\\"ksmm fh \\\">标签:<a href=\\\"http://iwebad.com/video/other/\\\">\\r\\n\\t\\t 其他类视频广告</a>\\r\\n\\t\\t<a href=\\\"http://iwebad.com/tag/BBC\\\">BBC</a>\\t\\t <a href=\\\"http://iwebad.com/company/80.html\\\" title=\\\"BBDO广告公司\\\"><strong>BBDO广告公司</strong></a></p>\\r\\n\\t  <p class=\\\"ksmm02 fh\\\">除非注明，否则本站文章均为原创翻译，转载请注明出处，并标注本文链接：http://iwebad.com/video/2999.html</p>\\r\\n\\t    </div>\"}"
    val js2 = "{\"site_name\": \"ebrun\", \"post_title\": \"艾格中国销售收入暴跌28.7％ 至4840万欧元\", \"module\": \"brand\", \"post_url\": \"http://www.ebrun.com/20170725/239459.shtml\", \"content_text\": \"宣布私有化即将退市的法国内衣和服装零售商ETAM Développement SCA(TAM.PA) 艾格上周四发布二季度及中期业绩，该集团二季度中国销售仍持续低迷，高双数暴跌。截止6月30日二季度，ETAM Développement SCA中国收入暴跌28.7％至4840万欧元，可比基础上跌幅仍达到20.2％，中国销售亦拖累集团二季度收入下跌3.2％至2.755亿欧元，可比基础上，集团二季度整体收入跌幅3.5％。该法国集团表示，中国市场自4月份宣布管理层变动后开始精简成本重组计划，包括关店进行单一仓库物流操作，除降低成本外亦推进过季产品销售由于中国市场急转直下，ETAM Développement SCA重新聚焦欧洲市场，同时发展在线渠道业务，截止二季度该集团在欧洲新增92间门店，其中81间位于法国本土之外，这些新增店铺刺激集团欧洲中期收入有3.4％的增长。截止6月30日，ETAM Développement SCA全球共运营3767个销售点，其中987间位于欧洲，2442间位于中国，另有338间国际特许经营店，上半年期内，集团新增21个内衣销售门店以及5间1.2.3品牌门店，而中国市场期内净关闭154间门店。ETAM Développement SCA目前已将完成私有化，将有待监管部门批准进行退市。6月7日，Milchior家族旗下的Finora联手Tarica和Lindemann两大家族宣布以每股49.3欧元的价格对ETAM Développement SCA进行私有化，较要约前一交易日公司收市价32.51欧元有高达53.8％的溢价。由于三大家族对ETAM Développement SCA合共持股高达96.28％，同时拥有97.92％的投票权，该收购只要经过法国金融市场管理局（简称）的批准后，ETAM Développement SCA即可强制退市。AMF已经于7月18日批准了ETAM Développement SCA的私有化方案，公司将于今日正式开放交易，10个交易后退市。诞生于1916年的ETAM由Max Lindemann创立于德国，初始业务为内衣及袜子，随后品牌迅速扩张至欧洲其他主要国家，而公司的发展壮大主要由于有赖于法国人Martin Milchior，他于1933年购入21间店铺，并成立Sociétédes Etablissements Milchior，同时在法国里尔建立工厂；1958年，两大家族将生意合并最终成立ETAM品牌。ETAM一直走在时代前列，抓住每一个趋势，从女权主义风格到色情风格，同时在1980、90年代则精于库存管理。1994年，ETAM进入中国市场，是该品牌的转折点，随后中国市场成为该集团最大的市场，更重要的是，品牌在中国采用的采、制、销一体化模式，成为随后国际品牌在中国市场的惯用模式。不过与此同时，品牌在欧洲的英国、意大利市场则遭遇挫败。在浮浮沉沉中，ETAM的真正坠落始于金融危机，不但欧洲市场发展不均衡同时遭遇新晋快时尚品牌H&M和Zara的严重冲击，其中国市场建立的壁垒亦被国际品牌在中国市场的激进扩张打破。因此，可以说，中国市场是ETAM国际扩张最成功的市场，而其过去10年的衰退，同样源于中国市场消费者口味快速变化及视野开阔。电商资讯第一入口问答｜波罗蜜重押直播 持续性能有谱？【版权提示】亿邦动力网倡导尊重与保护知识产权。如发现本站文章存在版权问题，烦请提供版权疑问、身份证明、版权证明、联系方式等发邮件至run@ebrun.com，我们将及时沟通与处理。\", \"crawl_time\": 1501137352, \"_id\": \"http://www.ebrun.com/20170725/239459.shtml\", \"type\": \"commerce\", \"content_html\": \"<div class=\\\"clearfix cmsDiv\\\">\\r\\n        <p>宣布私有化即将退市的法国内衣和服装<a href=\\\"http://www.ebrun.com/retail/\\\" class=\\\"ebkw\\\" title=\\\"零售\\\">零售</a>商ETAM Développement SCA(TAM.PA) 艾格上周四发布二季度及中期业绩，该集团二季度中国销售仍持续低迷，高双数暴跌。</p><p>截止6月30日二季度，ETAM Développement SCA中国收入暴跌28.7％至4840万欧元，可比基础上跌幅仍达到20.2％，中国销售亦拖累集团二季度收入下跌3.2％至2.755亿欧元，可比基础上，集团二季度整体收入跌幅3.5％。</p><p>该法国集团表示，中国市场自4月份宣布管理层变动后开始精简成本重组计划，包括关店进行单一仓库物流操作，除降低成本外亦推进过季产品销售</p><p>由于中国市场急转直下，ETAM Développement SCA重新聚焦欧洲市场，同时发展在线渠道业务，截止二季度该集团在欧洲新增92间门店，其中81间位于法国本土之外，这些新增店铺刺激集团欧洲中期收入有3.4％的增长。</p><p>截止6月30日，ETAM Développement SCA全球共运营3767个销售点，其中987间位于欧洲，2442间位于中国，另有338间国际特许经营店，上半年期内，集团新增21个内衣销售门店以及5间1.2.3品牌门店，而中国市场期内净关闭154间门店。</p><p>ETAM Développement SCA目前已将完成私有化，将有待监管部门批准进行退市。6月7日，Milchior家族旗下的Finora联手Tarica和Lindemann两大家族宣布以每股49.3欧元的价格对ETAM Développement SCA进行私有化，较要约前一交易日公司收市价32.51欧元有高达53.8％的溢价。</p><p>由于三大家族对ETAM Développement SCA合共持股高达96.28％，同时拥有97.92％的投票权，该收购只要经过法国金融市场管理局（简称）的批准后，ETAM Développement SCA即可强制退市。</p><p>AMF已经于7月18日批准了ETAM Développement SCA的私有化方案，公司将于今日正式开放交易，10个交易后退市。</p><p>诞生于1916年的ETAM由Max Lindemann创立于德国，初始业务为内衣及袜子，随后品牌迅速扩张至欧洲其他主要国家，而公司的发展壮大主要由于有赖于法国人Martin Milchior，他于1933年购入21间店铺，并成立Sociétédes Etablissements Milchior，同时在法国里尔建立工厂；1958年，两大家族将生意合并最终成立ETAM品牌。</p><p>ETAM一直走在时代前列，抓住每一个趋势，从女权主义风格到色情风格，同时在1980、90年代则精于库存管理。</p><p>1994年，ETAM进入中国市场，是该品牌的转折点，随后中国市场成为该集团最大的市场，更重要的是，品牌在中国采用的采、制、销一体化模式，成为随后国际品牌在中国市场的惯用模式。不过与此同时，品牌在欧洲的英国、意大利市场则遭遇挫败。</p><p>在浮浮沉沉中，ETAM的真正坠落始于金融危机，不但欧洲市场发展不均衡同时遭遇新晋快时尚品牌H&amp;M和<a href=\\\"http://www.ebrun.com/label/co/zara.html\\\" class=\\\"ebkw\\\" title=\\\"Zara\\\">Zara</a>的严重冲击，其中国市场建立的壁垒亦被国际品牌在中国市场的激进扩张打破。</p><p>因此，可以说，中国市场是ETAM国际扩张最成功的市场，而其过去10年的衰退，同样源于中国市场消费者口味快速变化及视野开阔。</p>\\r\\n<!--文章尾部主站首页入口链接-->\\r\\n<p class=\\\"ybfirst_go\\\"><img src=\\\"http://imgs.ebrun.com/images/201511/ybfirst.png\\\"><a href=\\\"http://www.ebrun.com/\\\" title=\\\"\\\" target=\\\"_blank\\\">电商资讯第一入口</a></p>\\r\\n\\r\\n       <div id=\\\"cyPk\\\" role=\\\"cylabs\\\" data-use=\\\"pk\\\"></div>\\r\\n\\r\\n                                                                                                                                                                                                                                                                                                                                                                                                                                                                 \\t           <div class=\\\"xqy_qyk clearfix\\\" id=\\\"corp\\\" style=\\\"display:none;\\\">         \\r\\n</div>\\r\\n<script type=\\\"text/javascript\\\" src=\\\"http://imgs.ebrun.com/js/201501/corparticle.js\\\"></script>     \\r\\n           <!-- 老虎证券 -->\\r\\n<!--\\r\\n<iframe src=\\\"https://www.tigerbrokers.com/activity/quotation/ebrun/popular.html\\\" class=\\\"tigerstock\\\" style=\\\"width: 600px;height: 195px;border: 1px solid #e6e6e6;margin: 30px 0 20px 0;padding: 20px 10px;\\\"></iframe>\\r\\n-->\\r\\n\\t                  \\t           <p style=\\\"text-align:center;\\\"><a href=\\\"http://m.ebrun.com/wenda/134.html\\\" target=\\\"_blank\\\"><img src=\\\"http://imgs.ebrun.com/resources/2017_07/2017_07_26/2017072691015010615600934.jpg\\\"></a></p><p class=\\\"newtext\\\" style=\\\"text-align: center;\\\"><a href=\\\"http://m.ebrun.com/wenda/134.html\\\" target=\\\"_blank\\\"><span style=\\\"font-family: 微软雅黑, 'Microsoft YaHei'; font-size: 18px; color: rgb(0, 0, 255);\\\"><strong>问答｜波罗蜜重押直播 持续性能有谱？</strong></span></a></p><p class=\\\"newtext\\\">【版权提示】亿邦动力网倡导尊重与保护知识产权。如发现本站文章存在版权问题，烦请提供版权疑问、身份证明、版权证明、联系方式等发邮件至run@ebrun.com，我们将及时沟通与处理。</p>\\r\\n               \\t   \\t\\r\\n    </div>\"}"
    val js3 = "{\"site_name\": \"jiemian\", \"post_title\": \"Instagram Stories成为广告主的新阵地\", \"module\": \"brand\", \"post_url\": \"http://www.jiemian.com/article/1502138.html\", \"content_text\": \"尽管上线以来饱受争议，Instagram Stories还是势头不错。具体来说，它“山寨”Snapchat的阅后即焚功能，在Instagram的页面上停留24小时便会被消灭掉，你可以用它来拍照，录个几十秒的短视频。总之无论干什么它只有24个小时的寿命。7月20号，由移动营销公司TUNE在西雅图举办的全球移动营销大会POSTBACK上，Instagram产品经理Ashley Yuki公布了关于Stories的一些新的数据和案例。目前的Instagram Stories日活用户数量已经达到了2.5亿。如果你回顾一下它过去的情况，就会发现这个去年8月才上线的功能，用户数量增长速度相当惊人。2017年1月它的这个数字才是1.5亿，4月达到2亿，现在较1月份的数字增长了67%。而“原创”的Snapchat Stories2月份的月活数量为1.58亿。你可以在Instagram Stories上进行各种创作。Instagram Stories的户外广告。Instagram也成为了商业品牌最新的营销阵地。因为和传统图片广告位相比，手机竖版视频有更大的信息量和更为沉浸的体验。它所附带的滤镜和贴纸也显得活泼有趣，同时24小时的时效性增加了不少新鲜感。自从今年1月份Instagram Stories邀请Airbnb、耐克、ASOS、Capital One、别克、Netflix、Michael Kors等30个品牌作为首批客户体验广告以来，商家纷纷采用这种内容短暂存在的新颖方式讲述品牌故事。根据Ashley Yuki在大会现场公布的数据，80%的用户会关注品牌账户，有三分之一热门Stories是商业内容。Airbnb 的Instagram Stories广告主可以在平台上以程序化方式购买Instagram Stories广告，按照展示付费。基于Facebook大数据的精准推送功能也可以同时引入，比如基于年龄、性别和地点划分用户。从绝大多数来看，它们使用Stories发布内容，最重要的目的还是和用户互动。“利用 Instagram Stories 投放的沉浸式广告可吸引并邀请我们的社区成员参与体验。”Airbnb 社交营销和内容全球负责人Eric Toda说，“从用户体验出发创建并发布快拍，这让我们可以吸引并发掘那些真正想通过 Airbnb 预订专属体验之旅的用户。”“我们希望 2017 年在国际市场上取得更大的发展，并且分享如何利用Instagram Stories在新客户中提高品牌知名度和参与度。”ASOS 营销内容与互动总监Leila Thabet也如此表示。广告主们对Instagram Stories的兴趣，也让Facebook受益不少。在7月27日公布的今年第一季度财报中，Instagram助推公司的销售额增长45%至93亿美元。高于此前分析师预期。而在Facebook的整体广告营收中，移动广告的占比达到87%，比上一季度的84%有所增长。虽然眼下Instagram在和Snapchat的激烈竞争中处于上风，但许多广告主并没有轻易放弃Snapchat的阵地。在《神偷奶爸2》的宣传期，环球影业还在Snapchat投放了一波广告。这家公司的数字营销执行副总裁Doug Neil表示：“Snapchat仍然是我们接触观众的非常重要的平台。”《神偷奶爸2》在Snapchat上制作的广告。“广告主对增加的渠道感到兴奋，但我们还没有观察到太多客户从Snapchat转移到Instagram上去。”纽约营销代理公司Deutsch总监Danielle Johnsen Karr分析称，“作为一个广告渠道，Instagram Stories的价格相对昂贵，并且是独立于Facebook和Instagram标准购买之外的。” 欢迎长按下方二维码，关注界面营销频道微信公众号“看你卖”（kannimai）。更多专业报道，请点击下载“界面新闻”APP0马越界面记者界面驻北京记者，关注消费、营销、广告、创意等。沟通请发邮件至mayue@jiemian.com关注作者已关注私信\", \"crawl_time\": 1501137351, \"_id\": \"http://www.jiemian.com/article/1502138.html\", \"type\": \"commerce\", \"content_html\": \"<div class=\\\"article-main\\\"><div class=\\\"article-img\\\"><img src=\\\"//img2.jiemian.com/101/original/20170727/150112503094585200_a580x330.jpg\\\" alt=\\\"\\\"><p><span></span></p></div><div class=\\\"article-content\\\"><p>尽管上线以来饱受争议，Instagram Stories还是势头不错。具体来说，它“山寨”Snapchat的阅后即焚功能，在Instagram的页面上停留24小时便会被消灭掉，你可以用它来拍照，录个几十秒的短视频。总之无论干什么它只有24个小时的寿命。</p>\\r\\n\\r\\n<p>7月20号，由移动营销公司TUNE在西雅图举办的全球移动营销大会POSTBACK上，Instagram产品经理Ashley Yuki公布了关于Stories的一些新的数据和案例。目前的Instagram Stories日活用户数量已经达到了2.5亿。如果你回顾一下它过去的情况，就会发现这个去年8月才上线的功能，用户数量增长速度相当惊人。2017年1月它的这个数字才是1.5亿，4月达到2亿，现在较1月份的数字增长了67%。而“原创”的Snapchat Stories2月份的月活数量为1.58亿。</p>\\r\\n\\r\\n<figure class=\\\"content-img-focus img-focus\\\"><img alt=\\\"\\\" src=\\\"//img2.jiemian.com/101/original/20170727/15011250689697000_a580xH.png\\\">\\r\\n<figcaption>你可以在Instagram Stories上进行各种创作。</figcaption>\\r\\n</figure>\\r\\n\\r\\n<figure class=\\\"content-img-focus img-focus\\\"><img alt=\\\"\\\" src=\\\"//img2.jiemian.com/101/original/20170727/150109274426726000_a580xH.jpg\\\">\\r\\n<figcaption>Instagram Stories的户外广告。</figcaption>\\r\\n</figure>\\r\\n\\r\\n<p>Instagram也成为了商业品牌最新的营销阵地。因为和传统图片广告位相比，手机竖版视频有更大的信息量和更为沉浸的体验。它所附带的滤镜和贴纸也显得活泼有趣，同时24小时的时效性增加了不少新鲜感。</p>\\r\\n\\r\\n<p>自从今年1月份Instagram Stories邀请Airbnb、耐克、ASOS、Capital One、别克、Netflix、Michael Kors等30个品牌作为首批客户体验广告以来，商家纷纷采用这种内容短暂存在的新颖方式讲述品牌故事。根据Ashley Yuki在大会现场公布的数据，80%的用户会关注品牌账户，有三分之一热门Stories是商业内容。</p>\\r\\n\\r\\n<figure class=\\\"content-img-focus img-focus\\\"><img alt=\\\"\\\" src=\\\"//img2.jiemian.com/101/original/20170727/15010932243569900_a580xH.jpg\\\">\\r\\n<figcaption>Airbnb 的Instagram Stories</figcaption>\\r\\n</figure>\\r\\n\\r\\n<p>广告主可以在平台上以程序化方式购买Instagram Stories广告，按照展示付费。基于Facebook大数据的精准推送功能也可以同时引入，比如基于年龄、性别和地点划分用户。从绝大多数来看，它们使用Stories发布内容，最重要的目的还是和用户互动。</p>\\r\\n\\r\\n<p>“利用 Instagram Stories 投放的沉浸式广告可吸引并邀请我们的社区成员参与体验。”Airbnb 社交营销和内容全球负责人Eric Toda说，“从用户体验出发创建并发布快拍，这让我们可以吸引并发掘那些真正想通过 Airbnb 预订专属体验之旅的用户。”</p><p class=\\\"report-view\\\"><img src=\\\"//img2.jiemian.com/101/original/20170727/150112503094585200.jpg\\\"></p>\\r\\n\\r\\n<p>“我们希望 2017 年在国际市场上取得更大的发展，并且分享如何利用Instagram Stories在新客户中提高品牌知名度和参与度。”ASOS 营销内容与互动总监Leila Thabet也如此表示。</p>\\r\\n\\r\\n<p>广告主们对Instagram Stories的兴趣，也让Facebook受益不少。在7月27日公布的今年第一季度财报中，Instagram助推公司的销售额增长45%至93亿美元。高于此前分析师预期。而在Facebook的整体广告营收中，移动广告的占比达到87%，比上一季度的84%有所增长。</p>\\r\\n\\r\\n<p>虽然眼下Instagram在和Snapchat的激烈竞争中处于上风，但许多广告主并没有轻易放弃Snapchat的阵地。在《神偷奶爸2》的宣传期，环球影业还在Snapchat投放了一波广告。这家公司的数字营销执行副总裁Doug Neil表示：“Snapchat仍然是我们接触观众的非常重要的平台。”</p>\\r\\n\\r\\n<figure class=\\\"content-img-focus img-focus\\\"><img alt=\\\"\\\" src=\\\"//img2.jiemian.com/101/original/20170727/150109331615019400_a580xH.jpg\\\">\\r\\n<figcaption>《神偷奶爸2》在Snapchat上制作的广告。</figcaption>\\r\\n</figure>\\r\\n\\r\\n<p>“广告主对增加的渠道感到兴奋，但我们还没有观察到太多客户从Snapchat转移到Instagram上去。”纽约营销代理公司Deutsch总监Danielle Johnsen Karr分析称，“作为一个广告渠道，Instagram Stories的价格相对昂贵，并且是独立于Facebook和Instagram标准购买之外的。”</p>\\r\\n\\r\\n<p> </p>\\r\\n\\r\\n<p>欢迎长按下方二维码，关注界面营销频道微信公众号“看你卖”（kannimai）。</p>\\r\\n\\r\\n<p><img alt=\\\"\\\" class=\\\"img-focus\\\" src=\\\"//img2.jiemian.com/101/original/20170727/15010933747234300_a580xH.jpg\\\"></p>\\r\\n<!--------------------- 来源 --------------------><p>更多专业报道，请<a href=\\\"http://a.jiemian.com/index.php?m=app&amp;a=redirect\\\" style=\\\"color:#113a65;\\\" target=\\\"_blank\\\">点击下载“界面新闻”APP</a></p>\\r\\n<script type=\\\"text/javascript\\\">\\t//执行获取文章信息函数\\r\\n\\tvar aid = '1502138';\\r\\n</script><div class=\\\"share-view\\\"><div class=\\\"share-title\\\"><i class=\\\"ui-icon ui-share\\\"></i></div><a href=\\\"javascript:void(0);\\\" onclick=\\\" __track(1,2,'aid:'+aid+'#qd:sina_weibo');\\\" class=\\\"jm_sina\\\" title=\\\"分享到新浪微博\\\" data=\\\"@界面\\\" share=\\\"true\\\"><i class=\\\"ui-icon ui-weibo\\\"></i></a><!-- <a href=\\\"javascript:void(0);\\\" class=\\\"jm_qq\\\" url=\\\"https://a.jiemian.com/index.php?m=article&a=share&aid=1502138\\\" title=\\\"分享到QQ好友\\\" share=\\\"true\\\"><i class=\\\"ui-icon ui-qq\\\"></i></a> --><a href=\\\"javascript:void(0);\\\" onclick=\\\" __track(1,2,'aid:'+aid+'#qd:weixin');\\\" class=\\\"jm_weixin\\\" title=\\\"分享到微信朋友圈\\\" share=\\\"true\\\"><i class=\\\"ui-icon ui-weixin\\\"></i></a><a href=\\\"javascript:void(0);\\\" onclick=\\\" __track(1,2,'aid:'+aid+'#qd:qq_weibo');\\\" class=\\\"jm_qq_weibo\\\" title=\\\"分享到QQ微博\\\" data=\\\"@界面\\\" share=\\\"true\\\"><i class=\\\"ui-icon ui-tencent-weibo\\\"></i></a><a href=\\\"javascript:void(0);\\\" onclick=\\\" __track(1,2,'aid:'+aid+'#qd:qzone');\\\" class=\\\"jm_qzone\\\" title=\\\"分享到QQ空间\\\" data=\\\"@界面\\\" share=\\\"true\\\"><i class=\\\"ui-icon ui-qzone\\\"></i></a><!--<a href=\\\"javascript:void(0)\\\" class=\\\"jm_like\\\" title=\\\"顶\\\" id='ding' onclick=\\\"ding()\\\"><i class=\\\"ui-icon ui-like\\\"></i><span id=\\\"ding_count\\\">0</span></a>--><!--<a href=\\\"javascript:void(0)\\\" class=\\\"jm_unlike\\\" title=\\\"踩\\\" onclick=\\\"cai()\\\"><i class=\\\"ui-icon ui-unlike\\\"></i><span id=\\\"cai_count\\\">0</span></a>--><!--<a href=\\\"javascript:void(0)\\\" title=\\\"收藏\\\"  class=\\\"jm-collection\\\" onclick=\\\"addCollect(1502138)\\\"><i class=\\\"ui-icon ui-collection\\\"></i><span id=\\\"collect_count\\\">0</span></a>--></div></div><div class=\\\"article-btn\\\"><span class=\\\"zan-btn\\\" id=\\\"1502138\\\" url=\\\"https://a.jiemian.com/index.php?m=dingcai&amp;a=ding&amp;aid=1502138\\\" geturl=\\\"https://a.jiemian.com/index.php?m=dingcai&amp;a=getDing&amp;aid=1502138\\\"><i>0</i></span></div><div class=\\\"a-b-ad\\\" id=\\\"ad-content\\\"><script>CNZZ_SLOT_Async('2373100', 'ad-content');</script></div><div class=\\\"article-footer\\\"><div class=\\\"article-author item-0\\\"><div class=\\\"author-avatar\\\"><a href=\\\"https://a.jiemian.com/index.php?m=user&amp;a=center&amp;id=100743852\\\"><img src=\\\"//img.jiemian.com/userface/80x80/696/384/100743852-1456481969.jpg\\\" alt=\\\"\\\" width=\\\"60\\\" height=\\\"60\\\"></a></div><div class=\\\"author-main\\\"><a href=\\\"https://a.jiemian.com/index.php?m=user&amp;a=center&amp;id=100743852\\\" class=\\\"author-name\\\">马越</a><span class=\\\"vip\\\"></span><span class=\\\"author-mate\\\">界面记者</span><p>界面驻北京记者，关注消费、营销、广告、创意等。沟通请发邮件至mayue@jiemian.com</p></div><div class=\\\"follow-set\\\"><p><a href=\\\"javascript:void%20(0)\\\" node-type=\\\"sub_btn_text\\\" node-uid=\\\"100743852\\\" id=\\\"sub100743852\\\"><i class=\\\"jm-icon s-icon icon-rss\\\"></i><i id=\\\"dingyue\\\">关注作者</i></a><a href=\\\"javascript:void%20(0)\\\" node-type=\\\"sub_btn_del\\\" node-uid=\\\"100743852\\\" id=\\\"sub2100743852\\\" style=\\\"display:none;\\\"><i class=\\\"jm-icon s-icon icon-minus\\\"></i><i id=\\\"dingyue\\\">已关注</i></a><a href=\\\"javascript:;\\\" class=\\\"pm\\\" node-type=\\\"pm_btn_text\\\" node-uid=\\\"100743852\\\"><i class=\\\"jm-icon s-icon icon-pm\\\"></i>\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t私信\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t</a></p></div></div></div></div>\"}"
    val js4 = "{\"site_name\": \"jiemian\",\"content_text\": \"巴拉巴拉\"}"

    val json = replaceContext(js)
    println(json)


  }
}
