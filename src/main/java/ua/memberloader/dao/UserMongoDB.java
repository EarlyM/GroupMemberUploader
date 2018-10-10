package ua.memberloader.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

public class UserMongoDB {

    private MongoCollection<Document> collection;

    public UserMongoDB(MongoDB mongoDB, String collection) {
        this.collection = mongoDB.getCollection(collection);
        this.collection.createIndex(new BasicDBObject("id", 1));
    }

    public synchronized void insertUsers(List<Document> users){
        collection.insertMany(users);
    }

    public synchronized void insertLikes(Integer userId, List<Document> likes){
        collection.findOneAndUpdate(Filters.eq("id", userId), Updates.pushEach("likes", likes));
    }
}
