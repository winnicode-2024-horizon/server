package id.winnicode.horizon.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import id.winnicode.horizon.data.entity.News
import id.winnicode.horizon.data.entity.User
import id.winnicode.horizon.data.entity.UserToken
import id.winnicode.horizon.data.repository.NewsRepository
import id.winnicode.horizon.data.repository.UserRepository
import id.winnicode.horizon.data.repository.UserTokenRepository
import id.winnicode.horizon.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import kotlin.jvm.optionals.getOrNull


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/snippets")
class UserBookmarkControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userTokenRepository: UserTokenRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Test
    fun bookmarksFailed() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/bookmarks")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(
            document(
                "news-add-bookmarks-fail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun bookmarksSuccess() {
        var token: String? = null

        val user = User()
        user.apply {
            username = "test"
            firstName = "first name"
            lastName = "last name"
            email = "abc@example.com"
            password = BCrypt.hashpw("rahasia", BCrypt.gensalt())
        }
        userRepository.save(user)

        val request: LoginUserRequest =
            LoginUserRequest(
                email = "abc@example.com",
                password = "rahasia"
            )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk()
        ).andDo { result: MvcResult ->
            val response: WebResponse<LoginUserResponse> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<LoginUserResponse>>() {}
            )
            assertNull(response.message)
            assertNotNull(response.data.token)
            assertNotNull(response.data.expiredAt)

            val userTokenDb: UserToken? = userTokenRepository.findByToken(response.data.token).getOrNull()
            assertNotNull(userTokenDb)
            assertEquals(userTokenDb!!.token, response.data.token)

            token = response.data.token
        }

        assertNotNull(token)



        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/bookmarks?id=2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer $token"
                )
        ).andExpectAll(
            status().isOk()
        ).andDo(
            document(
                "news-add-bookmarks-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer $token"
                )
        ).andExpectAll(
            status().isOk()
        ).andDo { result: MvcResult ->
            val response: WebResponse<NewsResponse> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<NewsResponse>>() {}
            )
            assertNull(response.message)
            assertEquals("${ResponseStatusType.SUCCESS}", response.status)
            assertEquals(1, response.data.size)
        }.andDo(
            document(
                "news-get-bookmarks-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/bookmarks/news/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer $token"
                )
        ).andExpectAll(
            status().isOk()
        ).andDo { result: MvcResult ->
            val response: WebResponse<Boolean> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<Boolean>>() {}
            )
            assertNull(response.message)
            assertEquals("${ResponseStatusType.SUCCESS}", response.status)
            assertTrue(response.data)
        }.andDo(
            document(
                "news-get-is-bookmarked-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/users/bookmarks?id=2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer $token"
                )
        ).andExpectAll(
            status().isOk()
        ).andDo(
            document(
                "news-delete-bookmarks-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer $token"
                )
        ).andExpectAll(
            status().isOk()
        ).andDo { result: MvcResult ->
            val response: WebResponse<NewsResponse> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<NewsResponse>>() {}
            )
            assertNull(response.message)
            assertEquals("${ResponseStatusType.SUCCESS}", response.status)
            assertEquals(0, response.data.size)
        }
    }
}