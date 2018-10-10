package ua.memberloader.worker.jobs;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import ua.memberloader.AppStarter;
import ua.memberloader.dao.UserMongoDB;
import ua.memberloader.http.VKAPIMethodCaller;
import ua.memberloader.http.VKURIBuilder;
import ua.memberloader.http.parameters.Fields;
import ua.memberloader.http.parameters.VKMethod;
import ua.memberloader.http.parameters.VKParameter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import ua.memberloader.parser.JsonFields;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class UserLoaderJob extends BaseJob implements Runnable {

    private static final Integer DEFAULT_OFFSET = 1000;
    private static final AtomicInteger count = new AtomicInteger(0);
    private volatile static Integer AVAILABLE_USERS = null;

    private CountDownLatch countDownLatch;

    public UserLoaderJob(CountDownLatch countDownLatch, UserMongoDB mongoDB){
        super(mongoDB);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {

        boolean error = false;
        List<NameValuePair> pairList = new LinkedList<>();
        addDefaultParameter(pairList);
        addParameter(pairList);

        do{
            caller = new VKAPIMethodCaller();
            URI uri;

            try {

                if(!error){
                    addOffsetOrReplace(pairList, count, DEFAULT_OFFSET);
                }

                error = false;

                uri = builder.buildURI(VKMethod.GET_MEMBERS, pairList);
                String json = caller.send(uri);

                AVAILABLE_USERS = parser.getFieldValue(json, JsonFields.COUNT, Integer.class);

                JSONArray users = parser.getFieldValue(json, JsonFields.ITEMS, JSONArray.class);
                List<Document> userDocument = new LinkedList<>();

                for(int i = 0; i < users.length(); i++){
                   userDocument.add(Document.parse(users.get(i).toString()));
                }

                userDB.insertUsers(userDocument);

            } catch (JSONException e) {
                error = true;
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }


        } while(AVAILABLE_USERS > count.get() || error);

        countDownLatch.countDown();
    }

    private void addParameter(List<NameValuePair> parameter){
        parameter.add(new BasicNameValuePair(VKParameter.GROUP_ID.getParameterName(), "just_str"));
        parameter.add(new BasicNameValuePair(VKParameter.FIELDS.getParameterName(), Fields.SEX.getField()));
    }

}
