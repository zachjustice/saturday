package saturday.domain;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "topic_invite_statuses")
public class TopicInviteStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="label")
    private String role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "TopicInviteStatus{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
