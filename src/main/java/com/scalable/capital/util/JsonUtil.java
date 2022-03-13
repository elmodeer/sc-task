package com.scalable.capital.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.math.BigDecimal;

@Slf4j
public class JsonUtil {

    public static BigDecimal get(JsonElement baseObj, String key) {

        try {
            String[] keys = key.split("\\.");
            if (keys.length > 1) {
                if (baseObj instanceof JsonObject) {
                    baseObj = baseObj.getAsJsonObject().get(keys[0]);
                    key = key.substring(keys[0].length() + 1);
                    return get(baseObj, key);
                }
                else if (baseObj instanceof JsonArray) {
                    baseObj = baseObj.getAsJsonArray().get(0);
                    return get(baseObj, key);
                }

            }
            return baseObj.getAsJsonObject().get(key).getAsBigDecimal();

        } catch (Exception e ) {
            log.error("Invalid key: " + key);
        }
        return null;
    }

}
