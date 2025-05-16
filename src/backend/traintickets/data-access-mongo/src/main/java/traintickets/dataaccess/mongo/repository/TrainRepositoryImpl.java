package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.model.RaceDocument;
import traintickets.dataaccess.mongo.model.ScheduleDocument;
import traintickets.dataaccess.mongo.model.TrainDocument;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class TrainRepositoryImpl implements TrainRepository {
    private final MongoExecutor mongoExecutor;
    private final MongoCollection<TrainDocument> trainCollection;
    private final MongoCollection<RaceDocument> raceCollection;
    private final MongoCollection<ScheduleDocument> scheduleCollection;

    public TrainRepositoryImpl(MongoExecutor mongoExecutor) {
        this.mongoExecutor = mongoExecutor;
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
        raceCollection = mongoExecutor.getDatabase().getCollection("races", RaceDocument.class);
        scheduleCollection = mongoExecutor.getDatabase().getCollection("schedules", ScheduleDocument.class);
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
        return mongoExecutor.transactionFunction(session -> {
            var badRaces = StreamSupport.stream(scheduleCollection.find(session).spliterator(), false)
                    .filter(schedule -> {
                        var departure = schedule.departure();
                        var first = departure != null && !(departure.before(start) || departure.after(end));
                        var arrival = schedule.arrival();
                        var second = arrival != null && !(arrival.before(start) || arrival.after(end));
                        return first || second;
                    }).map(ScheduleDocument::raceId).collect(Collectors.toSet());
            var badTrains = StreamSupport.stream(raceCollection.find(session, Filters.in("_id", badRaces))
                    .spliterator(), false).map(RaceDocument::trainId).collect(Collectors.toSet());
            return StreamSupport.stream(trainCollection.find(session, Filters.not(Filters.in("_id", badTrains)))
                    .spliterator(), false).map(TrainDocument::toTrain).toList();
        });
    }
}
