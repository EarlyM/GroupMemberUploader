package ua.memberloader.http.parameters;

public enum VKParameter {

    DOMAIN("domain"),
    GROUP_ID("group_id"),
    OFFSET("offset"),
    COUNT("count"),
    FIELDS("fields"),
    VERSION("v"),
    TOKEN("access_token"),
    TYPE("type"),
    OWNER_ID("owner_id"),
    ITEM_ID("item_id"),
    FILTER("filter");

    private String parameterName;

    VKParameter(String parameterName){
        this.parameterName = parameterName;
    }

    public String getParameterName(){
        return parameterName;
    }
}
