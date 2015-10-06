package nullpointer.test.model;

import java.math.BigDecimal;
import java.util.Date;

public class RateResponse
{
    final private String code;
    final private BigDecimal rate;
    final private Date date;

    public String getCode() {
        return code;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Date getDate() {
        return date;
    }

    public RateResponse(String code, BigDecimal rate, Date date) {
        if (code == null)
            throw new NullPointerException("code");
        if (code.isEmpty())
            throw new IllegalArgumentException("code is empty");
        if (rate == null)
            throw new NullPointerException("rate");
        if (date == null)
            throw new NullPointerException("date");
        this.code = code;
        this.rate = rate;
        this.date = date;
    }

    @Override
    public String toString() {
        return "CurrencyRate [code=" + code + ", rate=" + rate + ", date=" + date + "]";
    }
}
