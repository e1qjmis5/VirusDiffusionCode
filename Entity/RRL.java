package Entity;

import java.util.HashSet;
import java.util.Set;

public class RRL {

    private Set<Station> stationSet;
    private Set<Node> nodeSet;
    public int id;
    public boolean isSelected;

    public RRL(int id) {
        this.stationSet = new HashSet<>();
        this.nodeSet = new HashSet<>();
        this.id = id;
        this.isSelected = false;
    }

    public void addNode(Node node) {
        this.nodeSet.add(node);
        this.stationSet.add(node.getTrip().getBeginStation());
        this.stationSet.add(node.getTrip().getEndStation());
    }

    public int getWidth() {
        int width = 0;
        for (Node node : nodeSet) {
            width += node.getReverseEdgeNum();
        }
        return width;
    }

    public boolean isContain(Station station) {
        return stationSet.contains(station);
    }

    public int getNodeNum() {
        return nodeSet.size();
    }

}
