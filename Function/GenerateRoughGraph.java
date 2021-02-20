package Function;

import Entity.Node;
import Entity.Passenger;
import Setting.Setting;
import Entity.Trip;

import java.util.*;

public class GenerateRoughGraph {


    private HashMap<String, Node> graph = new HashMap<>();
    private ArrayList<Node> nodeArrayList;
    private HashMap<String, HashMap> tripHashMap; // <Day, <RouteID, <CardID, ArrayList<Trip>>>>
    //    private HashMap<String, HashMap> passengerTripHashMap;
    private HashMap<String, Passenger> passengerList;
    private TripCalculation tripCalculation;
    private Set<Node> lastNodeSet;
    private int edgeNumber;


//    public GenerateRoughGraph(HashMap<String, HashMap> tripHashMap, HashMap<String, HashMap> passengerTripHashMap) {
//        this.nodeArrayList = new ArrayList<>();
//        this.tripHashMap = tripHashMap; //<Day, <RouteID, <CardID, ArrayList<Trip>>>>
//        this.passengerTripHashMap = passengerTripHashMap;
//        this.passengerList = new HashMap<String, Passenger>();
//        this.tripCalculation = new TripCalculation();
//        this.lastNodeSet = new HashSet<>();
//        this.edgeNumber = 0;
//    }

    public GenerateRoughGraph(HashMap<String, HashMap> tripHashMap, HashMap<String, Passenger> passengerList) {
        this.nodeArrayList = new ArrayList<>();
        this.tripHashMap = tripHashMap; //<Day, <RouteID, <CardID, ArrayList<Trip>>>>
        this.passengerList = passengerList;
        this.tripCalculation = new TripCalculation();
        this.lastNodeSet = new HashSet<>();
        this.edgeNumber = 0;
    }

    public void startGenerate() {
        createNode();
        generateSelfInfection();
        createEdges();
        System.out.println("Graph is generated.");
        printLine();
    }

    public void regenerate() {
        for (Node node : nodeArrayList) {
            node.reset();
        }
        generateSelfInfection();
        createEdges();
    }

    public void generateSelfInfection() {
        for (Passenger passenger : passengerList.values()) {
            Collections.sort(passenger.getTripList());
            createPersonalEdges(passenger);
        }
    }

    private void createNode() {
        Node node;
        for (Passenger passenger : passengerList.values()) {
            for (Trip trip : passenger.getTripList()) {
                node = new Node(passenger, trip);
                trip.setNode(node);
                nodeArrayList.add(node);
            }
            lastNodeSet.add(passenger.getTripList().get(passenger.getTripList().size() - 1).getNode());
        }
    }

//    private void createNode() {
//        Node node;
//        for(Passenger passenger:passengerList.values()){
//            HashMap<String, ArrayList<Trip>> oneRouteHashMap = passengerTripHashMap.get(passenger.getCardID());
//
//            //each entity is all trips of one route of one passenger
//            for (Map.Entry<String, ArrayList<Trip>> tripList : oneRouteHashMap.entrySet()) {
//                ArrayList<Trip> oneRouteList = tripList.getValue();
//                for (Trip trip : oneRouteList) {
//                    passenger.addTrip(trip);
//                    node = new Node(passenger, trip);
//                    trip.setNode(node);
//                    nodeArrayList.add(node);
//                }
//            }
//            lastNodeSet.add(passenger.getTripList().get(passenger.getTripList().size() - 1).getNode());
//        }
////        String[] cardIDList = new String[passengerTripHashMap.size()];
////        passengerTripHashMap.keySet().toArray(cardIDList);
////        Passenger passenger;
////
////
////        System.out.println("1. Create Node");
////        for (String cardID : cardIDList) {
////            if (!passengerList.containsKey(cardID)) {
////                passenger = new Passenger(cardID);
////                passengerList.put(cardID, passenger);
////            } else {
////                passenger = passengerList.get(cardID);
////            }
////
////            //each entity is all trips of one passenger·
////            HashMap<String, ArrayList<Trip>> oneRouteHashMap = passengerTripHashMap.get(cardID);
////
////            //each entity is all trips of one route of one passenger
////            for (Map.Entry<String, ArrayList<Trip>> tripList : oneRouteHashMap.entrySet()) {
////                ArrayList<Trip> oneRouteList = tripList.getValue();
////                for (Trip trip : oneRouteList) {
////                    passenger.addTrip(trip);
////                    node = new Node(passenger, trip);
////                    trip.setNode(node);
////                    nodeArrayList.add(node);
////                }
////            }
////            lastNodeSet.add(passenger.getTripList().get(passenger.getTripList().size() - 1).getNode());
////        }
//    }

//    private void createNode() {
//        String[] cardIDList = new String[passengerTripHashMap.size()];
//        passengerTripHashMap.keySet().toArray(cardIDList);
//        Passenger passenger;
//        Node node;
//        Trip lastTrip = null;
//
//        System.out.println("1. Create Node");
//        for (String cardID : cardIDList) {
//            if (!passengerList.containsKey(cardID)) {
//                passenger = new Passenger(cardID);
//                passengerList.put(cardID, passenger);
//            } else {
//                passenger = passengerList.get(cardID);
//            }
//
//            //each entity is all trips of one passenger·
//            HashMap<String, ArrayList<Trip>> oneRouteHashMap = passengerTripHashMap.get(cardID);
//
//            //each entity is all trips of one route of one passenger
//            for (Map.Entry<String, ArrayList<Trip>> tripList : oneRouteHashMap.entrySet()) {
//                ArrayList<Trip> oneRouteList = tripList.getValue();
//                for (Trip trip : oneRouteList) {
//                    if (!graph.containsKey(getNodeCode(trip))) {
//                        node = new Node(passenger, trip);
//                        trip.setNode(node);
//                        node.setCode(getNodeCode(trip));
//                    } else
//                        node = graph.get(getNodeCode(trip));
//                    passenger.addTrip(trip);
//                    node = new Node(passenger, trip);
//                    trip.setNode(node);
//                    nodeArrayList.add(node);
//                }
//            }
//            lastNodeSet.add(passenger.getTripList().get(passenger.getTripList().size() - 1).getNode());
//        }
//    }


    /**
     * Create edges for one person
     * from the previous trip to the next trip with 100% infection
     */
    private void createPersonalEdges(Passenger passenger) {
        passenger.sortTrip();
        ArrayList<Trip> tripList = passenger.getTripList();
        for (int i = 0; i < tripList.size() - 1; i++) {
            createEdge(tripList.get(i), tripList.get(i + 1), 1.0);
            this.edgeNumber++;
        }
    }

    private void createEdges() {
        System.out.println("2. Generate outside infection");
        int dayIndex = 0;
        double infRatio;
        ArrayList<Trip> tripList1, tripList2;

        Object[] list = tripHashMap.keySet().toArray();
        List<String> timeList = Arrays.asList(Arrays.copyOf(list, list.length, String[].class));
        Collections.sort(timeList);
        Trip trip = null;

        for (String time : timeList) {
            HashMap<String, HashMap> oneRouteHashMap = tripHashMap.get(time);
            //HashMap<String, HashMap> oneRouteHashMap = oneDayTripEntity.getValue();
            if (Setting.dis1)
                System.out.println(" - Day " + dayIndex + " (" + time + ") | Route Number " + oneRouteHashMap.entrySet().size());

            for (Map.Entry<String, HashMap> oneRouteEntity : oneRouteHashMap.entrySet()) { //each entity is all trips of one route of one day
                HashMap<String, ArrayList<Trip>> tripDayHashMap = oneRouteEntity.getValue();
                String[] cardIDList = new String[tripDayHashMap.size()];
                tripDayHashMap.keySet().toArray(cardIDList);

                for (int i = 0; i < cardIDList.length - 1; i++) {
                    tripList1 = tripDayHashMap.get(cardIDList[i]); //trips/person/route/day p1
                    for (int j = i + 1; j < cardIDList.length; j++) {
                        tripList2 = tripDayHashMap.get(cardIDList[j]);//trips/person/route/day p2

                        for (Trip trip1 : tripList1) {
                            //checkInf(trip1, time, cardIDList[i]);
//                            if (trip1.getPassenger() == null)
//                                continue;
                            for (Trip trip2 : tripList2) {
                                //checkInf(trip2, time, cardIDList[j]);
//                                if (trip2.getPassenger() == null)
//                                    continue;

                                int hour1 = trip1.getBeginTime().getHours() / Setting.TIME_INTERVAL;
                                int hour2 = trip2.getBeginTime().getHours() / Setting.TIME_INTERVAL;

                                if (hour1 != hour2)
                                    continue;

                                long exposureDuration = Math.min((trip2.getEndTime().getTime() - trip2.getBeginTime().getTime()),
                                        (trip1.getEndTime().getTime() - trip1.getBeginTime().getTime()));
                                infRatio = tripCalculation.getInfPro((int) (exposureDuration / 1000));

                                if (infRatio > 0.05) {
                                    if (Setting.GAMMA > 0) {
                                        trip = trip1.getPassenger().getInfectedTrip(trip2);
                                        if (trip != null) {
                                            createEdge(trip2, trip, infRatio);
                                            this.edgeNumber++;
                                        }
                                        trip = trip2.getPassenger().getInfectedTrip(trip1);
                                        if (trip != null) {
                                            createEdge(trip1, trip, infRatio);
                                        }
                                    } else {
                                        createEdge(trip2, trip1, infRatio);
                                        createEdge(trip1, trip2, infRatio);
                                        this.edgeNumber += 2;
                                    }
                                }
                            }
                        }

                    }
                }
            }
            dayIndex++;
        }
    }

    private void checkInf(Trip trip, String time, String cardID) {
        if (!trip.getDay().equals(time))
            System.out.println("ERROR");
        if (!trip.getCardID().equals(cardID))
            System.out.println("ERROR");
    }

    public void createEdge(Trip trip1, Trip trip2, double p) {
        trip1.getNode().addNeighbor(trip2.getNode(), p);
        trip2.getNode().addReverseNeighbor(trip1.getNode(), p);
    }

    public ArrayList<Node> getNodeList() {
        return nodeArrayList;
    }

//    public HashMap<String, HashMap> getTripHashMap() {
//        return tripHashMap;
//    }
//
//    public HashMap<String, HashMap> getPassengerTripHashMap() {
//        return passengerTripHashMap;
//    }
//
//    public int getEdgeNumber() {
//        return edgeNumber;
//    }

    public HashMap<String, Passenger> getPassengerList() {
        return passengerList;
    }
//
//    public TripCalculation getTripCalculation() {
//        return tripCalculation;
//    }

    public Set<Node> getLastNodeSet() {
        return lastNodeSet;
    }

    public int getPassengerNum() {
        return passengerList.size();
    }

    private void printLine() {
        System.out.println("---------------------------------------------------------");
    }

//    private int getNodeCode(Trip trip) {
//        return (trip.getCardID() + trip.getDay()).hashCode();
//    }
}
