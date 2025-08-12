package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        int START_N = 1000;
        int END_N = 128000;

        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        for (int n = START_N; n <= END_N; n *= 2) {
            Ns.addLast(n);
            opCounts.addLast(n);

            // 创建新的测试列表，并监测写入到达 n 所使用时间
            AList<Integer> testList = new AList<>();
            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < n; i += 1) {
                testList.addLast(i);
            }
            double timeInSeconds = sw.elapsedTime();

            times.addLast(timeInSeconds);
        }

        System.out.printf("Timing table for addLast\n");
        printTimingTable(Ns, times, opCounts);
    }
}
