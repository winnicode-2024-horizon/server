package id.winnicode.horizon.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.winnicode.horizon.data.entity.News;
import id.winnicode.horizon.data.entity.NewsCategory;
import id.winnicode.horizon.model.UserCommentRequest;
import id.winnicode.horizon.data.entity.User;
import id.winnicode.horizon.data.repository.UserRepository;
import id.winnicode.horizon.data.repository.UserTokenRepository;
import id.winnicode.horizon.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/snippets")
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        userTokenRepository.deleteAll();
    }

    @Test
    void getNewsFailed() throws Exception {
        mockMvc.perform(
                get("/api/news")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                document(
                        "news-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        );
    }

    @Test
    void getNewsSuccess() throws Exception {
        AtomicReference<String> token = new AtomicReference<>();

        User user = new User();
        user.setUsername("test");
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setEmail("abc@example.com");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));

        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest("abc@example.com", "rahasia");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            token.set(response.getData().getToken());
        });

        mockMvc.perform(
                get("/api/news")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<NewsResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getMessage());
            assertNotNull(response.getData().getNews());
        }).andDo(
                document(
                        "news-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        );
    }

    @Test
    void getNewsByIdSuccess() throws Exception {
        AtomicReference<String> token = new AtomicReference<>();

        User user = new User();
        user.setUsername("test");
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setEmail("abc@example.com");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));

        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest("abc@example.com", "rahasia");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            token.set(response.getData().getToken());
        });

        mockMvc.perform(
                get("/api/news/" + 3)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<NewsResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getMessage());
            assertNotNull(response.getData().getNews());
        }).andDo(
                document(
                        "news-id-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        );
    }

    @Test
    void getNewsByCategorySuccess() throws Exception {
        AtomicReference<String> token = new AtomicReference<>();

        User user = new User();
        user.setUsername("test");
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setEmail("abc@example.com");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));

        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest("abc@example.com", "rahasia");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            token.set(response.getData().getToken());
        });

        NewsCategory category = NewsCategory.GENERAL;

        mockMvc.perform(
                get("/api/news/category?category=" + category)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<NewsResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getMessage());
            assertNotNull(response.getData().getNews());
        }).andDo(
                document(
                        "news-category-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        );
    }

    @Test
    void searchNewsFailed() throws Exception {
        mockMvc.perform(
                get("/api/news/search?q=news")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                document(
                        "search-news-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        );
    }

    @Test
    void searchNewsSuccess() throws Exception {
        AtomicReference<String> token = new AtomicReference<>();

        User user = new User();
        user.setUsername("test");
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setEmail("abc@example.com");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));

        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest("abc@example.com", "rahasia");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            token.set(response.getData().getToken());
        });

        mockMvc.perform(
                get("/api/news/search?q=technology")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<NewsResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getMessage());
            assertNotNull(response.getData().getNews());
        }).andDo(
                document(
                        "search-news-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        );
    }

    @Test
    void commentNewsFailed() throws Exception {
        mockMvc.perform(
                get("/api/news/comment")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                document(
                        "comment-news-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        );
    }

    @Test
    void commentNewsSuccess() throws Exception {
        AtomicReference<String> token = new AtomicReference<>();

        User user = new User();
        user.setUsername("test");
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setEmail("abc@example.com");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));

        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest("abc@example.com", "rahasia");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            token.set(response.getData().getToken());
        });

        UserCommentRequest userCommentRequest = new UserCommentRequest("Good Information!");

        mockMvc.perform(
                post("/api/news/comment?newsId=1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(userCommentRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getMessage());
        }).andDo(
                document(
                        "comment-news-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        );

        mockMvc.perform(
                get("/api/news/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<NewsResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getMessage());
            List<News> news = response.getData().getNews();
            assertNotNull(news);
            assertEquals(1, news.size());
            assertEquals(1, news.get(0).getComments().size());
        });
    }
}