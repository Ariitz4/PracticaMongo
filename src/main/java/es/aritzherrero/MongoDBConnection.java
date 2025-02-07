package es.aritzherrero;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.Arrays;

public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "sample_airbnb";
    private static final String COLLECTION_NAME = "listingsAndReviews";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBConnection() {
        mongoClient = MongoClients.create(CONNECTION_STRING);
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(COLLECTION_NAME);
    }

    public void insertListing(Document listing) {
        collection.insertOne(listing);
        System.out.println("Nuevo alojamiento insertado: " + listing.toJson());
    }

    public void readListing(String id) {
        Document listing = collection.find(Filters.eq("_id", id)).first();
        System.out.println("--- Leer (Consultar) ---");
        System.out.println(listing != null ? listing.toJson() : "No encontrado");
    }

    public void updateListing(String id, String field, Object newValue) {
        Bson filter = Filters.eq("_id", id);
        Bson updateOperation = Updates.set(field, newValue);
        collection.updateOne(filter, updateOperation);
        System.out.println("Alojamiento actualizado");
    }

    public void deleteListing(String id) {
        collection.deleteOne(Filters.eq("_id", id));
        System.out.println("Alojamiento eliminado");
    }

    public void close() {
        mongoClient.close();
    }

    public static void main(String[] args) {
        MongoDBConnection db = new MongoDBConnection();
        Document newListing = new Document("name", "Apartamento en la playa")
                .append("summary", "Un hermoso apartamento junto al mar")
                .append("price", 120);
        db.insertListing(newListing);

        String id = newListing.getObjectId("_id").toHexString();
        db.readListing(id);
        db.updateListing(id, "price", 150);
        db.readListing(id);
        db.deleteListing(id);
        db.close();
    }
}

