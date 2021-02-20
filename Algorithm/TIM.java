package Algorithm;

import Entity.*;
import Function.Combination;
import Setting.Setting;

import java.util.*;

public class TIM {
    private HashMap<String, Station> stationHashMap; // <station id, station>
//    private Set<Node> graph;
    private Set<RRSet> RRSetSet;
    private Node[] lastNodeSet;
    private int checkPointNum;
    private double avgCoverNum;
    private double edgeNum; // m
    private double nodeNum; // n
    private int theta;

    public TIM() {
        RRSetSet = new HashSet<>();
    }

    public ArrayList<Station> getCheckPoint() {
        System.out.println(" - TIM ");
//        getGraphInf();
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
        return findSolution();
    }

//    private void getGraphInf() {
//        if (Setting.dis2)
//            System.out.println("1. Generate Graph Information");
//        if (graph == null || graph.size() == 0) {
//            System.out.println("! ERROR (TIM) Graph is empty");
//            return;
//        }
//        this.nodeNum = graph.size();
//        this.edgeNum = 0;
//        for (Node node : graph) {
//            this.edgeNum += node.getEdgeNum();
//        }
//        if (Setting.dis2) {
//            System.out.println(" - Node Num " + this.nodeNum);
//            System.out.println(" - Edge Num " + this.edgeNum);
//            printLine();
//        }
//    }

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
        int z = (int) (Math.log(nodeNum) / Math.log(2.0) - 1.0);
        Random random = new Random();
        RRSet rrSet;
        if (Setting.dis2)
            System.out.println(" - Max iteration " + z);
        for (int i = 1; i <= z; i++) {
            if (Setting.dis2)
                System.out.println(" - Iteration (" + i + "/" + z + ")");
            c = (6.0 * l * Math.log(nodeNum) + 6.0 * (Math.log(nodeNum) / Math.log(2))) * Math.pow(2, i);
            if (c > cPre) {
                generateNum = (int) (c - cPre);
                cPre = c;
            }
            if (Setting.dis2)
                System.out.println("   * Generate RRSet Num " + generateNum);
            for (int n = 1; n <= generateNum; n++) {
                itemIndex = random.nextInt(randMax);
                rrSet = generateRRSet(lastNodeSet[itemIndex]);
                RRSetSet.add(rrSet);
                avgCoverNum += rrSet.getNodeNum();
                kr = 1.0 - Math.pow((1.0 - rrSet.getWidth() / edgeNum), checkPointNum * avgCoverNum / RRSetSet.size());
                sum += kr;
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
        double a = Setting.L * Math.pow(Setting.EPSILON, 2) / (checkPointNum * avgCoverNum + Setting.L);
        double epsilon = 5.0 * Math.pow(a, (1.0 / 3.0));
        if (Setting.dis2)
            System.out.println(" - epsilon' " + epsilon);

        double lambda = (2.0 + epsilon) * Setting.L * checkPointNum * avgCoverNum * Math.log(nodeNum) * Math.pow(epsilon, -2);
        double theta = lambda / kpt;

        int randMax = lastNodeSet.length;
        int itemIndex;
        Random random = new Random();
        RRSet rrSet;
        if (Setting.dis2)
            System.out.println(" - Theta' " + theta);

        for (int n = 1; n <= checkPointNum; n++) {
            itemIndex = random.nextInt(randMax);
            rrSet = generateRRSet(lastNodeSet[itemIndex]);
            RRSetSet.add(rrSet);
            avgCoverNum += rrSet.getWidth();
        }

        double size1 = RRSetSet.size();
        ArrayList<Station> stationList = findSolution();
        for (Station station : stationList) {
            selectStation(station);
        }
        double size2 = RRSetSet.size();
        double f = (size1 - size2) / size1;
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

    private ArrayList<Station> findSolution() {
        if (Setting.dis2) {
            System.out.println("4. Find solution");
            System.out.println(" * Update station covers RRSets");
        }
        for (Station station : stationHashMap.values()) {
            updateCoverRRSets(station, 0);
        }
        PriorityQueue<Station> stationQueue = new PriorityQueue<>(stationHashMap.values());

        Station station;
        ArrayList<Station> candidates = new ArrayList<>();
        for (int i = 0; i < checkPointNum; i++) {
            do {
                station = stationQueue.poll();
                if (station.getUpdateTime() == i) {
                    candidates.add(station);
                    selectStation(station);
                    //System.out.println(" - Candidate RR Num " + station.getCoverRRNum() + "  " + station.getUpdateTime() + "/" + i);
                } else {
                    updateCoverRRSets(station, i);
                    stationQueue.add(station);
                    station = null;
                }
            } while (station == null);
            if (Setting.dis2)
                System.out.println(" * Find " + i + "  RRSets Num " + RRSetSet.size());
        }
        return candidates;
    }

    private void selectStation(Station station) {
        for (RRSet rrSet : station.getRRSets()) {
            RRSetSet.remove(rrSet);
        }

    }

    private void updateCoverRRSets(Station station, int iteration) {
        int count = 0;
        for (RRSet rrSet : RRSetSet) {
            if (rrSet.isContain(station)) {
                station.addRRSet(rrSet);
                count++;
            }
        }
        station.setCoverRRNum(count, iteration);
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
            RRSetSet.add(generateRRSet(lastNodeSet[itemIndex]));
            //RRSetSet.add(rrSet);
            if ((Setting.dis2) && (n % 10000 == 0)) {
                endTime = System.currentTimeMillis();
                System.out.println(" - Finish (10k) " + (n / 10000) + "/" + (theta / 10000)
                        + "  | " + (endTime - startTime) / 1000 + " s");
            }
        }
    }

    private RRSet generateRRSet(Node node) {
        RRSet rrSet = new RRSet(RRSetSet.size());
        Queue<Node> queue = new ArrayDeque<>();
        Set<Node> visitedNodeSet = new HashSet<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            node = queue.poll();
            rrSet.addNode(node);
            for (Node newNode : node.getReverseNodesP()) {
                if (!visitedNodeSet.contains(newNode) && queue.size() < 10000) {
                    visitedNodeSet.add(newNode);
                    queue.add(newNode);
                }
            }
        }

        return rrSet;
    }

    private double avgCoverNum() {
        double num = 0;
        for (Station station : stationHashMap.values()) {
            num += station.getVolume();
        }
        return num / stationHashMap.values().size();
    }

    private double getLambda() {
        double lambda = (8.0 + 2.0 * Setting.EPSILON) * nodeNum * Math.pow(Setting.EPSILON, -2.0);
        lambda *= (Setting.L * Math.log(nodeNum) + Math.log(Combination.getC3((int) nodeNum, checkPointNum)) + Math.log(2.0));
        return lambda;
    }

    public void setStationHashMap(HashMap<String, Station> stationHashMap) {
        this.stationHashMap = stationHashMap;
    }

    public void setCheckPointNum(int num) {
        this.checkPointNum = num;
    }

    public void setEdgeNum(int edgeNum) {
        this.edgeNum = edgeNum;
    }

    public void setNodeNum(int nodeNum) {
        this.nodeNum = nodeNum;
    }

//    public void setGraph(Set<Node> graph) {
//        this.graph = graph;
//    }

    public void setLastNodeSet(Set<Node> lastNodeSet) {
        this.lastNodeSet = new Node[lastNodeSet.size()];
        lastNodeSet.toArray(this.lastNodeSet);
    }

    private void printLine() {
        System.out.println("---------------------------------------------------------");
    }

}
