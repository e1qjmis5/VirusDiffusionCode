package Setting;

public class Setting {

    public static boolean dis1 = true; // display general debug information

    public static boolean dis2 = true; // display more general debug information

    public static double EC50 = 5000;

    public static double N = 2;

    public static int PHI = 2; // the passenger that the times of taking bus is less than (PERIOD * PHI) will be removed.

    public static int PERIOD = 5; // prediction day

    public static double REMOVEROUTE = 0; // the route that the number of riding is less than MAXIMUM*EPSILON will be remove

    public static int GAMMA = 1; //incubation

    public static double EPSILON = 0.1;

    public static double L = 1.0;

    public static int INF_NUM = 1;

    public static int CHICK_NUM = 50;

    public static int infThreshold = 900;

    public static int infModel = 0;// influence model, 1 ec50, 2, inf threshold

    public static int system = -1; // system : 0 window, 1 mac, 2 linux

    public static boolean COMPARE_SNAPSHOT = false;

    public static boolean SHOW_MODELING_TIME = true;

    public static int TIME_INTERVAL; // the interval of snapshot

    public static double maxInfPer;

    public static int MAX_INF = 1;  // whether run max influence  // 0 no max; 1 max k; 2 arg min max

    public static final String FILE = "jan16.csv";
    //public static final String FILE = "TripRecord.csv";
    //public static final String TRIPFILE = "./src/Data/TripRecord.csv";// 320W records; only has bus records; splite; 4W passengers
    //public static final String TRIPFILE = "./src/Data/jan16.csv";// 5000W records; bus records 610W; splite; 15W passengers
    //public static final String TRIPFILE = "./src/Data/march.csv"; // 1000W records; splite ;


    public static final int EXP_REPEAT_TIME = 50;
}
