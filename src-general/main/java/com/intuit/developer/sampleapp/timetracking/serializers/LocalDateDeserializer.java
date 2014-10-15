package com.intuit.developer.sampleapp.timetracking.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.LocalDate;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/25/14
 * Time: 8:55 AM
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final String jsonStr = jsonParser.getText();
        return LocalDate.parse(jsonStr);
    }
}
