package greeting.example

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
class Greeting @JsonCreator constructor(
        @JsonProperty("id") val id: Long,
        @JsonProperty("content") val content: String) {

}