package cn.datapark.process.education.reg;

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
    //    public static final Logger LOG = LoggerFactory.getLogger(Content.class);
    //文档对象
    public Document doc;
    //infoMap数据格式：key:<p>分割的每一行网页代码</p>   value:是CountInfo中每个属性的赋值
    public Map<Element, ContentDeepExtractor.CountInfo> infoMap = new HashMap();

    public CASHtmlImgConvert imgConvert = new CASHtmlImgConvert();

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

    //  文档由多个Elements和TextNodes组成
    // infomap的值，是在这个方法中添加的
    public CountInfo calculateInfo(Node node) {
        ContentDeepExtractor.CountInfo countInfo;
        String dataNode;
        int length;
        //判断是Element还是TextNode
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

    /*

     */
    public Elements getContentElement(String url) throws Exception {

        this.doc.select("script,noscript,style,iframe,br").remove();
        //infomap 在这个方法中遍历 element的child节点，来put到infomap中
        this.calculateInfo(this.doc.body());
        double maxScore = 0.0D;
        Element element = null;
        //遍历key
        Iterator iterator = this.infoMap.entrySet().iterator();

//        循环选出重要的element ？
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
        //element:所有的网页代码
        //element.childNodes 是一个ArrayList，里面的每一个元素都是一行带有<标签>的代码
//        if(element == null) {
//            throw new Exception("extraction failed");
//
//        }else {
//
//            Element element1 = new Element(Tag.valueOf("div"),"");
////            System.out.println(element1);
//            Elements element2 = getAimElement(element,element1).children();
////            System.out.println("初步过滤 :"+element2);
//            Elements elements = extractorDeeps(element2,url);
////            System.out.println("深度过滤 :"+elements);
//            return elements ;
//        }
        Element element1 = new Element(Tag.valueOf("div"),"");
        Elements element2 = getAimElement(element,element1,url).children();
        Elements elements = extractorDeeps(element2,url);
        return elements ;
    }


    public Elements extractorDeeps( Elements element2,String url){
        System.out.println(url);
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
        // eg:i=17,element2.size=22,number=5,
        if (element2.size()>=number){
            while (i<=element2.size()-1){

                if (i>=element2.size()-number){//当i数值到达element2后几行的时候，开始进行下面规则的匹配

                    size = size+1;
                    // 如果element2.get(i)存在值
                    if (element2.get(i).hasText()){
                        double boost = 0.0d;
                        // 如果element2.get(i)的文本中能匹配上括号中的关键字则进入循环体
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

                        }else {// element2.get(i)的文本中没能匹配上括号中的关键字则执行以下

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
                            }else {//eg: i=21 ，element2.size()=22 , i+2>element2.size()
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
        }
        return element2;
    }

    public Element getAimElement(Element element,Element resElement,String url){
        StringBuffer childStrb = new StringBuffer();
        if (element!=null){
            Elements node = element.children();
            for (int i =0;i<node.size();i++){
                if (node.get(i).hasText()){
                    if (node.get(i).childNodeSize()>1){
//                        String line= node.get(i).toString();
                        if (node.get(i).nodeName().equals("p")){
                            if(url.contains("www.adquan.com")||url.contains("iwebad.com")) {//带有<p>标签这两个网站
                                if (node.get(i).children().toString().contains("img")){//判断<p>标签中是否含有img
                                    List childList = node.get(i).childNodes();
                                    for (int j = 0; j < childList.size(); j++) {//循环带有img标签的cildList
                                        String html = childList.get(j).toString();
                                        if (html.contains("img")) {
                                            String Path = imgConvert.changeImgSrc(html, url);
                                            String SrcPath = "<img src =" + "\"" + Path + "\"" + "/>";
                                            childStrb.append(SrcPath);
                                        }else{
                                            childStrb.append(html.replaceAll("<svg.*?>|<path.*?>",""));
                                        }
                                    }
                                    resElement.append(String.valueOf(childStrb));//标签中含有img，则添加将for循环每个子节点拼接在一起的html
                                }else{//<p>标签中不含有img，则直接添加
                                    resElement.append(String.valueOf(node.get(i)).replaceAll("<svg.*?>|<path.*?>",""));
                                }
                            }else {//带有<p>标签的其他网站
                                resElement.append(String.valueOf(node.get(i)));
                            }
                        }else if(node.get(i).nodeName().equals("span")){//含有span标签，直接添加
                            resElement.append(String.valueOf(node.get(i)));
                        }else {//不带有<p>标签的其他网站
                            getAimElement(node.get(i),resElement,url);
                        }
                    }else if (node.get(i).nodeName().equals("a")&&node.get(i).hasAttr("href")){
                        resElement.append(String.valueOf(node.get(i)));

                    }else if (!(node.get(i).toString().equals("<p>&nbsp;</p>")||node.get(i).toString().equals("<p>&nbsp;&nbsp;</p>"))){
                        resElement.append("<p>" + node.get(i).text() + "</p>");
                    }else {
                        resElement.append("<p>" + node.get(i).text() + "</p>");
                    }
                }else {//node.get(i)中不含text
                    if (node.get(i).childNodeSize()>1){
                        getAimElement(node.get(i),resElement,url);
                    }else {
                        Pattern pattern = Pattern.compile("(<img([^>]*)>)");
                        Matcher matcher = pattern.matcher(node.get(i).toString());
                        if (matcher.find()) {
                            //在这添加 img 的转换
                            //在这里先判断图片的问题，如果图片的链接没有显示域名，则添加上连接
                            if(url.contains("www.adquan.com")||url.contains("iwebad.com")){
                            String Path = imgConvert.changeImgSrc(matcher.group(0),url);
                            String SrcPath = "<img src ="+"\""+Path+"\""+"/>";
                            resElement.append(SrcPath);
                            }else {//图片链接完整的网站
                            resElement.append(matcher.group(0));
                            }
                        }
                    }
                }
            }
        }
        return resElement;
    }

    public static String getContent(String html,String url) throws Exception {
        //判断传进来的html数据是否是36氪的数据，如果是36氪的数据，在36氪的html数据拼接上 <div></div>
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
        String content = ContentDeepExtractor.getContent(data_html,url);
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------------------------");
    }

}
