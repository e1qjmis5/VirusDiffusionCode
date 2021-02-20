package Algorithm;

import Entity.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TopK {
    private HashMap<String, Station> stationHashMap; // <station id, station>
    private int checkPointNum;

    public ArrayList<Station> getCheckPoint() {
        ArrayList<Station> candidate = new ArrayList<>();
        Station[] stationList = stationHashMap.values().toArray(new Station[0]);
        Station tem;

        for (int i = 1; i < stationList.length - 1; i++) {
            for (int j = i + 1; j < stationList.length; j++) {
                if (stationList[i].getVolume() < stationList[j].getVolume()) {
                    tem = stationList[i];
                    stationList[i] = stationList[j];
                    stationList[j] = tem;
                }
            }
        }

        candidate.addAll(Arrays.asList(stationList).subList(0, checkPointNum));

        return candidate;
    }

    public void setStationHashMap(HashMap<String, Station> stationHashMap) {
        this.stationHashMap = stationHashMap;
    }

    public void setCheckPointNum(int num) {
        this.checkPointNum = num;
    }
}
