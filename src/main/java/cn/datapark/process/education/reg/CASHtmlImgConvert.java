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
        String oldSrcPath=changeImgSrc("<img src=\"/d/file/video/food/2017-08-15/836c7f445a1fdf1edf4748ea2febf652.jpg\" alt=\"英国赛百味广告 人生不要重复\">","http://iwebad.com/video/3017.html");
//        if(oldSrcPath!=null){
//            for(String str:oldSrcPath){
//                System.out.println(str);
//            }
//        }
        System.out.println(oldSrcPath);
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
    public static String changeImgSrc(String htmlPath, String url)
    {

        StringBuilder oldSrcPath = new StringBuilder();
        java.net.URL  webpageurl = null;
        String websiteURL = null;

        try {
            Parser parser = new Parser(htmlPath);
            //标签名过滤器
            NodeFilter filter = new TagNameFilter ("img");
            NodeList nodes = parser.extractAllNodesThatMatch(filter);
            System.out.println("nodes-------:"+nodes.toHtml());
//            <img src="/d/file/video/necessary/2017-08-15/f74fc3bbdf7fc45d4088b7f26471bb06.jpg" alt=\"致敬\u201c万米王\u201d莫·法拉赫 耐克拍了一支\u201c励志鸡汤\u201d\"/>
            Node eachNode = null;
            ImageTag imageTag = null;
            String headURL  = url.split("\\:")[0];
            System.out.println("headURL-----------"+headURL);
            webpageurl = new  java.net.URL(url);
            websiteURL = webpageurl.getHost();
            System.out.println("websiteURL-----------"+headURL+"://"+websiteURL);
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
                            path=headURL+"://"+websiteURL+path;
//                        oldSrcPath.append(path+",");
                        oldSrcPath.append(path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str=oldSrcPath.toString();
        //返回图片数组
//        return str.substring(0,str.length()-1).split(",");
        return str;
    }



//    public static String changeImgSrc_1(String htmlPath, String url)
//    {
//
//        StringBuilder oldSrcPath = new StringBuilder();
//        java.net.URL  webpageurl = null;
//        String websiteURL = null;
//
//        try {
//            Parser parser = new Parser(htmlPath);
//            //标签名过滤器
//            NodeFilter filter = new TagNameFilter ("img");
//            NodeList nodes = parser.extractAllNodesThatMatch(filter);
//            System.out.println("nodes-------:"+nodes.toHtml());
//            Node eachNode = null;
//            ImageTag imageTag = null;
//            String headURL  = url.split("\\:")[0];
//            System.out.println("headURL-----------"+headURL);
//            webpageurl = new  java.net.URL(url);
//            websiteURL = webpageurl.getHost();
//            System.out.println("websiteURL-----------"+headURL+"://"+websiteURL);
//            if (nodes != null)
//            {
////              遍历所有的img节点
//                for (int i = 0; i < nodes.size(); i++)
//                {
//                    eachNode = (Node)nodes.elementAt(i);
//                    if (eachNode instanceof ImageTag)
//                    {
//                        imageTag = (ImageTag)eachNode;
////                      获得html文本的原来的src属性
//                        String path=imageTag.getAttribute("src");
//                        System.out.println("path-------:"+path);
//                        if(path.startsWith(""))
//                            path=headURL+"://"+websiteURL+path;
//                        oldSrcPath.append(path+",");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String str=oldSrcPath.toString();
//        //返回图片数组
//        return str;
//    }



}
