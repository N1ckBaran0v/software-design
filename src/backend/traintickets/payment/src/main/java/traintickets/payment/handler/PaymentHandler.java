package traintickets.payment.handler;

import traintickets.businesslogic.payment.PaymentData;

import java.util.function.Consumer;

public interface PaymentHandler extends Consumer<PaymentData> {
    Class<? extends PaymentData> paymentDataClass();
}
