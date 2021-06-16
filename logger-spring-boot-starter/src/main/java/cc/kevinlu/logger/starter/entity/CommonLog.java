package cc.kevinlu.logger.starter.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author chuan
 */
@Data
public class CommonLog implements Serializable {

    /**
     * 日志类型
     */
    private String        type;
    /**
     * 跟踪ID
     */
    private String        traceId;
    /**
     * 日志标题
     */
    private String        title;
    /**
     * 操作内容
     */
    private String        operation;
    /**
     * 执行方法
     */
    private String        method;

    /**
     * 请求路径
     */
    private String        url;
    /**
     * 参数
     */
    private String        params;
    /**
     * ip地址
     */
    private String        ip;
    /**
     * 耗时
     */
    private Long          executeTime;
    /**
     * 地区
     */
    private String        location;
    /**
     * 创建人
     */
    private String        createBy;
    /**
     * 更新人
     */
    private String        updateBy;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 删除标识
     */
    private String        isDeleted;
    /**
     * 异常信息
     */
    private String        exception;

}
