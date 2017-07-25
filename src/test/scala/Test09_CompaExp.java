import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cluster on 2017/7/25.
 */



    public class Test09_CompaExp {
    public static void main(String[] args) {
        Test09_Stu stu1=new Test09_Stu("foyd",22.1);
        Test09_Stu stu2=new Test09_Stu("teddy",19.1);
        Test09_Stu stu3=new Test09_Stu("dean",26.1);
        Test09_Stu stu4=new Test09_Stu("lucas",19.1);
        Test09_Stu stu5=new Test09_Stu("tina",26.1);

        List<Test09_Stu> list = new ArrayList<Test09_Stu>();
        list.add(stu1);
        list.add(stu2);
        list.add(stu3);
        list.add(stu4);
        list.add(stu5);

        Comparator<Test09_Stu> comparator = new Comparator<Test09_Stu>() {
            public int compare(Test09_Stu p1, Test09_Stu p2) {//return必须是int，而str.age是double,所以不能直接return (p1.age-p2.age)
                if((p1.age-p2.age)<0)
                    return -1;
                else if((p1.age-p2.age)>0)
                    return 1;
                else return 0;
            }
        };
        //jdk 7sort有可能报错，
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        //表示，使用以前版本的sort来排序
        Collections.sort(list,comparator);

        for(int i=0;i<list.size();i++)
        {
            System.out.println(list.get(i).age+"  "+list.get(i).name);
        }

    }
}
