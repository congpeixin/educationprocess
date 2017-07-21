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
    int TOP_SENTENCES=3;//前top-n句子
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

        String text2="日前肯德基英国和爱尔兰地区的 YouTube 发布了一条广告视频，介绍自己的新品“Clean Eating”汉堡，" +
                "同它的名字一样，这是一款号称史上最健康的汉堡。这是肯德基和健康美食达人 Figgy Poppleton-Rice 一起带来的，" +
                "最大的看点就是它不同于传统的汉堡结构，面包片换成了花椰菜，夹着的内容包括鸡胸肉丝和羽衣甘蓝，" +
                "无糖的杏仁酸奶取代了蛋黄酱，辅料当中居然还有冰块。你想想这东西是怎么做出来的。" +
                "看视频吧：值得一提的是，在 Figgy Poppleton-Rice 同学 Twitter 的自我介绍当中，" +
                "不仅介绍自己是健康美食狂热迷，还有花椰菜鉴赏家……这似乎促成了这次的合作呢。" +
                "但看到最后你就知道了，这还是一次官方恶搞啊，实际上肯德基要推出的是名为“Dirty Louisiana”的新汉堡，" +
                "我们熟悉的味道又回来了——炸鸡块、腌黄瓜，奶酪有两层，够劲够粗旷，对得上它的名字，但只是让人稍微费解一点，" +
                "为什么要用健康汉堡来恶搞自己呢对吧，或许是希望让大家了解“Dirty Louisiana”并不影响健康，" +
                "和那个“Clean Eating”一样安全？……留给你自己品味吧，新汉堡会在英国和爱尔兰地区上架，可以期待它卖到更多地方。";

		/*Map<Integer,String> keySentences=summary.summarize(text,"MMR");
		for(Map.Entry<Integer,String> entry:keySentences.entrySet()){
			System.out.println(entry.getKey()+"   "+entry.getValue());
		}*/

        //meanstd利用均值和标准差过滤非重要句子
//        String keySentences=summary.summarize(text2, "meanstd");
//        System.out.println("meanstd利用均值和标准差过滤非重要句子: "+keySentences);

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
