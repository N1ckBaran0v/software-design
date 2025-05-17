package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.model.PlaceDocument;
import traintickets.dataaccess.mongo.model.RailcarDocument;
import traintickets.dataaccess.mongo.model.TrainDocument;

import java.util.*;
import java.util.stream.StreamSupport;

public final class RailcarRepositoryImpl implements RailcarRepository {
    private final MongoExecutor mongoExecutor;
    private final MongoCollection<RailcarDocument> railcarCollection;
    private final MongoCollection<PlaceDocument> placeCollection;
    private final MongoCollection<TrainDocument> trainCollection;

    public RailcarRepositoryImpl(MongoExecutor mongoExecutor) {
        this.mongoExecutor = mongoExecutor;
        railcarCollection = mongoExecutor.getDatabase().getCollection("railcars", RailcarDocument.class);
        placeCollection = mongoExecutor.getDatabase().getCollection("places", PlaceDocument.class);
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
    }

    @Override
    public void addRailcar(Railcar railcar) {
        mongoExecutor.transactionConsumer(session -> {
            if (railcarCollection.find(session, Filters.eq("model", railcar.model())).first() != null) {
                throw new EntityAlreadyExistsException(String.format("Railcar %s already exists", railcar.model()));
            }
            var railcarId = Objects.requireNonNull(railcarCollection.insertOne(session, new RailcarDocument(railcar))
                    .getInsertedId()).asObjectId().getValue();
            var placeDocuments = railcar.places().stream().map(place -> new PlaceDocument(railcarId, place)).toList();
            placeCollection.insertMany(session, placeDocuments);
        });
    }

    @Override
    public Iterable<Railcar> getRailcarsByType(String type) {
        return mongoExecutor.transactionFunction(session -> StreamSupport.stream(
                railcarCollection.find(Filters.eq("type", type)).spliterator(), false).map(railcarDocument ->
                railcarDocument.toRailcar(StreamSupport.stream(placeCollection.find(session,
                                Filters.eq("railcarId", railcarDocument.id())).spliterator(), false)
                        .map(PlaceDocument::toPlace).toList())).toList());
    }

    @Override
    public Iterable<Railcar> getRailcarsByTrain(TrainId trainId) {
        return mongoExecutor.transactionFunction(session -> {
            var train = trainCollection.find(session, Filters.eq("_id", new ObjectId(trainId.id()))).first();
            var ids = train == null ? Set.of() : new HashSet<>(train.railcars());
            return StreamSupport.stream(railcarCollection.find(session, Filters.in("_id", ids)).spliterator(), false)
                    .map(railcarDocument -> railcarDocument.toRailcar(StreamSupport.stream(placeCollection.find(session,
                                    Filters.eq("railcarId", railcarDocument.id())).spliterator(), false)
                            .map(PlaceDocument::toPlace).toList())).toList();
        });
    }
}
