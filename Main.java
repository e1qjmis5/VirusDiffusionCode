import Setting.Setting;

public class Main {

    public static void main(String[] args) {

        systemInfTest();

        Experiment experiment = new Experiment();
        experiment.run();

//        Initialization initialization = new Initialization();
//
//        GenerateTripGraph generator = new GenerateTripGraph(initialization.getTripHashMap(), initialization.getPassengerHashMap());
//        generator.startGenerate();
//
//        DiffusionSimulator diffusionSimulator = new DiffusionSimulator();
//        diffusionSimulator.setGraph(generator.getGraph());
//        diffusionSimulator.setTripHashMap(generator.getTripHashMap());
//        diffusionSimulator.setPassengerList(generator.getPassengerList());
//        diffusionSimulator.setPassengerTripHashMap(generator.getPassengerTripHashMap());
//        diffusionSimulator.setTripQueue(initialization.getTripQueue());
//        for (int i = 0; i < 10; i++) {
//            System.out.println("--------- infection iteration " + i + " ----------");
//            diffusionSimulator.setInfNum(10000);
//            diffusionSimulator.startDiffusion();
//            diffusionSimulator.reset();
//        }


//        TIM tim = new TIM();
//        tim.setGraph(generator.getGraph());
//        tim.setLastNodeSet(generator.getLastNodeSet());
//        tim.setStationHashMap(initialization.getStationHashMap());
//        tim.setCheckPointNum(50);
//        tim.getCheckPoint();


//        TopK topK = new TopK();
//        topK.setCheckPointNum(50);
//        topK.setStationHashMap(initialization.getStationHashMap());

//        diffusionSimulator.setCandidate(topK.getCheckPoint());
//
//        diffusionSimulator.setInfNum(20);
//        diffusionSimulator.startDiffusion();
    }

    public static void systemInfTest() {
        String OS = System.getProperty("os.name").toLowerCase();
        System.out.print("System Type - ");
        if (isWindows(OS)) {
            System.out.println("Windows CORE");
            Setting.system = 0;
        } else if (isMac(OS)) {
            System.out.println("MAC OS CORE");
            Setting.system = 1;
        } else if (isUnix(OS)) {
            System.out.println("LINUX CORE");
            Setting.system = 2;
        }

    }

    public static boolean isWindows(String name) {
        return name.contains("win");
    }

    public static boolean isMac(String name) {
        return name.contains("mac");
    }

    public static boolean isUnix(String name) {
        return (name.contains("nix") || name.contains("nux") || name.contains("aix"));
    }

}
