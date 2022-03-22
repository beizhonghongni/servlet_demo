package com.test;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DB {
    private Map<Integer, Map<LocalDate, String>> datas = new HashMap<>();
    // 0-49
    private final Integer maxRoom = 50;


    /**
      也可以用ConcurrentMap 线程安全的map来实现，synchronized用的比较多
     */
    public synchronized Map<String, Object> order(Map<String, String> paramMap){
        Map<String, Object> result = new HashMap<>();
        try {
            String name = paramMap.get("name");
            String dateStr = paramMap.get("date");
            Integer roomNo = Integer.valueOf(paramMap.get("roomNo"));
            LocalDate date = null;
            try {
                date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
            }catch (DateTimeParseException ex){
                result.put("code", 500);
                result.put("data", "date format error, yyyyMMdd required.");
                return result;
            }
            if (StringUtils.isBlank(name) || Objects.isNull(date)) {
                result.put("code", 500);
                result.put("data", "input error");
                return result;
            }
            if (date.isBefore(LocalDate.now())) {
                result.put("code", 500);
                result.put("data", "date error");
                return result;
            }
            if (roomNo < 0 || roomNo > maxRoom - 1) {
                result.put("code", 500);
                result.put("data", "room no error");
                return result;
            }
            if (datas.containsKey(roomNo)) {
                Map<LocalDate, String> curRoomRecords = datas.get(roomNo);
                if (curRoomRecords.containsKey(date)) {
                    result.put("code", 200);
                    result.put("data", "target room is ordered.");
                    return result;
                } else {
                    curRoomRecords.put(date, name);
                    result.put("code", 200);
                    result.put("data", "success");
                    return result;
                }
            } else {
                LocalDate finalDate = date;
                datas.put(roomNo, new HashMap<LocalDate, String>() {{
                    this.put(finalDate, name);
                }});
                result.put("code", 200);
                result.put("data", "success");
                return result;
            }
        }catch (Exception ex){
            result.put("code", 500);
            result.put("data", ex.getMessage());
            return result;
        }
    }

    public synchronized Map<String, Object> queryDate(Map<String, String> paramMap){
        Map<String, Object> result = new HashMap<>();
        try {
            String dateStr = paramMap.get("date");
            if (StringUtils.isBlank(dateStr)) {
                result.put("code", 500);
                result.put("data", "input error");
                return result;
            }
            LocalDate date = null;
            try {
                date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
            }catch (DateTimeParseException ex){
                result.put("code", 500);
                result.put("data", "date format error, yyyyMMdd required.");
                return result;
            }
            List<Pair<String, Integer>> orders = new ArrayList<>();
            for (Map.Entry<Integer, Map<LocalDate, String>> integerMapEntry : datas.entrySet()) {
                Integer orderRoomNo = integerMapEntry.getKey();
                for (Map.Entry<LocalDate, String> localDateStringEntry : integerMapEntry.getValue().entrySet()) {
                    LocalDate orderDate = localDateStringEntry.getKey();
                    String orderName = localDateStringEntry.getValue();
                    if(orderDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")).equals(dateStr)){
                        orders.add(Pair.of(orderName, orderRoomNo));
                    }
                }
            }
            result.put("code", 200);
            result.put("data", orders);
            return result;
        }catch (Exception ex){
            result.put("code", 500);
            result.put("data", ex.getMessage());
            return result;
        }
    }

    public synchronized Map<String, Object> queryCustomer(Map<String, String> paramMap){
        Map<String, Object> result = new HashMap<>();
        try {
            String customer = paramMap.get("name");
            if (StringUtils.isBlank(customer)) {
                result.put("code", 500);
                result.put("data", "input error");
                return result;
            }
            List<Pair<String, Integer>> orders = new ArrayList<>();
            for (Map.Entry<Integer, Map<LocalDate, String>> integerMapEntry : datas.entrySet()) {
                Integer orderRoomNo = integerMapEntry.getKey();
                for (Map.Entry<LocalDate, String> localDateStringEntry : integerMapEntry.getValue().entrySet()) {
                    LocalDate orderDate = localDateStringEntry.getKey();
                    String orderName = localDateStringEntry.getValue();
                    if(orderName.equals(customer)){
                        orders.add(Pair.of(orderDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), orderRoomNo));
                    }
                }
            }
            result.put("code", 200);
            result.put("data", orders);
            return result;
        }catch (Exception ex){
            result.put("code", 500);
            result.put("data", ex.getMessage());
            return result;
        }
    }
    private static class SingletonClassInstance{
        private static final DB instance=new DB();
    }
    private DB(){}
    public static DB getInstance(){
        return SingletonClassInstance.instance;
    }
}
