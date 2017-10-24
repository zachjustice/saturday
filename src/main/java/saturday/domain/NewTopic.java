package saturday.domain;

import org.hibernate.validator.constraints.NotEmpty;

public class NewTopic {

    private String name;
    private String description;
    private int creator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "NewTopic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creator=" + creator +
                '}';
    }
}
