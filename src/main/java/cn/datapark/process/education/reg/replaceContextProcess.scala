package cn.datapark.process.education.reg

import org.json.JSONObject

/**
  * Created by cluster on 2017/8/10.
  */
object replaceContextProcess {
  def replaceContext(str: String): JSONObject ={
    var content_text = ""
    var content_html = ""
    val json = new JSONObject(str)
    if (json.has("content_html")){
      if (json.get("site_name").toString == "36氪"){
        content_text = json.get("content_text").toString.replaceAll(".*36氪经授权发布。|.*授权36氪转载。|编者按：本文[来|转]自.*?，|本文来自.*?，","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p>.*编者按：.*本文[来|转|作|由].*?</p>|<p>.*编者按：.*文章[来|转|作|由].*?</p>|<p>本文[来|转|作|由].*?</p>|<p>编辑\\|.*?</p>|<p>文\\|.*?</p>|<p>作者\\|.*?</p>|<p>编辑 \\|.*?</p>|<p>文 \\|.*?</p>|<p>作者 \\|.*?</p>|<p>我是36氪.*?</p>|<em>我是36氪.*?</em>|<strong>我是36氪.*?</strong>|<p>编译组出品.*?</p>|<p>【编译组出品】.*?</p>|<p><em>作者.*?</em></p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")

        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "麦迪逊邦"){
        content_text = json.get("content_text").toString.replaceAll("信息来源[：|自].*", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p>\\s*.*来源[：| ：|自|于|\\ |].*</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "雷锋网"){
        content_text = json.get("content_text").toString.replaceAll("雷锋网..文章，未经授权禁止转载。详情见转载须知。|\\(公众号：雷锋网\\)|.*按：本文作者.*首发.*?。", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("\\(公众号：雷锋网\\)|<p>雷锋网..文章，未经授权禁止转载。详情见.*转载须知</a>。</p>|<p>.*按：本文作者.*首发.*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "镁客网"){
        content_text = json.get("content_text").toString.replaceAll("最后，记得关注微信公众号：镁客网（im2maker），更多干货在等你！", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p.*>.*[本文|关注].*微信公众号.*</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "钛媒体"){
        content_text = json.get("content_text").toString.replaceAll("更多精彩内容，关注钛媒体微信号（ID：taimeiti），或者下载钛媒体App|【钛媒体作者：.*】", "").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p>.*[本文|关注].*微信号.*</p>|<img .* alt=\"钛媒体\"/>|<div class=\"sm\"><p>([\\s\\S]*?)</div>|<div class=\"txt\">([\\s\\S]*?)</div>" +
          "|<div class=\"author-cont\">([\\s\\S]*?)</div>|<div class=\"author-info clear\">([\\s\\S]*?)</div>|<div class=\"authors fl\">([\\s\\S]*?)</div>|<p>【钛媒体作者：.*】</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "速途网"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<b>.*速途.*消息.*</b>|<img src=.*logo_sootoo.jpg\"/>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "虎嗅"){
        content_text = json.get("content_text").toString.replaceAll("\\*文章为作者独立观点，不代表虎嗅网立场|未来面前，你我还都是孩子，还不去下载 虎嗅App 猛嗅创新！|" +
          "虎嗅注：本文[来|转]自.*公众号.*?。","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<div class=\"neirong-shouquan\">([\\s\\S]*?)</div>|<div class=\"neirong-shouquan-public\">([\\s\\S]*?)</div>|<span.*>.*[本文|关注].*微信公众号.*?</span>|" +
          "<p>诸君晚安，咱明天.*?</p>|<p class=\"img-center-box\"><img src=\"https://img.huxiucdn.com/article/cover/201708/02/212255464925.png\"></p>|<p>还未加入24小时讨论的嗅友们赶快扫描二维码或者.*戳我下载/升级最新版虎嗅APP.*?</p>|<p>以及，明天同一时间，虎嗅晚报见哦~</p>" +
          "|<span class=\"text-remarks\">来源：.*?</span>|<span class=\"text-remarks\">欢迎订阅，想了解更多资讯，关注我们吧。</span>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "艾瑞咨询"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<div class=\"brand-title\">([\\s\\S]*?)</div>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "网络广告人社区"){
        content_text = json.get("content_text").toString.replaceAll("除非注明，.*html","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<div class=\"messages_user_name\">([\\s\\S]*?)</div>|<div class=\"newsfrom fh\">([\\s\\S]*?)</div>|<p class=\"ksmm fh \">([\\s\\S]*?)</p>|<p class=\"ksmm02 fh\">.*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
    }
      else if (json.get("site_name").toString == "砍柴网"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p>来源[：| ：|自|于].*?</p>|<p><span.*>来源[：| ：|自|于].*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "界面"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<div class=\"article-btn\">([\\s\\S]*?)</div>|<div class=\"a-b-ad\" id=\"ad-content\">([\\s\\S]*?)</div>|<div class=\"author-avatar\">([\\s\\S]*?)</div>|<div class=\"author-main\">([\\s\\S]*?)</div>|<div class=\"follow-set\">([\\s\\S]*?)</div>|<div class=\"article-source\">([\\s\\S]*?)</div>|<p>本文转载.*?</p>|<p><em>本文转载.*?</p>|<p>更多专业报道，请.*点击下载“界面新闻”APP.*?</p>|<p>.*阅读更多有关科技的内容，请点击查看.*?</p>|<p>来源[：|自].*?</p>|<p>欢迎长按下方二维码.*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "理想生活实验室"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<strong>注：.*微信.*?</strong>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "猎云网"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<span class=\"poperweima\">([\\s\\S]*?)</span>|<span class=\"pop-erweima\">([\\s\\S]*?)</span></span>|<p.*>.+猎云网.*报道.*?</p>|<p class=\"article-copyright-bar\">([\\s\\S]*?)</p>|" +
          "<div class=\"article-copyright-bar\">([\\s\\S]*?)</div>","").replaceAll("（）|（微信号：）|（微信：）","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "比特网"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p class=\"instructions\">([\\s\\S]*?)</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "极客公园"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "机器之心"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<img src=\"https://image.jiqizhixin.com/uploads/wangeditor/[0-9a-z]{8}\\-[0-9a-z]{4}\\-[0-9a-z]{4}\\-[0-9a-z]{4}\\-[0-9a-z]{12}\\/[0-9]{5}机器之能图标.png\"/>|<p><b>[\\u4e00-\\u9fa5]{2} \\| .*?</p>|<p>[\\u4e00-\\u9fa5]{2} \\｜.*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "数英网"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<div class=\"weixin_qrcode_pic\">([\\s\\S]*?)</div>|<strong>扫描二维码.*（数英网APP用户需点击放大二维码，长按识别）.*?</p>|<strong>扫描二维码.*?</p>|\\(数英网APP用户需点击放大二维码，长按识别\\)|扫描二维码，查看视频！.*?</p>|<p style=\"text-align: center;\"><strong>长按扫码.*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "广告门"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<div class=\"msg_box\">([\\s\\S]*?)</div>|<div class=\"user_msg\">([\\s\\S]*?)</div>|<div class=\"share_ww\">([\\s\\S]*?)</div>|<p class=\"text_time2\">([\\s\\S]*?)</p>|<p class=\"t_label\">([\\s\\S]*?)</p>|<div class=\"box_02\" id=\"dianzan\" data-flag=\"2\" data-postid=\"41738\" data-zannum=\"6\">([\\s\\S]*?)</div>|<div class=\"message message02\">([\\s\\S]*?)</div>|<div class=\"new_Recruitment\">([\\s\\S]*?)</div>|↓|<span.*>.*来源.*?</span>|关注大胆，拒绝一切小心翼翼|<strong>相关阅读.*?</span>.*?</span>|<strong>--- 正 --- 在 --- 载 --- 入 --- 答 --- 案 ---</strong>|\n-.*分割线.*?<br/>|<div class=\"box_02\".*>.*?</div>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "好奇心日报"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p>翻译 .*?</p>|<p class=\"category x25\">.*?</p>|<div class=\"share-favor-bd clearfix\">([\\s\\S]*?)</div>|<div class=\"article-detail-ft\">([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>|<div class=\"com-related-banners long-article\">([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>|<div class=\"com-related-comments wide\".*>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>|<p class=\"\">.*本文.*授权《好奇心日报》发布.*?</p>|<p class=\"excerpt\">.*?</p>|<p nocleanhtml=\"true\"><strong><em>＊.*即使我们允许了也不许转载＊.*?</p>|<div class=\"author\">([\\s\\S]*?)</div>|<p nocleanhtml=\"true\">© 2017 THE NEW YORK TIMES</p>|<p class=\"lazylood\">.*?</p>|<p>注：.*?</p>|<div class=\"com-related-articles\">([\\s\\S]*)</div>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "动点科技"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<footer>([\\s\\S]*?)</footer>|<p><em>注：本文.*?</p>|<p>.*本文来.*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "创业邦"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p>本文.*?</p>|<p><img src=\"http://img2.cyzone.cn/uploadfile/2017/0804/20170804103840244.jpg\"/></p>|<p>这个夏末，你与大咖有个约！</p>|<p><a href=\"http://www.huodongxing.com.*?>点击这里   </a> or 扫描下方二维码</p>|<embed id=.*?>|<p><em>本文由.*?</p>|（微信搜索：ichuangyebang）","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "亿邦动力网"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p class=\"ybfirst_go\">.*?</p>|<p><a href=\"http://m.ebrun.com/wenda/.*?</p>|<p class=\"newtext\".*>.*?</p>|<a href=\"http://www.ebrun.com/tc/.*?</a>|<span class=\"ebrun_viewpoint\">.*?</span>.*?</span></span>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "亿欧网"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<p>更多.*↓↓↓.*?</p>|<hr/>|<p.*>.*报名[详情|请戳|请点击连接|链接|连接].*?</p>|<p><span>本文作者.*?</p>|<p><em>本文.*作者.*?</p>|<p.*><a href=.*ad/id.*?</p>|<p class=\"copyrightState\">.*?</p>|<p><em><span>本文.*作者.*?</p>|<p>注：文章中所涉.*欢迎向亿欧举报。</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "socialbeta"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("55 互动 旗下创意子品牌|<img src=\"http://img.socialbeta.com/upload/12996-1500373000.png\".*?>|<a href=\"http://hunt.socialbeta.com/\".*?>.*?</a>|更多案例尽在 HUNT:|<a href=\"http://socialbeta.com.*>案例一周.*?</a>|<h2.*>案例一周.*?</h2>|<strong>[\\u4e00-\\u9fa5]{3}：.*?</strong>|<blockquote>.*?</blockquote>|<p>「案例一周」.*?</p>|<p><strong>「海外案例一周」.*?</p>|<p><strong>本周的精彩案例看点：.*?</p>|<p style=\"white-space: normal;\">.*【案例一周】.*?</p>|<p style=\"text-align: justify;\">.*「案例一周」.*?</p>|<strong>[A-Za-z]*：[A-Za-z]*?</strong>|<p style=\"white-space: normal;\">.*【品牌制片厂】</a>是 SocialBeta 推出的.*「别看烂片啦！来看广告啊喂！」.*?</p>|<p><a href=.*>【品牌制片厂】</a>是 SocialBeta 推出的栏目.*我们的口号是：「别看烂片啦！来看广告啊喂！」</p>|<p hiragino=\"\".*>欢迎关注我们的微信公众号：品牌制片厂（brandfilm），每周三 20:46 定时更新。</p>|<strong>本文也将在之后持续更新（在 SocialBeta 微信号后台直接回复「 2017好文 」即可获取）。</strong>|<p style=\"text-align: justify;\">无论你是刚入行不久的新人还是已在广告营销圈打拼多时的行业精英.*赶快收藏起来吧！</p>|<p style=\"margin: 0px;.*>扫描下方二维码，在【品牌制片厂】公众号.*?</p>|<img src=\"http://img.socialbeta.com/upload/4317-1495693498.gif\".*?>|<strong>SocialBeta：</strong>每日分享最新社交、数字、移动领域营销趋势，搜罗创意的案例，发布各地热门营销职位。欢迎扫一扫，关注微信公众号.*?</p>|<p>.*【案例一周】.*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "i黑马"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("<div class=\"author\">([\\s\\S]*?)</div>|<p><span><strong>[\\u4e00-\\u9fa5]*\\丨.*?</p>|<p><span><strong>[\\u4e00-\\u9fa5]* \\|.*?</p>|<p>[\\u4e00-\\u9fa5]* \\|.*?</p>|<p>[\\u4e00-\\u9fa5]*\\丨.*?</p>|<p>【黑马高调早报】.*?</p>|<div class=\"mesinfo cf\">([\\s\\S]*?)</div>([\\s\\S]*?)</div>([\\s\\S]*?)</div>|<img src=\"http://upload.iheima.com/2017/0806/1501996243730.jpg\" border=\"0\" alt=\"1\"/>|<p><a href=\"http://www.iheima.com\".*?</p>|<p><span><strong>[\\u4e00-\\u9fa5]* \\| .*?</p>|<p class=\"copyright\">.*?</p>","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else if (json.get("site_name").toString == "donews"){
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("（记者 .*?）","").replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
      else{
        content_text = json.get("content_text").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        content_html = json.get("content_html").toString.replaceAll("[\\x{10000}-\\x{10FFFF}]", "")
        json.put("content_text",content_text)
        json.put("content_html",content_html)
      }
    }
    json
  }
}
