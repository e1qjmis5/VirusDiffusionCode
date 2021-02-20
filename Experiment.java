import Algorithm.SIM;
import Algorithm.TIM;
import Algorithm.TopK;
import Algorithm.kPass;
import Entity.*;
import Setting.*;
import Function.DiffusionSimulator;
import Function.GenerateRoughGraph;
import Function.GenerateTripGraph;
import Function.OutputExperiment;
import generator.Initialization;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class Experiment {
    private String fileName;
    private ArrayList<ExpSetting> expList;
    private Initialization initialization;
    private GenerateTripGraph generator;
    private GenerateRoughGraph generator2;
    private DiffusionSimulator diffusionSimulator;
    private DiffusionSimulator diffusionSimulator2;
    private OutputExperiment outputExperiment;
    private kPass kPass;

    private ArrayList<Double> timeList = new ArrayList<>(); //this is only for modeling time, for testing
    int timeRecord = 0;

    public Experiment() {
        ExpPlan expPlan = new ExpPlan();
        expList = expPlan.getExpPlanList();
    }

    public void run() {
        int expIndex = 0;
        ExpSetting preExp = null;

        for (ExpSetting exp : expList) {
            exp.expIndex = expIndex++;
            loadSetting(exp);
            if (initialization == null) {
                initialization = new Initialization();
                if (Setting.COMPARE_SNAPSHOT) {
                    generator2 = new GenerateRoughGraph(initialization.getTripHashMap(), initialization.getPassengerList());
                    generator2.startGenerate();
                    exp.numPassenger = generator2.getPassengerNum();
                    diffusionSimulator = new DiffusionSimulator();
                    diffusionSimulator.setNodeArrayList(generator2.getNodeList());
                    //diffusionSimulator.setTripHashMap(generator.getTripHashMap());
                    diffusionSimulator.setPassengerList(generator2.getPassengerList());
                    //diffusionSimulator.setPassengerTripHashMap(generator.getPassengerTripHashMap());
                } else {
                    generator = new GenerateTripGraph(initialization.getTripHashMap(), initialization.getPassengerList());
                    generator.startGenerate();
                    exp.numPassenger = generator.getPassengerNum();
                    diffusionSimulator = new DiffusionSimulator();
                    diffusionSimulator.setNodeArrayList(generator.getNodeList());
                    //diffusionSimulator.setTripHashMap(generator.getTripHashMap());
                    diffusionSimulator.setPassengerList(generator.getPassengerList());
                    //diffusionSimulator.setPassengerTripHashMap(generator.getPassengerTripHashMap());
                }
//                diffusionSimulator.setTripQueue(initialization.getTripQueue());

            } else {
                boolean reload = false;
                if (exp.period != preExp.period) {
                    cleanMemory();
                    initialization = new Initialization();
                    reload = true;
                    if (Setting.COMPARE_SNAPSHOT) {
//                        generator2 = new GenerateRoughGraph(initialization.getTripHashMap(), initialization.getPassengerHashMap());
                        generator2 = new GenerateRoughGraph(initialization.getTripHashMap(), initialization.getPassengerList());
                        generator2.startGenerate();
                    } else {
//                        generator = new GenerateTripGraph(initialization.getTripHashMap(), initialization.getPassengerHashMap());
                        generator = new GenerateTripGraph(initialization.getTripHashMap(), initialization.getPassengerList());
                        generator.startGenerate();
                    }
                }
                if (exp.gamma != preExp.gamma || exp.ec50 != preExp.ec50 || exp.phi != preExp.phi
                        || exp.timeInterval != preExp.timeInterval) {
                    loadSetting(exp);
                    if (Setting.COMPARE_SNAPSHOT)
                        generator2.regenerate();
                    else
                        generator.regenerate();
                    reload = true;
                }
                if (Setting.COMPARE_SNAPSHOT)
                    exp.numPassenger = generator2.getPassengerNum();
                else
                    exp.numPassenger = generator.getPassengerNum();
                if (reload) {
                    kPass = null;
                    if (Setting.COMPARE_SNAPSHOT) {
                        diffusionSimulator = new DiffusionSimulator();
                        diffusionSimulator.setNodeArrayList(generator2.getNodeList());
                        //diffusionSimulator.setTripHashMap(generator.getTripHashMap());
                        diffusionSimulator.setPassengerList(generator2.getPassengerList());
                        //diffusionSimulator.setPassengerTripHashMap(generator.getPassengerTripHashMap());
                    } else {
                        diffusionSimulator = new DiffusionSimulator();
                        diffusionSimulator.setNodeArrayList(generator.getNodeList());
                        //diffusionSimulator.setTripHashMap(generator.getTripHashMap());
                        diffusionSimulator.setPassengerList(generator.getPassengerList());
                        //diffusionSimulator.setPassengerTripHashMap(generator.getPassengerTripHashMap());
                    }
//                    diffusionSimulator.setTripQueue(initialization.getTripQueue());
                }
            }

            evaluation(exp);
            writeExperiment(exp);
            preExp = exp;
        }
        outputExperiment.close();
        timeRecord = 0;
        printSummary();
    }

    private void cleanMemory() {
        expList = null;
        initialization = null;
        generator = null;
        generator2 = null;
        diffusionSimulator = null;
        diffusionSimulator = new DiffusionSimulator();
    }

    private void evaluation(ExpSetting expSetting) {
        printLine();
        System.out.println("Experiment " + expSetting.expIndex);
        System.out.printf("|%-15s|%-15s|%-15s%n",
                "Period " + expSetting.period,
                "Gamma " + expSetting.gamma,
                "Check " + expSetting.checkNum);
        System.out.printf("|%-15s|%-15s%n",
                "Ec50 " + expSetting.ec50,
                "N " + expSetting.n);

        int finalInfNum;
        long time;

        for (int alg : expSetting.algList) {
            ArrayList<Station> solution = new ArrayList<>();
            switch (alg) {
                case 0:
                    System.out.println("No Checkpoint");
                    diffusionSimulator.setCandidate(new ArrayList<>());
                    expSetting.timeList.add((long) 0);
                    break;
                case 1:
                    System.out.println("Top-K");
                    time = System.currentTimeMillis();
                    solution = topK();
                    time = System.currentTimeMillis() - time;
                    diffusionSimulator.setCandidate(solution);
                    expSetting.timeList.add(time);
                    break;
                case 2:
                    System.out.println("TIM");
                    time = System.currentTimeMillis();
                    solution = tim();
                    time = System.currentTimeMillis() - time;
                    diffusionSimulator.setCandidate(solution);
                    expSetting.timeList.add(time);
                case 3:
                    System.out.println("SIM");
                    time = System.currentTimeMillis();
                    solution = sim();
                    time = System.currentTimeMillis() - time;
                    diffusionSimulator.setCandidate(solution);
                    expSetting.timeList.add(time);
            }

            printLine();
            //random infection
            for (int i = 0; i < expSetting.infNumList.size(); i++) {
                System.out.println("Random Inf Diffusion");
                int infNum = expSetting.infNumList.get(i);
                infNum = expSetting.numPassenger * infNum / 10000;
                System.out.print("Start Inf Number " + infNum + ",  ");
                System.out.println(expSetting.infNumList.get(i) / 100.0 + " %");

                finalInfNum = countInfNumRandom(infNum);
                expSetting.finalInfNum.add(finalInfNum);
                System.out.printf("Random inf : %-10s | avgInf : %-10s%n", infNum, finalInfNum);
                printLine();
            }
            //Max infection
            if (Setting.MAX_INF > 0) {
                for (int i = 0; i < expSetting.infNumList.size(); i++) {
                    System.out.println("Maximum Inf Diffusion" + (Setting.MAX_INF == 1 ? "Max" : "MinMax"));
                    int infNum = expSetting.infNumList.get(i);
                    infNum = expSetting.numPassenger * infNum / 10000;
                    System.out.print("Start Inf Number " + infNum + ",  ");
                    System.out.println(expSetting.infNumList.get(i) / 100.0 + " %");

                    finalInfNum = countInfNumMaxK(infNum, solution);
                    expSetting.finalInfNum.add(finalInfNum);
                    System.out.printf("Maximum Inf : %-10s | avgInf : %-10s%n", infNum, finalInfNum);
                    printLine();
                }
            }

            for (Station station : solution) {
                station.clearCheckPoint();
            }


        }
        printLine();
    }

    //top k infected passenger
    private int countInfNumMaxK(int infNum, ArrayList<Station> solution) {
        generateKPass(solution);
        Set<Passenger> infPass = kPass.getKPassenger(infNum);

        return runDiffusion(infPass);
    }

    //random infected passenger
    private int countInfNumRandom(int infNum) {
        generateKPass(null);
        Set<Passenger> infPass = kPass.getKRanPassenger(infNum);
        ;

        return runDiffusion(infPass);
    }

    private int runDiffusion(Set<Passenger> infPass) {
        int finalInfNum = 0;
        diffusionSimulator.resetRepeatTime();
        long time = System.currentTimeMillis();
        for (int i = 0; i < Setting.EXP_REPEAT_TIME; i++) {
            diffusionSimulator.setInfectedPassengerList(infPass);
            finalInfNum += countInfNum(infPass.size());
        }
        time = System.currentTimeMillis() - time;
        time /= Setting.EXP_REPEAT_TIME;
        if (Setting.dis1)
            System.out.println("Modeling time " + time + " ms");
        timeList.add((double) time);
        return finalInfNum / Setting.EXP_REPEAT_TIME;
    }

    private int countInfNum(int infNum) {
        diffusionSimulator.setInfNum(infNum);
        diffusionSimulator.startDiffusion();
        //System.out.println(i + "th " + diffusionSimulator.countInfectionNum());
        infNum = diffusionSimulator.countInfectionNum();
        diffusionSimulator.reset();
        return infNum;
    }

    private ArrayList<Station> topK() {
        TopK topK = new TopK();
        topK.setCheckPointNum(Setting.CHICK_NUM);
        topK.setStationHashMap(initialization.getStationHashMap());
        return topK.getCheckPoint();
//
//
//        diffusionSimulator.setCandidate(checkpointList);
//        int finalInfNum = countInfNum();
//        for (Station station : checkpointList) {
//            station.clearCheckPoint();
//        }
//        return finalInfNum;
    }

    private ArrayList<Station> tim() {
        TIM tim = new TIM();
        //tim.setGraph(generator.getGraph());
        tim.setEdgeNum(generator.getEdgeNumber());
        tim.setNodeNum(generator.getNodeList().size());
        tim.setLastNodeSet(generator.getLastNodeSet());
        tim.setStationHashMap(initialization.getStationHashMap());
        tim.setCheckPointNum(Setting.CHICK_NUM);
        return tim.getCheckPoint();
//        diffusionSimulator.setCandidate(checkpointList);
//        int finalInfNum = countInfNum();
//        for (Station station : checkpointList) {
//            station.clearCheckPoint();
//        }
//        return finalInfNum;
    }

    private ArrayList<Station> sim() {
        SIM sim = new SIM();
//        sim.setGraph(generator.getGraph());
        sim.setEdgeNum(generator.getEdgeNumber());
        sim.setNodeNum(generator.getNodeList().size());
        sim.setLastNodeSet(generator.getLastNodeSet());
        sim.setStationHashMap(initialization.getStationHashMap());
        sim.setCheckPointNum(Setting.CHICK_NUM);
        return sim.getCheckPoint();
//        diffusionSimulator.setCandidate(checkpointList);
//        int finalInfNum = countInfNum();
//        for (Station station : checkpointList) {
//            station.clearCheckPoint();
//        }
//        return finalInfNum;
    }

    private void generateKPass(ArrayList<Station> solution) {
        if (kPass == null) {
            kPass = new kPass();
            if (Setting.COMPARE_SNAPSHOT) {
                kPass.setNodeArrayList(generator2.getNodeList());
                kPass.setLastNodeSet(generator2.getLastNodeSet());
                kPass.setPassengerList(generator2.getPassengerList());
            } else {
                if (solution != null && Setting.MAX_INF == 2)
                    kPass.setCandidate(solution);
                kPass.setNodeArrayList(generator.getNodeList());
                kPass.setLastNodeSet(generator.getLastNodeSet());
                kPass.setPassengerList(generator.getPassengerList());
            }
            kPass.setStationHashMap(initialization.getStationHashMap());
        }
//        diffusionSimulator.setCandidate(checkpointList);
//        int finalInfNum = countInfNum();
//        for (Station station : checkpointList) {
//            station.clearCheckPoint();
//        }
//        return finalInfNum;
    }

    private void writeExperiment(ExpSetting exp) {
        if (fileName == null) {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd");
            Date date = new Date();
            fileName = "Experiment " + sdf.format(date);
            outputExperiment = new OutputExperiment(fileName);
        }
        ArrayList<String> content = new ArrayList<>();
        int index = expList.indexOf(exp);

        content.add("------------------------ Experiment " + index + " ------------------------\r\n");

        content.add("Data File : " + Setting.FILE + "\r\n");
        if (Setting.COMPARE_SNAPSHOT)
            content.add("Model : Snapshot\n");
        else
            content.add("Model : Dynamic Graph\n");

        if (Setting.infModel == 1)
            content.add("Inf Model : ec50\n");
        else
            content.add("Inf Model : Threshold\n");

        if (!Setting.COMPARE_SNAPSHOT) {
            if (exp.infModel == 1) {
                content.add(String.format("|%-15s|%-15s|%-15s|%-15s|%-15s%n",
                        "Period " + exp.period,
                        "Gamma " + exp.gamma,
                        "Check Num " + exp.checkNum,
                        "Ec50 " + exp.ec50,
                        "N " + exp.n));
            } else if (exp.infModel == 2) {
                content.add(String.format("|%-15s|%-15s|%-15s|%-15s|%-15s%n",
                        "Period " + exp.period,
                        "Gamma " + exp.gamma,
                        "Check Num " + exp.checkNum,
                        "Threshold " + exp.infThreshold,
                        "N " + exp.n));
            }
        } else {
            content.add(String.format("|%-15s|%-15s|%-15s|%-15s|%-15s|%-15s%n",
                    "Period " + exp.period,
                    "Gamma " + exp.gamma,
                    "Check Num " + exp.checkNum,
                    "Ec50 " + exp.ec50,
                    "N " + exp.n,
                    "Snap TIme " + exp.timeInterval));
        }


        content.add("---------------------------------------------------------------------\r\n");
        content.add(String.format("|%-20s", "ALG Name"));
        for (int i = 0; i < exp.infNumList.size(); i++) {
            content.add(String.format("|%-15s", exp.numPassenger * exp.infNumList.get(i) / 10000 + "(" + exp.infNumList.get(i) / 100.0 + "%)"));
        }
        content.add("|Time\r\n");
        int algNum = exp.algList.length;
        int z = Setting.MAX_INF > 0 ? 2 : 1;

        for (int i = 0; i < algNum; i++) {
            switch (exp.algList[i]) {
                case 0:
                    content.add(String.format("|%-10s", "No Check"));
                    break;
                case 1:
                    content.add(String.format("|%-10s", "Top-k"));
                    break;
                case 2:
                    content.add(String.format("|%-10s", "Tim+"));
                    break;
                case 3:
                    content.add(String.format("|%-10s", "SIM"));
                    break;

            }
            content.add(String.format("%-10s", "-Random"));
            for (int k = 0; k < exp.infNumList.size(); k++) {
                content.add(String.format("|%-15s", exp.finalInfNum.get((i * z) * exp.infNumList.size() + k)));
            }
            content.add("\r\n");
            content.add(String.format("%-21s", ""));
            if (Setting.SHOW_MODELING_TIME) {
                for (int k = 0; k < exp.infNumList.size(); k++) {
                    content.add(String.format("|%-15s", timeList.get(timeRecord++) + " ms"));
                }
            }
            content.add("\r\n");
            if (Setting.MAX_INF > 0) {
                content.add(String.format("%-11s", ""));
                if (Setting.MAX_INF == 1)
                    content.add(String.format("%-10s", "-Max"));
                else
                    content.add(String.format("%-10s", "-MinMax"));
                for (int k = 0; k < exp.infNumList.size(); k++) {
                    content.add(String.format("|%-15s", exp.finalInfNum.get((i * z) * exp.infNumList.size() + exp.infNumList.size() + k)));
                }
                content.add("|" + exp.timeList.get(i) + " ms\r\n");
                content.add(String.format("%-21s", ""));
                if (Setting.SHOW_MODELING_TIME) {
                    for (int k = 0; k < exp.infNumList.size(); k++) {
                        content.add(String.format("|%-15s", timeList.get(timeRecord++) + " ms"));
                    }
                }
                content.add("\r\n");
            }

        }

        outputExperiment.writeFile(content);
    }

    private void printSummary() {
        System.out.println("++++++++++++++++++++++ Experiment Results Summary ++++++++++++++++++++++");
        for (int n = 0; n < expList.size(); n++) {
            ExpSetting exp = expList.get(n);
            System.out.println("------------------------ Experiment " + n + " ------------------------");

            if (!Setting.COMPARE_SNAPSHOT) {
                if (exp.infModel == 1) {
                    System.out.printf("|%-15s|%-15s|%-15s|%-15s|%-15s%n",
                            "Period " + exp.period,
                            "Gamma " + exp.gamma,
                            "Check Num " + exp.checkNum,
                            "Ec50 " + exp.ec50,
                            "N " + exp.n);
                } else if (exp.infModel == 2) {
                    System.out.printf("|%-15s|%-15s|%-15s|%-15s%n",
                            "Period " + exp.period,
                            "Gamma " + exp.gamma,
                            "Check Num " + exp.checkNum,
                            "Threshold " + exp.infThreshold);
                }
            } else {
                System.out.printf("|%-15s|%-15s|%-15s|%-15s|%-15s|%-15s%n",
                        "Period " + exp.period,
                        "Gamma " + exp.gamma,
                        "Check Num " + exp.checkNum,
                        "Ec50 " + exp.ec50,
                        "N " + exp.n,
                        "Snap TIme " + exp.timeInterval);
            }

            printLine();
            System.out.printf("|%-20s", "ALG Name");
            for (int i = 0; i < exp.infNumList.size(); i++) {
                System.out.printf("|%-15s", exp.numPassenger * exp.infNumList.get(i) / 10000 + "(" + exp.infNumList.get(i) / 100.0 + "%)");
            }
            System.out.println("|Time");
            int algNum = exp.algList.length;
            int z = Setting.MAX_INF > 0 ? 2 : 1;
            for (int i = 0; i < algNum; i++) {
                switch (exp.algList[i]) {
                    case 0:
                        System.out.printf("|%-10s", "No Check");
                        break;
                    case 1:
                        System.out.printf("|%-10s", "Top-k");
                        break;
                    case 2:
                        System.out.printf("|%-10s", "Tim+");
                        break;
                    case 3:
                        System.out.printf("|%-10s", "SIM");
                        break;

                }
                System.out.printf("%-10s", "-Random");
                for (int k = 0; k < exp.infNumList.size(); k++) {
                    System.out.printf("|%-15s", exp.finalInfNum.get((i * z) * exp.infNumList.size() + k));
                }
                System.out.println("");
                System.out.printf("%-21s", "");
                if (Setting.SHOW_MODELING_TIME) {
                    for (int k = 0; k < exp.infNumList.size(); k++) {
                        System.out.printf("|%-15s", timeList.get(timeRecord++) + "ms");
                    }
                }
                System.out.println("");
                System.out.printf("%-11s", "");
                if (Setting.MAX_INF == 1)
                    System.out.printf("%-10s", "-Max");
                else
                    System.out.printf("%-10s", "-MinMax");
                for (int k = 0; k < exp.infNumList.size(); k++) {
                    System.out.printf("|%-15s", exp.finalInfNum.get((i * z) * exp.infNumList.size() + exp.infNumList.size() + k));
                }
                System.out.println("|" + exp.timeList.get(i) + " ms");
                System.out.printf("%-21s", "");
                if (Setting.SHOW_MODELING_TIME) {
                    for (int k = 0; k < exp.infNumList.size(); k++) {
                        System.out.printf("|%-15s", timeList.get(timeRecord++) + "ms");
                    }
                }
                System.out.println("");
            }


        }

        System.out.println("++++++++++++++++++++++ Experiment Results Summary ++++++++++++++++++++++");
    }

    /**
     * Generate setting for each advertiser
     *
     * @param exp the experiment setting
     */
    private void loadSetting(ExpSetting exp) {
        Setting.EC50 = exp.ec50;
        Setting.N = exp.n;
        Setting.PHI = exp.phi;
        Setting.PERIOD = exp.period;
        Setting.GAMMA = exp.gamma;
        Setting.EPSILON = exp.epsilon;
        Setting.L = exp.l;
        Setting.CHICK_NUM = exp.checkNum;
        Setting.TIME_INTERVAL = exp.timeInterval;
    }

    private void printLine() {
        System.out.println("---------------------------------------------------------");
    }
}
