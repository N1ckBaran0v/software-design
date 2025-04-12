package traintickets.businesslogic.service;

import traintickets.businesslogic.api.RailcarService;
import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.session.SessionManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class RailcarServiceImpl implements RailcarService {
    private final RailcarRepository railcarRepository;
    private final SessionManager sessionManager;

    public RailcarServiceImpl(RailcarRepository railcarRepository, SessionManager sessionManager) {
        this.railcarRepository = Objects.requireNonNull(railcarRepository);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    public void addRailcar(String sessionId, Railcar railcar) {
        railcar.validate();
        railcarRepository.addRailcar(sessionManager.getUserInfo(sessionId).role(), railcar);
    }

    @Override
    public List<Railcar> getRailcars(String sessionId, String type) {
        var role = sessionManager.getUserInfo(sessionId).role();
        return StreamSupport.stream(railcarRepository.getRailcarsByType(role, type).spliterator(), false).toList();
    }
}
