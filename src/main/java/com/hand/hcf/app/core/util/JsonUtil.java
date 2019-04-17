package com.hand.hcf.app.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsonorg.JSONObjectDeserializer;
import com.fasterxml.jackson.datatype.jsonorg.JSONObjectSerializer;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public abstract class JsonUtil {
    protected static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(JSONObject.class, new JSONObjectSerializer());
        module.addDeserializer(JSONObject.class, new JSONObjectDeserializer());
        objectMapper.registerModule(module);
    }

    /**
     * Returns the json String for the json Object
     *
     * @param jsonObject the Object that needs to be serialized
     * @return the json String, empty string will be returned when there is Exception
     */
    public static String toJson(Object jsonObject) {
        try {
            log.info("trying to serialize java object to json string: jsonObject->{}", jsonObject);
            return objectMapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the JSONObject for the json String
     *
     * @param jsonString the String that will be deserialized
     * @return the JSONObject Object
     */
    public static JSONObject toJSONObject(String jsonString) {
        log.info("trying to deserialize json String to JSON Object: jsonString");
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the JSONArray for the json String
     *
     * @param jsonString the String that will be deserialized
     * @return the JSONArray Object
     */
    public static JSONArray toJsonArray(String jsonString) {
        try {
            log.info("trying to deserialize json String to JSON Array: jsonString->{}", jsonString);
            return new JSONArray(jsonString);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        }
        return new JSONArray();
    }

    /**
     * Returns the JSONObject with the sequenceNumber i from the jsonArray
     *
     * @param jsonArray the JSONArray
     * @param i         sequenceNumber
     * @return the JSONObject with the sequenceNumber i
     */
    public static JSONObject getJsonObject(JSONArray jsonArray, int i) {
        try {
            log.info("trying to get JSONObject with sequenceNumber i from JSONArray: jsonArray->{}, i->{}", jsonArray, i);
            return jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        }
        return new JSONObject();
    }

    /**
     * Returns the String value for the jsonArray with index i
     *
     * @param jsonArray the JSONArray
     * @param i         sequenceNumber
     * @return the String value
     */
    public static String getString(JSONArray jsonArray, int i) {
        try {
            log.info("trying to get String from jsonArray: jsonArray->{}, i->{}", jsonArray, i);
            return jsonArray.getString(i);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    public static byte[] toBytes(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json.getBytes(Charset.forName("utf8")), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(byte[] json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, TypeReference typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the boolean value of the key contained in the jsonString String
     *
     * @param jsonString the jsonString String which contains the boolean entry
     * @param key        the entry key
     * @return boolean value, false will be returned when there is JSONException
     */
    public static boolean getBoolean(String jsonString, String key) {
        try {
            log.info("trying to get boolean value: key->{}, jsonString->{}", key, jsonString);
            return new JSONObject(jsonString).getBoolean(key);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Returns the String value of the key contained in the jsonString String
     *
     * @param jsonString the jsonString String which contains the String entry
     * @param key        the entry key
     * @return String value, empty String will be returned when where is JSONException
     */
    public static String getString(String jsonString, String key) {
        try {
            log.info("trying to get String value: key->{}, jsonString->{}", key, jsonString);
            return new JSONObject(jsonString).getString(key);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * Returns the String value of the key for the JSONObject
     *
     * @param jsonObject the JSONObject which contains the String entry
     * @param key        the entry key
     * @return String value, empty String will be returned when where is JSONException
     */
    public static String getString(JSONObject jsonObject, String key) {
        log.info("trying to get String value: key->{}, jsonObject->{}", key, jsonObject);
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                log.error(e.getMessage(), e);
            }
        }
        return "";
    }

    /**
     * Returns the double value of the key for the JSONObject
     *
     * @param jsonObject the JSONObject which contains the String entry
     * @param key        the entry key
     * @return Double value, min double will be returned when where is JSONException
     */
    public static Double getDouble(JSONObject jsonObject, String key) {
        log.info("trying to get String value: key->{}, jsonObject->{}", key, jsonObject);
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getDouble(key);
            } catch (JSONException e) {
                log.error(e.getMessage(), e);
            }
        }
        return Double.MIN_VALUE;
    }

    /**
     * Returns the double value of the key for the JSONObject
     *
     * @param jsonObject the JSONObject which contains the String entry
     * @param key        the entry key
     * @return JSONObject value, null will be returned when where is JSONException
     */
    public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
        log.info("trying to get String value: key->{}, jsonObject->{}", key, jsonObject);
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getJSONObject(key);
            } catch (JSONException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Returns the boolean value of the key for the JSONObject
     *
     * @param jsonObject the JSONObject which contains the String entry
     * @param key        the entry key
     * @return String value, empty String will be returned when where is JSONException
     */
    public static boolean getBoolean(JSONObject jsonObject, String key) {
        log.info("trying to get Boolean value: key->{}, jsonObject->{}", key, jsonObject);
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getBoolean(key);
            } catch (JSONException e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * Returns int value of the key for the JSONObject
     *
     * @param jsonObject the JSONObject which contains the String entry
     * @param key        the entry key
     * @return int value, zero will be returned when where is JSONException
     */
    public static int getInt(JSONObject jsonObject, String key) {
        log.info("trying to get int value: key->{}, jsonObject->{}", key, jsonObject);
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getInt(key);
            } catch (JSONException e) {
                log.error(e.getMessage(), e);
            }
        }
        return 0;
    }

    /**
     * Put the entry to the JSONObject
     *
     * @param jsonObject the JSONObject
     * @param key        the entry key
     * @param value      the value
     */
    public static void put(JSONObject jsonObject, String key, Object value) {
        try {
            log.info("trying to get String value: key->{}, jsonString->{}", key, jsonObject);
            jsonObject.put(key, value);
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        }
    }

}
