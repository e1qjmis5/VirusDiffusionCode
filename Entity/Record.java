package Entity;

import java.util.Date;

public class Record {
    private String cardID, day, route, beginStation, endStation;
    private Date beginTime, endTime;

    public Record(String cardID, Date beginTime, Date endTime, String day,
                  String beginStation, String endStation, String route) {
        this.cardID = cardID;
        this.day = day;
        this.route = route;
        this.beginStation = beginStation;
        this.endStation = endStation;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public String getCardID() {
        return cardID;
    }

    public String getDay() {
        return day;
    }

    public String getRoute() {
        return route;
    }

    public String getBeginStation() {
        return beginStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
