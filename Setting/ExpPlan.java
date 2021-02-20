package Setting;

import java.util.ArrayList;

public class ExpPlan {

    ArrayList<ExpSetting> expPlanList;
    ExpSetting defaultSetting = new ExpSetting();

    public ExpPlan() {
        expPlanList = new ArrayList<>();
        setDefaultSetting();

//        expPlanList.addAll(expGenerate());
        expPlanList.addAll(expGenerate2());

//        expPlanList.add(exp01());
//        expPlanList.add(exp02());
//        expPlanList.add(exp03());
//        expPlanList.add(exp04());
//        expPlanList.add(exp05());
//        expPlanList.add(exp06());
//        expPlanList.add(exp07());
//        expPlanList.add(exp08());
//        expPlanList.add(exp09());


//
//        expPlanList.add(exp11());
//        expPlanList.add(exp12());
//        expPlanList.add(exp13());
//        expPlanList.add(exp14());
//        expPlanList.add(exp15());
//
//        expPlanList.add(exp21());
//        expPlanList.add(exp22());
//        expPlanList.add(exp23());
//        expPlanList.add(exp24());
//        expPlanList.add(exp25());
//
//        expPlanList.add(exp31());
//        expPlanList.add(exp32());
//        expPlanList.add(exp33());
//        expPlanList.add(exp34());
//        expPlanList.add(exp35());
    }

    public ArrayList<ExpSetting> getExpPlanList() {
        return expPlanList;
    }

    private void setDefaultSetting() {
        defaultSetting.ec50 = 1600;
        defaultSetting.n = 3;
        defaultSetting.phi = 2; // the passenger that the times of taking bus is less than (PERIOD * PHI) will be removed.
        defaultSetting.period = 15; // prediction day
        defaultSetting.gamma = 1; //incubation
        defaultSetting.epsilon = 0.1;
        defaultSetting.l = 1.0;
        defaultSetting.infNumList = new ArrayList<>();
        defaultSetting.infNumList.add(1);       //      0.01%
        defaultSetting.infNumList.add(5);      //       0.05%
        defaultSetting.infNumList.add(10);      //      0.1%
        defaultSetting.infNumList.add(20);      //      0.2%
        defaultSetting.infNumList.add(50);      //      0.5%
        defaultSetting.infNumList.add(100);     //      1%
//        defaultSetting.infNumList.add(200);     //      2%
//        defaultSetting.infNumList.add(500);     //      5/100
//        defaultSetting.infNumList.add(1000);    //      1/10
//        defaultSetting.infNumList.add(10000);   //      1/1
        defaultSetting.checkNum = 50;
        defaultSetting.algList = new int[]{3, 1, 0};
//        defaultSetting.algList = new int[]{0};
        defaultSetting.infThreshold = 800;
        defaultSetting.infModel = 1;
        defaultSetting.timeInterval = 1;

        for (int i : defaultSetting.infNumList) {
            if (i > Setting.maxInfPer)
                Setting.maxInfPer = i;
        }
    }

    private ArrayList<ExpSetting> expGenerate2() {
        Setting.COMPARE_SNAPSHOT = false;
        ArrayList<ExpSetting> expList = new ArrayList<>();
        int[] ecList = {3600};
        int[] gammaList = {3};
        int[] checkList = {25,50,75};
        int[] periodList = {14};
        for (int m : periodList) {
            for (int j : ecList) {
                for (int k : gammaList) {
                    for (int l : checkList) {
                        ExpSetting exp = new ExpSetting(defaultSetting);
                        exp.ec50 = j;
                        exp.gamma = k;
                        exp.checkNum = l;
                        exp.period = m;
                        expList.add(exp);
                    }
                }
            }
        }
        return expList;
    }

    //snapshot
    private ArrayList<ExpSetting> expGenerate() {
        int count = 0;
        Setting.COMPARE_SNAPSHOT = true;
        ArrayList<ExpSetting> expList = new ArrayList<>();
        int[] ecList = {5400, 3600, 1800};
        int[] gammaList = {1, 3};
        int[] periodList = {14};
        int[] snapTIme = {2, 3};
        for (int m : periodList) {
            for (int j : ecList) {
                for (int k : gammaList) {
                    for (int l : snapTIme) {
                        ExpSetting exp = new ExpSetting(defaultSetting);
                        exp.ec50 = j;
                        exp.gamma = k;
                        exp.timeInterval = l;
                        exp.period = m;
                        expList.add(exp);
                    }
                }
            }
        }
        return expList;
    }

    private ExpSetting exp01() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 25;
        exp.ec50 = 900;
        exp.gamma = 3;
        exp.period = 7;
        return exp;
    }

    private ExpSetting exp02() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 50;
        exp.ec50 = 900;
        exp.gamma = 3;
        exp.period = 10;
        return exp;
    }

    private ExpSetting exp03() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 75;
        exp.ec50 = 900;
        exp.gamma = 3;
        return exp;
    }

    private ExpSetting exp04() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 25;
        exp.ec50 = 1800;
        exp.gamma = 3;
        return exp;
    }

    private ExpSetting exp05() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 50;
        exp.ec50 = 1800;
        exp.gamma = 3;
        return exp;
    }

    private ExpSetting exp06() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 75;
        exp.ec50 = 1800;
        exp.gamma = 3;
        return exp;
    }

    private ExpSetting exp07() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 25;
        exp.ec50 = 1800;
        exp.gamma = 3;
        return exp;
    }

    private ExpSetting exp08() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 50;
        exp.ec50 = 3600;
        exp.gamma = 3;
        return exp;
    }

    private ExpSetting exp09() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.checkNum = 75;
        exp.ec50 = 3600;
        exp.gamma = 3;
        return exp;
    }

    private ExpSetting exp12() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 500;
        exp.gamma = 2;
        return exp;
    }

    private ExpSetting exp13() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 1000;
        exp.gamma = 2;
        return exp;
    }

    private ExpSetting exp14() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 2000;
        exp.gamma = 2;
        return exp;
    }

    private ExpSetting exp15() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 10000;
        exp.gamma = 2;
        return exp;
    }

    private ExpSetting exp21() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 100;
        exp.gamma = 5;
        return exp;
    }

    private ExpSetting exp22() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 500;
        exp.gamma = 5;
        return exp;
    }

    private ExpSetting exp23() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 1000;
        exp.gamma = 5;
        return exp;
    }

    private ExpSetting exp24() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 2000;
        exp.gamma = 5;
        return exp;
    }

    private ExpSetting exp25() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 10000;
        exp.gamma = 5;
        return exp;
    }

    private ExpSetting exp31() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 100;
        exp.gamma = 10;
        return exp;
    }

    private ExpSetting exp32() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 500;
        exp.gamma = 10;
        return exp;
    }

    private ExpSetting exp33() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 1000;
        exp.gamma = 10;
        return exp;
    }

    private ExpSetting exp34() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 2000;
        exp.gamma = 10;
        return exp;
    }

    private ExpSetting exp35() {
        ExpSetting exp = new ExpSetting(defaultSetting);
        exp.ec50 = 10000;
        exp.gamma = 10;
        return exp;
    }


}
