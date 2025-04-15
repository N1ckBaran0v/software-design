package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.List;

public record Route(List<RaceId> races, List<Schedule> starts, List<Schedule> ends) implements Serializable {
}
