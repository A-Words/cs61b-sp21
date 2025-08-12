package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    public static void timeGetLast() {
        int START_N = 1000;
        int END_N = 128000;
        int M = 1000;

        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        for (int n = START_N; n <= END_N; n *= 2) {
            Ns.addLast(n);
            opCounts.addLast(M);

            // 创建 SLList 并添加 n 个元素
            SLList<Integer> testList = new SLList<>();
            for (int i = 0; i < n; i += 1) {
                testList.addLast(i);
            }

            // 计算执行 M 次 getLast 所需时间
            Stopwatch sw = new Stopwatch();
            for (int m = 0; m < M; m += 1) {
                testList.getLast();
            }
            double timeInSeconds = sw.elapsedTime();

            times.addLast(timeInSeconds);
        }

        System.out.printf("Timing table for getLast\n");
        printTimingTable(Ns, times, opCounts);
    }

}
