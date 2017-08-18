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
        Elements element2 = getAimElement(element,element1).children();
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
                //将element2中的元素追加到doc1中
                //在这里先判断图片的问题，如果图片的链接没有显示域名，则添加上连接(字符串拼接？)，如果有，则直接追加上
                doc1.append(element2.get(i).text().trim());
                i++;
            }
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
        //判断传进来的html数据是否是36氪的数据，如果是36氪的数据，在36氪的html数据拼接上 <div></div>
        if (url.contains("36kr")){
             html = "<div>"+html+"</div>";
            //在处理一个html字符串。我们可能需要对其进行解析，并提取其内容，或校验其格式是否完整，格式不完整会将数据的html代码补齐
            Document doc = Jsoup.parse(html);
            ContentDeepExtractor content = new ContentDeepExtractor(doc);
            System.out.println("******************"+"36氪数据"+url+"********************");
            return String.valueOf(content.getContentElement(url));
        }
        Document doc = Jsoup.parse(html);
        ContentDeepExtractor content = new ContentDeepExtractor(doc);
        System.out.println("******************"+"不是36氪数据"+url+"********************");
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

//        {"site_name":"数英网","post_title":"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋","crawl_time":1502856382,"post_url":"http://www.digitaling.com/projects/22838.html","module":"brand","content_text":"嘻哈在过去五年之内从没有如此受到品牌主尤其是中国品牌主的青睐。最近受到《中国有嘻哈》这档综艺的影响，支付宝、麦当劳、梅赛德斯奔驰等等一系列以嘻哈为元素的广告密集发布，不管你到底了不了解什么是嘻哈，至少能够说得出flow、freestyle、hook这样的关键字眼好像就可以装一把hiphop了。远在大洋那一端的google不知道是否也看到嘻哈风潮在亚洲（至少是日韩）正hit，为庆祝嘻哈音乐诞生44周年，8月12日，google doodle 制作了一支视频来回顾嘻哈文化的44年。无字幕版这一支视频主要是为了庆祝嘻哈音乐中最重要的发明之一：break。1973年8月11日，一个叫 Kool Herk 的 DJ 在纽约北部布朗克斯区举办了一场派对，就在这个派对上，一种新的音乐诞生了。DJ 使用了两台黑胶唱盘，重复播放唱片中最精彩的 break 桥段，让人们有更长的时间可以尽情跳舞，而这种舞蹈也就是现在人们所说的breaking（霹雳舞）；后来又出现了MC这种职业，MC本意是活动主持人，他们会随着音乐节奏说唱，带动现场气氛。而在这个重要的纪念日，谷歌邀请网友担任DJ和大家一起庆祝。除了涂鸦风格的科普短片之外Google还特别上线了可以让用户自己做DJ的功能玩法谷歌准备了两台唱片机和一整盒热门唱片大家可以挑选经典黑胶唱片通过混音滑杆进行互动喜欢嘻哈的朋友们可以试着玩一玩 DJ 打碟扫描二维码，立即体验！（能不能打开就看有没有靠谱VPN了）","_id":"http://www.digitaling.com/projects/22838.html","type":"commerce","content_html":"<div><div class=\"article_con mg_b_50\" id=\"article_con\">&#13;\n                            <p/><p>嘻哈在过去五年之内从没有如此受到品牌主尤其是中国品牌主的青睐。<br/><\/p><p>最近受到《中国有嘻哈》这档综艺的影响，支付宝、麦当劳、梅赛德斯奔驰等等一系列以嘻哈为元素的广告密集发布，不管你到底了不了解什么是嘻哈，至少能够说得出flow、freestyle、hook这样的关键字眼好像就可以装一把hiphop了。<\/p><p>远在大洋那一端的google不知道是否也看到嘻哈风潮在亚洲（至少是日韩）正hit，为庆祝嘻哈音乐诞生44周年，8月12日，google doodle 制作了一支视频来回顾嘻哈文化的44年。<\/p><p><br/><span>无字幕版<\/span><\/p><p>这一支视频主要是为了庆祝嘻哈音乐中最重要的发明之一：break。1973年8月11日，一个叫 Kool Herk 的 DJ 在纽约北部布朗克斯区举办了一场派对，就在这个派对上，一种新的音乐诞生了。DJ 使用了两台黑胶唱盘，重复播放唱片中最精彩的 break 桥段，让人们有更长的时间可以尽情跳舞，而这种舞蹈也就是现在人们所说的breaking（霹雳舞）；后来又出现了MC这种职业，MC本意是活动主持人，他们会随着音乐节奏说唱，带动现场气氛。而在这个重要的纪念日，谷歌邀请网友担任DJ和大家一起庆祝。<\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170814/1502709635756526.gif\" title=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\" alt=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\" border=\"0\" vspace=\"0\"/><\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170814/1502709614121949.gif\" title=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\" border=\"0\" vspace=\"0\" alt=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\"/><\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170814/1502709613748985.gif\" title=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\" border=\"0\" vspace=\"0\" alt=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\"/><\/p><p>除了涂鸦风格的科普短片之外<\/p><p>Google还特别上线了可以让用户自己做DJ的功能玩法<\/p><p>谷歌准备了两台唱片机和一整盒热门唱片<\/p><p>大家可以挑选经典黑胶唱片通过混音滑杆进行互动<\/p><p>喜欢嘻哈的朋友们可以试着玩一玩 DJ 打碟<\/p><p><strong>扫描二维码，立即体验！<\/strong><br/><span>（能不能打开就看有没有靠谱VPN了）<\/span><\/p><p><span><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502764351538598.png\" title=\"1502764351538598.png\" alt=\"450c765cbfc4dd3b5495ccd124ade14a.png\"/><\/span><\/p><p><span/><\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502764693169377.png\" title=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\" border=\"0\" vspace=\"0\" alt=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\"/><\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502764693104382.png\" title=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\" border=\"0\" vspace=\"0\" alt=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\"/><\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502764694213759.png\" title=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\" border=\"0\" vspace=\"0\" alt=\"纪念嘻哈音乐诞生44周年，Google doodle上线新彩蛋\"/><\/p>&#13;\n                        <\/div>&#13;\n                        <\/div>"}
//        {"site_name":"数英网","post_title":"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告","crawl_time":1502856382,"post_url":"http://www.digitaling.com/projects/22803.html","module":"brand","content_text":"这是一则华为最近在国外投放的创意广告，不到最后还真猜不到是华为P10的广告。全片没有一句对白，全凭人物的表情、动作以及背景音乐的调控，营造出既有点神秘、紧张，又带点轻松、逗趣的氛围。一个美女在上班路上被四个黑衣人跟踪了而且是明目张胆的别人能看见的那种（但显然女主看不到）女主只要一掏手机，他们就自动上线放下手机又自动下线，时刻保持严阵以待的样子终于当女主准备自拍时，他们露出了真面目：化妆师、造型师、灯光师、导演，为女主秒秒钟搞定一张美美哒自拍。没错，这支广告就是主打华为P10的人像摄影功能，让每一张自拍照都像封面大片一样。","_id":"http://www.digitaling.com/projects/22803.html","type":"commerce","content_html":"<div><div class=\"article_con mg_b_50\" id=\"article_con\">&#13;\n                            <p/><p/><p>这是一则华为最近在国外投放的创意广告，不到最后还真猜不到是华为P10的广告。<\/p><p>全片没有一句对白，全凭人物的表情、动作以及背景音乐的调控，营造出既有点神秘、紧张，又带点轻松、逗趣的氛围。<\/p><p>一个美女在上班路上被四个黑衣人跟踪了<br/><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502764996201056.gif\" title=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\" border=\"0\" vspace=\"0\" alt=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\"/><\/p><p>而且是明目张胆的别人能看见的那种<span>（但显然女主看不到）<\/span><br/><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502764995801343.gif\" title=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\" border=\"0\" vspace=\"0\" alt=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\"/><\/p><p>女主只要一掏手机，他们就自动上线<br/><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502764993710431.gif\" title=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\" border=\"0\" vspace=\"0\" alt=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\"/><\/p><p>放下手机又自动下线，时刻保持严阵以待的样子<br/><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502764998617418.gif\" title=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\" border=\"0\" vspace=\"0\" alt=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\"/><br/><\/p><p>终于当女主准备自拍时，他们露出了真面目：化妆师、造型师、灯光师、导演，为女主秒秒钟搞定一张美美哒自拍。<\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502765001501682.gif\" title=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\" border=\"0\" vspace=\"0\" alt=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\"/><\/p><p>没错，这支广告就是主打<strong>华为P10的人像摄影功能，让每一张自拍照都像封面大片一样。<\/strong><\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502767789112919.jpg\" title=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\" alt=\"美女遭四个黑衣人跟踪？不到最后猜不到是华为广告\" border=\"0\" vspace=\"0\"/><\/p>&#13;\n                        <\/div>&#13;\n                        <\/div>"}
//        {"site_name":"数英网","post_title":"看呆了！佳能的多米诺骨牌效应广告《完美时刻》","crawl_time":1502856383,"post_url":"http://www.digitaling.com/projects/22841.html","module":"brand","content_text":"在一个相互联系的系统中，一个很小的初始能量就可能产生一系列的连锁反应，人们把这种现象称为\u201c多米诺骨牌效应\u201d或\u201c多米诺效应\u201d。佳能最新大片《完美时刻》，从一名女摄影师将镜头盖滚出去开始，一系列多米诺效应就此产生。在她捕捉这些完美瞬间时，拥有一台合适的相机是多么重要。一顶蓝色的假发？不小心被小孩子的蓝色气球水击中而已他没有在喷火是同事使用焊接用的火枪造成错位假象所以，在正确的时间和正确的地点，如果没有合适的相机，那么一切都没有意义。Being at the right place at the time means nothing without the right camera. Be prepared and own the moment with the Canon EOS Rebel T7i.《完美时刻》","_id":"http://www.digitaling.com/projects/22841.html","type":"commerce","content_html":"<div><div class=\"article_con mg_b_50\" id=\"article_con\">&#13;\n                            <p/><p>在一个相互联系的系统中，一个很小的初始能量就可能产生一系列的连锁反应，人们把这种现象称为\u201c多米诺骨牌效应\u201d或\u201c多米诺效应\u201d。<\/p><p>佳能最新大片《完美时刻》，从一名女摄影师将镜头盖滚出去开始，一系列多米诺效应就此产生。在她捕捉这些完美瞬间时，拥有一台合适的相机是多么重要。<br/><\/p><p><strong>一顶蓝色的假发？<br/>不小心被小孩子的蓝色气球水击中而已<\/strong><\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502780498940788.jpg\" title=\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\" alt=\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\" border=\"0\" vspace=\"0\"/><\/p><p><br/><\/p><p><strong>他没有在喷火<br/>是同事使用焊接用的火枪造成错位假象<\/strong><br/><\/p><p><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502780571804697.jpg\" title=\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\" alt=\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\" border=\"0\" vspace=\"0\"/><\/p><p>所以，在正确的时间和正确的地点，如果没有合适的相机，那么一切都没有意义。<\/p><p>Being at the right place at the time means nothing without the right camera. Be prepared and own the moment with the Canon EOS Rebel T7i.<\/p><p><span><strong>《完美时刻》<\/strong><\/span><\/p><p/><p><img src=\"http://file.digitaling.com/eImg/uimages/20170815/1502781017651043.jpg\" title=\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\" alt=\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\" border=\"0\" vspace=\"0\"/><\/p>&#13;\n                        <\/div>&#13;\n                        <\/div>"}



        String jsonstr = "{\"site_name\":\"数英网\",\"post_title\":\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\",\"crawl_time\":1502856383,\"post_url\":\"http://www.digitaling.com/projects/22841.html\",\"module\":\"brand\",\"content_text\":\"在一个相互联系的系统中，一个很小的初始能量就可能产生一系列的连锁反应，人们把这种现象称为\\u201c多米诺骨牌效应\\u201d或\\u201c多米诺效应\\u201d。佳能最新大片《完美时刻》，从一名女摄影师将镜头盖滚出去开始，一系列多米诺效应就此产生。在她捕捉这些完美瞬间时，拥有一台合适的相机是多么重要。一顶蓝色的假发？不小心被小孩子的蓝色气球水击中而已他没有在喷火是同事使用焊接用的火枪造成错位假象所以，在正确的时间和正确的地点，如果没有合适的相机，那么一切都没有意义。Being at the right place at the time means nothing without the right camera. Be prepared and own the moment with the Canon EOS Rebel T7i.《完美时刻》\",\"_id\":\"http://www.digitaling.com/projects/22841.html\",\"type\":\"commerce\",\"content_html\":\"<div><div class=\\\"article_con mg_b_50\\\" id=\\\"article_con\\\">&#13;\\n                            <p/><p>在一个相互联系的系统中，一个很小的初始能量就可能产生一系列的连锁反应，人们把这种现象称为\\u201c多米诺骨牌效应\\u201d或\\u201c多米诺效应\\u201d。<\\/p><p>佳能最新大片《完美时刻》，从一名女摄影师将镜头盖滚出去开始，一系列多米诺效应就此产生。在她捕捉这些完美瞬间时，拥有一台合适的相机是多么重要。<br/><\\/p><p><strong>一顶蓝色的假发？<br/>不小心被小孩子的蓝色气球水击中而已<\\/strong><\\/p><p><img src=\\\"http://file.digitaling.com/eImg/uimages/20170815/1502780498940788.jpg\\\" title=\\\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\\\" alt=\\\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\\\" border=\\\"0\\\" vspace=\\\"0\\\"/><\\/p><p><br/><\\/p><p><strong>他没有在喷火<br/>是同事使用焊接用的火枪造成错位假象<\\/strong><br/><\\/p><p><img src=\\\"http://file.digitaling.com/eImg/uimages/20170815/1502780571804697.jpg\\\" title=\\\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\\\" alt=\\\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\\\" border=\\\"0\\\" vspace=\\\"0\\\"/><\\/p><p>所以，在正确的时间和正确的地点，如果没有合适的相机，那么一切都没有意义。<\\/p><p>Being at the right place at the time means nothing without the right camera. Be prepared and own the moment with the Canon EOS Rebel T7i.<\\/p><p><span><strong>《完美时刻》<\\/strong><\\/span><\\/p><p/><p><img src=\\\"http://file.digitaling.com/eImg/uimages/20170815/1502781017651043.jpg\\\" title=\\\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\\\" alt=\\\"看呆了！佳能的多米诺骨牌效应广告《完美时刻》\\\" border=\\\"0\\\" vspace=\\\"0\\\"/><\\/p>&#13;\\n                        <\\/div>&#13;\\n                        <\\/div>\"}";

        String data_html = new JSONObject(jsonstr).getString("content_html");

        String url = new JSONObject(jsonstr).getString("post_url");

//        String url = "http://www.tmtpost.com/2729781.html";
//        String data_html = "<p>编者按：如何增强团队的凝聚力，这是很多管理者非常头痛的问题。事实上，增强凝聚力的最佳途径并非天天晨会洗脑或者慷慨画饼，给员工一些实打实的小福利，比如免费午餐，效果会出乎意料的好。本文编译自Hoteltonight公司CEO Sam Shank所写原题为\\u201cHey tech CEOs, want better collaboration? Offer your team free lunch\\u201d的文章，看看他是如何利用免费午餐提高团队凝聚力的吧。<br/><\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15020102/e2zhrdxat1bzm9f1.png!heading\\\" data-img-size-val=\\\"730,382\\\"/\\\\><\\/p><p   class=\\\"img-desc\\\">HotelTonight是一款移动旅行app，发行于2010年，用户总数达到一千五百万。<\\/p><p   class=\\\"img-desc\\\">HotelTonight帮助用户预定酒店，早至入住前七天，服务范围是南美、北美、欧洲、中东、非洲和澳洲。<\\/p><p   class=\\\"img-desc\\\"><br/><\\/p><p>几周前，在和其他科技初创企业的高管共享晚餐时，我们谈到如何培养优秀团队文化。大家纷纷提出很多高大上的点子（比如HotelTonight的轮盘赌游戏，我们会给随机抽中的团队成人一个旅行机会\\u2014\\u2014去哪儿都行、费用全包、说走就走）但是，在讨论的时候我们很快就有了个\\u201c少一点花费，多一点影响\\u201d的点子\\u2014\\u2014公司免费午餐。<\\/p><p>也不是所有人都能看到免费午餐带来的好处。甚至有一家公司CFO觉得很疑惑。他一直在纠结花费问题，也不知道提供午餐到底值不值得。我很能理解他:我们公司同样也紧盯着自己的花销，花每一块钱都思前想后。然而,他听完我的讲述，听到免费午餐在自己公司的影响\\u2014\\u2014留住员工、增进友谊和加强合作\\u2014\\u2014他便渐渐明白了。<\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15020236/aw2uqrcrkg1b34bz.jpg!heading\\\" data-img-size-val=\\\"421,243\\\" width=\\\"421\\\"/\\\\><\\/p><h3  ><strong>免费午餐的好处很多，我想强调其中一点: 合作。<\\/strong><\\/h3><p>免费午餐在硅谷已经是\\u201c家常便饭\\u201d了，在初创企业提供的福利中都涵盖这一项。但在硅谷以外，免费的午餐就像金色的锁链，是绑住员工，让他们留在公司的绝佳理由。<\\/p><p>当然这不是HotelTonight提供免费午餐的原因。事实上，我们没有把免费午餐当作传统意义上的\\u201c福利\\u201c。有午餐当然是好的，谁不喜欢免费食物,节省时间金钱呢？更为重要的共享午餐的过程和结果。<\\/p><p>HotelTonight团队工作异常努力。他们是我见过的最聪明最勤奋的一群人，我很荣幸跟他们共事。几年前我发现，他们工作专心致志，没日没夜，根本不离桌子半步。当时我担心团队成员相互疏离，工作筋疲力尽。藉由团体午餐，成员可以暂时离开座位，跟同事共同享用美餐（多亏了我们的厨艺超神的Sandra）<\\/p><p>也正因为我们身处酒店行业，在HT总部为团队和访客创造一片惬意舒适的空间，对我而言一直都很重要。所以，我们在设计办公室时，采取了我们合作的精品酒店的最佳元素，比如沙发，小角落还有公用工作空间。<\\/p><p>午休时，长长的午餐桌是一座桥，美食在这头，欢声笑语在那头。<\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15020504/wul4rvl84409mv4r.jpg!heading\\\" data-img-size-val=\\\"636,423\\\" width=\\\"636\\\"/\\\\><\\/p><h3  ><strong>铸造更深刻的连结\\u2014\\u2014企业文化和同事友谊<\\/strong><\\/h3><p>公用空间餐桌上深刻的交流、合作是我亲眼目睹，难以忘怀的，我也深受鼓舞。我看到不同团队的成员并肩而坐,相互交流，不管是周末闲趣，近期旅程还是手头工作，若不是公用空间，他们也不会有机会相互接触。<\\/p><p>谈话之后，有人受同事旅程启发，自己踏上心灵之旅；不同团队成员组队编程马拉松，一起头脑风暴，想办法解决长期难题。这是种催生友情的交流，让队友结伴参加马拉松；让来自不同团队的人组队去开创新公司,共同创造美好的新事物。我曾读到很多人的体会，都提到跟朋友共识可以提高生活质量,我也确实相信，因为我自己就有切身体会。<\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15022600/1evyjdpn5h326oq6.jpg!heading\\\" data-img-size-val=\\\"700,467\\\" width=\\\"700\\\"/\\\\><\\/p><p>&nbsp;举个例子，我们的战略伙伴团队的同事Donnie有一次告诉我：<\\/p><blockquote><p>\\u201c在午餐桌上发展的不只是友谊，跟其他人的谈话也对个人发展大有裨益。午餐时和财会 组的同事聊天，就让我增加了对公司处理税务的了解,我就知道如何更好的对外谈判。 这就像午餐课堂，一边上国际税务法入门课,一边吃沙拉。我也曾经从其他部门同事那里得 到公司市场交流活动地点绝佳的建议。我们的合作伙伴通常都十分赞叹我的选址，总以为我是本地人。\\u201c<\\/p><\\/blockquote><p>旅行带来的最大乐趣就是旅途中所认识的人，跟生活经历迥乎不同的人来一场随兴谈话，会让你大有收获。我当然是非常赞成旅行的，但我相信大家不总是想要到远方去，建立起将一些关系。跟同事一起享用午餐,了解他们的背景，互相鼓舞，有时结果会令人意想不到,不管是从私人角度还是工作角度。这也是为什么HotelTonight为员工提供免费午餐。<\\/p><p>&nbsp;<\\/p><p><img alt=\\\"Hoteltonight CEO：企业凝聚力不够？不如请团队吃免费午餐吧！\\\" src=\\\"https://pic.36krcnd.com/avatar/201708/15022600/fd5ujhe7limdm75z.jpg!heading\\\" data-img-size-val=\\\"673,337\\\" width=\\\"673\\\"/\\\\><\\/p><p>以上是HotelTonight CEO／联合创始人Sam Shank的观点, 他本人最喜欢的是烧烤日午餐。<\\/p><p>原文链接：<a href=\\\"https://www.tnooz.com/article/hey-tech-ceos-want-better-collaboration-offer-your-team-free-lunch/\\\" _src=\\\"https://www.tnooz.com/article/hey-tech-ceos-want-better-collaboration-offer-your-team-free-lunch/\\\">https://www.tnooz.com/article/hey-tech-ceos-want-better-collaboration-offer-your-team-free-lunch/<\\/a> <\\/p><p>编译组出品。编辑：郝鹏程<\\/p>";
        String content = ContentDeepExtractor.getContent(data_html,url);
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------------------------");
    }

}
