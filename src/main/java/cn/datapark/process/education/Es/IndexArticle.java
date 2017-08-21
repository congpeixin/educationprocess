package cn.datapark.process.education.Es;


import cn.datapark.process.education.Summary.NewsSummary;
import com.hankcs.hanlp.HanLP;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by datapark-2 on 2017/7/25.
 *
 * 将kafka传来的数据也存一份给ES
 *
 */
public class IndexArticle implements Serializable {

    private NewsSummary summary = new NewsSummary();

    public void storageArticle(JSONObject jsonObj){

        List<String> keywordList = HanLP.extractKeyword(jsonObj.get("post_title").toString().replace(" ","")+jsonObj.getString("content_text"), 4);
        Map articleInfoMap = new HashMap();
        String title = jsonObj.get("post_title").toString().replace(" ","");// 标题
        String content =jsonObj.getString("content_text");//内容
        String time = ChangeTime(new Long(jsonObj.getInt("crawl_time")));//时间  格式yyyy-MM-dd
        String category = jsonObj.get("module").toString();
        String[] keyWord = keywordList.toArray(new String[keywordList.size()]);// 关键字
//        String core = summary.summarize(jsonObj.getString("content_text"));//核心提示
        String origin = jsonObj.get("site_name").toString();
        String id = Base64.getUrlEncoder().encodeToString(title.getBytes());
        articleInfoMap.put("title",title);//文章标题
        articleInfoMap.put("content",content);//文章内容
        articleInfoMap.put("time",time);//抓取事件
        articleInfoMap.put("keyWord",keyWord);//关键词
        articleInfoMap.put("origin",origin);//文章出处（网站）
        articleInfoMap.put("category",category);//文章出处（网站）
        boolean isCreated = false;
        IndexResponse response = null;
        XContentBuilder doc = null;
        try {
            doc = jsonBuilder()
                    .startObject()
                    .field("article_info", articleInfoMap)
                    .endObject();

            response = EsUtil.getInstance().getESInstance().prepareIndex("dpdata", "article", id)
                    .setSource(doc).execute().actionGet();
            isCreated = response.isCreated();
            if (isCreated == true) {
                System.out.println("index success"+id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String ChangeTime(Long timep){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timep))*1000L));   // 时间戳转换成时间
        return sd;
    }


    public static void main(String[]args){
//        String time = new IndexArticle().ChangeTime(new Long(1500946485));
//        System.out.print(time);
//        String title = "天猫为何都要开始做综艺节目了？";
//        String id = Base64.getUrlEncoder().encodeToString(title.getBytes());
//
//        System.out.println(id);
//        System.out.println(new String(Base64.getUrlDecoder().
//                decode(id)));
        NewsSummary summary = new NewsSummary();
        String content = "本文授权转载ACGdoge。伴随着智能手机的发展，国内的地铁、" +
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
                "更多专业报道，请点击下载“界面新闻”APP0,公益广告 广告 除非注明，否则本站文章均为原创翻译，转载请注明出处，并标注本文链接：http://iwebad.com/video/2993.html";
//        String core = content.replaceFirst(".*本文.*转载.*?[。]","").replaceFirst("除非注明.*","");
////        String core = summary.summarize(content.replaceFirst(".*本文.*转载.*?[。]","").replaceFirst("除非注明.*",""), "MMR");

//        System.out.print(core);


    }
}
