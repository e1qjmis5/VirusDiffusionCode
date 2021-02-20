package Entity;

import Setting.Setting;

import java.util.*;

public class Passenger implements Comparable<Passenger> {
    private final String cardID;
    private final ArrayList<Trip> tripList;
    private final HashMap<Integer, ArrayList<Trip>> dayTripsHashMap;//<DayIndex, Trips>
    private boolean isBlocked;
    private final Set<RRSet2> rrSets;
    private int coverRRNum;
    private int updateTime;
    private Trip firstTrip;
    public boolean available;

    public Passenger(String cardID) {
        this.coverRRNum = 0;
        this.updateTime = 0;
        this.cardID = cardID;
        this.isBlocked = false;
        this.tripList = new ArrayList<>();
        this.dayTripsHashMap = new HashMap<>();
        this.rrSets = new HashSet<>();
        this.available = true;
    }


    public void updateCoverNum(int updateTime) {
        this.coverRRNum = 0;
        for (RRSet2 rrSet : rrSets) {
            if (!rrSet.isSelected) {
                this.coverRRNum++;
            }
        }
        this.updateTime = updateTime;
    }

    public int getUpdateTime() {
        return this.updateTime;
    }

    public int getCoverRRNum() {
        return coverRRNum;
    }

    public void resetCoverNum() {
        this.coverRRNum = rrSets.size();
        this.updateTime = 0;
    }

    public void addRRSet(RRSet2 rrSet) {
        this.rrSets.add(rrSet);
        this.coverRRNum = rrSets.size();
    }

    public Set<RRSet2> getRRSets() {
        return rrSets;
    }

    public Trip getFirstTrip() {
        return firstTrip;
    }

    public boolean isInfected() {
        for (int i = Setting.PERIOD - 1; i >= 0; i--) {
            if (this.dayTripsHashMap.get(i) != null) {
                ArrayList<Trip> list = this.dayTripsHashMap.get(i);
                Trip trip = list.get(list.size() - 1);
                return (trip.isInfected);
            }
        }
        return false;
    }

    public void reset() {
        isBlocked = false;
    }

    public void initializeInfection() {
        for (int i = 0; i < Setting.PERIOD; i++) {
            if (this.dayTripsHashMap.get(i) != null) {
                this.dayTripsHashMap.get(i).get(0).infect();
                firstTrip = this.dayTripsHashMap.get(i).get(0);
                break;
            }
        }
    }

//    public boolean checkInitialInfection() {
//        for (int i = 0; i < Setting.PERIOD; i++) {
//            if (this.dayTripsHashMap.get(i) != null) {
//                if (this.dayTripsHashMap.get(i).get(0) != null) {
//                    if (this.dayTripsHashMap.get(i).get(0).isInfected)
//                        return true;
//                }
//            }
//        }
//        return false;
//    }

    public String getCardID() {
        return this.cardID;
    }

    public void addTrip(Trip trip) {
        this.tripList.add(trip);
        ArrayList<Trip> tripList;
        if (dayTripsHashMap.containsKey(trip.getDayIndex())) {
            tripList = dayTripsHashMap.get(trip.getDayIndex());
        } else {
            tripList = new ArrayList<>();
            dayTripsHashMap.put(trip.getDayIndex(), tripList);
        }
        tripList.add(trip);
        Collections.sort(tripList);
    }

    public void sortTrip() {
        Collections.sort(tripList);
        for (int i = 0; i < tripList.size(); i++) {
            tripList.get(i).setIndex(i);
        }
    }

    public ArrayList<Trip> getTripList() {
        return tripList;
    }

    public HashMap<Integer, ArrayList<Trip>> getDayTripsHashMap() {
        return dayTripsHashMap;
    }

    public Trip getInfectedTrip(Trip trip2) {
        int day = trip2.getDayIndex();
        day += Setting.GAMMA;
        Trip trip = null;
        if (day >= Setting.PERIOD)
            return null;
        if (dayTripsHashMap.size() == 0) {
            System.out.println("! ERROR (Passenger.java) - dayTripsHashMap is empty");
            return null;
        }
        for (int i = day; i < Setting.PERIOD; i++) {
            if (dayTripsHashMap.get(i) != null) {
                trip = dayTripsHashMap.get(i).get(0);
                break;
            }
        }
        return trip;
    }

    public void block() {
        this.isBlocked = true;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public int compareTo(Passenger o) {
        return Integer.compare(o.coverRRNum, this.coverRRNum);
    }

}
