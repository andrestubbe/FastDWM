package fastdwm.benchmark;

import fastdwm.FastDWM;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Benchmark {

    @org.openjdk.jmh.annotations.Benchmark
    public boolean benchmarkBeginAndEndTimerPeriod() {
        boolean ok = FastDWM.beginTimerPeriod(1);
        FastDWM.endTimerPeriod(1);
        return ok;
    }
}
