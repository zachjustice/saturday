package storyboard.domain;


import com.fasterxml.jackson.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zachjustice on 7/29/17.
 */
@Entity
@Table(name = "pages")
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "content")
    @Length(max = 500, message = "*Page content must be less than 500 characters")
    @NotEmpty(message = "*Page content is empty.")
    private String content;

    @Column(name = "created", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    @Column(name = "modified", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modified;

    @ManyToOne
    @JoinColumn(name="author_id", nullable=false)
    private Author author;

    @OneToMany(mappedBy = "prevPage", cascade = CascadeType.ALL)
    private List<Action> actions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Action> getActions() {
        return actions == null ? new ArrayList<>() : actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        List<Integer> actionIds = actions.stream()
                .map(Action::getId)
                .collect(Collectors.toList());

        String createdStr = this.created == null ? "null" : created.toString();
        String modifiedStr = this.modified == null ? "null" : modified.toString();

        return "Page{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", created=" + createdStr +
                ", modified=" + modifiedStr +
                ", author=" + author.getId() +
                ", actions=" + actionIds.toString() +
                '}';
    }
}
