package cc.kevinlu.logger.starter.storage;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;

import cc.kevinlu.logger.starter.annotation.StorageMapping;
import cc.kevinlu.logger.starter.constants.StorageType;
import cc.kevinlu.logger.starter.context.AppContextUtils;
import cc.kevinlu.logger.starter.entity.ExceptionLog;
import cc.kevinlu.logger.starter.event.helper.ExLogHelper;
import cc.kevinlu.logger.starter.prop.CcLoggerProperties;
import cc.kevinlu.logger.starter.storage.strategy.StorageStrategy;

/**
 * @author chuan
 */
@StorageMapping(StorageType.NACOS)
public class NacosSupport implements StorageStrategy {

    private CcLoggerProperties ccLoggerProperties;

    private ConfigService      configService;

    public static final String GROUP_KEY   = "group";
    public static final String DATA_ID_KEY = "data-id";

    private static String      GROUP       = "DEFAULT_GROUP";
    private static String      DATA_ID     = "ex-log-records";

    @Override
    public void storage(ExceptionLog exLog) {
        try {
            String content = configService.getConfig(DATA_ID, GROUP, 30000L);
            JSONArray array = ExLogHelper.exLogArray(exLog, content);
            configService.publishConfig(DATA_ID, GROUP, array.toJSONString(), ConfigType.JSON.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        this.ccLoggerProperties = AppContextUtils.getBean(CcLoggerProperties.class);
        if (ccLoggerProperties.getStorage() == null) {
            return;
        }
        Map<String, String> extension = ccLoggerProperties.getStorage().getExtension();
        if (extension == null) {
            return;
        }
        GROUP = extension.getOrDefault(GROUP_KEY, GROUP);
        DATA_ID = extension.getOrDefault(DATA_ID_KEY, DATA_ID);
        try {
            configService = NacosFactory.createConfigService(ccLoggerProperties.getStorage().getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
