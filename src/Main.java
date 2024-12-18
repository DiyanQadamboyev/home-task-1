import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //Oddiy usul:
        long boshlanganVaqt=System.currentTimeMillis();
        long oddiyQoshish = calculateSimpleSum(1, 1_000_000);
        long tugaganVaqt=System.currentTimeMillis();
        long qanchaVaqtKetgani=tugaganVaqt-boshlanganVaqt;
        System.out.println("Oddiy usulda yigindisi="+oddiyQoshish);
        System.out.println("Oddiy usulda yigindi uchun ketgan vaqt: "+qanchaVaqtKetgani+" millisekund");
        System.out.println("----------------------------------------------------------------------");
        // Thread orqali yechim

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Long>> futures = new ArrayList<>();

        int range = 1_000_000 / 5;
        boshlanganVaqt = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            int start = i * range + 1;
            int end = (i + 1) * range;
            futures.add(executor.submit(new SumTask(start, end)));
        }

        long parallelSum = 0;
        for (Future<Long> future : futures) {
            parallelSum += future.get();
        }

        tugaganVaqt = System.currentTimeMillis();
        long threadUchunVaqt = tugaganVaqt-boshlanganVaqt;
        executor.shutdown();

        System.out.println("ExecutorServicedan foydalanilganda yig'indi: " + parallelSum);
        System.out.println("ExecutorServiceda yigindi uchun ketgan vaqti: " + threadUchunVaqt + " millisekund");

        long difference = qanchaVaqtKetgani - threadUchunVaqt;
        System.out.println("ExecutorService usuli oddiy usuldan " + difference + " millisekund tezroq ishladi.");
    }

    private static long calculateSimpleSum(int start, int end) {
        long sum = 0;
        for (int i = start; i <= end; i++) {
            sum += i;
        }
        return sum;
    }
    static class SumTask implements Callable<Long> {
        private final int start;
        private final int end;

        public SumTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Long call() {
            long sum = 0;
            for (int i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        }
    }
}