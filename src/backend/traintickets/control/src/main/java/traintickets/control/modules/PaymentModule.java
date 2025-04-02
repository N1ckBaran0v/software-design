package traintickets.control.modules;

import traintickets.businesslogic.payment.PaymentManager;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.payment.handler.NoOpHandler;
import traintickets.payment.handler.PaymentHandler;
import traintickets.payment.manager.PaymentManagerImpl;

public final class PaymentModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(PaymentManager.class,
                        beanProvider -> new PaymentManagerImpl(beanProvider.getInstances(PaymentHandler.class)))
                .addSingleton(PaymentHandler.class, NoOpHandler.class);
    }
}
