package Entity;

import java.util.Date;

public class Trip implements Comparable<Trip> {
    private String cardID;
    private String routeID;
    private String day;
    private Date beginTime, endTime;
    private Station beginStation, endStation;
    private Node node;
    private Integer dayIndex;
    private Integer index;// the index of a trip of a passenger
    public boolean isInfected;

    public Trip(String cardID) {
        this.cardID = cardID;
    }

    public int hasCode() {
        return (cardID + routeID + beginTime.toString()).hashCode();
    }

    public Passenger getPassenger() {
        if (node == null)
            return null;
        return node.getPassenger();
    }

    public void infect() {
        this.isInfected = true;
        node.isInfected = true;
    }

    public void reset() {
        this.isInfected = false;
        node.isInfected = false;
    }

    public boolean isInfected() {
        return isInfected;
    }

    public String getCardID() {
        return cardID;
    }

    public Station getBeginStation() {
        return beginStation;
    }

    public void setBeginStation(Station beginStation) {
        this.beginStation = beginStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public void setEndStation(Station endStation) {
        this.endStation = endStation;
    }

//    public String getRouteID() {
//        return routeID;
//    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Integer getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(Integer dayIndex) {
        this.dayIndex = dayIndex;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public int compareTo(Trip candidate) {
        return this.beginTime.compareTo(candidate.getBeginTime());
    }
}
