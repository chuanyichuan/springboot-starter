package cc.kevinlu.logger.starter.storage;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

import com.alibaba.fastjson.JSONObject;

import cc.kevinlu.logger.starter.annotation.StorageMapping;
import cc.kevinlu.logger.starter.constants.StorageType;
import cc.kevinlu.logger.starter.context.AppContextUtils;
import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.prop.CcLoggerProperties;
import cc.kevinlu.logger.starter.storage.strategy.StorageStrategy;

/**
 * @author chuan
 */
@StorageMapping(StorageType.ELASTICSEARCH)
public class ElasticsearchSupport implements StorageStrategy {

    private RestHighLevelClient restHighLevelClient;
    private CcLoggerProperties  ccLoggerProperties;

    public static final String  DOCUMENT_INDEX_KEY = "document-index";
    public static final String  RETRY_TIMES_KEY    = "retry-times";

    /**
     * 文档索引
     */
    private static String       documentIndex      = "cc-ex-log-index";
    /**
     * 出错重试次数
     */
    private static int          retryTimes         = 5;

    @Override
    public void storage(ExceptionLog exLog) {
        IndexRequest indexRequest = new IndexRequest().index(documentIndex).id(UUID.randomUUID().toString())
                .source(JSONObject.toJSONString(exLog), XContentType.JSON);
        int times = 0;
        while (times < retryTimes) {
            try {
                IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                if (indexResponse.status().equals(RestStatus.CREATED) || indexResponse.status().equals(RestStatus.OK)) {
                    break;
                }
                times++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init() {
        this.ccLoggerProperties = AppContextUtils.getBean(CcLoggerProperties.class);
        if (ccLoggerProperties.getStorage() == null) {
            return;
        }
        CcLoggerProperties.StorageEngineProperties storage = ccLoggerProperties.getStorage();
        Map<String, String> extension = storage.getExtension();
        if (extension == null || !extension.containsKey(DOCUMENT_INDEX_KEY)) {
            return;
        }
        documentIndex = extension.getOrDefault(DOCUMENT_INDEX_KEY, documentIndex);
        if (extension.containsKey(RETRY_TIMES_KEY)) {
            retryTimes = Integer.parseInt(extension.getOrDefault(RETRY_TIMES_KEY, "5"));
        }
    }
}
