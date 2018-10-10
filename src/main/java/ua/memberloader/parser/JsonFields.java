package ua.memberloader.parser;

public enum JsonFields {
    RESPONSE("response"),
    COUNT("count"),
    ITEMS("items"),
    LIKES("likes"),
    OWNER_ID("owner_id"),
    ITEM_ID("id");



    private String fildName;

    JsonFields(String field){
        this.fildName = field;
    }

    public String getFieldName(){
        return fildName;
    }
}
