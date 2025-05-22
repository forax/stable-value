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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

// Benchmark                    Mode  Cnt     Score   Error  Units
// StableListBench.array        avgt    5   427,889 ± 1,553  ns/op
// StableListBench.arraylist    avgt    5   605,542 ± 1,372  ns/op
// StableListBench.asList       avgt    5   572,532 ± 2,574  ns/op
// StableListBench.list_of      avgt    5   615,069 ± 0,341  ns/op
// StableListBench.stable_list  avgt    5  1897,948 ± 2,119  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
/*@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(
    value = 1,
    jvmArgs = {"--enable-preview"})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class StableListBench {
  private String[] array = IntStream.range(0, 1_024).mapToObj(i -> "" + i).toArray(String[]::new);
  private List<String> listof = List.of(array);
  private List<String> aslist = Arrays.asList(array);
  private List<String> arraylist = new ArrayList<>(listof);
  private List<String> stableList = StableValue.list(1_024, i -> "" + i);

  @Benchmark
  public int array() {
    var sum = 0;
    for(var item : array) {
      sum += item.length();
    }
    return sum;
  }

  @Benchmark
  public int list_of() {
    var sum = 0;
    for(var item : listof) {
      sum += item.length();
    }
    return sum;
  }

  @Benchmark
  public int asList() {
    var sum = 0;
    for(var item : aslist) {
      sum += item.length();
    }
    return sum;
  }

  @Benchmark
  public int arraylist() {
    var sum = 0;
    for(var item : arraylist) {
      sum += item.length();
    }
    return sum;
  }

  @Benchmark
  public int stable_list() {
    var sum = 0;
    for(var item : stableList) {
      sum += item.length();
    }
    return sum;
  }
}
*/
