package cc.kevinlu.logger.starter.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import cc.kevinlu.logger.starter.prop.CcLoggerProperties;

/**
 * es客户端
 *
 * @author chuan
 */
public class ElasticsearchConfig implements FactoryBean<RestHighLevelClient>, InitializingBean, DisposableBean {

    private final static String SCHEME = "http";

    private RestHighLevelClient restHighLevelClient;

    @Resource
    private CcLoggerProperties  ccLoggerProperties;

    @Override
    public void destroy() throws Exception {
        if (restHighLevelClient != null) {
            restHighLevelClient.close();
        }
    }

    @Override
    public RestHighLevelClient getObject() throws Exception {
        return restHighLevelClient;
    }

    @Override
    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (ccLoggerProperties.getStorage() == null) {
            return;
        }
        CcLoggerProperties.StorageEngineProperties storage = ccLoggerProperties.getStorage();
        String address = storage.getAddress();
        if (StringUtils.isBlank(address)) {
            return;
        }
        String[] hosts = address.split(",");
        List<HttpHost> httpHostList = new ArrayList<>(hosts.length);
        for (String host : hosts) {
            String[] hp = host.split(":");
            httpHostList.add(new HttpHost(hp[0], Integer.parseInt(hp[1]), SCHEME));
        }
        buildClient(httpHostList);
    }

    private void buildClient(List<HttpHost> httpHostList) {
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHostList.toArray(new HttpHost[0])));
    }
}
