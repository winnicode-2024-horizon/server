package id.winnicode.horizon.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import id.winnicode.horizon.data.entity.User
import id.winnicode.horizon.data.entity.UserToken
import id.winnicode.horizon.data.repository.UserRepository
import id.winnicode.horizon.data.repository.UserTokenRepository
import id.winnicode.horizon.model.LoginUserRequest
import id.winnicode.horizon.model.LoginUserResponse
import id.winnicode.horizon.model.ResponseStatusType
import id.winnicode.horizon.model.WebResponse
import org.junit.jupiter.api.AfterEach
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
import kotlin.jvm.optionals.getOrNull

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/snippets")
class AuthControllerTest {

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

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
        userTokenRepository.deleteAll()
    }

    @Test
    @Throws(Exception::class)
    fun loginFailed() {
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
            status().isUnauthorized()
        ).andDo { result: MvcResult ->
            val response: WebResponse<String> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<String>>() {}
            )

            assert(response.status == "${ResponseStatusType.FAIL}")
            assertNotNull(response.message)
        }.andDo(
            document(
                "auth-login-fail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun loginSuccess() {
        val user: User = User()
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

            val userDb: User? = userRepository.findById(user.username).getOrNull()
            assertNotNull(userDb)

            val userToken: UserToken? = userTokenRepository.findByToken(response.data.token).getOrNull()
            assertNotNull(userToken)
            assertEquals(userToken!!.token, response.data.token)
        }.andDo(
            document(
                "auth-login-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun logoutFailed() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/logout")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(
            document(
                "auth-logout-fail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun logoutSuccess() {
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
            MockMvcRequestBuilders.post("/api/users/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer $token"
                )
        ).andExpectAll(
            status().isOk()
        ).andDo { result: MvcResult ->
            val response: WebResponse<String> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<String>>() {}
            )
            assertNull(response.message)
            assertEquals("${ResponseStatusType.SUCCESS}", response.status)
            assertEquals("Ok", response.data)

            val userTokenDb: UserToken? = userTokenRepository.findByToken(token!!).getOrNull()
            assertNull(userTokenDb)
        }.andDo(
            document(
                "auth-logout-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }
}