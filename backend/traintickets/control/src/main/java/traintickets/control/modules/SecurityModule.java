package traintickets.control.modules;

import traintickets.businesslogic.session.SessionManager;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.security.session.SessionManagerImpl;

public final class SecurityModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(SessionManager.class, SessionManagerImpl.class);
    }
}
