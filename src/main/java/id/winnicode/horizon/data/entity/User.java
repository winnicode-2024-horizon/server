package id.winnicode.horizon.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @JsonProperty(access = WRITE_ONLY)
    private String email;

    @JsonProperty(access = WRITE_ONLY)
    private String password;

    @JsonProperty(access = WRITE_ONLY)
    @ManyToMany
    @JoinTable(
            name = "users_bookmarks",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    private List<News> bookmarks;
}
