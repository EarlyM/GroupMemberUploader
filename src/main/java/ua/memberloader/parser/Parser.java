package ua.memberloader.parser;

import org.json.JSONException;
import org.json.JSONObject;

public class Parser {

    @SuppressWarnings("unchecked")
    public <T> T getFieldValue(String json, JsonFields field, Class<T> cast) throws JSONException {
        JSONObject response = getResponse(json);
        return (T) response.get(field.getFieldName());

    }

    private JSONObject getResponse(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        return obj.getJSONObject(JsonFields.RESPONSE.getFieldName());
    }

}
