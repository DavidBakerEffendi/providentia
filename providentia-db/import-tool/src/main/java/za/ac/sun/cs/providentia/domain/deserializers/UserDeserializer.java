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
            object.setYelpingSince(
                    LocalDateTime.parse(node.get("yelping_since").asText(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (node.has("review_count")) {
            object.setReviewCount(node.get("review_count").asInt());
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
        if (node.has("average_stars")) {
            object.setAverageStars(node.get("average_stars").asDouble());
        }

//        if (node.has("compliment_hot")) {
//            object.setComplimentHot(node.get("compliment_hot").asInt());
//        }
//        if (node.has("compliment_more")) {
//            object.setComplimentMore(node.get("compliment_more").asInt());
//        }
//        if (node.has("compliment_profile")) {
//            object.setComplimentProfile(node.get("compliment_profile").asInt());
//        }
//        if (node.has("compliment_cute")) {
//            object.setComplimentCute(node.get("compliment_cute").asInt());
//        }
//        if (node.has("compliment_list")) {
//            object.setComplimentList(node.get("compliment_list").asInt());
//        }
//        if (node.has("compliment_note")) {
//            object.setComplimentNote(node.get("compliment_note").asInt());
//        }
//        if (node.has("compliment_plain")) {
//            object.setComplimentPlain(node.get("compliment_plain").asInt());
//        }
//        if (node.has("compliment_cool")) {
//            object.setComplimentCool(node.get("compliment_cool").asInt());
//        }
//        if (node.has("compliment_funny")) {
//            object.setComplimentFunny(node.get("compliment_funny").asInt());
//        }
//        if (node.has("compliment_writer")) {
//            object.setComplimentWriter(node.get("compliment_writer").asInt());
//        }
//        if (node.has("compliment_photos")) {
//            object.setComplimentPhotos(node.get("compliment_photos").asInt());
//        }

        return object;
    }
}
