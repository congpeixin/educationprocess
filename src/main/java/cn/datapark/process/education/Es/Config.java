package cn.datapark.process.education.Es;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by datapark-2 on 2016/9/26.
 */
public class Config {

    private static Map mapInstance = null;
    private static Config instance = new Config();
    public static Map getMapInstance(){
        return mapInstance;
    }
    private Config(){
        mapInstance=new HashMap();
        Properties prop = new Properties();
        String filePath = System.getProperty("user.dir") + "/src/main/resources/config.properties";
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration en = prop.propertyNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String value = prop.getProperty(key);
            mapInstance.put(key, value);
        }
    }
    public static Config getInstance(){
        return instance;
    }
    public static void main(String[]args){
        Map map = Config.getInstance().getMapInstance();
        for (Object key:map.keySet()){
            System.out.println(map.get(key));
        }
    }
}
