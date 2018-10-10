package ua.memberloader.http.parameters;

public enum VKMethod {

    GET_MEMBERS("/method//groups.getMembers"),
    GET_POST("/method//wall.get"),
    GET_LIKES("/method//likes.getList");

    private String path;

    VKMethod(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
