package cn.datapark.process.education.reg

import org.json.JSONObject

/**
  * Created by cluster on 2017/8/16.
  */
object replaceContent_html {

  def replaceContext(str: String): JSONObject ={
    var content_text = ""
    var content_html = ""
    val json = new JSONObject(str)
    if (json.has("content_html")){
      content_html = ContentDeepExtractor.getContent(json.get("content_html").toString,json.get("post_url").toString)
      json.put("content_html",content_html)
    }
    json
  }

  def main(args: Array[String]) {
     val jsonStr = "{\"site_name\": \"digitaling\", \"post_title\": \"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\", \"module\": \"brand\", \"post_url\": \"http://www.digitaling.com/projects/22375.html\", \"content_text\": \"创业者可能就是这样，为了一个可能只是梦的梦想踽踽独行，为了可以尝到梦想的甜头，不知道要吃多久现实的苦头。他们总被贴上“勇敢”的标签，也就就意味着和“不勇敢”有关的事情都要自己承受。为大局，为面子，为了给员工的信心，为了能撑下去…他们只是默默在坚持，自己吃的的苦却很少对别人说起。这一次，钉钉想要为创业者发声，好卖就通过先丧后燃的方式，让大众看到了更立体的创业者形象。文案中洞察到创业者从工作到生活中的苦，以场景化的“丧”文案，还原现实中的创业者的故事，再以正能量满满的“坚持很酷”去打动创业者乃至上班族的心。在钉钉这波战役里，每个坚持为自己工作的人，都能找到自己的影子。第一波内容：创业很苦“感觉自己这次会成功，这种感觉已经是第六次”“亏了钱，失去了健康”“怕配不上曾经的梦想，也怕辜负了遭受的苦难”“陪聊 陪酒 陪笑 赔本”“28岁，头发白了一半”“刚来三天的新同事提离职，理由是他也决定去创业”“为了想做的事，去做不想做的事”“在车里哭完，笑着走进办公室”第二波内容：坚持很酷“玻璃心，磨成了钻石心”“解决用户的不快乐，让我快乐”“公司终于出现一年以上的老员工”“试别人不敢试的噩梦，造别人不敢造的美梦”“只怕一生碌碌无为”“曾经仰望的行业巨头，现在成了竞争对手”“固执的坚持很傻么？未必。我听说傻人有傻福”“在这里，血液流动得更快”“创业很苦，坚持很酷”\", \"crawl_time\": 1500946491, \"_id\": \"http://www.digitaling.com/projects/22375.html\", \"type\": \"commerce\", \"content_html\": \"<div class=\\\"article_con mg_b_50\\\" id=\\\"article_con\\\">\\r\\n                            <p></p><p style=\\\"text-align: left;\\\">创业者可能就是这样，<span style=\\\"font-size: 14px;\\\">为了一个可能只是梦的梦想踽踽独行，</span><span style=\\\"font-size: 14px;\\\">为了可以尝到梦想的甜头，不知道要吃多久现实的苦头。</span></p><p>他们总被贴上“勇敢”的标签，<span style=\\\"font-size: 14px;\\\">也就就意味着和“不勇敢”有关的事情都要自己承受。</span></p><p>为大局，为面子，为了给员工的信心，为了能撑下去…</p><p>他们只是默默在坚持，<span style=\\\"font-size: 14px;\\\">自己吃的的苦却很少对别人说起。</span></p><p><strong><span style=\\\"font-size: 14px;\\\">这一次，钉钉想要为创业者发声，好卖就通过先丧后燃的方式，让大众看到了更立体的创业者形象。</span></strong><span style=\\\"font-size: 14px;\\\">文案中洞察到创业者从工作到生活中的苦，以场景化的“丧”文案，还原现实中的创业者的故事，再以正能量满满的“坚持很酷”去打动创业者乃至上班族的心。在钉钉这波战役里，每个坚持为自己工作的人，都能找到自己的影子。</span><br></p><h3 style=\\\"text-align: center;\\\"><br></h3><h2 style=\\\"text-align: center;\\\"><span style=\\\"color: rgb(255, 51, 102);\\\">第一波内容：创业很苦</span></h2><p style=\\\"text-align: center;\\\">“感觉自己这次会成功，这种感觉已经是第六次”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953118220416.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"442\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“亏了钱，失去了健康”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953131274657.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" style=\\\"font-size: 14px;\\\" width=\\\"640\\\" height=\\\"433\\\" border=\\\"0\\\" vspace=\\\"0\\\"></p><p style=\\\"text-align: center;\\\">“怕配不上曾经的梦想，也怕辜负了遭受的苦难”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953144567821.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"470\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“陪聊 陪酒 陪笑 赔本”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953164240198.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"443\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“28岁，头发白了一半”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953264739807.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"443\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“刚来三天的新同事提离职，理由是他也决定去创业”<img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953278559933.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" style=\\\"font-size: 14px;\\\" width=\\\"640\\\" height=\\\"447\\\" border=\\\"0\\\" vspace=\\\"0\\\"></p><p style=\\\"text-align: center;\\\">“为了想做的事，去做不想做的事”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953298366422.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"480\\\" border=\\\"0\\\" vspace=\\\"0\\\"></p><p style=\\\"text-align: center;\\\">“在车里哭完，笑着走进办公室”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953311585127.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"412\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"><br></p><h3><span style=\\\"color: rgb(255, 51, 102);\\\"><br></span></h3><h2 style=\\\"text-align: center;\\\"><span style=\\\"color: rgb(255, 51, 102);\\\">第二波内容：坚持很酷</span></h2><p style=\\\"text-align: center;\\\">“玻璃心，磨成了钻石心”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953879664690.png\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"325\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“解决用户的不快乐，让我快乐”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953892986402.png\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"345\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“公司终于出现一年以上的老员工”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953913161563.png\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"343\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“试别人不敢试的噩梦，造别人不敢造的美梦”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953924679710.png\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"353\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“只怕一生碌碌无为”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953941656021.png\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"323\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“曾经仰望的行业巨头，现在成了竞争对手”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953954721824.png\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"322\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“固执的坚持很傻么？未必。<br><span style=\\\"font-size: 14px;\\\">我听说傻人有傻福”<br></span><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953978188053.png\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"410\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“在这里，血液流动得更快”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499953991883886.png\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"328\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p><p style=\\\"text-align: center;\\\">“创业很苦，坚持很酷”<br><img src=\\\"http://file.digitaling.com/eImg/uimages/20170713/1499954576547378.jpg\\\" title=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" alt=\\\"直戳创业者心窝的钉钉，告诉你什么样的坚持才是酷\\\" width=\\\"640\\\" height=\\\"480\\\" border=\\\"0\\\" vspace=\\\"0\\\" style=\\\"font-size: 14px;\\\"></p>\\r\\n                        </div>\"}"
     println(replaceContext(jsonStr))
  }
}
