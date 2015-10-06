package nullpointer.test.model;

import java.util.Date;

public class RateRequest
{
    final private String code;
    final private Date date;

    public String getCode() {
        return code;
    }

    public Date getDate() {
        return date;
    }

    public RateRequest(String code, Date date) {
        if (code == null)
            throw new NullPointerException("code");
        if (code.isEmpty())
            throw new IllegalArgumentException("code is empty");
        if (date == null)
            throw new NullPointerException("date");
        this.code = code;
        this.date = date;
    }

    @Override
    public String toString() {
        return "RateRequest [code=" + code + ", date=" + date + "]";
    }
}
