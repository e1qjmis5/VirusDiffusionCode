package Entity;

public class RRSet {

//    private Set<Station> stationSet;
//    private Set<Node> nodeSet;
    public int id;
    public boolean isSelected;
    public int influence;

    public RRSet(int id) {
//        this.stationSet = new HashSet<>();
//        this.nodeSet = new HashSet<>();
        this.id = id;
        this.isSelected = false;
        this.influence = 0;
    }

    public void addNode() {
        influence++;
    }

    public void addNode(Node node) {
    }

    public int getWidth() {
//        int width = 0;
//        for (Node node : nodeSet) {
//            width += node.getReverseEdgeNum();
//        }
//        return width;
        return influence;
    }

    public boolean isContain(Station station) {
        return true;
        //return stationSet.contains(station);
    }

    public int getNodeNum() {
        return influence;
    }

}
