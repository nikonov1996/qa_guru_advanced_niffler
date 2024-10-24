package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.jupiter.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;

import java.beans.Transient;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Builder
public record UserJson(
        @JsonProperty("id")
    UUID id,
        @JsonProperty("username")
    String username,
        @JsonProperty("firstname")
    String firstname,
        @JsonProperty("surname")
    String surname,
        @JsonProperty("fullname")
    String fullname,
        @JsonProperty("currency")
    CurrencyValues currency,
        @JsonProperty("photo")
    String photo,
        @JsonProperty("photoSmall")
    String photoSmall,
        @JsonProperty("friendState")
    FriendState friendState,
        @Transient
    String password,
        @Transient
        User.UserType userType
) {
}

  