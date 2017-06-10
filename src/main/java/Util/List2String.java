package Util;

import java.util.List;

/**
 * Created by cluster on 2017/6/9.
 */
public class List2String {
    public String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0,sb.toString().length()-1);
    }
}
