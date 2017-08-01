package com.hankcs.hanlp.summary;


import com.hankcs.hanlp.algorithm.MaxHeap;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;

/**
 * 基于TextRank算法的关键字提取，适用于单文档
 * @author hankcs
 */
public class TextRankKeyword extends KeywordExtractor
{
    /**
     * 提取多少个关键字
     */
    int nKeyword = 10;
    /**
     * 阻尼系数（ＤａｍｐｉｎｇＦａｃｔｏｒ），一般取值为0.85
     */
    final static float d = 0.85f;
    /**
     * 最大迭代次数
     */
    final static int max_iter = 200;
    final static float min_diff = 0.001f;

    /**
     * 提取关键词
     * @param document 文档内容
     * @param size 希望提取几个关键词
     * @return 一个列表
     */
    public static List<String> getKeywordList(String document, int size)
    {
        TextRankKeyword textRankKeyword = new TextRankKeyword();
        textRankKeyword.nKeyword = size;

        return textRankKeyword.getKeyword(document);
    }

    /**
     * 提取关键词
     * @param content
     * @return
     */
    public List<String> getKeyword(String content)
    {
        Set<Map.Entry<String, Float>> entrySet = getTermAndRank(content, nKeyword).entrySet();
        List<String> result = new ArrayList<String>(entrySet.size());
        for (Map.Entry<String, Float> entry : entrySet)
        {
            result.add(entry.getKey());
        }
        return result;
    }

    /**
     * 返回全部分词结果和对应的rank
     * @param content
     * @return
     */
    public Map<String,Float> getTermAndRank(String content)
    {
        assert content != null;
        List<Term> termList = defaultSegment.seg(content);
//        List<Term> termList = PartOfSpeechTagging.process(WordSegmenter.seg(content, SegmentationAlgorithm.BidirectionalMaximumMatching));
        //在这里替换分词方法
        //返回格式：[如果/c, 太忙/nz, 太/d, 累/a, 不/d, 想/v, 烧/vi, 脑/n, 看/v, 电影/n, ，/w, 那/rzv
        return getRank(termList);

    }

    /**
     * 返回分数最高的前size个分词结果和对应的rank
     * @param content
     * @param size
     * @return
     */
    public Map<String,Float> getTermAndRank(String content, Integer size)
    {
        Map<String, Float> map = getTermAndRank(content);
        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : new MaxHeap<Map.Entry<String, Float>>(size, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        }).addAll(map.entrySet()).toList())
        {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * 使用已经分好的词来计算rank
     * @param termList
     * @return
     */
    public Map<String,Float> getRank(List<Term> termList)
    {
        List<String> wordList = new ArrayList<String>(termList.size());
        for (Term t : termList)
        {
            if (shouldInclude(t))
            {
                wordList.add(t.word);
            }
        }
//        System.out.println(wordList);
        Map<String, Set<String>> words = new TreeMap<String, Set<String>>();
        Queue<String> que = new LinkedList<String>();
        for (String w : wordList)
        {
            if (!words.containsKey(w))
            {
                words.put(w, new TreeSet<String>());
            }
//            que.offer(w);
//            if (que.size() > 5)
//            {
//                que.poll();
//            }
//
//            for (String w1 : que)
//            {
//                for (String w2 : que)
//                {
//                    if (w1.equals(w2))
//                    {
//                        continue;
//                    }
//
//                    words.get(w1).add(w2);
//                }
//            }
            // 复杂度O(n-1)
            if (que.size() >= 5) {
                que.poll();
            }
            for (String qWord : que) {
                if (w.equals(qWord)) {
                    continue;
                }
                //既然是邻居,那么关系是相互的,遍历一遍即可
                words.get(w).add(qWord);
                words.get(qWord).add(w);
            }
            que.offer(w);
        }
//        System.out.println(words);
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i)
        {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet())
            {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String element : value)
                {
                    int size = words.get(element).size();
                    if (key.equals(element) || size == 0) continue;
                    m.put(key, m.get(key) + d / size * (score.get(element) == null ? 0 : score.get(element)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }
            score = m;
            if (max_diff <= min_diff) break;
        }

        return score;
    }

   public static void main(String[] args){
       String content = "界面新闻记者近日发现，小霸王游戏机项目已经进入加速阶段。名为“playruyi.com”的网站已于近期上线，域名拥有者为珠海市小霸王领先科技有限公司上海分公司（下称小霸王上海）。该网站主要介绍名为“如意”的神秘项目，文案中包括“迎接游戏未来”，“极致游戏体验”，“整合游戏生态”等字样，并附有两则招聘启事。查看网页源代码时，出现 “Console Games”,“PC Games”,“ Xbox”, “PlayStation”等字样。\n" +
           "\n" +
           "在社交网站领英上，界面记者在一家名为“Subor Advanced Technology”的企业介绍页面中发现有关“如意”的更多信息，该项目在领英被称为Project RUYI，其中介绍称：“Project RUYI是由一群在游戏圈耕耘多年、热爱游戏、享受游戏的创业者们所创立的高性能游戏主机项目。依托香港上市公司益华控股的支持，得到旗下“小霸王”品牌授权、资源整合等各方面支持，在上海所建立的研发中心。”据称，Project RUYI立项已近一年，“与世界顶级半导体研发公司、欧美日韩游戏开发商建立了战略级合作关系。” \n" +
           "\n" +
           "2016年6月，小霸王高端游戏机首次浮出水面。据中山政府网站消息，小霸王文化产业有限公司与美国AMD 公司正式签订合作协议，双方将共同投资4亿多元定制一款由AMD 独家授权的芯片，进军文化教育市场。据称，该芯片为一款虚拟现实（VR）游戏主机芯片，综合性能将超PS4两倍，为VR 技术提供全面优化支持。\n" +
           "\n" +
           "益华控股在今年初发布的2016年度财报中对该合作亦有提及，称其联营公司小霸王文化目前正在中国展开及发展生产游戏机、虚拟现实及开发电子游戏方面的业务。同时，小霸王文化正在与一家跨国半导体公司（供货商）合作购买半导体产品以供生产可全面支持虚拟现实的游戏系统。供货商为一家于美国注册成立的公司，其业务为供应半导体产品供服务器、工作站、个人计算机及内置系统使用。可以判断该半导体公司正是AMD。\n" +
           "\n" +
           "界面记者查询工商注册资料发现，珠海市小霸王领先科技有限公司上海分公司的企业法人为吴松，公开履历显示，吴松为该公司CEO与创始人，曾多次参与国内游戏主机相关项目。据触乐报道，吴松曾任斧子科技（现蓝港科技）CSO并作为联合创始人，负责战略与内容规划，斧子科技曾发布国产安卓游戏主机战斧F1。吴松此前曾在英伟达任职，是微软Xbox游戏主机入华团队的最早成员之一。\n" +
           "\n" +
           "目前小霸王上海仍处于扩张阶段，界面记者在多家招聘网站上发现小霸王上海的招聘信息，职位包括技术开发支持经理、UX设计师、客户端工程师等， 6月期间招聘信息发布较为密集。\n" +
           "\n" +
           "据了解，小霸王上海CTO来自Crytek上海工作室。在领英上，共有11人显示为Subor Advanced Technology旗下员工，界面记者发现，相当部分员工来自Crytek上海，入职时间集中于2016年末至2017年初。Crytek为德国知名游戏引擎制作商与游戏开发商，旗下CryEngine为行业领先的游戏引擎，Crytek因经营不善于2016年末爆发财务危机，并于2016年末关闭上海工作室。\n" +
           "\n" +
           "从“如意”立项仅不到一年的时间推测，该游戏机研发进度并不会太快。一位从事游戏主机开发的从业者告诉界面记者，开发一款游戏主机分为硬件，系统和支持游戏三个部分。“硬件最容易，主要是要找到性价比、散热功耗都比较平衡的配置，花的时间会比较多一点。还有就是一些游戏外设的硬件开发，标准外设如手柄，如果像国内一些厂商直接用公版模块、蓝牙连接，成本很低，周期也很短。”公开报道显示，微软Xbox One游戏机研发时间约3年，而任天堂Switch、索尼PS4等游戏主机研发周期大致相仿。 \n" +
           "\n" +
           "一位知情人士称，因为目前小霸王上海的团队规模并没有太大，同时国内做游戏机难度不小，“搞一个主机平台坑太多了。”该知情人士告诉界面记者。\n" +
           "\n" +
           "提到游戏内容，该知情人士认为，小霸王上海目前采取的是先争取国外开发者的策略。“我知道的是，目前国内的CP基本没有深入谈过合作，之后推动也来得及。海外的游戏内容会更难拿些，因为有大厂也有小厂，没人能搞定那么多，忙不过来的。”他说，“谈判会比较慢，很多是James（吴松）亲自谈的。";
       List<Term> termList = defaultSegment.seg(content);
       System.out.println("带词性的分词 : "+termList);

       List<String> keylist = getKeywordList(content,4);
       System.out.println("获取关键词列表 : "+keylist);

       List<String> keyword = new TextRankKeyword().getKeyword(content);
       System.out.println("获取关键词列表 : "+keyword);

       System.out.println("******返回分数最高的前size个分词结果和对应的rank******");
       Map<String,Float> mapkeyword = new TextRankKeyword().getTermAndRank(content,4);
       for (String word : mapkeyword.keySet()){
           System.out.println(word+ ":"+mapkeyword.get(word));
       }
       System.out.println("******使用已经分好的词来计算rank****");
       Map<String,Float> mapkeyword1 = new TextRankKeyword().getRank(termList);
       for (String word : mapkeyword1.keySet()){
           System.out.println(word+ ":"+mapkeyword1.get(word));
       }
   }

}
