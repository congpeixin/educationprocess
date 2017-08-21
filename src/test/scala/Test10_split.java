/**
 * Created by cluster on 2017/7/25.
 */
public class Test10_split {
    public static void main(String[] args){
        String a = "日前肯德基英国和爱尔兰地区的 YouTube 发布了一条广告视频，介绍自己的新品“Clean Eating”汉堡，同它的名字一样，这是一款号称史上最健康的汉堡。&&这是肯德基和健康美食达人 Figgy Poppleton-Rice 一起带来的，最大的看点就是它不同于传统的汉堡结构，面包片换成了花椰菜，夹着的内容包括鸡胸肉丝和羽衣甘蓝，无糖的杏仁酸奶取代了蛋黄酱，辅料当中居然还有冰块。&&你想想这东西是怎么做出来的。&&";
        String[] str = a.split("&&");

//        for (int i = 0; i<str.length;i++){
//            System.out.println(str[i]);
//        }
//

        String img = "<img src=\"/d/file/video/food/2017-08-15/836c7f445a1fdf1edf4748ea2febf652.jpg\" alt=\"英国赛百味广告 人生不要重复\">";
        if (img.contains("<img")){
            System.out.println("you");
        }else {
            System.out.println("wu");
        }
    }
}
