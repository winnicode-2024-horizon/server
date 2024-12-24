package id.winnicode.horizon.controller;

import id.winnicode.horizon.data.entity.News;
import id.winnicode.horizon.model.UserCommentRequest;
import id.winnicode.horizon.data.entity.UserToken;
import id.winnicode.horizon.model.NewsResponse;
import id.winnicode.horizon.model.ResponseStatusType;
import id.winnicode.horizon.model.WebResponse;
import id.winnicode.horizon.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping(
            path = "/api/news",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<NewsResponse> news() {
        NewsResponse response = newsService.news();
        return WebResponse.<NewsResponse>builder()
                .status(ResponseStatusType.SUCCESS.toString())
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/api/news/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<NewsResponse> newsById(@PathVariable Long id) {
        NewsResponse response = newsService.newsById(id);
        return WebResponse.<NewsResponse>builder()
                .status(ResponseStatusType.SUCCESS.toString())
                .data(response)
                .build();
    }

    // /api/news/category?category=general
    @GetMapping(
            path = "/api/news/category",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<NewsResponse> newsByCategory(@RequestParam String category) {
        NewsResponse response = newsService.newsByCategory(category.toUpperCase());
        return WebResponse.<NewsResponse>builder()
                .status(ResponseStatusType.SUCCESS.toString())
                .data(response)
                .build();
    }

    // /api/news/search?q=news
    @GetMapping(
            path = "/api/news/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<NewsResponse> searchNews(@RequestParam String q) {
        NewsResponse response = newsService.searchNews(q);
        return WebResponse.<NewsResponse>builder()
                .status(ResponseStatusType.SUCCESS.toString())
                .data(response)
                .build();
    }

    // /api/news/comment?newsId=1
    @PostMapping(
            path = "/api/news/comment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> commentNews(
            @RequestBody UserCommentRequest request,
            UserToken userToken,
            @RequestParam Long newsId
    ) {
        newsService.commentNews(request, userToken, newsId);
        return WebResponse.<String>builder()
                .status(ResponseStatusType.SUCCESS.toString())
                .data("Ok")
                .build();
    }
}
