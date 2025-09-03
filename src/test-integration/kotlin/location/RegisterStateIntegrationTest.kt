package location

import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryRepository
import br.com.study.codedesignpractice.location.state.CreateStateRequest
import br.com.study.codedesignpractice.location.state.CreateStateResponse
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import writeAsJson
import java.util.UUID

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class RegisterStateIntegrationTest(
    @param:Autowired private val mockMvc: MockMvc,
    @param:Autowired private val countryRepository: CountryRepository
) {

    companion object {
        const val STATES_V1_PATH = "/v1/states"
    }

    @Test
    fun `should be able to register a state`() {
        val country = countryRepository.save(Country(name = "India"))

        val mockMvcResult = mockMvc.post(STATES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "New Delhi", "countryId": "${country.id}"}"""
        }
            .andExpect { status { isCreated()} }
            .andDo { print() }
            .andReturn()

        val actualResponse = mockMvcResult.response.contentAsString
        JSONAssert.assertEquals(expectedResponse(mockMvcResult), actualResponse, true)
    }

    fun expectedResponse(mockMvcResult: MvcResult): String? {
        val mapper = jsonMapper { addModules(kotlinModule(), JavaTimeModule()) } // TODO("write why this is needed")
        val stateResponse = mapper.readValue(mockMvcResult.response.contentAsString, CreateStateResponse::class.java)

        return """
            {
                "id": "${stateResponse.id}",
                "name": "New Delhi",
                "country": {
                    "id": "${stateResponse.country.id}",
                    "name": "India"
                }
            }
        """.trimIndent()
    }

    @Nested
    inner class TestRegisterStateNameValidation {

        @Test
        fun `should validate if the state name is not blank`() {
            val country = countryRepository.save(Country(name = "India"))

            val invalidStateNamesRequest = listOf(
                CreateStateRequest(name = "", countryId = country.id),
                CreateStateRequest(name = " ", countryId = country.id),
                CreateStateRequest(name = null, countryId = country.id),
            )

            invalidStateNamesRequest.forEach {
                mockMvc.post(STATES_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = it.writeAsJson()
                }.andExpect {
                    status { isBadRequest()}
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("""{"invalidProperties":["name"],"errorMessages":["must not be blank"]}""")
                    }
                }.andDo { print() }
            }
        }

        @Test
        fun `should validate if the state name is unique`() {
            val country = countryRepository.save(Country(name = "India"))

            mockMvc.post(STATES_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = """{"name": "New Delhi", "countryId": "${country.id}"}"""
            }.andExpect {
                status { isCreated()}
            }

            mockMvc.post(STATES_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = """{"name": "New Delhi", "countryId": "${country.id}"}"""
            }.andExpect {
                status { isBadRequest()}
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["name"],"errorMessages":["State name must be unique"]}""")
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterStateCountryValidation {

        @Test
        fun `should validate if the state country is not null`() {
            mockMvc.post(STATES_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = """{"name": "New Delhi"}"""
            }.andExpect {
                status { isBadRequest()}
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["countryId"],"errorMessages":["must not be null"]}""")
                }
            }.andDo { print() }
        }

        @Test
        fun `should validate if the state country exists`() {
            mockMvc.post(STATES_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = """{"name": "New Delhi", "countryId": "${UUID.randomUUID()}"}"""
            }.andExpect {
                status { isBadRequest()}
                content {
                    json("""{"invalidProperties":["countryId"],"errorMessages":["Value must exist in the database"]}""")
                }
            }.andDo { print() }
        }
    }
}