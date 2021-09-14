package cc.kevinlu.json.serializer.starter.utils;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * @author chuan
 */
@Component
public class SpringEmbeddedValueUtils implements EmbeddedValueResolverAware {

    private static StringValueResolver resolver;

    public static String getProperty(String name) {
        return StringUtils.trimAllWhitespace(SpringEmbeddedValueUtils.resolver.resolveStringValue(name));
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        SpringEmbeddedValueUtils.resolver = resolver;
    }
}
