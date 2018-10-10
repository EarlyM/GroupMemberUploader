package ua.memberloader.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;

import java.nio.charset.Charset;

public class VKAPIMethodCaller {

    private HttpClient client;

    public VKAPIMethodCaller() {
        this.client = HttpClients.createDefault();
    }

    public String send(URI uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if(entity != null) {
            return IOUtils.toString(entity.getContent(), Charset.defaultCharset());
        }
        return null;
    }
}
