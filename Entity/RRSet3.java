package Entity;

// compress RR set
public class RRSet3 {

//    private Set<Passenger> passengersSet;
//    private Set<Node> nodeSet;
    public int id;
    public boolean isSelected;
    public int influence;
//    public int maxInf;

    public RRSet3(int id) {
        this.influence = 0;
//        maxInf = 0;
//        this.passengersSet = new HashSet<>();
//        this.nodeSet = new HashSet<>();
        this.id = id;
        this.isSelected = false;
    }

    public void addNode() {
        influence++;
        //this.nodeSet.add(node);
//        influence++;
//        this.passengersSet.add(node.getPassenger());
    }

    public void compress() {
//        nodeSet = null;
    }

    public int getWidth() {
//        int width = 0;
//        for (Node node : nodeSet) {
//            width += node.getReverseEdgeNum();
//        }
//        return width;
        return influence;
    }

}
