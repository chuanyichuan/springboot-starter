package cc.kevinlu.json.serializer.starter.processor;

/**
 * 类处理
 * 
 * @author chuan
 */
public class SupportCondition {

    private Class[] filterClass;

    private Object  entity;

    public SupportCondition(Builder builder) {
        this.filterClass = builder.filterClass;
        this.entity = builder.entity;
    }

    public Class[] getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class[] filterClass) {
        this.filterClass = filterClass;
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

        private Class[] filterClass;

        private Object  entity;

        public Builder filterClass(Class[] filterClass) {
            this.filterClass = filterClass;
            return this;
        }

        public Builder entity(Object entity) {
            this.entity = entity;
            return this;
        }

        public SupportCondition build() {
            return new SupportCondition(this);
        }

    }

}
