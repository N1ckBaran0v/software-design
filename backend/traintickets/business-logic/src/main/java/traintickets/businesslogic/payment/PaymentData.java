package traintickets.businesslogic.payment;

import java.math.BigDecimal;

public interface PaymentData {
    BigDecimal getSum();
    void setSum(BigDecimal sum);
}
