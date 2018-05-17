package saturday.domain.oneSignal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OneSignalNotification {
    private String appId;
    private OneSignalContents contents;
    private List<OneSignalFilter> filters;

    @JsonProperty("app_id")
    public String getAppId() {
        return appId;
    }

    @JsonProperty("app_id")
    public void setAppId(String appId) {
        this.appId = appId;
    }

    public OneSignalContents getContents() {
        return contents;
    }

    public void setContents(OneSignalContents contents) {
        this.contents = contents;
    }

    public List<OneSignalFilter> getFilters() {
        if (filters == null) {
            filters = new ArrayList<>();
        }
        return filters;
    }

    public void setFilters(List<OneSignalFilter>  filters) {
        this.filters = filters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneSignalNotification that = (OneSignalNotification) o;
        return Objects.equals(appId, that.appId) &&
                Objects.equals(contents, that.contents) &&
                Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {

        return Objects.hash(appId, contents, filters);
    }
}
