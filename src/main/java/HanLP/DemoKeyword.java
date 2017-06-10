/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/7 19:25</create-date>
 *
 * <copyright file="DemoChineseNameRecoginiton.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014+ 上海林原信息科技有限公司. All Right Reserved+ http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package HanLP;

import com.hankcs.hanlp.HanLP;

import java.util.List;

/**
 * 关键词提取
 * @author hankcs
 */
    public class DemoKeyword
    {
        public static void main(String[] args)
        {
            String content = "Enrique Norton，Herzog & de Meuron。螺型形的结构将位于南海岸东北边的柯林斯公园。 Zaha Hadid has become the latest in a string of architects to design a car park for Miami Beach. The car park was commissioned following the popularity of other completed Miami garages by architects including Frank Gehry， Enrique Norton and Herzog & de Meuron， whose concrete and glass structure was featured last year. The spiraling structure will be located in the Collins Park area in the northeast of South Beach. ";
            List<String> keywordList = HanLP.extractKeyword(content, 10);
            System.out.println(keywordList);
        }
}
