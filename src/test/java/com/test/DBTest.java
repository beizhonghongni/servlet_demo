package com.test;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DBTest {
    private DB db = DB.getInstance();
    DateTimeFormatter yyyyMMddFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    Map<String, String> getParamMap(String name, String date, Integer roomNo){
        return new HashMap<String, String>() {{
            this.put("name", name);
            this.put("date", date);
            this.put("roomNo", String.valueOf(roomNo));
        }};
    }
    @org.junit.jupiter.api.Test
    void order() {
        assertEquals(db.order(getParamMap(
                "Jack", LocalDate.now().format(yyyyMMddFormatter), 1
        )).get("code"), 200);
        assertEquals(db.order(getParamMap(
                "Jack", LocalDate.now().minusDays(1).format(yyyyMMddFormatter), 1
        )).get("code"), 500);
        assertEquals(db.order(getParamMap(
                "Jack", LocalDate.now().format(yyyyMMddFormatter), -1
        )).get("code"), 500);
        assertEquals(db.order(getParamMap(
                "Jack", LocalDate.now().format(yyyyMMddFormatter), 50
        )).get("code"), 500);
        assertEquals(db.order(getParamMap(
                "Jack", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 1
        )).get("code"), 500);
        assertEquals(db.order(getParamMap(
                "",LocalDate.now().format(yyyyMMddFormatter), 30
        )).get("code"), 500);
    }

    @org.junit.jupiter.api.Test
    void queryDate() {
        assertEquals(db.queryDate(getParamMap(
                "", LocalDate.now().format(yyyyMMddFormatter), null
        )).get("code"), 200);
        assertEquals(db.queryDate(getParamMap(
                "", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), null
        )).get("code"), 500);
        Integer afterDays = (int)Math.floor(Math.random() * 100);
        db.order(getParamMap(
                "Jack", LocalDate.now().plusDays(afterDays).format(yyyyMMddFormatter), 1
        ));
        List<Pair<String, Integer>> data = (List<Pair<String, Integer>>)db.queryDate(getParamMap(
                "", LocalDate.now().plusDays(afterDays).format(yyyyMMddFormatter), null
        )).get("data");
        assertEquals(data.size(), 1);
        assertEquals(data.get(0).getKey(),  "Jack");
        assertEquals(data.get(0).getValue(),  1);
    }

    @org.junit.jupiter.api.Test
    void queryCustomer() {
        assertEquals(db.queryCustomer(getParamMap(
                "Jack", null, null
        )).get("code"), 200);
        assertEquals(db.queryCustomer(getParamMap(
                "", null, null
        )).get("code"), 500);
        Integer afterDays = (int)Math.floor(Math.random() * 100);
        db.order(getParamMap(
                "Jack", LocalDate.now().plusDays(afterDays).format(yyyyMMddFormatter), 1
        ));
        List<Pair<String, Integer>> data = (List<Pair<String, Integer>>)db.queryCustomer(getParamMap(
                "Jack", null, null
        )).get("data");
        assertEquals(data.size(), 1);
        assertEquals(data.get(0).getKey(),  LocalDate.now().plusDays(afterDays).format(yyyyMMddFormatter));
        assertEquals(data.get(0).getValue(),  1);
    }
}