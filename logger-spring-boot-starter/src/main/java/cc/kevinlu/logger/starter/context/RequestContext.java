package cc.kevinlu.logger.starter.context;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author chuan
 */
@Data
@Builder
public class RequestContext {

    /**
     * traceId
     */
    private String traceId;

    @Tolerate
    public RequestContext() {
    }
}
