package Setting;

import java.util.ArrayList;

public class ExpSetting {


    public int[] algList; // 0 non-check; 1 topk; 2 TIM+; 3 SIM

    public double ec50;

    public double n;

    public int phi; // the passenger that the times of taking bus is less than (PERIOD * PHI) will be removed.

    public int period; // prediction day

    public int gamma; //incubation

    public double epsilon;

    public double l;

    public ArrayList<Integer> infNumList;  // x / 10000

    public int checkNum;

    public ArrayList<Integer> finalInfNum;

    public int expIndex;

    public int numPassenger;

    public ArrayList<Long> timeList;

    public int infThreshold;

    public int infModel;

    public int timeInterval;

    public ExpSetting() {
        this.finalInfNum = new ArrayList<>();
        this.timeList = new ArrayList<>();
    }

    public ExpSetting(ExpSetting defaultSetting) {
        this.ec50 = defaultSetting.ec50;
        this.n = defaultSetting.n;
        this.phi = defaultSetting.phi;
        this.period = defaultSetting.period;
        this.gamma = defaultSetting.gamma;
        this.epsilon = defaultSetting.epsilon;
        this.l = defaultSetting.l;
        this.infNumList = defaultSetting.infNumList;
        this.checkNum = defaultSetting.checkNum;
        this.finalInfNum = new ArrayList<>();
        this.timeList = new ArrayList<>();
        this.algList = defaultSetting.algList;
        this.infThreshold = defaultSetting.infThreshold;
        this.infModel = defaultSetting.infModel;
        this.timeInterval= defaultSetting.timeInterval;
    }
}
