package traintickets.control.modules;

import traintickets.businesslogic.api.*;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.service.*;
import traintickets.businesslogic.session.SessionManager;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;

public final class BusinessLogicModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
//        builder
//                .addSingleton(AuthService.class, beanProvider -> {
//                    var repo = beanProvider.getInstance(UserRepository.class);
//                    var manager = beanProvider.getInstance(SessionManager.class);
//                    return new AuthServiceImpl(repo, manager, "temp");
//                })
//                .addSingleton(CommentService.class, CommentServiceImpl.class)
//                .addSingleton(FilterService.class, FilterServiceImpl.class)
//                .addSingleton(RaceService.class, RaceServiceImpl.class)
//                .addSingleton(RouteService.class, RouteServiceImpl.class)
//                .addSingleton(TicketService.class, TicketServiceImpl.class)
//                .addSingleton(TrainService.class, TrainServiceImpl.class)
//                .addSingleton(UserService.class, UserServiceImpl.class);
    }
}
