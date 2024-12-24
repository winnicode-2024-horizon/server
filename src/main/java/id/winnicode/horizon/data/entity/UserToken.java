package id.winnicode.horizon.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_token")
public class UserToken {

    @Id
    private String id;

    private String token;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;
}
