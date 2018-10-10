package ua.memberloader.worker.jobs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ua.memberloader.dao.UserMongoDB;
import ua.memberloader.http.VKAPIMethodCaller;
import ua.memberloader.http.parameters.Fields;
import ua.memberloader.http.parameters.VKMethod;
import ua.memberloader.http.parameters.VKParameter;
import ua.memberloader.parser.JsonFields;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class LikeLoaderJob extends BaseJob implements Runnable {

    private static final Integer DEFAULT_OFFSET_WALL = 100;
    private static final Integer DEFAULT_OFFSET_LIKES = 1000;
    private static final AtomicInteger count = new AtomicInteger(0);
    private volatile static Integer AVAILABLE_POST = null;
    private Map<Integer, List<Document>> usersLike = new HashMap<>();

    private CountDownLatch countDownLatch;

    public LikeLoaderJob(UserMongoDB userMongoDB, CountDownLatch countDownLatch) {
        super(userMongoDB);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        List<NameValuePair> wallParameter = new LinkedList<>();
        addDefaultParameter(wallParameter);
        addWallParameter(wallParameter);
        wallParameter.add(new BasicNameValuePair(VKParameter.DOMAIN.getParameterName(), "just_str"));
        boolean error = false;

        do{

            caller = new VKAPIMethodCaller();
            URI uri;
            try{

                if(!error){
                    addOffsetOrReplace(wallParameter, count, DEFAULT_OFFSET_WALL);
                }

                error = false;

                uri = builder.buildURI(VKMethod.GET_POST, wallParameter);
                String json = caller.send(uri);

                AVAILABLE_POST = parser.getFieldValue(json, JsonFields.COUNT, Integer.class);

                JSONArray posts = parser.getFieldValue(json, JsonFields.ITEMS, JSONArray.class);
                JSONObject post;
                for(int i = 0; i < posts.length(); i++){
                    post = posts.getJSONObject(i);
                    Integer likesCount = getLikesCount(post);
                    if(likesCount == 0){
                        continue;
                    }

                    addUserLikes(post);
                }

                saveLikes();
                usersLike.clear();

            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }

        } while (AVAILABLE_POST > count.get() || error);


        countDownLatch.countDown();

    }

    private void saveLikes() {

        for(Integer id : usersLike.keySet()){
            userDB.insertLikes(id, usersLike.get(id));
        }

    }

    private void addUserLikes(JSONObject post) throws JSONException {
        AtomicInteger count = new AtomicInteger(0);
        Integer availableLikes = 0;
        boolean error = false;

        List<NameValuePair> likesParameter = new LinkedList<>();
        addDefaultParameter(likesParameter);
        addLikesParameter(post, likesParameter);

        do {

            caller = new VKAPIMethodCaller();
            URI uri;

            try{

                if(!error){
                    addOffsetOrReplace(likesParameter, count, DEFAULT_OFFSET_LIKES);
                }

                error = false;

                uri = builder.buildURI(VKMethod.GET_LIKES, likesParameter);
                String json = caller.send(uri);
                availableLikes = parser.getFieldValue(json, JsonFields.COUNT, Integer.class);

                JSONArray likes = parser.getFieldValue(json, JsonFields.ITEMS, JSONArray.class);

                String itemId = post.getString(JsonFields.ITEM_ID.getFieldName());


                for(int i = 0; i < likes.length(); i++){
                    Integer userId = likes.getInt(i);

                    Document document = new Document();
                    document.append("post_id", itemId);

                    List<Document> documents = usersLike.get(userId);

                    if(documents == null){
                        documents = new LinkedList<>();
                        documents.add(document);
                        usersLike.put(userId, documents);
                    } else {
                        documents.add(document);
                    }
                }


            } catch (JSONException e){
                e.printStackTrace();
                error = true;
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }

        } while (availableLikes > count.get() || error);
    }

    private Integer getLikesCount(JSONObject post) throws JSONException {
        JSONObject likes = post.getJSONObject(JsonFields.LIKES.getFieldName());
        return likes.getInt(JsonFields.COUNT.getFieldName());

    }

    private void addWallParameter(List<NameValuePair> parameter){
        parameter.add(new BasicNameValuePair(VKParameter.DOMAIN.getParameterName(), "just_str"));
    }

    private void addLikesParameter(JSONObject post, List<NameValuePair> likesParameter) throws JSONException {

        String ownerId = post.getString(JsonFields.OWNER_ID.getFieldName());
        String itemId = post.getString(JsonFields.ITEM_ID.getFieldName());

        likesParameter.add(new BasicNameValuePair(VKParameter.TYPE.getParameterName(), Fields.POST.getField()));
        likesParameter.add(new BasicNameValuePair(VKParameter.OWNER_ID.getParameterName(), ownerId));
        likesParameter.add(new BasicNameValuePair(VKParameter.ITEM_ID.getParameterName(), itemId));
        likesParameter.add(new BasicNameValuePair(VKParameter.FILTER.getParameterName(), Fields.LIKES.getField()));
        likesParameter.add(new BasicNameValuePair(VKParameter.COUNT.getParameterName(), String.valueOf(DEFAULT_OFFSET_LIKES)));
    }
}
