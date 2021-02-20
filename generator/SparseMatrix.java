package generator;

import fileIO.MyFileWriter;

public class SparseMatrix {

    double[][] matrix;

    public void generator(int n) {
        double value;
        matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                value = Math.random();
                if (value < 0.05)
                    matrix[i][j] = value * 1000.0;
                else
                    matrix[i][j] = 0.0;
            }
        }
    }

    public void write() {
        MyFileWriter myFileWriter = new MyFileWriter("testFile.txt");
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            System.out.println(i);
            for (int j = 0; j < n; j++) {
                myFileWriter.writeToFile((i+1) + " " + (j+1) + " " + matrix[i][j] + "\r\n");
            }
        }
        myFileWriter.close();
    }
}
