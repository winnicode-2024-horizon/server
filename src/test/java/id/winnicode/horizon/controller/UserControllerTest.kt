package id.winnicode.horizon.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import id.winnicode.horizon.data.entity.User
import id.winnicode.horizon.data.entity.UserToken
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
import kotlin.jvm.optionals.getOrNull

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/snippets")
class UserControllerTest {

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
    fun registerFailed() {
        val request: RegisterUserRequest = RegisterUserRequest(
            username = "",
            firstName = "",
            lastName = "",
            email = "",
            password = ""
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isBadRequest()
        ).andDo { result: MvcResult ->
            val response: WebResponse<String> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<String>>() {}
            )

            assertNotNull(response.message)
        }.andDo(
            document(
                "user-register-fail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun registerSuccess() {
        val request: RegisterUserRequest = RegisterUserRequest(
            username = "test",
            firstName = "first name",
            lastName = "last name",
            email = "abc@example.com",
            password = "rahasia"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk()
        ).andDo { result: MvcResult ->
            val response: WebResponse<String> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<String>>() {}
            )

            assert(response.status == "${ResponseStatusType.SUCCESS}")
            assert(response.data == "Ok")
            assertNull(response.message)
        }.andDo(
            document(
                "user-register-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun profileUserFailed() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/profile")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo(
            document(
                "user-profile-fail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun profileUserSuccess() {
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
            MockMvcRequestBuilders.get("/api/users/profile")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer $token"
                )
        ).andExpectAll(
            status().isOk()
        ).andDo { result: MvcResult ->
            val response: WebResponse<UserResponse> = objectMapper.readValue(
                result.response.contentAsString,
                object : TypeReference<WebResponse<UserResponse>>() {}
            )
            assertNull(response.message)
            assertEquals("${ResponseStatusType.SUCCESS}", response.status)
            assertNotNull(response.data.username)
            assertNotNull(response.data.firstName)
            assertNotNull(response.data.lastName)
            assertNotNull(response.data.email)
        }.andDo(
            document(
                "user-profile-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }


    @Test
    fun updateUserFailed() {
        val request: UpdateUserRequest =
            UpdateUserRequest(
                currentPassword = "rahasia"
            )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/update")
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
                "user-update-fail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun updateUserSuccess() {
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

        val loginRequest: LoginUserRequest =
            LoginUserRequest(
                email = "abc@example.com",
                password = "rahasia"
            )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
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
        val updateRequest: UpdateUserRequest =
            UpdateUserRequest(
                firstName = "test 123",
                currentPassword = "rahasia"
            )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
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
        }.andDo(
            document(
                "user-update-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun deleteUserFailed() {
        val request: DeleteUserRequest =
            DeleteUserRequest(
                email = "",
                password = ""
            )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/delete")
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
                "user-delete-fail",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }

    @Test
    fun deleteUserSuccess() {
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

        val loginRequest: LoginUserRequest =
            LoginUserRequest(
                email = "abc@example.com",
                password = "rahasia"
            )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
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
        val deleteRequest: DeleteUserRequest =
            DeleteUserRequest(
                email = "abc@example.com",
                password = "rahasia"
            )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/delete")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest))
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

            val userDb: User? = userRepository.findById("test").getOrNull()
            assertNull(userDb)
        }.andDo(
            document(
                "user-delete-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
            )
        )
    }
}