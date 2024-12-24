package id.winnicode.horizon.data.repository;

import id.winnicode.horizon.data.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query(value = "SELECT * FROM news WHERE MATCH(title, description, content) AGAINST(:query)", nativeQuery = true)
    List<News> searchNews(@Param("query") String query);

    @Query(value = "SELECT * FROM news WHERE category = :category", nativeQuery = true)
    List<News> getByCategory(@Param("category") String category);
}
