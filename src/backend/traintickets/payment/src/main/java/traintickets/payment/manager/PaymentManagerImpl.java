package traintickets.payment.manager;

import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.payment.PaymentManager;
import traintickets.payment.handler.PaymentHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PaymentManagerImpl implements PaymentManager {
    private final Map<Class<? extends PaymentData>, PaymentHandler> handlers = new HashMap<>();

    public PaymentManagerImpl(Iterable<PaymentHandler> paymentHandlers) {
        for (var handler : Objects.requireNonNull(paymentHandlers)) {
            Objects.requireNonNull(handler);
            handlers.put(handler.paymentDataClass(), handler);
        }
    }

    @Override
    public void pay(PaymentData paymentData) {
        handlers.get(paymentData.getClass()).accept(paymentData);
    }
}
