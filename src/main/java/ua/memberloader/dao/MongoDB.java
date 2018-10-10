package ua.memberloader.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDB {

    private MongoClient client;
    private MongoDatabase db;
    public MongoDB(String host, Integer port, String database){
        client = new MongoClient(host, port);
        System.out.println(client.getCredentialsList().toString());
        db = client.getDatabase(database);
    }

    protected MongoCollection<Document> getCollection(String collection){
        return db.getCollection(collection);
    }
}
