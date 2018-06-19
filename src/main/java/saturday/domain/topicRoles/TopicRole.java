package saturday.domain.topicRoles;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "topic_roles")
public class TopicRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "label")
    private String role;

    public TopicRole() {
    }

    public TopicRole(int id, String role) {
        this.id = id;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    protected void setRole(String role) {

    }

    @Override
    public String toString() {
        return "TopicRole{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicRole topicRole = (TopicRole) o;
        return id == topicRole.id &&
                Objects.equals(role, topicRole.role);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, role);
    }
}
