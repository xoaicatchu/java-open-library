package com.example.jackson;

import com.example.jackson.domain.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JacksonApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCustomSerializer() throws Exception {
        Money money = new Money(new BigDecimal("12345.67"), "USD");
        String json = objectMapper.writeValueAsString(money);
        assertThat(json).isEqualTo("\"USD 12,345.67\"");
    }

    @Test
    void testCustomDeserializer() throws Exception {
        String json = "\"EUR 9,999.99\"";
        Money money = objectMapper.readValue(json, Money.class);
        assertThat(money.getCurrency()).isEqualTo("EUR");
        assertThat(money.getAmount()).isEqualByComparingTo("9999.99");
    }

    @Test
    void testJsonViewPublic() throws Exception {
        EmailNotification notification = new EmailNotification(UUID.randomUUID(), "Hello", "test@example.com");
        String json = objectMapper.writerWithView(Views.Public.class).writeValueAsString(notification);
        assertThat(json).contains("Hello");
        assertThat(json).doesNotContain("test@example.com");
        assertThat(json).contains("\"type\":\"EMAIL\"");
    }

    @Test
    void testJsonViewDetail() throws Exception {
        EmailNotification notification = new EmailNotification(UUID.randomUUID(), "Hello", "test@example.com");
        String json = objectMapper.writerWithView(Views.Internal.class).writeValueAsString(notification);
        assertThat(json).contains("test@example.com");
    }

    @Test
    void testPolymorphicDeserialization() throws Exception {
        String json = """
            {
                "type": "SMS",
                "id": "123e4567-e89b-12d3-a456-426614174000",
                "message": "Your code is 1234",
                "phoneNumber": "+1234567890"
            }
            """;
        Notification notification = objectMapper.readValue(json, Notification.class);
        assertThat(notification).isInstanceOf(SmsNotification.class);
        assertThat(((SmsNotification) notification).getPhoneNumber()).isEqualTo("+1234567890");
    }

    @Test
    void testMixin() throws Exception {
        ThirdPartyUser user = new ThirdPartyUser("admin", "secret123");
        String json = objectMapper.writeValueAsString(user);
        assertThat(json).contains("admin");
        assertThat(json).doesNotContain("secret123");
    }

    @Test
    void testJsonCreator() throws Exception {
        String json = "{\"name\": \"testName\", \"value\": 42}";
        ImmutableData data = objectMapper.readValue(json, ImmutableData.class);
        assertThat(data.getName()).isEqualTo("testName");
        assertThat(data.getValue()).isEqualTo(42);
    }

    @Test
    void testJsonFilter() throws Exception {
        FilteredRecord record = new FilteredRecord("includeMe", "excludeMe");
        
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("dynamicFilter", SimpleBeanPropertyFilter.serializeAllExcept("field2"));
            
        String json = objectMapper.writer(filterProvider).writeValueAsString(record);
        assertThat(json).contains("includeMe");
        assertThat(json).doesNotContain("excludeMe");
    }

    @Test
    void testTreeModel() throws Exception {
        String json = "{\"name\":\"tree\",\"nested\":{\"value\":100}}";
        JsonNode rootNode = objectMapper.readTree(json);
        String name = rootNode.path("name").asText();
        int value = rootNode.path("nested").path("value").asInt();
        
        assertThat(name).isEqualTo("tree");
        assertThat(value).isEqualTo(100);
    }
}
