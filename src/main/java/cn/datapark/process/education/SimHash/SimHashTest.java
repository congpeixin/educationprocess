package cn.datapark.process.education.SimHash;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Created by cluster on 2017/6/13.
 */
public class SimHashTest implements Serializable {

    private static final Logger LOG = Logger.getLogger(SimHashTest.class);
    private static String simURL = null;
//    public static void main(String[] args){
//
//        //与已经抓取的文章对比,判断是否有相似文章,as为当前抓取的文章，simURL为库中存在相似文章的url
//        String jsonStr = "{\n" +
//                "  \"site_name\": \"toodaylab\",\n" +
//                "  \"post_title\": \"每日一图：百胜中国独立上市，中国最大的餐饮上市公司诞生\",\n" +
//                "  \"post_url\": \"http://www.toodaylab.com/73250\",\n" +
//                "  \"content_text\": \"今天（11 月 1 日），百胜中国以独立公司身份正式登陆纽约证交所进行交易，股票代码“YUMC”。这是百胜集团分拆计划的结果，而分拆出的百胜中国成为百胜餐饮集团在中国内地的特许经营商，拥有肯德基、必胜客和塔可钟（Taco Bell）三大品牌的独家经营权——之前在 2003 年塔可钟曾进入过中国市场，店铺主要开在上海和深圳，而在百胜中国“独立”之后，把塔可钟重新引入中国市场是今年接下来百胜中国要重点开展的工作。说回到这次纽约证交所敲钟。百胜中国这次独立上市有着更重要的意义，这意味着中国最大的餐饮上市公司已经全新亮相，公司覆盖全国 1100 多座城市、拥有 7300 多家餐厅和多达 40 多万名员工，而除了三大品牌的独家经营权，百胜中国还拥有东方既白、小肥羊等品牌，并且公司方面规划，上市后品牌下属餐厅数量还“预计将增加到目前的三倍”，这会是非常惊人的增长幅度。本文图片来自 CBN。\",\n" +
//                "  \"crawl_time\": 1496916120,\n" +
//                "  \"_id\": \"http://www.toodaylab.com/73250\",\n" +
//                "  \"type\": \"commerce\",\n" +
//                "  \"content_html\": \"<di0v class=\\\"post-content\\\">\\n                    <p><img src=\\\"http://files.toodaylab.com/2016/11/yumc_20161101221827_00.jpg\\\"></p><p>今天（11 月 1 日），百胜中国以独立公司身份正式登陆纽约证交所进行交易，股票代码“YUMC”。这是百胜集团分拆计划的结果，而分拆出的百胜中国成为百胜餐饮集团在中国内地的特许经营商，拥有肯德基、必胜客和塔可钟（Taco Bell）三大品牌的独家经营权——之前在 2003 年塔可钟曾进入过中国市场，店铺主要开在上海和深圳，而在百胜中国“独立”之后，把塔可钟重新引入中国市场是今年接下来百胜中国要重点开展的工作。</p><p>说回到这次纽约证交所敲钟。百胜中国这次独立上市有着更重要的意义，这意味着中国最大的餐饮上市公司已经全新亮相，公司覆盖全国 1100 多座城市、拥有 7300 多家餐厅和多达 40 多万名员工，而除了三大品牌的独家经营权，百胜中国还拥有东方既白、小肥羊等品牌，并且公司方面规划，上市后品牌下属餐厅数量还“预计将增加到目前的三倍”，这会是非常惊人的增长幅度。<br></p><p><strong>本文图片来自 </strong><a href=\\\"http://www.chinabusinessnews.com/2561-yum-brands-is-looking-for-partner-in-china-heres-why/\\\" target=\\\"_blank\\\"><strong>CBN</strong></a><strong>。</strong><br></p>\\n                </div>\"\n" +
//                "}";
//        JSONObject json = new JSONObject(jsonStr);
//        simURL = checkSimilarArticle(json);
//    }


    /**
     * 与已经抓取的文章对比,判断是否有相似文章
     *
     * @param json 当前抓取的文章
     * @return 已经抓取入库的相似文章url
     */
    public String checkSimilarArticle(JSONObject json) {
        try {
            if (ConfigUtil.getConfigInstance().getSimilarAlgorithm() == 1) {
//                return checkSimilarArticlebyES(json);
                return null;
            } else if (ConfigUtil.getConfigInstance().getSimilarAlgorithm() == 2) {
                // 采用simhash 去重文章
                return checkSimilarArticlebySimHash(json);
            } else {
//                LOG.error("check similar algorithm type "+ConfigUtil.getConfigInstance().getSimilarAlgorithm()+" not support now");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 用simhash去重
     *
     * @param json
     * @return
     */
    public String checkSimilarArticlebySimHash(JSONObject json) {
        long Start = System.currentTimeMillis();
        String simhashURL = "";
        String content = "";
        try {
//            ConfigUtil.initConfig(ArticleContentExtractBolt.class.getClassLoader().getResourceAsStream(ConfigUtil.topoConfigfile));
            ArticleExtractTopoConfig topoConfig = ConfigUtil.getConfigInstance();
            simhashURL = topoConfig.getSimhashServerAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json.has("content_text") && json.get("content_text")!= null){
             content = json.get("content_text").toString().replaceAll("[\\x{10000}-\\x{10FFFF}]", "").replaceFirst("更多专业报道.*","").replaceFirst(".*本文.*转载.*?[。]","").replaceFirst("除非注明.*","");
        }else if (json.has("conference_time") && json.get("post_title")!= null){
            content = json.get("post_title").toString().replace(" ","").replaceAll ("\\\\r\\\\n", "");
        }

        // 内容IK分词 算TF-IDF
        Map<String, String> wordsTFIDFValue = TFIDFUtil.countIDF(content);
        //请求接口 处理回调
        RequestModle requestModle = new RequestModle();
        requestModle.setUrl(json.get("post_url").toString());
        requestModle.setWeight(wordsTFIDFValue.get("idf"));
        requestModle.setWords(wordsTFIDFValue.get("words"));
        String tfs = wordsTFIDFValue.get("tfs");
        try {
            long httpStart = System.currentTimeMillis();
//            String response = HttpUtil.post("http://192.168.31.6:8080/simhashServer/v1/duplicateJudge/simhash", requestModle.toString());
//            String response = HttpUtil.post("http://localhost:8080/duplicateJudge/simhash", requestModle.toString());
            String response = HttpUtil.post(simhashURL, requestModle.toString());//"{\"func\":\"simhash\",\"requestData\":{ \"url\":\"" + url + '\"' +", \"words\":\"" + words + '\"' +", \"weight\":\"" + weight + '\"' +"}}"
            long httpEnd = System.currentTimeMillis();
            JSONObject object = new JSONObject(response);
            String finger = object.getString("finger");
            String status = object.getString("status");
            String url = object.getString("url");
            if ("EXIST".equalsIgnoreCase(status)) {
                // simhash 值存在 有相近的值
                long End = System.currentTimeMillis();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                // 相似文章不保存 文章内容

                return url;
            } else {
                // Hash值无重复 插入成功
                long End = System.currentTimeMillis();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.info("Similar article  error  happen URL:" + json.get("post_url").toString());
        }
        return null;
    }

}



