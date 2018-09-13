package saturday.domain;

import java.util.List;

public class InviteRequest {
    private List<String> emails;

    public InviteRequest() {
    }

    public InviteRequest(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
