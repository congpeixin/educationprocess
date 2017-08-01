/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/9/18 18:37</create-date>
 *
 * <copyright file="KeywordExtractor.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.summary;

import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.util.List;

/**
 * 提取关键词的基类
 * 过滤词性的地方
 * @author hankcs
 */
public class KeywordExtractor
{
    /**
     * 默认分词器
     */
    static Segment defaultSegment = StandardTokenizer.SEGMENT;

    /**
     * 是否应当将这个term纳入计算，原来过滤的留下的词 词性属于名词n、动词、副词d、形容词a
     *
     * 现在只要名词 n 开头，还有术语g 开头
     *
     * @param term
     * @return 是否应当
     */
    public boolean shouldInclude(Term term)
    {
        // 除掉停用词  停用词词性为 null
        if (term.nature == null) return false;
        String nature = term.nature.toString();
        char firstChar = nature.charAt(0);
        switch (firstChar)
        {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'j':
            case 'h':
            case 'i':
            case 'k':
            case 'l':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'y':
            case 'z':
            case 'x':
            case 'w':
            case 'v':
            {
                return false;
            }

//            case 'g':
//            case 'n':
//
//            {
//                return true;
//            }
            default:
            {
                if (term.word.trim().length() > 1 && !CoreStopWordDictionary.contains(term.word))
                {
                    return true;
                }
            }
            break;
        }

        return false;
    }

    /**
     * 设置关键词提取器使用的分词器
     * @param segment 任何开启了词性标注的分词器
     * @return 自己
     */
    public KeywordExtractor setSegment(Segment segment)
    {
        defaultSegment = segment;
        return this;
    }

    public static void main(String[] args){
        String content = "一个关于营销的广告";
        List<Term> termList = defaultSegment.seg(content);
        System.out.print(termList);
    }

}
