package id.winnicode.horizon.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_comments")
public class UserComment {

    @Id
    private Long id;

    private String comment;

    @JsonProperty(access = WRITE_ONLY)
    @Column(name = "news_id")
    private Long newsId;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;
}
