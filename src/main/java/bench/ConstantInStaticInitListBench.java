package bench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Benchmark                                Mode  Cnt  Score   Error  Units
// ConstantInStaticInitListBench.arrayList  avgt    5  0,726 ± 0,006  ns/op
// ConstantInStaticInitListBench.list_of    avgt    5  0,309 ± 0,009  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
/*@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "--enable-preview" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ConstantInStaticInitListBench {
  private static final List<String> LIST_OF;
  private static final List<String> ARRAYLIST;

  static {
    LIST_OF = List.of("John");
    var arrayList = new ArrayList<String>();
    arrayList.add("John");
    ARRAYLIST = arrayList;
  }

  @Benchmark
  public String list_of() {
    return LIST_OF.getFirst();
  }

  @Benchmark
  public String arrayList() {
    return ARRAYLIST.getFirst();
  }
}
*/
