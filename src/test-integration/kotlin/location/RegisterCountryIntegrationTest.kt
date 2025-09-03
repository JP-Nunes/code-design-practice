package location

import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryRequest
import br.com.study.codedesignpractice.location.country.CountryResponse
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
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

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class RegisterCountryIntegrationTest(@param:Autowired private val mockMvc: MockMvc) {

    companion object {
        const val COUNTRIES_V1_PATH = "/v1/countries"
    }

    @Test
    fun `should be able to register a country`() {
        val mockMvcResult = mockMvc.post(COUNTRIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "India"}"""
        }
            .andExpect { status { isCreated()} }
            .andDo { print() }
            .andReturn()

        val actualResponse = mockMvcResult.response.contentAsString
        JSONAssert.assertEquals(expectedResponse(mockMvcResult), actualResponse, true)
    }

    fun expectedResponse(mockMvcResult: MvcResult): String? {
        val mapper = jsonMapper { addModules(kotlinModule(), JavaTimeModule()) } // TODO("write why this is needed")
        val countryResponse = mapper.readValue(mockMvcResult.response.contentAsString, CountryResponse::class.java)

        return """
            {
                "id": "${countryResponse.id}",
                "name": "India"
            }
        """.trimIndent()
    }

    @Test
    fun `should validate if the country name is not blank`() {
        val invalidCountryNamesRequest = listOf(
            CountryRequest(name = ""),
            CountryRequest(name = " "),
            CountryRequest(name = null),
        )

        invalidCountryNamesRequest.forEach {
            mockMvc.post(COUNTRIES_V1_PATH) {
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
    fun `should validate if the country name is unique`() {
        val country = Country(name = "India")

        mockMvc.post(COUNTRIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = country.writeAsJson()
        }.andExpect {
            status { isCreated()}
        }

        mockMvc.post(COUNTRIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = country.writeAsJson()
        }.andExpect {
            status { isBadRequest()}
            content {
                contentType(MediaType.APPLICATION_JSON)
                json("""{"invalidProperties":["name"],"errorMessages":["Country name must be unique"]}""")
            }
        }.andDo { print() }
    }
}