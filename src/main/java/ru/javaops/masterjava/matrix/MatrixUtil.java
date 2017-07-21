package ru.javaops.masterjava.matrix;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static class IndexedMatrix {
        private Pair<Integer, Integer> index;
        private int[][] result;

        public IndexedMatrix(Pair<Integer, Integer> index, int[][] result) {
            this.index = index;
            this.result = result;
        }

        public int[][] getResult() {
            return result;
        }

        public void setResult(int[][] result) {
            this.result = result;
        }
    }

    public static class IndexedResult {
        private int index;
        private int[] result;

        public IndexedResult(int index, int[] result) {
            this.index = index;
            this.result = result;
        }

        public int[] getResult() {
            return result;
        }

        public void setResult(int[] result) {
            this.result = result;
        }
    }

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final ExecutorCompletionService<IndexedResult> completionServiceResult = new ExecutorCompletionService<>(executor);
        List<Future<IndexedResult>> listR = new ArrayList<>();

        /*int[][] BT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                BT[i][j] = matrixB[j][i];
            }
        }*/

        for (int j = 0; j < matrixSize; j++) {
            final int id = j;
            listR.add(completionServiceResult.submit(() -> doMatrixMultiply(id, matrixA, matrixB)));
        }

        while (!listR.isEmpty()) {
            Future<IndexedResult> matrixFuture = completionServiceResult.poll(1, TimeUnit.SECONDS);
            listR.remove(matrixFuture);
            IndexedResult rMatrix = matrixFuture.get();

            matrixC[rMatrix.index] = rMatrix.result;
        }
        int[][] CT = transponMatrix(matrixC);
        return CT;
    }

    private static int[][] transponMatrix(int[][] matrixC) {
        int matrixSize = matrixC.length;
        int[][] CT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                CT[i][j] = matrixC[j][i];
            }
        }
        return CT;
    }

    private static IndexedResult doMatrixMultiply(int j, int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[] matrixCR = new int[matrixSize];
        final int thatColumn[] = new int[matrixSize];

        for (int k = 0; k < matrixSize; k++) {
            thatColumn[k] = matrixB[k][j];
        }

        for (int i = 0; i < matrixSize; i++) {
            int sum = 0;
            int thisCRow[] = matrixA[i];

            for (int k = 0; k < matrixSize; k++) {
                sum += thisCRow[k] * thatColumn[k];
            }
            matrixCR[i] = sum;
        }

        return new IndexedResult(j, matrixCR);
    }


    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int thatColumn[] = new int[matrixSize];

        for (int j = 0; j < matrixSize; j++) {

            for (int k = 0; k < matrixSize; k++) {
                thatColumn[k] = matrixB[k][j];
            }

            for (int i = 0; i < matrixSize; i++) {
                int sum = 0;
                int thisCRow[] = matrixA[i];

                for (int k = 0; k < matrixSize; k++) {
                    sum += thisCRow[k] * thatColumn[k];
                }
                matrixC[i][j] = sum;
            }
        }

        /*for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }*/

        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
