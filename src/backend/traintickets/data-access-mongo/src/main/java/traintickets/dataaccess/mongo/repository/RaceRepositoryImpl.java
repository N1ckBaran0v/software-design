package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;
import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.model.RaceDocument;
import traintickets.dataaccess.mongo.model.ScheduleDocument;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class RaceRepositoryImpl implements RaceRepository {
    private final MongoExecutor mongoExecutor;
    private final MongoCollection<RaceDocument> raceCollection;
    private final MongoCollection<ScheduleDocument> scheduleCollection;

    public RaceRepositoryImpl(MongoExecutor mongoExecutor) {
        this.mongoExecutor = mongoExecutor;
        raceCollection = mongoExecutor.getDatabase().getCollection("races", RaceDocument.class);
        scheduleCollection = mongoExecutor.getDatabase().getCollection("schedules", ScheduleDocument.class);
    }

    @Override
    public void addRace(Race race) {
        mongoExecutor.transactionConsumer(session -> {
            var badIds = findBetween(session, race.schedule().getFirst().departure(),
                    race.schedule().getLast().arrival()).map(ScheduleDocument::raceId).collect(Collectors.toSet());
            if (StreamSupport.stream(raceCollection.find(session, Filters.in("_id", badIds))
                    .spliterator(), false).map(RaceDocument::trainId).map(ObjectId::toHexString)
                    .collect(Collectors.toSet()).contains(race.trainId().id())) {
                throw new TrainAlreadyReservedException(race.trainId());
            }
            var raceId = Objects.requireNonNull(raceCollection.insertOne(session, new RaceDocument(race))
                    .getInsertedId()).asObjectId().getValue();
            scheduleCollection.insertMany(session, race.schedule().stream()
                    .map(schedule -> new ScheduleDocument(raceId, schedule)).toList());
        });
    }

    private Stream<ScheduleDocument> findBetween(ClientSession session, Date start, Date end) {
        return StreamSupport.stream(scheduleCollection.find(session).spliterator(), false).filter(schedule -> {
            var departure = schedule.departure();
            var first = departure != null && !(departure.before(start) || departure.after(end));
            var arrival = schedule.arrival();
            var second = arrival != null && !(arrival.before(start) || arrival.after(end));
            return first || second;
        });
    }

    @Override
    public Optional<Race> getRace(RaceId raceId) {
        return mongoExecutor.transactionFunction(session -> {
            var raceDocument = raceCollection.find(session, Filters.eq("_id", new ObjectId(raceId.id()))).first();
            return Optional.ofNullable(raceDocument == null ? null : raceDocument.toRace(StreamSupport.stream(
                    scheduleCollection.find(session, Filters.eq("raceId", raceDocument.id())).spliterator(), false)
                    .map(ScheduleDocument::toSchedule).toList()
            ));
        });
    }

    @Override
    public List<Race> getRaces(Filter filter) {
        return mongoExecutor.transactionFunction(session -> {
            var map = new HashMap<ObjectId, List<Schedule>>();
            findBetween(session, filter.start(), filter.end()).forEach(scheduleDocument -> {
                var key = scheduleDocument.raceId();
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(scheduleDocument.toSchedule());
            });
            return filterSchedules(session, map, filter);
        });
    }

    private List<Race> filterSchedules(ClientSession session, Map<ObjectId, List<Schedule>> schedules, Filter filter) {
        var result = new ArrayList<Race>();
        if (filter.transfers() == 0) {
            for (var entry : schedules.entrySet()) {
                if (containsStations(entry.getValue(), filter.departure(), filter.destination()) == 2) {
                    addRace(session, result, entry);
                }
            }
        } else if (filter.transfers() == 1) {
            for (var entry : schedules.entrySet()) {
                if (containsStations(entry.getValue(), filter.departure(), filter.destination()) > 0) {
                    addRace(session, result, entry);
                }
            }
        } else {
            for (var entry : schedules.entrySet()) {
                if (entry.getValue().size() > 1) {
                    addRace(session, result, entry);
                }
            }
        }
        return result;
    }

    private int containsStations(List<Schedule> schedules, String... stations) {
        var found = new HashSet<String>();
        if (schedules.size() > 1) {
            for (var schedule : schedules) {
                for (var station : stations) {
                    if (station.equals(schedule.name())) {
                        found.add(station);
                    }
                }
            }
        }
        return found.size();
    }

    private void addRace(ClientSession session, List<Race> races, Map.Entry<ObjectId, List<Schedule>> entry) {
        var raceDocument = raceCollection.find(session, Filters.and(
                Filters.eq("_id", entry.getKey()), Filters.eq("finished", false))).first();
        if (raceDocument != null) {
            races.add(raceDocument.toRace(entry.getValue()));
        }
    }

    @Override
    public void updateRace(RaceId raceId, boolean isFinished) {
        mongoExecutor.executeConsumer(session -> raceCollection.updateOne(session,
                Filters.eq("_id", new ObjectId(raceId.id())), Updates.set("finished", isFinished)));
    }
}
