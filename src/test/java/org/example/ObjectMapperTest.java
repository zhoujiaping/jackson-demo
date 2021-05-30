package org.example;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;
import lombok.TextBlock;
import lombok.TextBlocks;
import org.exmaple.CustomCarDeserializer;
import org.exmaple.CustomCarSerializer;
import org.exmaple.model.Car;
import org.exmaple.model.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ObjectMapperTest {

    @Test
    @SneakyThrows
    public void testWrite(){
        ObjectMapper objectMapper = new ObjectMapper();
        Car car = new Car("yellow", "renault");
        //objectMapper.writeValue(new File("target/car.json"), car);
        String json = objectMapper.writeValueAsString(car);
        /*{"color":"yellow","type":"renault"}*/
        @TextBlock String expect = TextBlocks.lazyInit();
        Assertions.assertEquals(json,expect);
    }

    @Test
    @SneakyThrows
    public void testRead(){
        ObjectMapper objectMapper = new ObjectMapper();
        /*{"color":"yellow","type":"renault"}*/
        @TextBlock String json = TextBlocks.lazyInit();
        //可以从一个url获取json数据
        //Car car = objectMapper.readValue(new URL("file:src/test/resources/json_car.json"), Car.class);
        Car car = objectMapper.readValue(json, Car.class);
        Assertions.assertEquals(car.getColor(),"yellow");
        Assertions.assertEquals(car.getType(),"renault");
    }


    @Test
    @SneakyThrows
    public void testJsonNode(){
        ObjectMapper objectMapper = new ObjectMapper();
        /*{"color" : "Black", "type" : "FIAT" }"*/
        @TextBlock String json = TextBlocks.lazyInit();
        JsonNode jsonNode = objectMapper.readTree(json);
        String color = jsonNode.get("color").asText();
        Assertions.assertEquals(color,"Black");
    }

    @Test
    @SneakyThrows
    public void testTypeReference(){
        ObjectMapper objectMapper = new ObjectMapper();
        /*[{ "color" : "Black", "type" : "BMW" }, { "color" : "Red", "type" : "FIAT" }]*/
        @TextBlock String jsonCarArray = TextBlocks.lazyInit();
        System.out.println(jsonCarArray);
        List<Car> listCar = objectMapper.readValue(jsonCarArray, new TypeReference<List<Car>>(){});
        Assertions.assertTrue(listCar.size()==2);
    }

    @Test
    @SneakyThrows
    public void testCustom(){
        ObjectMapper objectMapper = new ObjectMapper();

        /*{ "color" : "Black", "type" : "Fiat", "year" : "1970" }
         */
        @TextBlock String jsonString
                = TextBlocks.lazyInit();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        Car car = objectMapper.readValue(jsonString, Car.class);

        JsonNode jsonNodeRoot = objectMapper.readTree(jsonString);
        JsonNode jsonNodeYear = jsonNodeRoot.get("year");
        String year = jsonNodeYear.asText();

        Assertions.assertEquals(year,"1970");
    }

    @Test
    @SneakyThrows
    public void testCustomSerializer(){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomCarSerializer",
                        new Version(1, 0, 0, null, null, null));
        module.addSerializer(Car.class, new CustomCarSerializer());
        mapper.registerModule(module);
        Car car = new Car("yellow", "renault");
        String carJson = mapper.writeValueAsString(car);
        /*{"car_brand":"renault"}*/
        @TextBlock String expect = TextBlocks.lazyInit();
        Assertions.assertEquals(carJson,expect);
    }

    @Test
    @SneakyThrows
    public void testCustomDeserializer(){
        /*{ "color" : "Black", "type" : "BMW" }*/
        @TextBlock String json = TextBlocks.lazyInit();;
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomCarDeserializer",
                        new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Car.class, new CustomCarDeserializer());
        mapper.registerModule(module);
        Car car = mapper.readValue(json, Car.class);
        Assertions.assertNull(car.getType());
    }

    @Test
    @SneakyThrows
    public void testDateFormat(){
        ObjectMapper objectMapper = new ObjectMapper();
        Request request = Request.builder().car(new Car("yellow","renault"))
                .datePurchased(new Date(2021-1900,04,30,11,43,0)).build();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        objectMapper.setDateFormat(df);
        String carAsString = objectMapper.writeValueAsString(request);
// output: {"car":{"color":"yellow","type":"renault"},"datePurchased":"2016-07-03 11:43"}
        /*2021-05-30 11:43*/
        @TextBlock String expect = TextBlocks.lazyInit();
        Assertions.assertTrue(carAsString.contains(expect));
    }
}
