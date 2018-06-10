package saturday.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "entities")
public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "token")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String token;

    @Column(name = "email")
    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String email;

    @Column(name = "password_hash")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // optional since they might be logging via social media

    @Column(name = "name")
    private String name;

    @Column(name = "birthday")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @Column(name = "gender" )
    private String gender;

    @Column(name = "isEmailConfirmed")
    private boolean isEmailConfirmed;

    @Column(name = "created", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    @Column(name = "modified", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modified;

    @Column(name = "picture_url" )
    private String pictureUrl;

    @Column(name = "fb_id" )
    private Long fbId ;

    @Column(name = "fb_access_token" )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String fbAccessToken;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "entity_roles", joinColumns = @JoinColumn(name = "entity_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @JsonIgnore
    public boolean isAdmin() {
        // TODO remove constant
        for(Role r: roles) {
            if(r.getId() == 2) {
                return true;
            }
        }

        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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


    public boolean getIsEmailConfirmed() {
        return isEmailConfirmed;
    }

    public void setIsEmailConfirmed(boolean isEmailConfirmed ) {
        this.isEmailConfirmed = isEmailConfirmed;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getFbId() {
        return fbId;
    }

    public void setFbId(Long fbId) {
        this.fbId = fbId;
    }

    public String getFbAccessToken() {
        return fbAccessToken;
    }

    public void setFbAccessToken(String fbAccessToken) {
        this.fbAccessToken = fbAccessToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id == entity.id &&
                isEmailConfirmed == entity.isEmailConfirmed &&
                Objects.equals(token, entity.token) &&
                Objects.equals(email, entity.email) &&
                Objects.equals(password, entity.password) &&
                Objects.equals(name, entity.name) &&
                Objects.equals(birthday, entity.birthday) &&
                Objects.equals(gender, entity.gender) &&
                Objects.equals(created, entity.created) &&
                Objects.equals(modified, entity.modified) &&
                Objects.equals(pictureUrl, entity.pictureUrl) &&
                Objects.equals(fbId, entity.fbId) &&
                Objects.equals(fbAccessToken, entity.fbAccessToken) &&
                Objects.equals(roles, entity.roles);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, token, email, password, name, birthday, gender, isEmailConfirmed, created, modified, pictureUrl, fbId, fbAccessToken, roles);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", gender='" + gender + '\'' +
                ", isEmailConfirmed=" + isEmailConfirmed +
                ", created=" + created +
                ", modified=" + modified +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", fbId=" + fbId +
                ", fbAccessToken='" + fbAccessToken + '\'' +
                '}';
    }
}
