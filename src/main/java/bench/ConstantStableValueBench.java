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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// Benchmark                                        Mode  Cnt  Score   Error  Units
// ConstantStableValueBench.stable_supplier_string  avgt    5  0,317 ± 0,007  ns/op
// ConstantStableValueBench.stable_value_list       avgt    5  0,313 ± 0,001  ns/op
// ConstantStableValueBench.stable_value_map        avgt    5  0,312 ± 0,001  ns/op
// ConstantStableValueBench.stable_value_string     avgt    5  0,313 ± 0,001  ns/op
// ConstantStableValueBench.string                  avgt    5  0,313 ± 0,001  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(
    value = 1,
    jvmArgs = {"--enable-preview"})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ConstantStableValueBench {
  private static final String STRING = "Am i a constant ?";

  public static final Supplier<String> STABLE_SUPPLIER = StableValue.supplier(() -> "Am i a constant ?");
  public static String getStableSupplierString() {
    return STABLE_SUPPLIER.get();
  }

  public static final StableValue<String> STABLE_VALUE = StableValue.of();
  public static String getStableValueString() {
    return STABLE_VALUE.orElseSet(() -> "Am i a constant ?");
  }

  public static final List<String> STABLE_LIST = StableValue.list(10, i -> "Am i a constant " + i + " ?");

  public static final Map<String, String> STABLE_MAP = StableValue.map(Set.of("foo", "bar"), key -> "Am i a constant " + key + " ?");

  @Benchmark
  public String string() {
    return STRING;
  }

  @Benchmark
  public String stable_supplier_string() {
    return getStableSupplierString();
  }

  @Benchmark
  public String stable_value_string() {
    return getStableValueString();
  }

  @Benchmark
  public String stable_value_list() {
    return STABLE_LIST.getFirst();
  }

  @Benchmark
  public String stable_value_map() {
    return STABLE_MAP.get("foo");
  }
}
