package saturday.domain.oneSignal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OneSignalFilters {
    private List<OneSignalFilter> filters;

    public List<OneSignalFilter> getFilters() {
        if (filters == null) {
            filters = new ArrayList<>();
        }

        return filters;
    }

    public void setFilters(List<OneSignalFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneSignalFilters that = (OneSignalFilters) o;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {

        return Objects.hash(filters);
    }
}


