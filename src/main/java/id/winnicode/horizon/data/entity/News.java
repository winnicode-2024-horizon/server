package id.winnicode.horizon.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "news")
public class News {

    @Id
    private Long id;

    private String title;

    @CreationTimestamp
    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "image_url")
    private String imageUrl;

    private String url;

    @Enumerated(EnumType.STRING)
    private NewsCategory category;

    private String author;

    private String description;

    private String content;

    @OneToMany(cascade = CascadeType.DETACH)
    @JoinColumn(name = "news_id", referencedColumnName = "id")
    private List<UserComment> comments;
}


