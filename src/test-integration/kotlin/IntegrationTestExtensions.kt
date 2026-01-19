import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

inline fun <reified T> T.writeAsJson(): String? {
    val jsonMapper = jsonMapper { addModules(kotlinModule(), JavaTimeModule()) }
    return jsonMapper.writeValueAsString(this)
}

inline fun <reified T> String.toClass(): T {
    val jsonMapper = jsonMapper { addModules(kotlinModule(), JavaTimeModule()) }
    return jsonMapper.readValue(this, T::class.java)
}