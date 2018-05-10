package saturday.domain.oneSignal;

import java.util.Objects;

public class OneSignalFilter {
    private String field;
    private String key;
    private String relation;
    private String value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneSignalFilter that = (OneSignalFilter) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(key, that.key) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(field, key, relation, value);
    }
}

