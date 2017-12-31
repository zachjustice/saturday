package saturday.domain;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.sql.Date;

@javax.persistence.Entity
@Table(name = "access_tokens")
public class AccessToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(targetEntity = Entity.class)
    @JoinColumn(name = "entity_id", nullable = false)
    private Entity entity;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration_date")
    private Date expirationDate;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        if(expiryTimeInMinutes == 0) {
            expiryTimeInMinutes = EXPIRATION;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "id=" + id +
                ", entity=" + entity +
                ", token='" + token + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
