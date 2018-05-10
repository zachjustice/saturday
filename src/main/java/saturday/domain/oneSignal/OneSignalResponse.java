package saturday.domain.oneSignal;

import java.util.Objects;

public class OneSignalResponse {

    private String id;
    private Integer recipients;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRecipients() {
        return recipients;
    }

    public void setRecipients(Integer recipients) {
        this.recipients = recipients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneSignalResponse that = (OneSignalResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(recipients, that.recipients);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, recipients);
    }
}
