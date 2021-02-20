package fileIO;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by marco on 29/03/2017.
 */
public class MyFileWriter {

    private Writer writer;
    private String outputFilePath;
    private String fileName;
    private final String separator;

    public MyFileWriter(String fileName) {
        separator = File.separator;
        this.fileName = fileName;
        checkDirector();
        checkFileExistes();
        setUpWriter();
    }

    private void checkDirector() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd");
            Date date = new Date();

            outputFilePath = new File(".").getCanonicalPath() + separator + "Experiment " + sdf.format(date);

            File directory = new File(outputFilePath);
            if (!directory.exists()) {
                directory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }
        } catch (Exception e) {
            System.out.println("Cannot create director!");
        }

    }

    private void checkFileExistes() {
        try {
            int fileIndex = 0;
            File tempFile = new File(outputFilePath + separator + fileName + " " + fileIndex + ".txt");
            while (tempFile.exists()) {
                fileIndex++;
                tempFile = new File(outputFilePath + separator + fileName + " " + fileIndex + ".txt");
            }
            outputFilePath += separator + fileName + " " + fileIndex + ".txt";
        } catch (Exception e) {
            System.out.println("Cannot get the root path!");
        }

    }

    private void setUpWriter() {
        try {
            File file = new File(outputFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
            writer = new BufferedWriter(outputStreamWriter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeToFile(String content) {

        try {
            writer.write(content);
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {

        try {
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
