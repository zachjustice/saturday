package saturday.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

public class TopicPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "label")
    private String permission;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {

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
