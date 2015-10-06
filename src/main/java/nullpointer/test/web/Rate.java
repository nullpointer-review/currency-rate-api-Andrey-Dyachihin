package nullpointer.test.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import nullpointer.test.model.RateController;
import nullpointer.test.model.RateRequest;
import nullpointer.test.model.RateResponse;

import org.json.JSONObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/rate")
public class Rate
{
    @Resource(name = "rate-controller")
    private RateController rateController;

    private JSONObject toJson(RateResponse currRate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            JSONObject result = new JSONObject();

            result.put("code", currRate.getCode());
            result.put("rate", currRate.getRate().setScale(4).toString());
            result.put("date", df.format(currRate.getDate()));

            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Error processing " + currRate, ex);
        }
    }

    private Date dayOffset(Date date, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, offset);
        return cal.getTime();
    }

    @RequestMapping("/")
    public void welcome() {
    }

    @RequestMapping(value = "/{code}/{date}", produces = "application/json")
    public @ResponseBody String byCodeDate( //
        @PathVariable("code") String code,
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return toJson(rateController.getRate(new RateRequest(code, date))).toString();
    }

    @RequestMapping(value = "/{code}", produces = "application/json")
    public @ResponseBody String byCode(@PathVariable("code") String code) {
        return byCodeDate(code, dayOffset(new Date(), 1));
    }
}
