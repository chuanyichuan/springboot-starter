package cc.kevinlu.logger.starter.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;

import com.alibaba.fastjson.JSONObject;

import cc.kevinlu.logger.starter.constants.LoggerConstants;
import cc.kevinlu.logger.starter.context.RequestContext;
import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuan
 */
@WebFilter(urlPatterns = "/**")
@ConditionalOnClass(value = { DispatcherServlet.class })
public class TraceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HeaderMapRequestWrapper wrapper = requestWrapper(request);
        String traceId = wrapper.getHeader(LoggerConstants.GLOBAL_TRACE_ID);
        if (!StringUtils.hasText(traceId)) {
            // traceId 不存在
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
            // 存入到请求Header中
            RequestContext context = RequestContext.builder().traceId(traceId).build();
            wrapper.addHeader(LoggerConstants.GLOBAL_TRACE_ID,
                    Base64.encode(JSONObject.toJSONString(context), Charset.defaultCharset()));
        } else {
            // 存在traceId，获取详细信息
            RequestContext context = JSONObject.parseObject(Base64.decodeStr(traceId, Charset.defaultCharset()),
                    RequestContext.class);
            traceId = context.getTraceId();
        }
        // 存入到MDC
        MDC.put(LoggerConstants.GLOBAL_TRACE_ID, traceId);
        chain.doFilter(wrapper, response);
    }

    private HeaderMapRequestWrapper requestWrapper(ServletRequest request) {
        return new HeaderMapRequestWrapper((HttpServletRequest) request);
    }

    @Slf4j
    private static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

        /**
         * Constructs a request object wrapping the given request.
         *
         * @param request The request to wrap
         * @throws IllegalArgumentException if the request is null
         */
        public HeaderMapRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        private Map<String, String> headerMap = new HashMap<>();

        public void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            log.info("======>header param named [{}]", name);
            // 先从本类获取
            String value = headerMap.get(name);
            if (value == null) {
                value = super.getHeader(name);
            }
            return value;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (headerMap.containsKey(name)) {
                return Collections.enumeration(Arrays.asList(headerMap.get(name)));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            for (String key : headerMap.keySet()) {
                names.add(key);
            }
            return Collections.enumeration(names);
        }

        @Override
        public int getIntHeader(String name) {
            String value = headerMap.get(name);
            if (value != null) {
                return Integer.valueOf(value);
            }
            return super.getIntHeader(name);
        }

    }
}
