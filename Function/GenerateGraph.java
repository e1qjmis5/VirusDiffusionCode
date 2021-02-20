package Function;

import Setting.Setting;
import Entity.Trip;

import java.text.SimpleDateFormat;
import java.util.*;

public class GenerateGraph {

    private HashMap<String, HashMap> tripHashMap; // <Day, <RouteID, <CardID, ArrayList<Trip>>>>
    private ArrayList<HashMap> adjacencyList;
    private HashMap<String, HashMap> passengerHashMap;

    private int[] xList;
    private int[] yList;
    private double[] valueList;

    public GenerateGraph(HashMap<String, HashMap> tripHashMap, HashMap<String, HashMap> passengerHashMap) {
        this.tripHashMap = tripHashMap; //<Day, <RouteID, <CardID, ArrayList<Trip>>>>
        this.passengerHashMap = passengerHashMap;
        this.adjacencyList = new ArrayList<HashMap>();
    }


    public void createContactHashMap() {
        //HashMap<String, HashMap> tripHashMap; // <Day, <RouteID, <CardID, ArrayList<Trip>>>>
        //HashMap<String, HashMap> routeDayHashMap; // <RouteID, <CardID, ArrayList<Trip>>>
        //HashMap<String, ArrayList<Trip>> tripDayHashMap; // <CardID, ArrayList<Trip>>
        HashMap<String, Double> personalContactHashMap;// record contact ratio from one to others
        HashMap<String, HashMap> adjacencyHashMap; //<cardId, <CardId, contact time>>

        TripCalculation tripCalculation = new TripCalculation();


        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        Object[] list = tripHashMap.keySet().toArray();
        List<String> timeList = Arrays.asList(Arrays.copyOf(list, list.length, String[].class));
        Collections.sort(timeList);
//        Date bt=sdf.parse(beginTime);
//        Date et=sdf.parse(endTime);
        //Collections.sort

        System.out.println("Total Days : " + tripHashMap.entrySet().size());
        int dayIndex = 0;
        int meanRouteNumber = 0;// the mean of the route number per day, used to filter data-less day
        //for (Map.Entry<String, HashMap> oneDayTripEntity : tripHashMap.entrySet()) { //day
        for (String time : timeList) {
            if (dayIndex == Setting.PERIOD)
                break;
            HashMap<String, HashMap> oneRouteHashMap = tripHashMap.get(time);
            //HashMap<String, HashMap> oneRouteHashMap = oneDayTripEntity.getValue();
            System.out.println("Day " + dayIndex + " (" + time + ") | Route Number " + oneRouteHashMap.entrySet().size());
            if (oneRouteHashMap.entrySet().size() < meanRouteNumber * 0.5) {
                System.out.println("Too less data, jump!");
                continue;
            } else {
                adjacencyHashMap = new HashMap<String, HashMap>();
                this.adjacencyList.add(adjacencyHashMap);
                meanRouteNumber = dayIndex * meanRouteNumber + oneRouteHashMap.entrySet().size();
                dayIndex++;
                meanRouteNumber = meanRouteNumber / dayIndex;
            }
            for (Map.Entry<String, HashMap> oneRouteEntity : oneRouteHashMap.entrySet()) { //each entity is all trips of one route of one day
                HashMap<String, ArrayList<Trip>> tripDayHashMap = oneRouteEntity.getValue();
                String[] cardIDList = new String[tripDayHashMap.size()];
                tripDayHashMap.keySet().toArray(cardIDList);
                for (int i = 0; i < cardIDList.length - 1; i++) {
                    ArrayList<Trip> tripList1 = tripDayHashMap.get(cardIDList[i]); //trips/person/route/day p1
                    for (int j = i + 1; j < cardIDList.length; j++) {
                        ArrayList<Trip> tripList2 = tripDayHashMap.get(cardIDList[j]);//trips/person/route/day p2
                        double infRatio = tripCalculation.getInfRatio(tripList1, tripList2);

                        if (infRatio > 0.0) { // cardIDList[j] can infect cardIDList[i]
                            if (!adjacencyHashMap.containsKey(cardIDList[i])) {
                                personalContactHashMap = new HashMap<String, Double>();
                                adjacencyHashMap.put(cardIDList[i], personalContactHashMap);
                            } else {
                                personalContactHashMap = adjacencyHashMap.get(cardIDList[i]);
                            }
                            if (personalContactHashMap.containsKey(cardIDList[j])) {
                                infRatio = 1.0 - infRatio * (1.0 - personalContactHashMap.get(cardIDList[j]));//p=1-p_new(1-p_old)
                                personalContactHashMap.put(cardIDList[j], infRatio);
                            } else {
                                personalContactHashMap.put(cardIDList[j], infRatio);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("ContactHashMap is completed");
    }

//    public void generateSparseAdjMatrix() {
//        int passengerSize = passengerHashMap.size();
//        String p1, p2;
//        int indexP1, indexP2, index1, index2, size;
//        double inf;
//        size = passengerSize * Setting.PERIOD;
//        System.out.println("Create adjacency matrix with size " + size + " x " + size);
//        ArrayList<Integer> xArrayList = new ArrayList<Integer>();
//        ArrayList<Integer> yArrayList = new ArrayList<Integer>();
//        ArrayList<Double> valueArrayList = new ArrayList<Double>();
//        HashMap<String, HashMap> adjacencyHashMap;
//
//        System.out.println("Generating sparse adjacency matrix");
//        for (int i = 1; i <= size; i++) {
//            if (i % Setting.PERIOD != 0) {
//                xArrayList.add(i);
//                yArrayList.add(i);
//                valueArrayList.add(1.0);
//            }
//        }
//        for (int day = 0; day < this.adjacencyList.size(); day++) {
//            adjacencyHashMap = this.adjacencyList.get(day);
//            for (Map.Entry<String, HashMap> onePassengerEntity : adjacencyHashMap.entrySet()) {
//                p1 = onePassengerEntity.getKey();
//                HashMap<String, Double> contactList = onePassengerEntity.getValue();
//                for (Map.Entry<String, Double> contactInf : contactList.entrySet()) {
//                    p2 = contactInf.getKey();
//                    inf = contactInf.getValue();
//                    indexP1 = passengerHashMap.get(p1);
//                    indexP2 = passengerHashMap.get(p2);
//                    index1 = getIndex(indexP1, day) + 1;
//                    // we move the adj matrix to right 1 step to build the symmetric matrix
//                    index2 = getIndex(indexP2, day) + 1;
//                    xArrayList.add(index1);
//                    yArrayList.add(index2);
//                    valueArrayList.add(inf);
//                    xArrayList.add(index2);
//                    yArrayList.add(index1);
//                    valueArrayList.add(inf);
//                }
//            }
//        }
//        System.out.println("Adjacency matrix is completed.");
//        //printSparseAdjMatrix(xArrayList, yArrayList, valueArrayList);
//        xList = xArrayList.stream().mapToInt(i -> i).toArray();
//        yList = yArrayList.stream().mapToInt(i -> i).toArray();
//        valueList = valueArrayList.stream().mapToDouble(i -> i).toArray();
//    }

    private void printSparseAdjMatrix(ArrayList<Integer> xArrayList, ArrayList<Integer> yArrayList, ArrayList<Double> valueArrayList) {
        for (int i = 0; i < xArrayList.size(); i++) {
            System.out.println("[ " + xArrayList.get(i) + " " + yArrayList.get(i) + " ] " + valueArrayList.get(i));
        }
    }

    private int getIndex(int index, int day) {
        return (index - 1) * Setting.PERIOD + day;
    }

//    private void generateAdjMatrix() {
//        int passengerSize = passengerHashMap.size();
//        String p1, p2;
//        int index1, index2, size;
//        double inf;
//        size = passengerSize * Setting.PERIOD;
//        System.out.println("Create adjacency matrix with size " + size + " x " + size);
//        double[][] adjMatrix = new double[size][size];
//        HashMap<String, HashMap> adjacencyHashMap;
//
//        System.out.println("Generating adjacency matrix");
//        for (int day = 0; day < this.adjacencyList.size(); day++) {
//            adjacencyHashMap = this.adjacencyList.get(day);
//            for (Map.Entry<String, HashMap> onePassengerEntity : adjacencyHashMap.entrySet()) {
//                p1 = onePassengerEntity.getKey();
//                HashMap<String, Double> contactList = onePassengerEntity.getValue();
//                for (Map.Entry<String, Double> contactInf : contactList.entrySet()) {
//                    p2 = contactInf.getKey();
//                    inf = contactInf.getValue();
//                    index1 = passengerHashMap.get(p1);
//                    index2 = passengerHashMap.get(p2);
//                    adjMatrix[index1][index2] = inf;
//                    adjMatrix[index2][index1] = inf;
//                }
//            }
//        }
//        System.out.println("Adjacency matrix is completed.");
//
//    }
}
