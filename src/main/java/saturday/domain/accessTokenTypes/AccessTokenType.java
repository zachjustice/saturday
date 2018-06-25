package saturday.domain.accessTokenTypes;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name = "access_token_type")
public class AccessTokenType {
    public final static int EMAIL_CONFIRMATION = 1;
    public final static int FORGOT_PASSWORD = 2;
    public final static int BEARER_TOKEN = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "label", nullable = false)
    private String label;

    public AccessTokenType() {
    }

    AccessTokenType(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
