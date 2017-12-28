package saturday.domain;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "topic_member_statuses")
public class TopicMemberStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="label")
    private String label;

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

    @Override
    public String toString() {
        return "TopicMemberStatus{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}
