package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.io.Serializable;
import java.util.List;

public record Railcar(RailcarId id, String model, String type, List<Place> places) implements Serializable {
    public void validate() {
        if (model == null || model.isEmpty() || type == null || type.isEmpty() || places == null) {
            throw new InvalidEntityException("Invalid railcar");
        }
        if (!places.isEmpty()) {
            var curr = places.getFirst();
            curr.validate();
            for (var i = 1; i < places.size(); ++i) {
                var prev = curr;
                curr = places.get(i);
                curr.validate();
                if (curr.number() <= prev.number()) {
                    throw new InvalidEntityException("Invalid railcar");
                }
            }
        }
    }
}
