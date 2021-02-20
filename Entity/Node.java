package Entity;

import Setting.Setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Node {
    private int code;
    private Passenger passenger;
    private Trip trip;
    private HashMap<Node, Double> neighborList; // neighbor infection probability
    private HashMap<Node, Double> reverseNeighborList; // neighbor infection probability
    public boolean isInfected;

    public Node(Passenger passenger, Trip trip) {
        this.passenger = passenger;
        this.trip = trip;
        this.code = trip.hasCode();
        reset();
    }

    public void reset() {
        this.neighborList = new HashMap<>();
        this.reverseNeighborList = new HashMap<>();
        this.isInfected = false;
    }

    public boolean isFirstTrip() {
        if (this.trip.getIndex() == 0) {
            return true;
        }
        return false;
    }

    public boolean isInfected() {
        return isInfected = true;
    }

    /**
     * infect a node with probability p
     *
     * @param node
     * @param p
     */
    public void addNeighbor(Node node, double p) {
        neighborList.put(node, p);
    }

    public void addReverseNeighbor(Node node, double p) {
        reverseNeighborList.put(node, p);
    }

    public void getInfection() {
        this.isInfected = true;
        this.trip.isInfected = true;
    }

    /**
     * this is only used to cancel the self infection if the infected person has been checked
     * at the end station because she can not infect the next self trip.
     * OR used to reset
     */
    public void cancelInfection() {
        this.isInfected = false;
        this.trip.isInfected = false;
    }

    /**
     * Get all reverse Nodes
     *
     * @return
     */
    public Node[] getAllReverseNodes() {
        Node[] nodeList = new Node[reverseNeighborList.size()];
        reverseNeighborList.keySet().toArray(nodeList);
        return nodeList;
    }

    /**
     * Get reverse Nodes with probability p
     *
     * @return
     */
    public Set<Node> getReverseNodesP() {
        Set<Node> nodeSet = new HashSet<>();
        Node[] nodeList = new Node[reverseNeighborList.size()];
        reverseNeighborList.keySet().toArray(nodeList);
        for (Node node : nodeList) {
            if (Math.random() <= reverseNeighborList.get(node))
                nodeSet.add(node);
        }
        return nodeSet;
    }

    public double getP(Node node) {
        return reverseNeighborList.get(node);
    }

    public Set<Node> getReverseNodes() {
        return reverseNeighborList.keySet();
    }

    public int getReverseEdgeNum() {
        return reverseNeighborList.size();
    }

    public int getEdgeNum() {
        return neighborList.size();
    }

    public ArrayList<Trip> diffuse() {
        ArrayList<Trip> infectedList = new ArrayList<>();
        if (!isInfected)
            return null;
        if (trip.getBeginStation().isCheckPoint()) {
            cancelInfection();
            this.passenger.block();
            return null;
        }
        Node[] nodeList = new Node[neighborList.size()];
        neighborList.keySet().toArray(nodeList);
        for (Node node : nodeList) {
            if (node.passenger.isBlocked())
                continue;
            if (node.isInfected)
                continue;
            if (Math.random() <= neighborList.get(node)) {
                node.getInfection();
                infectedList.add(node.getTrip());
            }
        }
        if (trip.getEndStation().isCheckPoint()) {
            cancelInfection();
            this.passenger.block();
            if (trip.getDayIndex() == Setting.PERIOD - 1)
                return infectedList;
            for (Node node : nodeList) {
                if (neighborList.get(node) == 1.0) {
                    nodeList[0].cancelInfection();
                    return infectedList;
                }
            }
        }
        return infectedList;
    }

    public Trip getTrip() {
        return trip;
    }

    public HashMap<Node, Double> getReverseNeighborList() {
        return reverseNeighborList;
    }

    public Passenger getPassenger() {
        return this.passenger;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
