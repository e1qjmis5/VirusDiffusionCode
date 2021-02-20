package Entity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Route {
    //private Bus bus; // Service Number
    private String busID;
    private ArrayList<Station> stationList1;
    private ArrayList<Station> stationList2;

    public Route(String busID) {
        this.busID = busID;
        stationList1 = new ArrayList<Station>();
        stationList2 = new ArrayList<Station>();
    }

    public String getBus() {
        return busID;
    }

    public void setBus(String busID) {
        this.busID = busID;
    }

    public void addStation(Station station, int direction) {
        if (direction == 1)
            stationList1.add(station);
        else
            stationList2.add(station);
    }

    public ArrayList<Station> getStationList(int direction) {
        if (direction == 1)
            return stationList1;
        else
            return stationList2;
    }


}
