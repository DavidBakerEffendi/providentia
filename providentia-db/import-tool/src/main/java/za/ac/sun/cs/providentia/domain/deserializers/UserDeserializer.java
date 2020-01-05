package za.ac.sun.cs.providentia.domain.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import za.ac.sun.cs.providentia.domain.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class UserDeserializer extends JsonDeserializer<User> {

    @Override
    public User deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(module);

        User object = new User();

        if (node.has("user_id")) {
            object.setUserId(node.get("user_id").asText());
        }
        if (node.has("name")) {
            object.setName(node.get("name").asText());
        }
        if (node.has("cool")) {
            object.setCool(node.get("cool").asInt());
        }
        if (node.has("yelping_since")) {
            LocalDateTime dateTime = LocalDateTime.parse(node.get("yelping_since").asText(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            object.setYelpingSince(dateTime.toInstant(ZoneOffset.of("-07:00")));
        }
        if (node.has("funny")) {
            object.setFunny(node.get("funny").asInt());
        }
        if (node.has("friends")) {
            object.setFriends(objectMapper.readValue(node.get("friends").toString(), String[].class));
        }
        if (node.has("fans")) {
            object.setFans(node.get("fans").asInt());
        }
        if (node.has("useful")) {
            object.setUseful(node.get("useful").asInt());
        }

        return object;
    }
}
