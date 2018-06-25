package saturday.domain.accessTokens;

import saturday.domain.accessTokenTypes.AccessTokenType;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "access_tokens")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER, name = "type_id")
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name="type_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private AccessTokenType type;

    @ManyToOne
    @JoinColumn(name="entity_id", referencedColumnName = "id", nullable = false)
    private saturday.domain.Entity entity;

    public AccessToken() {
    }

    public AccessToken(String token, AccessTokenType type, saturday.domain.Entity entity) {
        this.token = token;
        this.type = type;
        this.entity = entity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public AccessTokenType getType() {
        return type;
    }

    public void setType(AccessTokenType type) {
        this.type = type;
    }

    public saturday.domain.Entity getEntity() {
        return entity;
    }

    public void setEntity(saturday.domain.Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessToken that = (AccessToken) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(token, that.token) &&
                Objects.equals(expirationDate, that.expirationDate) &&
                Objects.equals(type, that.type) &&
                Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, token, expirationDate, type, entity);
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", expirationDate=" + expirationDate +
                ", type=" + type +
                ", entity=" + entity +
                '}';
    }
}
