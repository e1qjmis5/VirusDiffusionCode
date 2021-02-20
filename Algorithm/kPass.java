package Algorithm;

import Entity.*;
import Function.Combination;
import Setting.Setting;

import java.util.*;

public class kPass {
    private HashMap<String, Station> stationHashMap; // <station id, station>
    private HashMap<String, Passenger> passengerHashMap;
    private ArrayList<Node> nodeArrayList;
    private Set<RRSet2> RRSetSet;
    private Node[] lastNodeSet;
    private ArrayList<Passenger> passengerResultList;
    private ArrayList<Passenger> ranPassengerResultList;
    private Set<Passenger> prePassSet;

    private double avgCoverNum;
    private double edgeNum; // m
    private double nodeNum; // n
    private int passengerNum;
    private int theta;
    private int coverRRNum;
    private int infNum;

    public kPass() {
        RRSetSet = new HashSet<>();
    }

    public void reSet() {
        passengerResultList = null;
        ranPassengerResultList = null;
    }

    public Set<Passenger> getKRanPassenger(int infNum) {
        if (ranPassengerResultList == null) {
            this.passengerNum = this.lastNodeSet.length;
            this.infNum = (int) (this.passengerNum * Setting.maxInfPer / 10000);
            ranPassengerResultList = new ArrayList<>();
            int index;

            Object[] list = this.passengerHashMap.keySet().toArray();
            List<String> tempList = Arrays.asList(Arrays.copyOf(list, list.length, String[].class));
            List<String> cardIDList = new LinkedList<>(tempList);
            Random random = new Random();
            for (int i = 0; i < this.infNum; i++) {
                index = random.nextInt(cardIDList.size());
                ranPassengerResultList.add(passengerHashMap.get(cardIDList.get(index)));
                cardIDList.remove(index);
                //System.out.println(" - " + cardIDList.get(i) + " is infected");
            }
        }
        Set<Passenger> passList = new HashSet<>();
        for (int i = 0; i < infNum; i++) {
            passList.add(ranPassengerResultList.get(i));
        }
//        showPass(passList);
        checkPrePassSet(passList);
        return passList;
    }


    public Set<Passenger> getKPassenger(int infNum) {
        if (passengerResultList == null) {
            this.passengerNum = this.lastNodeSet.length;
            this.infNum = (int) (this.passengerNum * Setting.maxInfPer / 10000);
            generateKPassenger();
        }
        Set<Passenger> passList = new HashSet<>();
        for (int i = 0; i < infNum; i++) {
            passList.add(passengerResultList.get(i));
        }
//        showPass(passList);
        checkPrePassSet(passList);
        return passList;
    }

    private void checkPrePassSet(Set<Passenger> passList) {
        if (prePassSet == null || prePassSet.size() > passList.size()) {
            prePassSet = new HashSet<>();
            prePassSet.addAll(passList);
        }

        for (Passenger passenger : prePassSet) {
            if (!passList.contains(passenger))
                System.out.print("UkPass : un-find passenger!");
        }

        prePassSet = new HashSet<>();
        prePassSet.addAll(passList);
    }

    private void showPass(Set<Passenger> passList) {

        ArrayList<String> idList = new ArrayList<>();
        for (Passenger passenger : passList) {
            idList.add(passenger.getCardID().substring(0, 3));
        }
//        Collections.sort(idList);
        for (String id : idList) {
            System.out.print(id + ",");
        }
        System.out.println("");
    }

    private void generateKPassenger() {
        //System.out.println(" - SIM ");
        getGraphInf();
        passengerNum = lastNodeSet.length;
        int size = 1;
        long time = System.currentTimeMillis();
        double kpt = kptEstimation();
        time = System.currentTimeMillis() - time;
        if (Setting.dis2) {
            System.out.println(" - Running Time " + time + " ms");
            System.out.println(" - KPT " + kpt);
        }
        double lambda = getLambda();
        if (Setting.dis2)
            System.out.println(" - Lambda " + lambda);
        theta = (int) (lambda / kpt);
        if (Setting.dis2) {
            size = RRSetSet.size();
            System.out.println(" - Theta " + lambda / kpt + " (needed RRSet)");
            System.out.println(" - Estimate Time " + theta * time / size / 1000.0 + " s");
            printLine();
        }
        kpt = refineKPT(kpt);
        theta = (int) (lambda / kpt);
        if (Setting.dis2) {
            System.out.println(" - New Theta " + theta + " (needed RRSet)");
            System.out.println(" - Estimate Time " + theta * time / size / 1000.0 + " s");
            printLine();
        }
        generateRRSetSet();
        findSolution();
    }

    private void getGraphInf() {
        if (Setting.dis2)
            System.out.println("1. Generate Graph Information");
        if (nodeArrayList == null || nodeArrayList.size() == 0) {
            System.out.println("! ERROR (kPass) Graph is empty");
            return;
        }
        this.nodeNum = nodeArrayList.size();
        this.edgeNum = 0;
        for (Node node : nodeArrayList) {
            this.edgeNum += node.getEdgeNum();
        }
        if (Setting.dis2) {
            System.out.println(" - Node Num " + this.nodeNum);
            System.out.println(" - Edge Num " + this.edgeNum);
            printLine();
        }
    }

    private double kptEstimation() {
        if (Setting.dis2)
            System.out.println("2. Evaluate KPT");
        double cPre = 0;
        double c;
        double sum = 0;
        double l = 1.0;
        double kr;
        avgCoverNum = 0;
        int generateNum = 0;
        int itemIndex;
        int randMax = lastNodeSet.length;
        //int z = (int) (Math.log(nodeNum) / Math.log(2.0) - 1.0);
        int z = (int) (Math.log(edgeNum) / Math.log(2.0) - 1.0);
        Random random = new Random();
        RRSet2 rrSet;
        if (Setting.dis2)
            System.out.println(" - Max iteration " + z);
        for (int i = 1; i <= z; i++) {
            if (Setting.dis2)
                System.out.println(" - Iteration (" + i + "/" + z + ")");
            //c = (6.0 * l * Math.log(nodeNum) + 6.0 * (Math.log(nodeNum) / Math.log(2))) * Math.pow(2, i);
            c = (6.0 * l * Math.log(edgeNum) + 6.0 * (Math.log(Math.log(edgeNum) / Math.log(2)))) * Math.pow(2, i);
            if (c > cPre) {
                generateNum = (int) (c - cPre);
                cPre = c;
            }
            if (Setting.dis2)
                System.out.println("   * Generate RRSet Num " + generateNum);
            for (int n = 1; n <= generateNum; n++) {
                itemIndex = random.nextInt(randMax);
                rrSet = generateRRSet2(lastNodeSet[itemIndex]);
                RRSetSet.add(rrSet);
                avgCoverNum += rrSet.getWidth();
                kr = 1.0 - Math.pow((1.0 - rrSet.getWidth() / edgeNum), infNum);
                sum += kr;
//                rrSet.compress();
            }
            if (Setting.dis2) {
                System.out.println("   * Sum " + sum + " | Sum/c " + sum / c);
                System.out.println("   * RRSet Total Num " + RRSetSet.size());
                System.out.println("   * RRSet Avg Width " + avgCoverNum / RRSetSet.size());
            }
            //System.out.println("   * Sum/c " + sum / c);
            if ((sum / c) > (1.0 / Math.pow(2.0, i))) {
                return nodeNum * sum / (2.0 * c);
            }
        }
        return 1;
    }

    private double refineKPT(double kpt) {
        if (Setting.dis2)
            System.out.println("3. Refine KPT");
        double a = Setting.L * Math.pow(Setting.EPSILON, 2) / (passengerNum * avgCoverNum + Setting.L);
        double epsilon = 5.0 * Math.pow(a, (1.0 / 3.0));
        if (Setting.dis2)
            System.out.println(" - epsilon' " + epsilon);

        double lambda = (2.0 + epsilon) * Setting.L * nodeNum * Math.log(nodeNum) * Math.pow(epsilon, -2);
        double theta = lambda / kpt;

        int randMax = lastNodeSet.length;
        int itemIndex;
        Random random = new Random();
        RRSet2 rrSet;
        if (Setting.dis2)
            System.out.println(" - Theta' " + theta);

        for (int n = 1; n <= infNum; n++) {
            itemIndex = random.nextInt(randMax);
            rrSet = generateRRSet2(lastNodeSet[itemIndex]);
            RRSetSet.add(rrSet);
            avgCoverNum += rrSet.getWidth();
            //rrSet.compress();
        }

        double size1 = RRSetSet.size();
        ArrayList<Passenger> passengerList = findSolution();
        for (Passenger passenger : passengerList) {
            selectPassenger(passenger);
        }
        double f = coverRRNum / size1;
        double kpt2 = f * nodeNum / (1 + epsilon);

        if (Setting.dis2) {
            if (kpt > kpt2)
                System.out.println(" - Remaining KPT " + kpt);
            else {
                kpt = kpt2;
                System.out.println(" - Refine KPT " + kpt);
            }
        }
        return kpt;

    }

    private ArrayList<Passenger> findSolution() {
        if (Setting.dis2) {
            System.out.println("4. Find k Passenger");
        }
        for (Passenger passenger : passengerHashMap.values()) {
            //updateCoverRRSets(station, 0);
            passenger.resetCoverNum();
        }
        PriorityQueue<Passenger> passengerQueue = new PriorityQueue<>(passengerHashMap.values());

        Passenger passenger;
        passengerResultList = new ArrayList<>();
        int coverNum = 0;
        for (int i = 0; i < infNum; i++) {
            do {
                passenger = passengerQueue.poll();
                if (passenger.getUpdateTime() == i) {
                    coverNum += passenger.getCoverRRNum();
                    passengerResultList.add(passenger);
                    selectPassenger(passenger);
                    //System.out.println(" - Candidate RR Num " + station.getCoverRRNum() + "  " + station.getUpdateTime() + "/" + i);
                } else {
                    passenger.updateCoverNum(i);
                    //updateCoverRRSets(station, i);
                    passengerQueue.add(passenger);
                    passenger = null;
                }
            } while (passenger == null);
            if (Setting.dis2 && i % 1000 == 0)
                System.out.println(" * Find " + i + "   CoverNum " + coverNum + "/" + RRSetSet.size());
        }
        coverRRNum = coverNum;
        return passengerResultList;
    }

    private void selectPassenger(Passenger passenger) {
        for (RRSet2 rrSet : passenger.getRRSets()) {
            rrSet.isSelected = true;
            //RRSetSet.remove(rrSet);
        }

    }

    private void generateRRSetSet() {
        if (Setting.dis2)
            System.out.println("3. Generate RRSetSets");
        int itemIndex;
        int randMax = lastNodeSet.length;
        Random random = new Random();
        //RRSet rrSet;
        //RRSetSet = new HashSet<>(theta);
        long startTime = System.currentTimeMillis();
        long endTime;
        for (int n = 0; n < theta; n++) {
            itemIndex = random.nextInt(randMax);
            RRSetSet.add(generateRRSet2(lastNodeSet[itemIndex]));
            //RRSetSet.add(rrSet);
            if ((Setting.dis2) && (n % 500000 == 0)) {
                endTime = System.currentTimeMillis();
                System.out.println(" - Finish (500k) " + (n / 500000) + "/" + (theta / 500000)
                        + "  | " + (endTime - startTime) / 1000 + " s");
            }
        }
    }


    //add RRSet to station while generating
    private RRSet2 generateRRSet2(Node node) {
        RRSet2 rrSet = new RRSet2(RRSetSet.size());
        Queue<Node> queue = new ArrayDeque<>();
        Set<Node> visitedNodeSet = new HashSet<>();
        queue.add(node);

        Node nextNode;
        while (!queue.isEmpty()) {
            node = queue.poll();
            if (Setting.MAX_INF == 2 && node.getTrip().getBeginStation().isCheckPoint()) {
                continue;
            }
            rrSet.addNode();
            if (node.isFirstTrip())
                node.getPassenger().addRRSet(rrSet);
            Node[] nodes = node.getAllReverseNodes();
            double p = 1.0 / node.getReverseEdgeNum();
            int i = (int) (Math.ceil(Math.log(Math.random())) / Math.log(1 - p));
            if (nodes.length == 1) {
                visitedNodeSet.add(nodes[0]);
                queue.add(nodes[0]);
                continue;
            }
            while (i < nodes.length) {
                nextNode = nodes[i];
                if (!visitedNodeSet.contains(nextNode)) {
                    visitedNodeSet.add(nextNode);
                    queue.add(nextNode);
                }
                i += (int) (Math.ceil(Math.log(Math.random())) / Math.log(1.0 - p));
            }
        }
        return rrSet;
    }

//
//    //add RRSet to station while generating
//    private RRSet2 generateRRSet2(Node node1) {
//        RRSet2 rrSet = new RRSet2(RRSetSet.size());
//        Queue<Node> queue = new ArrayDeque<>();
//        Set<Node> visitedNodeSet = new HashSet<>();
//        queue.add(node1);
//        Node nextNode;
//        while (!queue.isEmpty()) {
//            nextNode = queue.poll();
//            nextNode.getPassenger().addRRSet(rrSet);
//            rrSet.addNode();
//            Set<Node> nodes = nextNode.getReverseNodesP();
//            for (Node node : nodes) {
//                if (!visitedNodeSet.contains(node)) {
//                    visitedNodeSet.add(node);
//                    queue.add(node);
//                }
//            }
////            double p = 1.0 / node.getReverseEdgeNum();
////            int i = (int) (Math.ceil(Math.log(Math.random())) / Math.log(1 - p));
////            while (i < nodes.length) {
////                nextNode = nodes[i];
////                if (!visitedNodeSet.contains(nextNode) && queue.size() < 10000) {
////                    if (node.getP(nextNode) > Math.random()) {
////                        visitedNodeSet.add(nextNode);
////                        queue.add(nextNode);
////                    }
////                }
////                i += (int) (Math.ceil(Math.log(Math.random())) / Math.log(1.0 - p));
////                if (nodes.length == 1)
////                    break;
////            }
//        }
//        return rrSet;
//    }

    private double avgCoverNum() {
        double num = 0;
        for (Station station : stationHashMap.values()) {
            num += station.getVolume();
        }
        return num / stationHashMap.values().size();
    }

    private double getLambda() {
        double com = Combination.getC3((int) nodeNum, infNum);
        if (Double.isInfinite(com))
            com = Double.MAX_VALUE;
        double a = (8.0 + 2.0 * Setting.EPSILON) * nodeNum * Math.pow(Setting.EPSILON, -2.0);
        double b = (Setting.L * Math.log(nodeNum) + Math.log(com) + Math.log(2.0));
        return a * b;
    }

    public void setStationHashMap(HashMap<String, Station> stationHashMap) {
        this.stationHashMap = stationHashMap;
    }

    public void setNodeArrayList(ArrayList<Node> nodeArrayList) {
        this.nodeArrayList = nodeArrayList;
    }

    public void setLastNodeSet(Set<Node> lastNodeSet) {
        this.lastNodeSet = new Node[lastNodeSet.size()];
        lastNodeSet.toArray(this.lastNodeSet);
    }

    public void setPassengerList(HashMap<String, Passenger> passengerList) {
        this.passengerHashMap = passengerList;
    }

    public void setCandidate(ArrayList<Station> candidate) {

        //this.candidate = candidate;
        for (Station station : candidate) {
            station.setCheckPoint();
        }
    }

    private void printLine() {
        System.out.println("---------------------------------------------------------");
    }


}
