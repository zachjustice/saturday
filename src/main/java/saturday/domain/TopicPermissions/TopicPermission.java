package saturday.domain.TopicPermissions;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "topic_permissions")
public class TopicPermission {
    public final static int CAN_POST = 1;
    public final static int CAN_INVITE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "label")
    private String permission;

    public TopicPermission() {}

    TopicPermission(int id, String permission) {
        this.id = id;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "TopicPermission{" +
                "id=" + id +
                ", permission='" + permission + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicPermission that = (TopicPermission) o;
        return id == that.id &&
                Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, permission);
    }
}
