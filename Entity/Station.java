package Entity;

import java.util.HashSet;
import java.util.Set;

public class Station implements Comparable<Station> {
    private int volume;
    private String id;
    private boolean isCheckPoint;
//    private Set<Trip> tripHashmap;
    //private ArrayList<RRSet> rrSets;
    private Set<RRSet> rrSets;
    private int coverRRNum;
    private int updateTime;

    public Station(String id) {
        this.volume = 0;
        this.coverRRNum = 0;
        this.updateTime = 0;
        this.id = id;
        this.isCheckPoint = false;
//        this.tripHashmap = new HashSet<>();
        //this.rrSets = new ArrayList<>();
        this.rrSets = new HashSet<>();
    }

    public void updateCoverNum(int updateTime) {
        this.coverRRNum = 0;
        for (RRSet rrSet : rrSets) {
            if (!rrSet.isSelected) {
                this.coverRRNum++;
            }
        }
        this.updateTime = updateTime;
    }

    public void resetCoverNum() {
        this.coverRRNum = rrSets.size();
        this.updateTime = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addTrip(Trip trip) {
        this.volume++;
    }

//    public boolean isContain(Trip trip) {
//        return tripHashmap.contains(trip);
//    }

    public int getVolume() {
        return this.volume;
    }

    public void setCheckPoint() {
        this.isCheckPoint = true;
    }

    public boolean isCheckPoint() {
        return this.isCheckPoint;
    }

    public void clearCheckPoint() {
        this.isCheckPoint = false;
    }

    public int getCoverRRNum() {
        return coverRRNum;
    }

    public int getUpdateTime() {
        return this.updateTime;
    }

    public void setCoverRRNum(int coverRRNum, int updateTime) {
        this.coverRRNum = coverRRNum;
        this.updateTime = updateTime;
    }

    public void addRRSet(RRSet rrSet) {
        this.rrSets.add(rrSet);
        this.coverRRNum = rrSets.size();
    }

    public Set<RRSet> getRRSets() {
        return rrSets;
    }

    @Override
    public int compareTo(Station o) {
        return Integer.compare(o.coverRRNum, this.coverRRNum);
    }
}
