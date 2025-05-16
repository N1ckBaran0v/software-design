package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.model.TrainDocument;

import java.util.Date;
import java.util.Optional;

public final class TrainRepositoryImpl implements TrainRepository {
    private final MongoExecutor mongoExecutor;
    private final MongoCollection<TrainDocument> trainCollection;

    public TrainRepositoryImpl(MongoExecutor mongoExecutor) {
        this.mongoExecutor = mongoExecutor;
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
    }

    @Override
    public void addTrain(Train train) {
        mongoExecutor.executeConsumer(session -> trainCollection.insertOne(session, new TrainDocument(train)));
    }

    @Override
    public Optional<Train> getTrain(TrainId trainId) {
        return mongoExecutor.executeFunction(session -> {
            var train = trainCollection.find(session, Filters.eq("_id", new ObjectId(trainId.id()))).first();
            return Optional.ofNullable(train == null ? null : train.toTrain());
        });
    }

    @Override
    public Iterable<Train> getTrains(Date start, Date end) {
        return null;
    }
}
