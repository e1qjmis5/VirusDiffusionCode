package Function;

import java.util.HashMap;

public class PartitionFramework {

    private HashMap<String, HashMap> tripHashMap; // <Day, <RouteID, <CardID, ArrayList<Trip>>>>
    private HashMap<String, Integer> passengerHashMap;

    public PartitionFramework(HashMap<String, HashMap> tripHashMap, HashMap<String, Integer> passengerHashMap) {
        this.tripHashMap = tripHashMap;
        this.passengerHashMap = passengerHashMap;
        Cluster cluster = new Cluster(tripHashMap, passengerHashMap);
        cluster.generateCluster();
    }
}
