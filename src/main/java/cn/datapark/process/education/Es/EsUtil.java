package cn.datapark.process.education.Es;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.Map;

/**
 * Created by datapark-2 on 2016/9/26.
 */
public class EsUtil {

    private static Client esClientInstance = null;
    private static EsUtil instance = new EsUtil();
    Map mapConfig= Config.getInstance().getMapInstance();
    private static final Logger LOG = Logger.getLogger(EsUtil.class);
    public Client getESInstance() {
        return esClientInstance;
    }
    /**
     * es client
     */

    private EsUtil() {
        try {
            Settings settings= ImmutableSettings.settingsBuilder()
                    // .put("client.transport.sniff",true)
                   // .put("number_of_shards", 6)
                    .put("cluster.name", mapConfig.get("clusterName"))
                            // .put("")
                            // .put("cluster.name","es.pd.dp")
                    .build();
            esClientInstance = new TransportClient(settings)//10.45.143.87
                    .addTransportAddress(new InetSocketTransportAddress(String.valueOf(mapConfig.get("ip")), 9300));
            //LOG.info("es client init success");
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
    public static EsUtil getInstance() {
        return instance;
    }
    public static void close(Client client) {
        esClientInstance.close();
    }
}
