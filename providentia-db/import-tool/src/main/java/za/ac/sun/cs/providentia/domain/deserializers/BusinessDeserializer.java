package za.ac.sun.cs.providentia.domain.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import za.ac.sun.cs.providentia.domain.Business;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class BusinessDeserializer extends JsonDeserializer<Business> {

    @Override
    public Business deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(module);

        Business object = new Business();

        if (node.has("business_id")) {
            object.setBusinessId(node.get("business_id").asText());
        }
        if (node.has("address")) {
            object.setAddress(node.get("address").asText());
        }
        if (node.has("postal_code")) {
            object.setPostalCode(node.get("postal_code").asText());
        }
        if (node.has("is_open")) {
            object.setIsOpen(node.get("is_open").asInt());
        }
        if (node.has("open")) {
            object.setOpen(node.get("open").asBoolean());
        }
        if (node.has("categories")) {
            if (!node.get("categories").isNull()) {
                String[] categories = node.get("categories").asText().split(",");
                List<String> catList = new LinkedList<>();
                for (String cat : categories) {
                    catList.add(titleCaseConversion(cat.trim()));
                }
                object.setCategories(catList);
            }
        }
        if (node.has("city")) {
            object.setCity(titleCaseConversion(node.get("city").asText()));
        }
        if (node.has("review_count")) {
            object.setReviewCount(node.get("review_count").asInt());
        }
        if (node.has("name")) {
            object.setName(node.get("name").asText());
        }
        if (node.has("longitude")) {
            object.setLongitude(node.get("longitude").asDouble());
        }
        if (node.has("latitude")) {
            object.setLatitude(node.get("latitude").asDouble());
        }
        if (node.has("state")) {
            object.setState(node.get("state").asText().toUpperCase());
        }
        if (node.has("stars")) {
            object.setStars(node.get("stars").asDouble());
        }

        return object;
    }

    /**
     * Converts the given input string as title-case.
     *
     * @param inputString the string to convert.
     * @return the inputString as title-case.
     */
    private static String titleCaseConversion(String inputString) {
        if (StringUtils.isBlank(inputString)) {
            return "";
        }

        if (StringUtils.length(inputString) == 1) {
            return inputString.toUpperCase();
        }

        StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());

        Stream.of(inputString.split(" ")).forEach(stringPart ->
        {
            if (stringPart.length() > 1)
                resultPlaceHolder.append(stringPart.substring(0, 1)
                        .toUpperCase())
                        .append(stringPart.substring(1)
                                .toLowerCase());
            else
                resultPlaceHolder.append(stringPart.toUpperCase());

            resultPlaceHolder.append(" ");
        });
        return StringUtils.trim(resultPlaceHolder.toString());
    }
}
