package id.winnicode.horizon.service;

import id.winnicode.horizon.data.entity.News;
import id.winnicode.horizon.data.entity.User;
import id.winnicode.horizon.data.entity.UserComment;
import id.winnicode.horizon.data.entity.UserToken;
import id.winnicode.horizon.data.repository.NewsRepository;
import id.winnicode.horizon.data.repository.UserCommentRepository;
import id.winnicode.horizon.model.NewsResponse;
import id.winnicode.horizon.model.UserCommentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    private final UserCommentRepository userCommentRepository;

    @Autowired
    public NewsService(NewsRepository newsRepository, UserCommentRepository userCommentRepository) {
        this.newsRepository = newsRepository;
        this.userCommentRepository = userCommentRepository;
    }

    @Transactional(readOnly = true)
    public NewsResponse news() {
        List<News> news = newsRepository.findAll();
        return NewsResponse.builder()
                .size(news.size())
                .news(news)
                .build();
    }

    @Transactional(readOnly = true)
    public NewsResponse newsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow();
        return NewsResponse.builder()
                .size(1)
                .news(List.of(news))
                .build();
    }

    @Transactional(readOnly = true)
    public NewsResponse newsByCategory(String category) {
        List<News> news = newsRepository.getByCategory(category);
        return NewsResponse.builder()
                .size(news.size())
                .news(news)
                .build();
    }

    @Transactional(readOnly = true)
    public NewsResponse searchNews(String query) {
        List<News> news = newsRepository.searchNews(query);
        return NewsResponse.builder()
                .size(news.size())
                .news(news)
                .build();
    }

    @Transactional()
    public void commentNews(
            UserCommentRequest request,
            UserToken userToken,
            Long newsId
    ) {
        System.out.println("Comment");
        String comment = request.getComment();
        User user = userToken.getUser();

        UserComment userComment = new UserComment(
                0L,
                comment,
                newsId,
                user
        );

        userCommentRepository.save(userComment);
    }
}
