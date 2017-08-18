package cn.datapark.process.education.reg;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;

/**
 * Html 中的body体中提取出Img标签中的src值
 *
 * @author XY
 *
 */
public class CASHtmlImgConvert {

    public static void main(String[] args) {
//演示
        String[] oldSrcPath=changeImgSrc("<img alt=\"\" src=\"/attached/image/20160116/20160116141455_775.jpg\" />","http://www.czb8688.com");
        if(oldSrcPath!=null){
            for(String str:oldSrcPath){
                System.out.println(str);
            }
        }
    }

    public static boolean isEmpty(String str){
        if(str!=null&&(!str.equals("")))
            return false;
        else
            return true;
    }

    /**
     *
     * @param htmlPath 本地的html路径 或者body
     */
    private static String[] changeImgSrc(String htmlPath,String url)
    {    StringBuilder oldSrcPath = new StringBuilder();
        try {
            Parser parser = new Parser(htmlPath);
            //标签名过滤器
            NodeFilter filter = new TagNameFilter ("img");
            NodeList nodes = parser.extractAllNodesThatMatch(filter);
            System.out.println("nodes-------:"+nodes.toHtml());
            Node eachNode = null;
            ImageTag imageTag = null;

            if (nodes != null)
            {
//              遍历所有的img节点
                for (int i = 0; i < nodes.size(); i++)
                {
                    eachNode = (Node)nodes.elementAt(i);
                    if (eachNode instanceof ImageTag)
                    {
                        imageTag = (ImageTag)eachNode;
//                      获得html文本的原来的src属性
                        String path=imageTag.getAttribute("src");
                        System.out.println("path-------:"+path);
                        if(path.startsWith(""))
                            path=url+path;
                        oldSrcPath.append(path+",");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str=oldSrcPath.toString();
        //返回图片数组
        return str.substring(0,str.length()-1).split(",");
    }




}
