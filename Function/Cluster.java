package Function;

import Entity.Route;

import java.util.*;

public class Cluster {


    private HashMap<String, HashMap> tripHashMap; // <Day, <RouteID, <CardID, ArrayList<Trip>>>>
    private HashMap<String, Integer> passengerHashMap;
    private ArrayList<HashMap<String, HashMap>> clusterList;
    private HashMap<String, Route> routeHashMap;
    ArrayList<ArrayList<String>> routeIdCluster;

    public Cluster(HashMap<String, HashMap> tripHashMap, HashMap<String, Integer> passengerHashMap) {
        this.tripHashMap = tripHashMap;
        this.passengerHashMap = passengerHashMap;
        this.clusterList = new ArrayList<>();
    }

    public void generateCluster() {
        spiltRoute(tripHashMap);
        getOverlapRatio(tripHashMap, tripHashMap);
    }

    private void generateRouteIDCluster() {
        this.routeIdCluster = new ArrayList<>();
        for (String routeID : this.routeHashMap.keySet()) {
            ArrayList<String> routeList = new ArrayList<>();
            routeList.add(routeID);
            this.routeIdCluster.add(routeList);
        }
    }

    private void generateTripCluster() {
    }

    private void spiltRoute(HashMap<String, HashMap> origianlTripHashMap) {
        String routeID;
        HashMap<String, HashMap> tripHashMap; // tripHashMap <Day, <RouteID, <CardID, ArrayList<Trip>>>>
        HashMap<String, HashMap> routeSplitHashMap = new HashMap<>(); //<RouteID, tripHashMap>
        HashMap<String, HashMap> routeDayHashMap; // <RouteID, <CardID, ArrayList<Trip>>>

        Object[] list = origianlTripHashMap.keySet().toArray();
        List<String> timeList = Arrays.asList(Arrays.copyOf(list, list.length, String[].class));
        Collections.sort(timeList);

        //for (Map.Entry<String, HashMap> oneDayTripEntity : tripHashMap.entrySet()) { //day
        for (String day : timeList) {
            HashMap<String, HashMap> oneRouteHashMap = origianlTripHashMap.get(day);
            for (Map.Entry<String, HashMap> oneRouteEntity : oneRouteHashMap.entrySet()) { //route
                routeID = oneRouteEntity.getKey();
                if (routeSplitHashMap.containsKey(routeID)) {
                    tripHashMap = routeSplitHashMap.get(routeID);
                } else {
                    tripHashMap = new HashMap<String, HashMap>();
                    routeSplitHashMap.put(routeID, tripHashMap);
                }
                if (tripHashMap.containsKey(day)) {
                    routeDayHashMap = tripHashMap.get(day);
                } else {
                    routeDayHashMap = new HashMap<String, HashMap>();
                    tripHashMap.put(day, routeDayHashMap);
                }
                routeDayHashMap.putAll((HashMap<String, HashMap>)origianlTripHashMap.get(day).get(routeID));
            }
        }


    }

    private double getOverlapRatio(HashMap<String, HashMap> tripHashMap1, HashMap<String, HashMap> tripHashMap2) {
        //if all passengers of tripHashMap1 are infected, what is the influence of tripHashMap2?
        //Step 1: find passengers S1 who are in both hash map
        //Step 2: sum influence of S1
        //Step 3: ratio = (sum - |S1|)/S2

        //Step 1
        HashMap<String, String> cardIDHashMap = generatePassengerList(tripHashMap1, tripHashMap2);

        //Step 2
        return 0.0;
    }

    private HashMap<String, String> generatePassengerList(HashMap<String, HashMap> tripHashMap1, HashMap<String, HashMap> tripHashMap2) {
        // <Day, <RouteID, <CardID, ArrayList<Trip>>>>
        Set<String> cardIDList1 = getPassengerList(tripHashMap1);
        Set<String> cardIDList2 = getPassengerList(tripHashMap2);
        HashMap<String, String> cardIDHashMap = new HashMap<>();
        for (String cardID : cardIDList1) {
            if (cardIDList2.contains(cardID))
                cardIDHashMap.put(cardID, cardID);
        }
        return cardIDHashMap;
    }

    private Set<String> getPassengerList(HashMap<String, HashMap> tripHashMap) {
        Set<String> cardIDList = new HashSet<>();
        for (Map.Entry<String, HashMap> oneRouteEntity : tripHashMap.entrySet()) {
            HashMap<String, HashMap> oneRouteHashMap = oneRouteEntity.getValue();
            for (Map.Entry<String, HashMap> oneCardEntity : oneRouteHashMap.entrySet()) { //route
                cardIDList.addAll(oneCardEntity.getValue().keySet());
            }
        }
        return cardIDList;
    }


}