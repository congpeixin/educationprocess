import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import java.util.*;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by datapark-2 on 2017/8/9.
 */
public class ContentDeepExtractor {
    public static final Logger LOG = LoggerFactory.getLogger(ContentDeepExtractor.class);
    public Document doc;
    public Map<Element, ContentDeepExtractor.CountInfo> infoMap = new HashMap();

    ContentDeepExtractor(Document doc) {
        this.doc = doc;
    }
    class CountInfo {
        int textCount = 0;
        int linkTextCount = 0;
        int tagCount = 0;
        int linkTagCount = 0;
        double density = 0.0D;
        double densitySum = 0.0D;
        int pCount = 0;
        ArrayList<Integer> leafList = new ArrayList();
        CountInfo() {
        }
    }
    public CountInfo calculateInfo(Node node) {

        ContentDeepExtractor.CountInfo countInfo;
        String dataNode;
        int length;
        if(!(node instanceof Element)) {
            if(node instanceof TextNode) {

                TextNode var7 = (TextNode)node;
                countInfo = new ContentDeepExtractor.CountInfo();
                dataNode = var7.text();
                length = dataNode.length();
                countInfo.textCount = length;
                countInfo.leafList.add(Integer.valueOf(length));
                return countInfo;
            } else {
                return new ContentDeepExtractor.CountInfo();
            }
        } else {

            Element element = (Element)node;
            countInfo = new ContentDeepExtractor.CountInfo();
            ContentDeepExtractor.CountInfo len1;
            for(Iterator text = element.childNodes().iterator(); text.hasNext(); countInfo.pCount += len1.pCount) {
                Node node1 = (Node)text.next();
                len1 = this.calculateInfo(node1);
                countInfo.textCount += len1.textCount;
                countInfo.linkTextCount += len1.linkTextCount;
                countInfo.tagCount += len1.tagCount;
                countInfo.linkTagCount += len1.linkTagCount;
                countInfo.leafList.addAll(len1.leafList);
                countInfo.densitySum += len1.density;
            }

            ++countInfo.tagCount;
            dataNode = element.tagName();
            //todo linkTextCount is <a href>
            if(dataNode.equals("a")) {
                countInfo.linkTextCount = countInfo.textCount;
                ++countInfo.linkTagCount;
            } else if(dataNode.equals("p")) {
                //todo pCount is <p>
                ++countInfo.pCount;
            }
            //todo textNode text()length - <a href>count
            length = countInfo.textCount - countInfo.linkTextCount;
            int var10 = countInfo.tagCount - countInfo.linkTagCount;
            //todo
            if(length != 0 && var10 != 0) {
                countInfo.density = ((double)length + 0.0D) / (double)var10;
            } else {
                countInfo.density = 0.0D;
            }
            this.infoMap.put(element, countInfo);
            return countInfo;
        }
    }
    public double calculateScore(Element tag) {

        CountInfo countInfo = this.infoMap.get(tag);
        double var = Math.sqrt(this.compute(countInfo.leafList) + 1.0D);
        double score = Math.log(var) * countInfo.densitySum * Math.log((double)(countInfo.textCount - countInfo.linkTextCount + 1)) * Math.log10((double)(countInfo.pCount + 2));
        return score;
    }
    public double compute(ArrayList<Integer> data) {

        if(data.size() == 0) {
            return 0.0D;
        } else if(data.size() == 1) {
            return (double)((data.get(0)).intValue() / 2);
        } else {
            double sum = 0.0D;
            Integer i;
            for(Iterator ave = data.iterator(); ave.hasNext(); sum += (double)i.intValue()) {
                i = (Integer)ave.next();
            }

            double ave1 = sum / (double)data.size();
            sum = 0.0D;

            Integer i1;
            for(Iterator iterator = data.iterator(); iterator.hasNext(); sum += ((double)i1.intValue() - ave1) * ((double)i1.intValue() - ave1)) {
                i1 = (Integer)iterator.next();
            }
            sum /= (double)data.size();
            return sum;
        }
    }
    public Elements getContentElement(String url) throws Exception {

        this.doc.select("script,noscript,style,iframe,br").remove();
        this.calculateInfo(this.doc.body());
        double maxScore = 0.0D;
        Element element = null;
        Iterator iterator = this.infoMap.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Element tag = (Element)entry.getKey();
            if(!tag.tagName().equals("a") && tag != this.doc.body()) {
                double score = this.calculateScore(tag);
                if(score > maxScore) {
                    maxScore = score;
                    element = tag;
                }
            }
        }

        if(element == null) {
            throw new Exception("extraction failed");
        }else {

            Element element1 = new Element(Tag.valueOf("div"),"");
            Elements element2 = getAimElement(element,element1).children();
            Elements elements = extractorDeeps(element2,url);
            return elements ;
        }
    }
    public Elements extractorDeeps( Elements element2,String url){


        StringBuffer doc1 = new StringBuffer();
        int i =0;
        int size =0;
        double divider = 70.0d;
        int number =0;
        java.net.URL  urls = null;
        try {
            urls = new  java.net.URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String host = urls.getHost();
        if (!("www.huxiu.com".equals(host))){
            number = 5;
        }else {
            number = 8;
        }
        while (i<=element2.size()-1){

            if (i>=element2.size()-number){

                size = size+1;
                if (element2.get(i).hasText()){
                    double boost = 0.0d;
                    if (element2.get(i).text().matches("((.*)?扫描((\\S){0,8}?)二维码(.*))|((.*)?关注((\\S){0,8}?)微信((\\S){0,5}?)公众号(.*))")
                            ||Pattern.compile("(<im([^>]*)>)?.*(<a.{0,3}href([^>]*)>)").matcher(element2.get(i).toString()).find()==true
                            ||Pattern.compile("(题图来自).{0,10}(<a.*href([^>]*)>)?").matcher(element2.get(i).toString()).find()==true){
                        int loop = element2.size()-1-i;
                        element2.remove(i);
                        while (loop > 0) {
                            element2.remove(element2.size() - 1);
                            loop--;
                        }
                        return element2;

                    }else {

                        double num = 0;
                        if (i+2<element2.size()) {
                            if (Pattern.compile("(<im([^>]*)>)?.*(<a.{0,3}href([^>]*)>)").matcher(element2.get(i+1).toString()).find()==false
                                    &&Pattern.compile("(题图来自).{0,10}(<a.*href([^>]*)>)?").matcher(element2.get(i+1).toString()).find()==false){
                                num = calculate(doc1.toString().trim(), element2.get(i).text().trim().concat(element2.get(i+1).text().trim()));

                            }else {
                                num = calculate(doc1.toString().trim(), element2.get(i).text().trim());
                            }

                            boost = size/divider;
                            if (num-boost < 0.2d) {

                                double num1 = calculate(doc1.toString().trim(), element2.get(i).text().trim()
                                        .concat(element2.get(i + 1).text().trim()).concat(element2.get(i+2).text().trim()));
                                if (!(num1-num>0.1d||num1>0.2d
                                        ||(Pattern.compile("(<img([^>]*)>)").matcher(element2.get(i+1).toString().concat(element2.get(i+2).toString())).find()==true))){
                                    int count = element2.size() - 1 - i;
                                    element2.remove(i);
                                    if (element2.get(i-1).hasText()==false){
                                        element2.remove(i-1);
                                    }
                                    while (count > 0) {
                                        element2.remove(element2.size() - 1);
                                        count--;
                                    }
                                    return element2;
                                }
                            }
                        }else {
                            divider=110.0d;
                            if (Pattern.compile("(<im([^>]*)>)?.*(<a.{0,3}href([^>]*)>)").matcher(element2.get(element2.size()-1).toString()).find()==false
                                    &&Pattern.compile("(题图来自).{0,10}(<a.*href([^>]*)>)?").matcher(element2.get(i-1).toString()).find()==false){
                                num = calculate(doc1.toString().trim(), element2.get(i).text().trim().concat(element2.get(element2.size()-1).text()));
                            }else {
                                num = calculate(doc1.toString().trim(), element2.get(i).text().trim());
                            }
                            boost = size/divider;
                            if (num-boost<0.2d){
                                int count = element2.size()-1-i;
                                element2.remove(i);
                                while (count>0){
                                    element2.remove(element2.size()-1);
                                    count--;
                                }
                                return element2;
                            }
                        }
                    }

                }else {
                    if (i==element2.size()-1){
                        element2.remove(i);
                        return element2;
                    }
                }
            }
            doc1.append(element2.get(i).text().trim());
            i++;
        }
        return element2;
    }

    public Element getAimElement(Element element,Element resElement){

        if (element!=null){
            Elements node = element.children();
            for (int i =0;i<node.size();i++){
                if (node.get(i).hasText()){

                    if (node.get(i).childNodeSize()>1){
                        if (node.get(i).nodeName().equals("p")){
                            resElement.append(String.valueOf(node.get(i)));
                        }else {
                            getAimElement(node.get(i),resElement);
                        }
                    }else if (node.get(i).nodeName().equals("a")&&node.get(i).hasAttr("href")){
                        resElement.append(String.valueOf(node.get(i)));

                    }else if (!(node.get(i).toString().equals("<p>&nbsp;</p>")||node.get(i).toString().equals("<p>&nbsp;&nbsp;</p>"))){
                        resElement.append("<p>" + node.get(i).text() + "</p>");
                    }else {
                        resElement.append("<p>" + node.get(i).text() + "</p>");
                    }
                }else {
                    if (node.get(i).childNodeSize()>1){
                        getAimElement(node.get(i),resElement);
                    }else {
                        Pattern pattern = Pattern.compile("(<img([^>]*)>)");
                        Matcher matcher = pattern.matcher(node.get(i).toString());
                        if (matcher.find()) {
                            resElement.append(matcher.group(0));
                        }
                    }
                }
            }
        }
        return resElement;
    }

    public static String getContent(String html,String url) throws Exception {

        if (url.contains("36kr")){
            html = "<div>"+html+"</div>";
            //在处理一个html字符串。我们可能需要对其进行解析，并提取其内容，或校验其格式是否完整，格式不完整会将数据的html代码补齐
            Document doc = Jsoup.parse(html);
            ContentDeepExtractor content = new ContentDeepExtractor(doc);
//            System.out.println("******************"+"36氪数据"+url+"********************");
            return String.valueOf(content.getContentElement(url));
        }
        Document doc = Jsoup.parse(html);
        ContentDeepExtractor content = new ContentDeepExtractor(doc);
//        System.out.println("******************"+"不是36氪数据"+url+"********************");
        return String.valueOf(content.getContentElement(url));
    }
    public static double calculate(String source, String dest){

        Map<String, int[]> wordAppearTimes = new HashMap<String, int[]>();

        List sList = strTokes(source);
        int sourceLen = sList.size();
        for(int i=0; i<sourceLen; ++i){
            if(wordAppearTimes.containsKey(sList.get(i)+"")){
                ++(wordAppearTimes.get(sList.get(i)+"")[0]);
            }else{
                int[] appearTimes = new int[2];
                appearTimes[0] = 1;
                appearTimes[1] = 0;
                wordAppearTimes.put(sList.get(i)+"", appearTimes);
            }
        }

        List dList = strTokes(dest);
        int destLen = dList.size();
        for(int i=0; i<destLen; ++i){
            if(wordAppearTimes.containsKey(dList.get(i)+"")){
                ++(wordAppearTimes.get(dList.get(i)+"")[1]);
            }else{
                int[] appearTimes = new int[2];
                appearTimes[0] = 0;
                appearTimes[1] = 1;
                wordAppearTimes.put(dList.get(i)+"", appearTimes);
            }
        }
        double sourceScore = 0.00;
        double destScore = 0.00;
        double product = 0.00;
        for(Map.Entry<String, int[]> entry : wordAppearTimes.entrySet()){
            sourceScore += entry.getValue()[0] * entry.getValue()[0];
            destScore += entry.getValue()[1] * entry.getValue()[1];
            product += entry.getValue()[0] * entry.getValue()[1];
        }
        sourceScore = Math.sqrt(sourceScore);
        destScore = Math.sqrt(destScore);
        return product / (sourceScore * destScore);
    }
    public static List strTokes(String str){

        IKAnalyzer analyzer = new IKAnalyzer(true);
        StringReader reader = new StringReader(str);

        List list = new ArrayList();
        try {
            TokenStream ts = analyzer.tokenStream("", reader);
            CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
            ts.reset();
            while(ts.incrementToken()){
                list.add(term.toString());
            }
            analyzer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;

    }
    public static void main(String[] args) throws Exception {

        String jsonstr = "{\"site_name\":\"广告门\",\"post_title\":\"吴亦凡成为I.T集团首位代言人\",\"crawl_time\":1502856402,\"post_url\":\"http://www.adquan.com/post-2-41834.html\",\"module\":\"brand\",\"content_text\":\"吴亦凡 I.T 2017-08-15 19:182287吴亦凡成为I.T集团首位代言人house                                0                               今日零时，香港最具规模的时装品牌零售店之一I.T通过官微宣布了吴亦凡成为其集团代言人的消息，同时公布了吴亦凡为之拍摄的TVC。这也是I.T集团的首位代言人，不过早在去年，吴亦凡就曾担任过I.T首席设计顾问。而近期吴亦凡在《中国有嘻哈》的圈粉表现与时尚装扮也十分符合I.T的品牌形象，更具备带货潜力。                                                                                        点赞                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             本周新增职位数：53个                                                                            ☆ 全国招聘服务，请致电 010-85887939                                                                                                                                                                                                                                                                                           信息已提交:            我们会在1-3个工作日内审核完毕，并用邮件通知您，请耐心等待，谢谢\",\"_id\":\"http://www.adquan.com/post-2-41834.html\",\"type\":\"commerce\",\"content_html\":\"<div><body id=\\\"readabilityBody\\\">\\n\\t\\n\\t\\n\\t\\n\\t\\n\\n\\n\\n\\t<div id=\\\"main\\\">\\n\\t<div class=\\\"main_content clearfix main_content2\\\">\\n\\t\\t<div class=\\\"m_con_left\\\">\\n\\t\\t\\t\\n\\t\\t\\t\\t<div class=\\\"txt_box\\\">\\n\\t\\t\\t\\t<p class=\\\"t_label\\\">\\n\\t\\t\\t\\t\\t<a href=\\\"/tag/吴亦凡\\\" target=\\\"_blank\\\">吴亦凡<\\/a> <a href=\\\"/tag/I.T\\\" target=\\\"_blank\\\">I.T<\\/a> \\t\\t\\t\\t<\\/p>\\n\\t\\t\\t\\t\\n\\t\\t\\t\\t<p class=\\\"text_time2\\\">\\n\\t\\t\\t\\t\\t<span>2017-08-15 19:18<\\/span>\\n\\t\\t\\t\\t\\t<span><svg t=\\\"1500022513563\\\" class=\\\"icon\\\" viewbox=\\\"0 0 1024 1024\\\" version=\\\"1.1\\\" xmlns=\\\"http://www.w3.org/2000/svg\\\" p-id=\\\"2097\\\" xmlns:xlink=\\\"http://www.w3.org/1999/xlink\\\"><defs/><path d=\\\"M1003.52 550.912c0 30.72-12.288 55.296-28.672 69.632v2.048c-88.064 98.304-247.808 266.24-462.848 266.24-215.04 0-372.736-165.888-462.848-266.24v-2.048c-16.384-14.336-28.672-40.96-28.672-69.632 0-30.72 12.288-55.296 28.672-69.632v-2.048c88.064-98.304 247.808-266.24 462.848-266.24s372.736 165.888 462.848 266.24v2.048c16.384 12.288 28.672 38.912 28.672 69.632zM512 335.872c-124.928 0-225.28 100.352-225.28 225.28s98.304 225.28 225.28 225.28 225.28-102.4 225.28-225.28c0-124.928-102.4-225.28-225.28-225.28z m4.096 346.112c-75.776 0-137.216-59.392-137.216-133.12s61.44-133.12 137.216-133.12h6.144c-6.144 10.24-8.192 22.528-8.192 34.816 0 43.008 34.816 75.776 77.824 75.776 20.48 0 38.912-8.192 53.248-20.48 4.096 14.336 8.192 28.672 8.192 43.008 0 73.728-61.44 133.12-137.216 133.12z\\\" fill=\\\"#ababab\\\" p-id=\\\"2098\\\"/><\\/svg><\\/span>\\n\\t\\t<span>2287<\\/span>\\n\\t\\t\\t\\t<\\/p>\\n\\t\\t\\t\\t\\n\\t\\t\\t\\t<h2 class=\\\"text_title\\\">吴亦凡成为I.T集团首位代言人<\\/h2>\\n\\t\\t\\t\\t<div class=\\\"text_time\\\">\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t<div class=\\\"user_msg\\\">\\n\\t\\t\\t\\t\\t\\t<img src=\\\"/images/ggm_logo_1128.png\\\" class=\\\"user_img\\\"/>\\n\\t\\t\\t\\t\\t\\t<span>house<\\/span>\\n\\t\\t\\t\\t\\t<\\/div>\\n\\t\\t\\t\\t\\t<div class=\\\"share_ww\\\">\\n\\t\\t\\t\\t\\t\\t<span data-toggle=\\\"modal\\\" data-target=\\\"#myModal\\\"><img class=\\\"weixin_ico\\\" src=\\\"images/wx_031.png\\\"/><\\/span>\\n\\t\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t\\t\\n\\t\\t                \\t\\t                <span class=\\\"look_num2\\\" id=\\\"favourite\\\">0<\\/span>\\n\\t\\t                \\n\\t               <\\/div>\\n\\t\\t\\t\\t<\\/div>\\n\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t\\t<p class=\\\"hr_line\\\"/>\\n\\t\\t\\t\\t<div class=\\\"con_Text\\\">\\n\\t\\t\\t\\t\\t<span>今日零时，香港最具规模的时装品牌零售店之一I.T通过官微宣布了吴亦凡成为其集团代言人的消息，同时公布了吴亦凡为之拍摄的TVC。这也是I.T集团的首位代言人，不过早在去年，吴亦凡就曾担任过I.T首席设计顾问。而近期吴亦凡在《中国有嘻哈》的圈粉表现与时尚装扮也十分符合I.T的品牌形象，更具备带货潜力。<\\/span>&#13;\\n&#13;\\n\\t\\t\\t\\t<\\/div>\\n\\t\\t\\t\\t<div class=\\\"bigzan_erweima\\\">\\n\\t                    \\n\\t                    \\t                    <div class=\\\"box_02\\\" id=\\\"dianzan\\\" data-flag=\\\"0\\\" data-postid=\\\"41834\\\" data-zannum=\\\"2\\\">\\n\\t                    \\t\\t\\t\\t\\t\\t        \\t<img src=\\\"images/dianzan_icon.png\\\"/><span>点赞<\\/span>\\n\\t\\t\\t\\t\\t        \\t                    <\\/div>\\n\\t                    <p class=\\\"clear\\\"/>\\n\\t            <\\/div>\\n\\t\\n\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\n\\t\\n\\t\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/div>\\n\\t\\n\\t\\t\\t\\t\\t\\t<p>\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t<\\/p>\\n\\t\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t<\\/div>\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t<div class=\\\"post_right\\\">\\n\\t\\t\\t\\t\\t\\t <div class=\\\"work_list_right\\\">\\n                \\n                \\n                \\n                \\n                \\n                \\n                \\n                                \\n                \\n                \\n                                \\n                               \\n                \\n                \\n                \\n                \\n                \\n                \\n\\n                \\n                <div class=\\\"w_l_inner w_l_inner2\\\">             \\n                    \\n                    \\n            \\n                    \\n                    \\n                    <div class=\\\"new_Recruitment\\\">\\n                        <p>\\n                            本周新增职位数：53个\\n                        <\\/p>\\n                        <p>\\n                            ☆ 全国招聘服务，请致电 010-85887939\\n                        <\\/p>\\n                    <\\/div>\\n                <\\/div>\\n                                \\n                \\n            <\\/div>\\n            <p class=\\\"clear\\\"/>\\n\\t\\t        <\\/div>\\n        \\n        \\n    <\\/div>\\n\\n\\n    \\n\\t\\t\\t\\t\\t<\\/div>\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t\\n\\t\\t\\t\\n\\t\\t\\t\\n\\t\\t\\t \\n\\t\\t\\t\\n\\t\\n\\t<a href=\\\"http://jintong.adquan.com/login\\\" target=\\\"_blank\\\">\\n\\t<\\/a>\\n\\t\\n\\n\\t\\n\\t\\n\\n\\t\\n\\n\\t\\n\\t\\n\\n\\t<p class=\\\"UserFeedback_Mask\\\"/>\\n\\t\\t\\n\\t\\t\\n\\t\\n\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n    <p class=\\\"shadow_login\\\"/>\\n\\n    \\n    \\n    \\n    \\n    \\n    \\n    \\n    \\n    \\n    \\n        \\n    \\n    \\n     \\n        \\n    \\n    \\n    \\n    \\n\\n     \\n    <div id=\\\"shensu02\\\" class=\\\"login2_box\\\">\\n        <div class=\\\"message message02\\\">\\n            <p>信息已提交:<\\/p>\\n            <p>我们会在1-3个工作日内审核完毕，并用<br/>邮件通知您，请耐心等待，谢谢<\\/p>\\n        <\\/div>\\n        \\n    <\\/div>\\n   \\n<\\/body>\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t\\n\\t \\t\\n\\t\\n\\n\\n\\n\\n\\t\\n\\n\\t\\n\\n     \\n    \\n    <\\/div>\"}";

        String data_html = new JSONObject(jsonstr).getString("content_html");

        String url = new JSONObject(jsonstr).getString("post_url");

//        String url = "http://www.tmtpost.com/2729781.html";
//        String data_html = "<p>编者按：如何增强团队的凝聚力，这是很多管理者非常头痛的问题。事实上，增强凝聚力的最佳途径并非天天晨会洗脑或者慷慨画饼，给员工一些实打实的小福利，比如免费午餐，效果会出乎意料的好。本文编译自Hoteltonight公司CEO Sam Shank所写原题为\\u201cHey tech CEOs, want better collaboration? Offer your team free lunch\\u201d的文章，看看他是如何利用免费午餐提高团队凝聚力的吧。<br/><\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15020102/e2zhrdxat1bzm9f1.png!heading\\\" data-img-size-val=\\\"730,382\\\"/\\\\><\\/p><p   class=\\\"img-desc\\\">HotelTonight是一款移动旅行app，发行于2010年，用户总数达到一千五百万。<\\/p><p   class=\\\"img-desc\\\">HotelTonight帮助用户预定酒店，早至入住前七天，服务范围是南美、北美、欧洲、中东、非洲和澳洲。<\\/p><p   class=\\\"img-desc\\\"><br/><\\/p><p>几周前，在和其他科技初创企业的高管共享晚餐时，我们谈到如何培养优秀团队文化。大家纷纷提出很多高大上的点子（比如HotelTonight的轮盘赌游戏，我们会给随机抽中的团队成人一个旅行机会\\u2014\\u2014去哪儿都行、费用全包、说走就走）但是，在讨论的时候我们很快就有了个\\u201c少一点花费，多一点影响\\u201d的点子\\u2014\\u2014公司免费午餐。<\\/p><p>也不是所有人都能看到免费午餐带来的好处。甚至有一家公司CFO觉得很疑惑。他一直在纠结花费问题，也不知道提供午餐到底值不值得。我很能理解他:我们公司同样也紧盯着自己的花销，花每一块钱都思前想后。然而,他听完我的讲述，听到免费午餐在自己公司的影响\\u2014\\u2014留住员工、增进友谊和加强合作\\u2014\\u2014他便渐渐明白了。<\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15020236/aw2uqrcrkg1b34bz.jpg!heading\\\" data-img-size-val=\\\"421,243\\\" width=\\\"421\\\"/\\\\><\\/p><h3  ><strong>免费午餐的好处很多，我想强调其中一点: 合作。<\\/strong><\\/h3><p>免费午餐在硅谷已经是\\u201c家常便饭\\u201d了，在初创企业提供的福利中都涵盖这一项。但在硅谷以外，免费的午餐就像金色的锁链，是绑住员工，让他们留在公司的绝佳理由。<\\/p><p>当然这不是HotelTonight提供免费午餐的原因。事实上，我们没有把免费午餐当作传统意义上的\\u201c福利\\u201c。有午餐当然是好的，谁不喜欢免费食物,节省时间金钱呢？更为重要的共享午餐的过程和结果。<\\/p><p>HotelTonight团队工作异常努力。他们是我见过的最聪明最勤奋的一群人，我很荣幸跟他们共事。几年前我发现，他们工作专心致志，没日没夜，根本不离桌子半步。当时我担心团队成员相互疏离，工作筋疲力尽。藉由团体午餐，成员可以暂时离开座位，跟同事共同享用美餐（多亏了我们的厨艺超神的Sandra）<\\/p><p>也正因为我们身处酒店行业，在HT总部为团队和访客创造一片惬意舒适的空间，对我而言一直都很重要。所以，我们在设计办公室时，采取了我们合作的精品酒店的最佳元素，比如沙发，小角落还有公用工作空间。<\\/p><p>午休时，长长的午餐桌是一座桥，美食在这头，欢声笑语在那头。<\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15020504/wul4rvl84409mv4r.jpg!heading\\\" data-img-size-val=\\\"636,423\\\" width=\\\"636\\\"/\\\\><\\/p><h3  ><strong>铸造更深刻的连结\\u2014\\u2014企业文化和同事友谊<\\/strong><\\/h3><p>公用空间餐桌上深刻的交流、合作是我亲眼目睹，难以忘怀的，我也深受鼓舞。我看到不同团队的成员并肩而坐,相互交流，不管是周末闲趣，近期旅程还是手头工作，若不是公用空间，他们也不会有机会相互接触。<\\/p><p>谈话之后，有人受同事旅程启发，自己踏上心灵之旅；不同团队成员组队编程马拉松，一起头脑风暴，想办法解决长期难题。这是种催生友情的交流，让队友结伴参加马拉松；让来自不同团队的人组队去开创新公司,共同创造美好的新事物。我曾读到很多人的体会，都提到跟朋友共识可以提高生活质量,我也确实相信，因为我自己就有切身体会。<\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15022600/1evyjdpn5h326oq6.jpg!heading\\\" data-img-size-val=\\\"700,467\\\" width=\\\"700\\\"/\\\\><\\/p><p>&nbsp;举个例子，我们的战略伙伴团队的同事Donnie有一次告诉我：<\\/p><blockquote><p>\\u201c在午餐桌上发展的不只是友谊，跟其他人的谈话也对个人发展大有裨益。午餐时和财会 组的同事聊天，就让我增加了对公司处理税务的了解,我就知道如何更好的对外谈判。 这就像午餐课堂，一边上国际税务法入门课,一边吃沙拉。我也曾经从其他部门同事那里得 到公司市场交流活动地点绝佳的建议。我们的合作伙伴通常都十分赞叹我的选址，总以为我是本地人。\\u201c<\\/p><\\/blockquote><p>旅行带来的最大乐趣就是旅途中所认识的人，跟生活经历迥乎不同的人来一场随兴谈话，会让你大有收获。我当然是非常赞成旅行的，但我相信大家不总是想要到远方去，建立起将一些关系。跟同事一起享用午餐,了解他们的背景，互相鼓舞，有时结果会令人意想不到,不管是从私人角度还是工作角度。这也是为什么HotelTonight为员工提供免费午餐。<\\/p><p>&nbsp;<\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15022600/fd5ujhe7limdm75z.jpg!heading\\\" data-img-size-val=\\\"673,337\\\" width=\\\"673\\\"/\\\\><\\/p><p>以上是HotelTonight CEO／联合创始人Sam Shank的观点, 他本人最喜欢的是烧烤日午餐。<\\/p><p>原文链接：<a href=\\\"https://www.tnooz.com/article/hey-tech-ceos-want-better-collaboration-offer-your-team-free-lunch/\\\" _src=\\\"https://www.tnooz.com/article/hey-tech-ceos-want-better-collaboration-offer-your-team-free-lunch/\\\">https://www.tnooz.com/article/hey-tech-ceos-want-better-collaboration-offer-your-team-free-lunch/<\\/a> <\\/p><p>编译组出品。编辑：郝鹏程<\\/p>";
        String content = ContentDeepExtractor.getContent(data_html,url);
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------------------------");
    }

}
