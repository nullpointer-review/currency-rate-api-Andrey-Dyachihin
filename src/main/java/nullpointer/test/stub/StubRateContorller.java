package nullpointer.test.stub;

import java.math.BigDecimal;

import nullpointer.test.model.RateController;
import nullpointer.test.model.RateRequest;
import nullpointer.test.model.RateResponse;

public class StubRateContorller implements RateController
{
    final private static BigDecimal RATE = new BigDecimal("2049103.40");

    @Override
    public RateResponse getRate(RateRequest request) {
        return new RateResponse(request.getCode(), RATE, request.getDate());
    }
}
