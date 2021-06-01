package com.company;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        //int threadCount = scanner.nextInt();

        //default values
        int nMembers = 2;
        int threadCount=1;
        String fileName="result.txt";
        BigDecimal e = BigDecimal.ZERO;

        //Start time
        long startTime = System.currentTimeMillis();

        //Reading values from the command prompt
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-p")) {
				nMembers = new Integer(args[i + 1]);
				}
			if (args[i].equals("-t") || args[i].equals("-task")) {
				threadCount = new Integer(args[i + 1]);
		    }
			if (args[i].equals("-o")) {
				fileName = args[i + 1];
			}
		}

        System.out.println("Parameteres called: p=" + nMembers + ", t=" + threadCount + ", filename:" + fileName);

        //Create array of threads
        EThread[] threads = new EThread[threadCount];

        for (int i = 0; i < threadCount; ++i) {
            System.out.println("Thread " + (i+1) + "started.");
            threads[i] = new EThread(threadCount, i, nMembers/threadCount, nMembers); 
            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            System.out.println("Thread " + (i + 1) + "finished.");
            threads[i].join();
        }

        for (int i = 0; i < threadCount; i++) {
            e = e.add(threads[i].getSum());
        }
        System.out.println("e = " + e);

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println("Total execution time for current run (millis): " + duration + " ms");
    }
}

class EThread extends Thread {
    private final int threadCount;
    private final int threadRemainder;
    private final int nMembers;
    private final int N;
    private BigDecimal sum = BigDecimal.ZERO;
    private static BigDecimal[] factorialArray;

    public EThread(int threadCount, int threadRemainder, int n, int nMem) {
        this.threadCount = threadCount;
        this.threadRemainder = threadRemainder;
        N = n;
        nMembers=nMem;
        factorialArray = new BigDecimal[1200000];
    }

    @Override
    public synchronized void run() {
        for (int i = threadRemainder; i <= nMembers; i += threadCount) {
            BigDecimal nom = BigDecimal.valueOf(2 * i + 1);
            BigDecimal denom = factorial(BigDecimal.valueOf(2 * i));
            sum = sum.add(nom.divide(denom, nMembers, RoundingMode.CEILING));
        }
    }
    public synchronized BigDecimal getSum() {
        return sum;
    }
    static synchronized BigDecimal factorial(BigDecimal n) {
        factorialArray[0] = BigDecimal.ONE;
        if (n.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ONE;
        if (factorialArray[n.intValue()] != null) 
            return factorialArray[n.intValue()];
        else {
            factorialArray[n.intValue()] = n.multiply(factorial(n.subtract(BigDecimal.ONE)));
        }

        return factorialArray[n.intValue()];
    }
}