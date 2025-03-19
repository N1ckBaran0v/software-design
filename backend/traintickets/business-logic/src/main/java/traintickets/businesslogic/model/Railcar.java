package traintickets.businesslogic.model;

import java.util.List;

public record Railcar(RailcarId id, String type, List<Place> places) {
}
