package bench;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

// Benchmark                               Mode  Cnt  Score   Error  Units
// LazyInitBench.lazy_dcl_string           avgt    5  0,731 ± 0,009  ns/op
// LazyInitBench.lazy_synchronized_string  avgt    5  5,413 ± 0,168  ns/op
// LazyInitBench.stable_supplier_string    avgt    5  0,894 ± 0,003  ns/op
// LazyInitBench.stable_value_string       avgt    5  0,829 ± 0,003  ns/op
// LazyInitBench.string                    avgt    5  0,313 ± 0,001  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
/*@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(
    value = 1,
    jvmArgs = {"--enable-preview"})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class LazyInitBench {
  private final String string = "hello ?";

  private String lazySyncString;
  private final Object syncLock = new Object();
  public String getLazySyncString() {
    synchronized (syncLock) {
      if (lazySyncString != null) {
        return lazySyncString;
      }
      return lazySyncString = "hello ?";
    }
  }

  private volatile String lazyDCLString;
  private final Object DCL_LOCK = new Object();
  public String getLazyDCLString() {
    if (lazyDCLString != null) {
      return lazyDCLString;
    }
    synchronized (DCL_LOCK) {
      if (lazyDCLString != null) {
        return lazyDCLString;
      }
      return lazyDCLString = "hello ?";
    }
  }

  public final Supplier<String> stableSupplier = StableValue.supplier(() -> "hello ?");
  public String getStableSupplierString() {
    return stableSupplier.get();
  }

  public final StableValue<String> stableValue = StableValue.of();
  public String getStableValueString() {
    return stableValue.orElseSet(() -> "hello ?");
  }

  @Benchmark
  public String string() {
    return string;
  }

  @Benchmark
  public String lazy_synchronized_string() {
    return getLazySyncString();
  }

  @Benchmark
  public String lazy_dcl_string() {
    return getLazyDCLString();
  }

  @Benchmark
  public String stable_supplier_string() {
    return getStableSupplierString();
  }

  @Benchmark
  public String stable_value_string() {
    return getStableValueString();
  }
}
*/