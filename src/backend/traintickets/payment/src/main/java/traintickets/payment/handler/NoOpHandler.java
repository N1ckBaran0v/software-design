package traintickets.payment.handler;

import traintickets.businesslogic.payment.PaymentData;
import traintickets.payment.data.NoOpPaymentData;

public final class NoOpHandler implements PaymentHandler {
    @Override
    public Class<? extends PaymentData> paymentDataClass() {
        return NoOpPaymentData.class;
    }

    @Override
    public void accept(PaymentData paymentData) {
    }
}
