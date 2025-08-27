package book

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

inline fun <reified T> T.writeAsJson(): String? {
    val mapper = jsonMapper { addModules(kotlinModule(), JavaTimeModule()) }
    return mapper.writeValueAsString(this)
}