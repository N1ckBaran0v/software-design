package ru.traintickets.control.modules;

import ru.traintickets.businesslogic.session.SessionManager;
import ru.traintickets.di.ApplicationContextBuilder;
import ru.traintickets.di.ContextModule;
import ru.traintickets.security.session.SessionManagerImpl;

public final class SecurityModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(SessionManager.class, SessionManagerImpl.class);
    }
}
