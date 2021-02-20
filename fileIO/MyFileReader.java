package fileIO;

import Setting.Setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MyFileReader {

    private BufferedReader bufferedReader;

    public MyFileReader(String inputFilePath) {
        try {
            String path = "";
            String separator = File.separator;

            switch (Setting.system) {
                case 0:
                    path = System.getProperty("user.dir")+ separator + "src" + separator + "Data";
                    break;
                case 1:
                    path = System.getProperty("user.dir") ;
                    break;
                case 2:
                    path = System.getProperty("user.dir") ;
                    break;
                default:
                    System.out.println("File reading fault!");
                    throw new IllegalStateException("Undetected system type " + Setting.system);
            }

            path += separator + inputFilePath;

//            if (Setting.system = 0)
//                String path = separator + "src" + separator + "Data" + separator + "TripRecord.csv";
//            else if (Setting)
//                String path = System.getProperty("user.dir") + inputFilePath;
            bufferedReader = new BufferedReader(new FileReader(path));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNextLine() {
        try {
            String line = bufferedReader.readLine();
            if (line != null) {
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
