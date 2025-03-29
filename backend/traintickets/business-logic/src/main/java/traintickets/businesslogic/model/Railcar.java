package traintickets.businesslogic.model;

import java.util.List;

public record Railcar(RailcarId id, String model, String type, List<Place> places) {
}
