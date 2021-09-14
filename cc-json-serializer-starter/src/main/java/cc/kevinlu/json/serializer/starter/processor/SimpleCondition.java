package cc.kevinlu.json.serializer.starter.processor;

/**
 * 简单命令实体
 */
public class SimpleCondition {

    private String condition;

    private Object entity;

    public SimpleCondition(Builder builder) {
        this.condition = builder.condition;
        this.entity = builder.entity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String condition;

        private Object entity;

        public Builder() {
        }

        public Builder condition(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder entity(Object entity) {
            this.entity = entity;
            return this;
        }

        public SimpleCondition build() {
            return new SimpleCondition(this);
        }
    }

}
