package org.ge.vernova.context;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScenarioContext {

    private final Map<String,Object> data = new HashMap<>();

    private final static String LAST_API_RESPONSE = "lastApiResponse";

    private static final Pattern CONTEXT_VARIABLE =
            Pattern.compile("\\{([^{}]+)}");

    public void setLastApiResponse(Response response) {
        data.put(LAST_API_RESPONSE, response);
    }

    public Response getLastApiResponse() {
        return (Response) data.get(LAST_API_RESPONSE);
    }

    public void set(String key, Object value){
        data.put(key,value);
    }

    public <T> T get(String key,Class<T> type){

        Object value = this.data.get(key);
        if(Objects.isNull(value)){
            return null;
        }
        return type.cast(value);
    }

    public String fillSafely(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        Matcher matcher = CONTEXT_VARIABLE.matcher(value);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object contextValue = data.get(key);

            if (contextValue != null) {
                matcher.appendReplacement(
                        result,
                        Matcher.quoteReplacement(contextValue.toString())
                );
            } else {
                matcher.appendReplacement(
                        result,
                        Matcher.quoteReplacement(matcher.group(0))
                );
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    /** Below Method is needed if ThreadLocal Way of implementation is done
     * Since we opted for pico container approach. Its not needed here.
     */

//    public void clear() {
//        data.clear();
//    }

}
