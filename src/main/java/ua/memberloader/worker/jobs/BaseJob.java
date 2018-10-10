package ua.memberloader.worker.jobs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import ua.memberloader.AppStarter;
import ua.memberloader.dao.UserMongoDB;
import ua.memberloader.http.VKAPIMethodCaller;
import ua.memberloader.http.VKURIBuilder;
import ua.memberloader.http.parameters.VKParameter;
import ua.memberloader.parser.Parser;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseJob {

    protected VKAPIMethodCaller caller;
    protected VKURIBuilder builder = new VKURIBuilder();
    protected Parser parser = new Parser();
    protected UserMongoDB userDB;

    public BaseJob(UserMongoDB userMongoDB){
        caller = new VKAPIMethodCaller();
        builder = new VKURIBuilder();
        parser = new Parser();
        userDB = userMongoDB;
    }

    protected void addOffsetOrReplace(List<NameValuePair> params, AtomicInteger count, Integer offset) {
        NameValuePair offsetValuePair = params.get(params.size() - 1);
        Integer value  = count.getAndAdd(offset);
        if(offsetValuePair.getName().equals(VKParameter.OFFSET.getParameterName())){
            params.set(params.size() - 1, new BasicNameValuePair(VKParameter.OFFSET.getParameterName(), String.valueOf(value)));
        } else {
            params.add(new BasicNameValuePair(VKParameter.OFFSET.getParameterName(), String.valueOf(offset)));
        }
    }

    protected void addDefaultParameter(List<NameValuePair> params){
        params.add(new BasicNameValuePair(VKParameter.TOKEN.getParameterName(), AppStarter.TOKEN));
        params.add(new BasicNameValuePair(VKParameter.VERSION.getParameterName(), "5.85"));
    }
}
