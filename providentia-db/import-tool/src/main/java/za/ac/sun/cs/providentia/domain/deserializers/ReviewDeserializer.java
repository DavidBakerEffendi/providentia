package za.ac.sun.cs.providentia.domain.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import za.ac.sun.cs.providentia.domain.Review;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ReviewDeserializer extends JsonDeserializer<Review> {

    @Override
    public Review deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        Review object = new Review();

        if (node.has("business_id")) {
            object.setBusinessId(node.get("business_id").asText());
        }
        if (node.has("cool")) {
            object.setCool(node.get("cool").asInt());
        }
        if (node.has("date")) {
            object.setDate(LocalDateTime.parse(node.get("date").asText(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toEpochSecond(ZoneOffset.of("-07:00")));
        }
        if (node.has("funny")) {
            object.setFunny(node.get("funny").asInt());
        }
        if (node.has("user_id")) {
            object.setUserId(node.get("user_id").asText());
        }
        if (node.has("useful")) {
            object.setUseful(node.get("useful").asInt());
        }
        if (node.has("review_id")) {
            object.setReviewId(node.get("review_id").asText());
        }
        if (node.has("stars")) {
            object.setStars(node.get("stars").asDouble());
        }
        if (node.has("text")) {
            object.setText(node.get("text").asText());
        }

        return object;
    }
}
