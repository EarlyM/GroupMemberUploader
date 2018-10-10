package ua.memberloader.http;

import ua.memberloader.http.parameters.VKMethod;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class VKURIBuilder {

    public URI buildURI(VKMethod method, List<NameValuePair> params) throws URISyntaxException {
        URIBuilder builder = createBuilder();
        builder.setPath(method.getPath());
        builder.setParameters(params);
        return builder.build();
    }

    private URIBuilder createBuilder(){
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("api.vk.com");
        return  builder;
    }
}
