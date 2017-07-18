package cn.datapark.process.education.Summary;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本摘要提取文中重要的关键句子，使用top-n关键词在句子中的比例关系
 * 返回过滤句子方法为：1.均值标准差，2.top-n句子，3.最大边缘相关top-n句子
 *
 * @author yan.shi
 *@date： 日期：2017-4-20 时间：上午9:45:21
 */
public class NewsSummary {
    int N=50;//保留关键词数量
    int CLUSTER_THRESHOLD=5;//关键词间的距离阀值
    int TOP_SENTENCES=6;//前top-n句子
    double λ=0.4;//最大边缘相关阀值
    final Set<String> styleSet=new HashSet<String>();//句子得分使用方法
    Set<String> stopWords=new HashSet<String>();//过滤停词
    Map<Integer,List<String>> sentSegmentWords=null;//句子编号及分词列表

    /**
     * 加载停词
     * @param path
     */
    private void loadStopWords(String path){
        BufferedReader br=null;
        try{
            br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));
            String line=null;
            while((line=br.readLine())!=null){
                stopWords.add(line);
            }
            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public NewsSummary(){
        this.loadStopWords("data/dictionary/stopwords.txt");
        System.out.println("加载停用词-词典完成");
        styleSet.add("meanstd");
        styleSet.add("default");
        styleSet.add("MMR");
    }

    /**
     * 每个句子得分  (keywordsLen*keywordsLen/totalWordsLen)
     * @param sentences 分句
     * @param topnWords keywords top-n关键词
     * @return
     */
    private Map<Integer,Double> scoreSentences(List<String> sentences,List<String> topnWords){
        //System.out.println("scoreSentences in...");
        Map<Integer, Double> scoresMap=new LinkedHashMap<Integer,Double>();//句子编号，得分
        sentSegmentWords=new HashMap<Integer,List<String>>();
        int sentence_idx=-1;//句子编号
        for(String sentence:sentences){
            sentence_idx+=1;
            List<String> words=this.IKSegment(sentence);//对每个句子分词
            sentSegmentWords.put(sentence_idx, words);
            List<Integer> word_idx=new ArrayList<Integer>();//每个关词键在本句子中的位置
            for(String word:topnWords){
                if(words.contains(word)){
                    word_idx.add(words.indexOf(word));
                }else
                    continue;
            }
            if(word_idx.size()==0)
                continue;
            Collections.sort(word_idx);
            //对于两个连续的单词，利用单词位置索引，通过距离阀值计算一个族
            List<List<Integer>> clusters=new ArrayList<List<Integer>>();//根据本句中的关键词的距离存放多个词族
            List<Integer> cluster=new ArrayList<Integer>();
            cluster.add(word_idx.get(0));
            int i=1;
            while(i<word_idx.size()){
                if((word_idx.get(i)-word_idx.get(i-1))<this.CLUSTER_THRESHOLD)
                    cluster.add(word_idx.get(i));
                else{
                    clusters.add(cluster);
                    cluster=new ArrayList<Integer>();
                    cluster.add(word_idx.get(i));
                }
                i+=1;
            }
            clusters.add(cluster);
            //对每个词族打分，选择最高得分作为本句的得分
            double max_cluster_score=0.0;
            for(List<Integer> clu:clusters){
                int keywordsLen=clu.size();//关键词个数
                int totalWordsLen=clu.get(keywordsLen-1)-clu.get(0)+1;//总的词数
                double score=1.0*keywordsLen*keywordsLen/totalWordsLen;
                if(score>max_cluster_score)
                    max_cluster_score=score;
            }
            scoresMap.put(sentence_idx,max_cluster_score);
        }
        //System.out.println("scoreSentences out...");
        return scoresMap;
    }

    /**
     * 这里使用IK进行分词
     * @param text
     * @return
     */
    private List<String> IKSegment(String text){
        List<String> wordList=new ArrayList<String>();
        Reader reader=new StringReader(text);
        IKSegmenter ik=new IKSegmenter(reader,true);
        Lexeme lex=null;
        try {
            while((lex=ik.next())!=null){
                String word=lex.getLexemeText();
                if(word.equals("nbsp") || this.stopWords.contains(word))
                    continue;
                if(word.length()>1 && word!="\t")
                    wordList.add(word);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return wordList;
    }

    /**
     * 分句
     * @param text
     * @return
     */
    private List<String> sentTokenizer(String text){
        //System.out.println("sentTokenizer in...");
        List<String> sentences=new ArrayList<String>();
//        String regEx="[!?。！？.;]"; //这里是根据标点分句，如果分句符号中存在 "." ,在分句的时候，就会出现 这种情况，“财报发布后，Inditex集团股价上涨2.” 也会把小数点进行分割
        String regEx="[!?。！？;]";
        Pattern p=Pattern.compile(regEx);
        Matcher m=p.matcher(text);
        String[] sent=p.split(text);
        int sentLen=sent.length;
        if(sentLen>0){
            int count=0;
            while(count<sentLen){
                if(m.find()){
                    sent[count]+=m.group();
                }
                count++;
            }
        }
        for(String sentence:sent){
            sentence=sentence.replaceAll("(&rdquo;|&ldquo;|&mdash;|&lsquo;|&rsquo;|&middot;|&quot;|&darr;|&bull;)", "");
            sentences.add(sentence.trim());
        }
        //System.out.println("sentTokenizer out...");
        return sentences;
    }

    /**
     * 计算文本摘要
     * @param text
     * @param style(meanstd,default,MMR)
     * @return
     */
    public String summarize(String text,String style){
        //System.out.println("summarize in...");
        try {
            if(!styleSet.contains(style) || text.trim().equals(""))
                throw new IllegalArgumentException("方法 summarize(String text,String style)中text不能为空，style必须是meanstd、default或者MMR");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }

        List<String> sentencesList=this.sentTokenizer(text);
        List<String> words=this.IKSegment(text);
        Map<String,Integer> wordsMap=new HashMap<String,Integer>();
        for(String word:words){
            Integer val=wordsMap.get(word);
            wordsMap.put(word,val==null?1:val+1);
        }
        //使用优先队列自动排序
        Queue<Entry<String, Integer>> wordsQueue=new PriorityQueue<Entry<String,Integer>>(wordsMap.size(),new Comparator<Entry<String,Integer>>(){

            public int compare(Entry<String, Integer> o1,
                               Entry<String, Integer> o2) {
                // TODO Auto-generated method stub
                return o2.getValue()-o1.getValue();
            }
        });
        wordsQueue.addAll(wordsMap.entrySet());
        if(N>wordsMap.size()) N=wordsQueue.size();
        List<String> wordsList=new ArrayList<String>(N);//top-n关键词
        for(int i=0;i<N;i++){
            Entry<String,Integer> entry=wordsQueue.poll();
            wordsList.add(entry.getKey());
        }

        Map<Integer,Double> scoresLinkedMap=scoreSentences(sentencesList,wordsList);//返回的得分,从第一句开始,句子编号的自然顺序
        List<Entry<Integer, Double>> sortedSentList=new ArrayList<Entry<Integer,Double>>(scoresLinkedMap.entrySet());//按得分从高到底排序好的句子，句子编号与得分
        //System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(sortedSentList, new Comparator<Entry<Integer, Double>>(){

            public int compare(Entry<Integer, Double> o1,Entry<Integer, Double> o2) {
                // TODO Auto-generated method stub
                return o2.getValue() == o1.getValue() ? 0 :
                        (o2.getValue() > o1.getValue() ? 1 : -1);
            }

        });

        Map<Integer,String> keySentence=null;

        //approach1,利用均值和标准差过滤非重要句子
        if(style.equals("meanstd")){
            keySentence=new LinkedHashMap<Integer,String>();
            double sentenceMean=0.0;//句子得分均值
            for(double value:scoresLinkedMap.values()){
                sentenceMean+=value;
            }
            sentenceMean/=scoresLinkedMap.size();
            double sentenceStd=0.0;//句子得分标准差
            for(Double score:scoresLinkedMap.values()){
                sentenceStd+=Math.pow((score-sentenceMean), 2);
            }
            sentenceStd=Math.sqrt(sentenceStd/scoresLinkedMap.size());
            for(Entry<Integer, Double> entry:scoresLinkedMap.entrySet()){
                if(entry.getValue()>(sentenceMean+0.5*sentenceStd))//过滤低分句子
                    keySentence.put(entry.getKey(), sentencesList.get(entry.getKey()));
            }
        }

        //approach2,默认返回排序得分top-n句子
        if(style.equals("default")){
            keySentence=new TreeMap<Integer,String>();
            int count=0;
            for(Entry<Integer, Double> entry:sortedSentList){
                count++;
                keySentence.put(entry.getKey(), sentencesList.get(entry.getKey()));
                if(count==this.TOP_SENTENCES)
                    break;
            }
        }

        //approach3,利用最大边缘相关，返回前top-n句子
        if(style.equals("MMR")){
            if(sentencesList.size()==2){
                return sentencesList.get(0)+sentencesList.get(1);
            }else if(sentencesList.size()==1)
                return sentencesList.get(0);
            keySentence=new TreeMap<Integer,String>();
            int count=0;
            Map<Integer,Double> MMR_SentScore=MMR(sortedSentList);
            for(Entry<Integer, Double> entry:MMR_SentScore.entrySet()){
                count++;
                int sentIndex=entry.getKey();
                String sentence=sentencesList.get(sentIndex);
                keySentence.put(sentIndex, sentence);
                if(count==this.TOP_SENTENCES)
                    break;
            }
        }
        StringBuilder sb=new StringBuilder();
        for(int  index:keySentence.keySet())
            sb.append(keySentence.get(index));
        //System.out.println("summarize out...");
        return sb.toString();
    }

    /**
     * 最大边缘相关(Maximal Marginal Relevance)，根据λ调节准确性和多样性
     * max[λ*score(i) - (1-λ)*max[similarity(i,j)]]:score(i)句子的得分，similarity(i,j)句子i与j的相似度
     * User-tunable diversity through λ parameter
     * - High λ= Higher accuracy
     * - Low λ= Higher diversity
     * @param sortedSentList 排好序的句子，编号及得分
     * @return
     */
    private Map<Integer,Double> MMR(List<Entry<Integer, Double>> sortedSentList){
        //System.out.println("MMR In...");
        double[][] simSentArray=sentJSimilarity();//所有句子的相似度
//        Map<Integer,Double> sortedLinkedSent=new LinkedHashMap<Integer,Double>();
        Map<Integer,Double> sortedLinkedSent=new ConcurrentHashMap<Integer,Double>();
        for(Entry<Integer, Double> entry:sortedSentList){
            sortedLinkedSent.put(entry.getKey(),entry.getValue());
        }
//        Map<Integer,Double> MMR_SentScore=new LinkedHashMap<Integer,Double>();//最终的得分（句子编号与得分）
        Map<Integer,Double> MMR_SentScore=new ConcurrentHashMap<Integer,Double>();//最终的得分（句子编号与得分）
        Entry<Integer, Double> Entry=sortedSentList.get(0);//第一步先将最高分的句子加入
        MMR_SentScore.put(Entry.getKey(), Entry.getValue());
        boolean flag=true;
        while(flag){
            int index=0;
            double maxScore=Double.NEGATIVE_INFINITY;//通过迭代计算获得最高分句子
            for(Map.Entry<Integer, Double> entry:sortedLinkedSent.entrySet()){
                if(MMR_SentScore.containsKey(entry.getKey())) continue;
                double simSentence=0.0;
                for(Map.Entry<Integer, Double> MMREntry:MMR_SentScore.entrySet()){//这个是获得最相似的那个句子的最大相似值
                    double simSen=0.0;
                    if(entry.getKey()>MMREntry.getKey())
                        simSen=simSentArray[MMREntry.getKey()][entry.getKey()];
                    else
                        simSen=simSentArray[entry.getKey()][MMREntry.getKey()];
                    if(simSen>simSentence){
                        simSentence=simSen;
                    }
                }
                simSentence=λ*entry.getValue()-(1-λ)*simSentence;
                if(simSentence>maxScore){
                    maxScore=simSentence;
                    index=entry.getKey();//句子编号
                }
            }
            MMR_SentScore.put(index, maxScore);
            if(MMR_SentScore.size()==sortedLinkedSent.size())
                flag=false;
        }
        //System.out.println("MMR out...");
        return MMR_SentScore;
    }

    /**
     * 每个句子的相似度，这里使用简单的jaccard方法，计算所有句子的两两相似度
     * @return
     */
    private double[][] sentJSimilarity(){
        //System.out.println("sentJSimilarity in...");
        int size=sentSegmentWords.size();
        double[][] simSent=new double[size][size];
        for(Entry<Integer, List<String>> entry:sentSegmentWords.entrySet()){
            for(Entry<Integer, List<String>> entry1:sentSegmentWords.entrySet()){
                if(entry.getKey()>=entry1.getKey()) continue;
                int commonWords=0;
                double sim=0.0;
                for(String entryStr:entry.getValue()){
                    if(entry1.getValue().contains(entryStr))
                        commonWords++;
                }
                sim=1.0*commonWords/(entry.getValue().size()+entry1.getValue().size()-commonWords);
                simSent[entry.getKey()][entry1.getKey()]=sim;
            }
        }
        //System.out.println("sentJSimilarity out...");
        return simSent;
    }

    public static void main(String[] args){
        NewsSummary summary=new NewsSummary();

        String text2="依靠引领行业趋势的商业模式优势，快时尚Zara依然一枝独秀。" +
                "Zara母公司、全球最大的快时尚集团Inditex（BME:ITX）昨日发布第一季度财报数据，" +
                "虽然快时尚行业整体已渐露疲态，但集团在报告期内依然录得惊人的增长，销售额与净利润均超过分析师预期。" +
                "在截至4月30日的三个月内，Inditex集团销售额同比大涨14%至56亿欧元，" +
                "其中欧洲、亚洲和美洲地区销售额增长最为强劲。集团毛利润同样增长14%至32亿欧元，" +
                "毛利率为58.2%，净利润则同比大涨18%至6.54亿欧元，是其主要竞争对手H&M集团（STO:HM）第一季度净利润的两倍有余。" +
                "Inditex集团旗下共有8个品牌，核心品牌Zara销售额占整体销售额的2/3，" +
                "除Zara外，其它品牌分别为Bershka、Massimo Dutti、Pull&Bear、Stradivarius、Zara Home、Oysho和Uterque。" +
                "图为Inditex集团旗下品牌该季度的业绩占比图报告还透露，在2月1日至6月3日期间，" +
                "Inditex集团的门店及线上可比零售额均录得了12%的增幅。Inditex在财报中表示，" +
                "公司旗下所有品牌在本季度都“增加了国际影响力”，在30个国家增设了93家门店，" +
                "截至报告期末，Inditex在全球93个国家拥有7385家门店。据悉，Inditex在过去一年中共创造了超过10000个新的工作岗位，" +
                "仅西班牙就超过2200个。种种迹象表明，Zara目前仍在处在增长期。而整个快时尚行业的增速却开始逐渐放缓，" +
                "除了引领行业趋势的商业模式优势，Zara到底还靠什么保持强劲增长？Societe Generale分析师Anne Critchlow写道，" +
                "Inditex集团是当今难得的一直紧跟潮流趋势，保持强劲增长且飞快地适应了时尚电商的运营机制的快时尚集团，" +
                "已经大大超越了H&M和Gap等竞争对手。据时尚头条网上月报道，深陷业绩泥潭的美国最大服饰零售商Gap，" +
                "在美国本土市场似乎有了新的转机，不过在包括中国的亚洲市场遭受重挫，大跌20.5%至2.83亿美元； " +
                "其他地区销售额同比增长6%至5300万美元。至于H&M，由于客户需求下降和采购成本上升，H&M的销售与利润受到影响。" +
                "截至2017年2月28日的2016财年报告显示，集团第一季度销售额增幅仅为4%，低于10％至15％的全年增长预期，" +
                "净利润则同比减少3%至24.6亿克朗约2.53亿欧元。 在瑞典股市交易市场挂牌的瑞典快时尚品牌H&M从2017年起始至今，" +
                "其股价下跌幅度已达16%。这使得其市值降至3530亿瑞典克朗约合410亿美元。而在2015年，其股市市值曾超过6000亿瑞典克朗，" +
                "相比目前市值已蒸发近一半。与此同时，整个快时尚行业还面临着“超快时尚”带来的威胁。不断崛起的时尚电商，" +
                "包括Boohoo.com和ASOS等平台在内的时尚电商正在以“超快时尚”（Ultra-Fashion）争夺那些越来越难满足的消费者，" +
                "可见，效率已成为时尚行业内部越来越重要的衡量标准。 对此Inditex集团表示将其电商业务引入更多新兴市场，" +
                "进一步扩大其在线的市场份额，同时升级其库存管理系统，以应对日趋激烈的时尚电商市场的挑战。" +
                "Inditex集团已越来越重视技术和数据对零售的驱动。集团首席执行官Pablo Isla在财报发布后的会议中透露，" +
                "集团已经引进了全球最先进的店铺库存跟踪系统，包括Zara、Massimo Dutti等品牌门店中也开始使用射频识别标签，" +
                "集团将在三年内将这些最新系统普及到集团旗下所有门店。然而，在复杂的市场环境中，Zara仍然稳占首位，" +
                "一部分原因是人们所熟知的“Zara模式”，即快速高效的物流、频繁的上新与补货。以往很多分析关注在ZARA的渠道和供应链配置上，" +
                "但是当越来越多品牌开始仿效“Zara模式”却始终追不上Zara，我们发现Zara保持强劲增长的最关键原因还是在于Zara非常注重时尚度。" +
                "在社交媒体Intagram关于Zara的帖子已接近2000万对时尚敏感的消费者往往发现，在众多快时尚品牌中，Zara仍然是时尚度最高的品牌。" +
                "尽管有关Zara复制奢侈品牌T台趋势，以及抄袭小众艺术家创意的指控至今还未停止，但是Zara的潮流趋势数据库已经变得越来越强大，" +
                "精准迎合着那些对时尚度和价格均敏感的消费群体，而这部分消费群体正在快速扩大。Zara品牌的设计总部可谓整个品牌的核心，" +
                "由350名设计师组成，与其竞争对手Gap、H&M和Primark不同，Zara没有首席设计师，每位设计师都有自主权，" +
                "产品样式最终会结合各地区最新销售数据来决定，因此设计部门拥有无与伦比的独立性，平均每周两次依据潮流走势灵活地向门店提供新品。" +
                "设计师们会根据每日反馈的销售数据分析畅销与滞销的产品，结果会直接影响未来几周的产品风格走向。相比之下，" +
                "有分析认为H&M的处境越来越尴尬的原因在于，论质量比不过优衣库，论速度比不过电商，论时尚度比不过Zara，" +
                "没有差异化的突出特点。而H&M集团旗下的COS被认为是集团的希望，设计和质量均十分精良，但是无论从品牌风格和运营模式上看，" +
                "都不完全属于快时尚范畴，而受众也毕竟有限。有品牌内部人士对时尚头条网透露，Zara每季商品中有超过50%的商品进行了元素重新混合和再设计，" +
                "这样产品就能跟上中国消费者快速变换的口味。在这一点上，“快速反应”模式展现了其优点,即能对消费者不断变换的口味做出实时反应，" +
                "而很多其他中国消费品公司永远无法做到这一点。 这种独特的运营模式还使他们可以在几周内将巴黎、米兰等的最新设计的低阶版本推向中国市场。" +
                "事实上，不仅仅是快时尚行业，在整个时尚行业中，时尚度是非常关键的竞争要素。麦肯锡咨询发布的中国奢侈品报告显示，" +
                "六至八年前，中国富裕人群购买奢侈品看重材质或做工，但是现在的奢侈品行业已经不可同日而语，核心产品和品牌经典款变得越来越重要，" +
                "年轻消费者更注重品牌的时尚度。随着社交媒体的兴起，穿搭图片分享变得越来越普遍，产品是否足够时尚或者说产品“好不好看”，" +
                "甚至超越了其他因素。这也就解释了为什么那么多消费者抱怨Zara产品质量不行，但仍然热衷于在Zara购买大量衣服的现象。" +
                "Inditex集团目前继续在全球快速扩张。为提高自身在时尚电商中的市场份额，Inditex集团在过去的一年中共推出或扩大20个市场的在线平台，" +
                "其中12个是新的，集团目前在43个国家提供电商业务。继今年上半年Zara在泰国、越南、新加坡和马来西亚推出电商之后，" +
                "品牌官网将于今年下半年登陆印度市场。对于当前的第二季度，Inditex依旧很有信心，预计销售额会延续强劲的增长态势，" +
                "并透露今年2月1日至6月3日的销售额以恒定汇率计算上涨了12%。财报发布后，Inditex集团股价上涨2.5%至每股36欧元，" +
                "目前其市值重新超过1000亿欧元至1102亿欧元。电商资讯第一入口马蹄社游学：探秘韩都衣舍新零售落地策略>>【版权提示】" +
                "亿邦动力网倡导尊重与保护知识产权。如发现本站文章存在版权问题，烦请提供版权疑问、身份证明、版权证明、" +
                "联系方式等发邮件至run@ebrun.com，我们将及时沟通与处理。";

		/*Map<Integer,String> keySentences=summary.summarize(text,"MMR");
		for(Map.Entry<Integer,String> entry:keySentences.entrySet()){
			System.out.println(entry.getKey()+"   "+entry.getValue());
		}*/

        //meanstd利用均值和标准差过滤非重要句子
        String keySentences=summary.summarize(text2, "meanstd");
        System.out.println("meanstd利用均值和标准差过滤非重要句子: "+keySentences);

        //default默认返回排序得分top-n句子
        String keySentences_1=summary.summarize(text2, "default");
        System.out.println("default默认返回排序得分top-n句子: "+keySentences_1);

        //MMR,利用最大边缘相关，返回前top-n句子
        String keySentences_2=summary.summarize(text2, "MMR");
        System.out.println("MMR,利用最大边缘相关，返回前top-n句子: "+keySentences_2);


//        String keySentences_3=summary.summarize(keySentences_2, "meanstd");
//        System.out.println("MMR处理过后的摘要在按照meanstd进行处理: "+keySentences_3);
//
//        String keySentences_4=summary.summarize(keySentences, "MMR");
//        System.out.println("meanstd处理过后的摘要在按照MMR进行处理: "+keySentences_4);
    }

}
