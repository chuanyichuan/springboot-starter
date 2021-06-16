package cc.kevinlu.mybatis.plugin.interceptors;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cc.kevinlu.mybatis.plugin.constants.CcPluginConstants;
import cc.kevinlu.mybatis.plugin.utils.SpringContextUtils;
import lombok.Data;

/**
 * @author chuan
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class UpdateInterceptor implements Interceptor {

    private Map<SqlCommandType, Set<PropEntity>> partFieldMap = new HashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        SqlCommandType commandType = mappedStatement.getSqlCommandType();
        fillParameter(commandType, parameter);
        Executor executor = (Executor) invocation.getTarget();
        return executor.update(mappedStatement, parameter);
    }

    private void fillParameter(SqlCommandType commandType, Object parameter) {
        if (parameter != null) {
            if (parameter instanceof Collection) {
                // iteration
                Iterator iter = ((Collection<?>) parameter).iterator();
                while (iter.hasNext()) {
                    Object obj = iter.hasNext();
                    fillParameter(commandType, obj);
                }
            } else {
                doFillParameter0(commandType, parameter);
            }
        }
    }

    private void doFillParameter0(SqlCommandType commandType, Object parameter) {
        if (!partFieldMap.isEmpty()) {
            Set<PropEntity> entitySet = this.partFieldMap.get(commandType);
            if (CollectionUtils.isEmpty(entitySet)) {
                return;
            }
            for (PropEntity entity : entitySet) {
                try {
                    Field updateFields = parameter.getClass().getDeclaredField(entity.getFieldName());
                    updateFields.setAccessible(true);
                    String reference = entity.getReferenceType();
                    int instanceType = entity.getInstanceType();
                    switch (instanceType) {
                        case 1:
                            // newInstance
                            updateFields.set(parameter, Class.forName(reference).newInstance());
                            break;
                        case 2:
                            // 从spring容器中获取
                            updateFields.set(parameter, SpringContextUtils.getBean(reference));
                            break;
                        default:
                            // 直接赋值
                            updateFields.set(parameter, reference);
                            break;
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void setProperties(Properties properties) {
        String fs = properties.getProperty(CcPluginConstants.THIRD_PART_FIELDS_NAME);
        if (StringUtils.hasText(fs)) {
            Map<String, String> map = JSONObject.parseObject(fs, new TypeReference<Map<String, String>>() {
            });
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String value = entry.getValue();
                String[] var = value.split(",");
                PropEntity entity = new PropEntity();
                String[] commandTypeArr = var[0].split("\\|");
                for (String type : commandTypeArr) {
                    SqlCommandType commandType = SqlCommandType.valueOf(type);
                    entity.setCommandType(commandType);
                    entity.setReferenceType(var[1]);
                    entity.setFieldName(entry.getKey());
                    entity.setInstanceType(Integer.parseInt(var[2]));
                    Set<PropEntity> set = this.partFieldMap.getOrDefault(commandType, new HashSet<>());
                    set.add(entity);
                    this.partFieldMap.put(commandType, set);
                }
            }
        }
    }

    @Data
    private static class PropEntity {
        private SqlCommandType commandType;
        private String         fieldName;
        private String         referenceType;
        private int            instanceType;
    }
}
