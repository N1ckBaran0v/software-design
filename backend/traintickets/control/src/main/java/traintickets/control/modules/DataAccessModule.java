package traintickets.control.modules;

import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.businesslogic.repository.*;
import traintickets.dataaccess.repository.*;

public final class DataAccessModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(CommentRepository.class, CommentRepositoryImpl.class)
                .addSingleton(FilterRepository.class, FilterRepositoryImpl.class)
                .addSingleton(RaceRepository.class, RaceRepositoryImpl.class)
                .addSingleton(RailcarRepository.class, RailcarRepositoryImpl.class)
                .addSingleton(TicketRepository.class, TicketRepositoryImpl.class)
                .addSingleton(TrainRepository.class, TrainRepositoryImpl.class)
                .addSingleton(UserRepository.class, UserRepositoryImpl.class);
    }
}
