package ua.memberloader.http.parameters;

public enum Fields {

    SEX("sex"),
    BDATE("bdate"),
    CITY("city"),
    COUNTRY("country"),
    PHOTO_50("photo_50"),
    PHOTO_100("photo_100"),
    PHOTO_200_ORIG("photo_200_orig"),
    PHOTO_200("photo_200"),
    PHOTO_400_ORIG("photo_400_orig"),
    PHOTO_MAX("photo_max"),
    PHOTO_MAX_ORIG("photo_max_orig"),
    ONLINE("online"),
    ONLINE_MOBILE("online_mobile"),
    LISTS("lists"),
    DOMAIN("domain"),
    HAS_MOBILE("has_mobile"),
    CONTACTS("contacts"),
    CONNECTIONS("connections"),
    SITE("site"),
    EDUCATION("education"),
    UNIVERSITIES("universities"),
    SCHOOLS("schools"),
    STATUS("status"),
    LAST_SEEN("last_seen"),
    COMMON_COUNT("common_count"),
    RELATION("relation"),
    RELATIVES("relatives"),

    POST("post"),
    LIKES("likes");

    private String field;

    Fields(String field){
        this.field = field;
    }

    public String getField(){
        return field;
    }
}
