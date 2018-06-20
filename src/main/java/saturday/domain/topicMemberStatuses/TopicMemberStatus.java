package saturday.domain.topicMemberStatuses;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "topic_member_statuses")
public class TopicMemberStatus {

    public static final int PENDING    = 1;
    public static final int REJECTED   = 2;
    public static final int ACCEPTED   = 3;
    public static final int RESCINDED  = 4;
    public static final int LEFT_TOPIC = 5;

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
