package Function;

import Entity.*;
import Setting.Setting;

import java.util.*;

public class DiffusionSimulator {

    //private ArrayList<Station> candidate;

    private ArrayList<Node> nodeArrayList;
    //    private HashMap<String, HashMap> tripHashMap; // <Day, <RouteID, <CardID, ArrayList<Trip>>>>
//    private HashMap<String, HashMap> passengerTripHashMap;
    private HashMap<String, Passenger> passengerList;
    private PriorityQueue<Trip> tripQueue;
    private Set<Passenger> infPas;
    private int infNum;
    private HashMap<Integer, Set<Passenger>> infectedPassengerList;
    private int repeatTime;

    public DiffusionSimulator() {
        nodeArrayList = null;
        passengerList = null;
        tripQueue = null;
        infectedPassengerList = new HashMap<>();
        repeatTime = 0;
    }

    public void startDiffusion() {
        //System.out.println("Start Diffusion");
        generateInfPassenger();
        infectionDiffusion();
        //testPrintPassengerInfection();
    }

    public void setInfectedPassengerList(Set<Passenger> infectedList) {
        this.infPas = infectedList;
    }

    /**
     * randomly select infected passengers
     */
    private void generateInfPassenger() {
        tripQueue = new PriorityQueue<>();
        if (infPas == null || infPas.size() != infNum) {
            //if (!infectedPassengerList.containsKey(infNum)) {
            infPas = new HashSet<>();
            int index;
            //System.out.println("1. Generate Infected Passengers");

            Object[] list = this.passengerList.keySet().toArray();
            List<String> tempList = Arrays.asList(Arrays.copyOf(list, list.length, String[].class));
            List<String> cardIDList = new LinkedList<>(tempList);
            Random random = new Random();
            for (int i = 0; i < infNum; i++) {
                index = random.nextInt(cardIDList.size());
                passengerList.get(cardIDList.get(index)).initializeInfection();
                tripQueue.add(passengerList.get(cardIDList.get(index)).getFirstTrip());
                infPas.add(passengerList.get(cardIDList.get(index)));
                cardIDList.remove(index);
                //System.out.println(" - " + cardIDList.get(i) + " is infected");
            }
            infectedPassengerList.put(infNum, infPas);
            if (Setting.dis1)
                System.out.println(" - Total Number " + infNum);
        } else {
            for (Passenger passenger : infPas) {
                passenger.initializeInfection();
                tripQueue.add(passenger.getFirstTrip());
            }
        }
    }

    private int infectionDiffusion() {
//        infectedSet = new HashSet<>();
        if (Setting.dis1 && this.repeatTime == 1) {
            System.out.println("2. Start Infection Diffusion");
            System.out.println(" - Total Infected Trip " + tripQueue.size());
        }
        int count = 0;
        Trip trip;
        PriorityQueue<Trip> tripQueue = new PriorityQueue<>(this.tripQueue);
        ArrayList<Trip> infectedList;

        while (tripQueue.size() > 0) {
            trip = tripQueue.poll();
            if (trip.getNode() == null) {
                continue;
            }
            infectedList = trip.getNode().diffuse();
            if (infectedList == null)
                continue;
            tripQueue.addAll(infectedList);
            count += infectedList.size();
        }

        if (Setting.dis1 && this.repeatTime == 1)
            System.out.println(" - Infected Trip " + count);
        this.repeatTime++;
        return count;
    }

    public int countInfectionNum() {
        if (Setting.dis1 && this.repeatTime == 1)
            System.out.println("3. Counting Infection Num");
        int count = 0;

        for (Passenger passenger : this.passengerList.values()) {
            if (passenger.isInfected())
                count++;
        }
        if (Setting.dis1 && this.repeatTime % 10 == 0)
            System.out.println(" - Infection Num " + count + " / " + passengerList.size());
        return count;
    }

//    public void testPrintPassengerInfection() {
//        printLine();
//        Object[] list = this.passengerList.keySet().toArray();
//        List<String> cardIDList = Arrays.asList(Arrays.copyOf(list, list.length, String[].class));
//        for (int i = 0; i < infNum * 10; i++) {
//            Passenger passenger = passengerList.get(cardIDList.get(i));
//            HashMap<Integer, ArrayList<Trip>> dayTripsHashMap = passenger.getDayTripsHashMap();
//            System.out.print("P " + i + " - ");
//            for (int n = 0; n < Setting.PERIOD; n++) {
//                if (dayTripsHashMap.get(n) != null) {
//                    for (Trip trip : dayTripsHashMap.get(n)) {
//                        if (trip.isInfected)
//                            System.out.print("1");
//                        else
//                            System.out.print("0");
//                    }
//                }
//            }
//            System.out.println("");
//        }
//    }

    public void reset() {
        for (Node node : nodeArrayList) {
            node.cancelInfection();
        }

        for (Passenger passenger : this.passengerList.values()) {
            passenger.reset();
        }

//        Object[] list = this.passengerList.keySet().toArray();
//        List<String> cardIDList = Arrays.asList(Arrays.copyOf(list, list.length, String[].class));
//        for (int i = 0; i < list.length; i++) {
//            passengerList.get(cardIDList.get(i)).reset();
//        }
    }

    public void setInfNum(int infNum) {
        this.infNum = infNum;
    }

    public void setNodeArrayList(ArrayList<Node> nodeArrayList) {
        this.nodeArrayList = nodeArrayList;
    }

//    public void setTripHashMap(HashMap<String, HashMap> tripHashMap) {
//        this.tripHashMap = tripHashMap;
//    }
//
//    public void setPassengerTripHashMap(HashMap<String, HashMap> passengerTripHashMap) {
//        this.passengerTripHashMap = passengerTripHashMap;
//    }

    public void setPassengerList(HashMap<String, Passenger> passengerList) {
        this.passengerList = passengerList;
    }

//    public void setInfectedSet(Set<Passenger> infectedSet) {
//        this.infectedSet = infectedSet;
//    }

//    public void setTripQueue(PriorityQueue<Trip> tripQueue) {
//        this.tripQueue = tripQueue;
//    }

    public void setCandidate(ArrayList<Station> candidate) {

        //this.candidate = candidate;
        for (Station station : candidate) {
            station.setCheckPoint();
        }
    }

    public void resetRepeatTime() {
        this.repeatTime = 0;
    }

//    private void printLine() {
//        System.out.println("---------------------------------------------------------");
//    }

}
