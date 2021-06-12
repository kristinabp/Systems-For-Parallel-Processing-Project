import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Scanner;
import java.util.Arrays;

class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        Scanner scanner = new Scanner(System.in);

        // default values
        int precision = 2;
        int numberOfThreads =1;
        BigDecimal e =BigDecimal.ZERO;
        BigInteger[] factorialArray;

        //Reading values from the command prompt
        for(int i=0; i<args.length; i++)
        {
            if(args[i].equals("-p"))
            {
                precision = Integer.parseInt(args[i+1]);
            }

            if(args[i].equals("-t"))
            {
                numberOfThreads= Integer.parseInt(args[i+1]);
            }
        }

        factorialArray=new BigInteger[precision + 2];
        Arrays.fill(factorialArray, BigInteger.ZERO);

        //start time
        long startTime=System.currentTimeMillis();

        //Create array of threads
        EThread[] threads = new EThread[numberOfThreads];

        for(int i=0; i<numberOfThreads; i++)
        {
            //System.out.println("Thread <" + (i+1) + "> started.");
            threads[i]=new EThread(i, numberOfThreads,precision, factorialArray);
            threads[i].start();
        }

        for(int i=0; i<numberOfThreads; i++)
        {
            System.out.println("Thread <" + (i+1) + "> finished.");
            threads[i].join();
        }

        for(int i=0; i<numberOfThreads; i++)
        {
            e=e.add(threads[i].getSum());
        }

        System.out.println("e= " + e);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Total execution time for current run (millis): " + duration + " ms");
    }
}

class EThread extends Thread
{
    private final int precision;
    private final int offset;
    private final int index;
    private BigDecimal sum = BigDecimal.ZERO;
    private static BigInteger[] factorialArray;

    public EThread(int start, int offset, int precision, BigInteger[] factorialArray)
    {
        this.index=start;
        this.offset=offset;
        this.precision=precision;
        EThread.factorialArray=factorialArray;
    }

    @Override
    public synchronized void run()
    {
        for(int i=index; i<=precision; i=i+offset)
        {
            sum=sum.add(getKthMember(2*i));
        }
    }

    private BigDecimal getKthMember(int k) {
        BigDecimal numerator = BigDecimal.valueOf(k).add(BigDecimal.ONE);
        BigDecimal denominator = new BigDecimal(getFactorial(k));

        return numerator.divide(denominator, precision, RoundingMode.CEILING);
    }

    private BigInteger getFactorial(int n) {
        int startPosition = 2;
        BigInteger result = BigInteger.ONE;

        int closestIndex = findClosestCalculatedFactorialIndex(n / 2);

        if (closestIndex != -1) {
            startPosition = 2*closestIndex + 1;
            result = factorialArray[closestIndex];
        }

        for (int i = startPosition; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }

        factorialArray[n/2] = result;
        return result;
    }

    private int findClosestCalculatedFactorialIndex(int n) {
        int left = 0, right = n - 1, mid = 0;
        while (right - left > 1) {
            mid = (left + right) / 2;
            if (factorialArray[mid].equals(BigInteger.ZERO) && factorialArray[mid - 1].equals(BigInteger.ZERO) && factorialArray[mid + 1].equals(BigInteger.ZERO)) {
                right = mid;
            } else {
                left = mid;
            }
        }
        while (mid >= 0 && factorialArray[mid].equals(BigInteger.ZERO)) {
            mid--;
        }
        return mid;
    }

    public synchronized BigDecimal getSum()
    {
        return sum;
    }
}