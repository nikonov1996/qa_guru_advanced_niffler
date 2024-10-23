package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Builder
public record CategoryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("username")
    String username,
    @JsonProperty("archived")
    boolean archived) {

}
