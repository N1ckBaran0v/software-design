package traintickets.payment.data;

import traintickets.businesslogic.payment.PaymentData;

import java.math.BigDecimal;

public final class NoOpPaymentData implements PaymentData {
    @Override
    public BigDecimal getSum() {
        return null;
    }

    @Override
    public void setSum(BigDecimal sum) {
    }
}
