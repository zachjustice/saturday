package saturday.domain;

public class NewTopicContent {
    private String name;
    private String title;
    private String subtitle;
    private String description;
    private Entity creator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Entity getCreator() {
        return creator;
    }

    public void setCreator(Entity creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "NewTopicContent{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", description='" + description + '\'' +
                ", creator=" + creator +
                '}';
    }
}
