/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/10/9 18:39</create-date>
 *
 * <copyright file="StandTokenizer.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.tokenizer;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

/**
 * 标准分词器
 * @author hankcs
 */
public class StandardTokenizer
{
    /**
     * 预置分词器
     */
    public static final Segment SEGMENT = HanLP.newSegment();

    /**
     * 分词
     * @param text 文本
     * @return 分词结果
     */
    public static List<Term> segment(String text)
    {
        return SEGMENT.seg(text.toCharArray());
    }

    /**
     * 分词
     * @param text 文本
     * @return 分词结果
     */
    public static List<Term> segment(char[] text)
    {
        return SEGMENT.seg(text);
    }

    /**
     * 切分为句子形式
     * @param text 文本
     * @return 句子列表
     */
    public static List<List<Term>> seg2sentence(String text)
    {
        return SEGMENT.seg2sentence(text);
    }


    public static void main(String[] args){
        String text = "本文转载自socialbeta。2017 年，可口可乐“Share-a-coke” 夏日昵称瓶战役又来了，不过可口可乐在美国的玩法，还是熟悉的味道，但有了不一样的配方和玩法。而且SocialBeta 亲测发现，特别适合广告人。James，Mandy，Neil，Sue……这些印着昵称的可乐瓶仍会出现在美国的线下门店贩售，而登陆可口可乐的这一定制页面，输入昵称，立刻就可以获得一首可口可乐为你定制的专属「昵称 song」。先来听听几个版本。Share an Ice coke with sue。Share an Ice coke with James。还有绿瓶装的 Share an Ice coke with Josh。共有超过 1000 首定制曲，囊括来从 Alyssa 到 Habib 1000 个昵称，包括姓（Last name）和名（First name），如果在定制页面中输入自己的名字，还没有专属曲，则你会收到来自可口可乐的一首《Sorry》， 歌词也很应景：别着急，还有明年呢（if we didn't have your name there's always next year）。据了解，800 个左右的名字（First name）延续了可口可乐 2014 年在美国推出的昵称瓶包装，基本覆盖了美国 13 - 34 岁人群的 77%以上的名字，在此基础上，可口可乐今年特别增加了不少姓氏昵称瓶，如 Edwards、rios 一家等。事实上，可口可乐 4 月就曾表示要在昵称瓶战役中增加姓氏款，可口可乐品牌总监 Evan Holod 介绍增加增加姓氏昵称瓶，可以增加更多人，特别是家人一起参与到分享的活动中。可口可乐曾玩过不少音乐营销，比如在美国推出歌词瓶，将皇后乐队的We are the Champions印在可乐瓶上。在中国也曾推出来台词瓶、歌词瓶。相比之下，今年的音乐昵称瓶的魅力在于为消费者增添了一种新的新奇感和和虚荣感，当你已经习惯在瓶装可乐上看到你的名字，还能听到为你唱的歌会不会让你有点兴奋。而虚荣炫耀感则可能源于，当这 1000 首定制曲中包含你的名字，却没有你的朋友，你可能会情不自禁地想要分享自己的专属曲，又或者，你可能会不停地搜索家人好友的名字，然后分享给他们。此外，可口可乐在Share-a-coke的基础上，增加了夏日应景元素“ice Cold”，以鼓励消费者赶紧享用夏日冰爽。关于昵称曲的制作，可口可乐邀请了 9 名歌手参与录制了由 25 首原创歌曲衍生的 1000 多首昵称曲，涵盖了各种音乐风格。例如，这首《Edwards》是用电吉他即兴演奏的。参与此次 Campaign 的代理商是 Fitzco//McCann，音乐作品制作由 Score a Score 完成。当然，不可免俗地回顾一下Share-a-coke这一经典战役，最早可追溯到 2011 年，当时可口可乐于在澳大利亚推出了这一极具个性化产品，并大获成功。随后可口可乐在各地市场进行了本土化营销。在中国，可口可乐从 13 年开始，先后推出了昵称瓶、歌词瓶、台词瓶等「夏日分享季」的主题活动，今年，可口可乐为了激发年轻人兴趣，围绕年轻人的话语圈，推出了“密语瓶”，邀请年轻人传递只有彼此才懂得的情绪和乐趣。此外，今年可口可乐邀请鹿晗成为新晋代言人，也特别推出了鹿晗定制款来源：socialbeta原标题：可口可乐今夏为昵称瓶制作了 1000 首昵称 Song，听说特别适合广告人更多专业报道，请点击下载“界面新闻”APP0";
        StandardTokenizer std = new StandardTokenizer();
        List<Term> list = std.segment(text);
        System.out.print(list);
    }


}
