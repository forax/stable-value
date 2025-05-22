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

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// Benchmark                                     Mode  Cnt  Score   Error  Units
// LazyStaticInitBench.lazy_class_string         avgt    5  0,312 ± 0,002  ns/op
// LazyStaticInitBench.lazy_dcl_string           avgt    5  0,728 ± 0,004  ns/op
// LazyStaticInitBench.lazy_synchronized_string  avgt    5  5,301 ± 0,022  ns/op
// LazyStaticInitBench.stable_supplier_string    avgt    5  0,313 ± 0,001  ns/op
// LazyStaticInitBench.stable_value_string       avgt    5  0,313 ± 0,001  ns/op
// LazyStaticInitBench.string                    avgt    5  0,313 ± 0,001  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
/*@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(
    value = 1,
    jvmArgs = {"--enable-preview"})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class LazyStaticInitBench {
  private static final String STRING = "Am i a constant ?";

  private static String LAZY_SYNC_STRING;
  private static final Object SYNC_LOCK = new Object();
  public static String getLazySyncString() {
    synchronized (SYNC_LOCK) {
      if (LAZY_SYNC_STRING != null) {
        return LAZY_SYNC_STRING;
      }
      return LAZY_SYNC_STRING = "Am i a constant ?";
    }
  }

  private static volatile String LAZY_DCL_STRING;
  private static final Object DCL_LOCK = new Object();
  public static String getLazyDCLString() {
    if (LAZY_DCL_STRING != null) {
      return LAZY_DCL_STRING;
    }
    synchronized (DCL_LOCK) {
      if (LAZY_DCL_STRING != null) {
        return LAZY_DCL_STRING;
      }
      return LAZY_DCL_STRING = "Am i a constant ?";
    }
  }

  public static String getLazyClassString() {
    enum Holder {
      ;
      private static final String STRING = "Am i a constant ?";
    }
    return Holder.STRING;
  }

  public static final Supplier<String> STABLE_SUPPLIER = StableValue.supplier(() -> "Am i a constant ?");
  public static String getStableSupplierString() {
    return STABLE_SUPPLIER.get();
  }

  public static final StableValue<String> STABLE_VALUE = StableValue.of();
  public static String getStableValueString() {
    return STABLE_VALUE.orElseSet(() -> "Am i a constant ?");
  }

  @Benchmark
  public String string() {
    return STRING;
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
  public String lazy_class_string() {
    return getLazyClassString();
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