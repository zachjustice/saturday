package storyboard.domain;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zachjustice on 7/30/17.
 */
@Entity
@Table(name = "actions")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "description")
    @Length(max = 500, message = "*Action description must be less than 500 characters")
    @NotEmpty(message = "*Please provide an action description.")
    private String description;

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

    @ManyToOne()
    @JoinColumn(name = "prev_page", referencedColumnName = "id", nullable=false)
    private Page prevPage;

    @ManyToOne()
    @JoinColumn(name = "next_page", referencedColumnName = "id")
    private Page nextPage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getPrevPage() {
        return prevPage == null ? null : prevPage.getId();
    }

    public void setPrevPage(Page prevPage) {
        this.prevPage = prevPage;
    }

    public Integer getNextPage() {
        return nextPage == null ? null : nextPage.getId();
    }

    public void setNextPage(Page nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public String toString() {
        String prevPageStr = this.prevPage == null ? "null" : prevPage.getId() + "";
        String nextPageStr = this.nextPage == null ? "null" : nextPage.getId() + "";
        String createdStr = this.created == null ? "null" : created.toString();
        String modifiedStr = this.modified == null ? "null" : modified.toString();

        return "Action{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", created=" + createdStr +
                ", modified=" + modifiedStr +
                ", author=" + author.getId() +
                ", prevPage=" + prevPageStr +
                ", nextPage=" + nextPageStr +
                '}';
    }
}
