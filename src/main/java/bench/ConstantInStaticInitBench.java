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

// Benchmark                               Mode  Cnt  Score   Error  Units
// ConstantInStaticInitBench.magic         avgt    5  0,309 ± 0,010  ns/op
// ConstantInStaticInitBench.magic_block   avgt    5  0,309 ± 0,005  ns/op
// ConstantInStaticInitBench.string        avgt    5  0,309 ± 0,004  ns/op
// ConstantInStaticInitBench.string_block  avgt    5  0,310 ± 0,004  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
/*@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "--enable-preview" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ConstantInStaticInitBench {
  private static final int MAGIC = 42;
  private static final String STRING = "Am i a constant ?";

  private static final int MAGIC_BLOCK;
  private static final String STRING_BLOCK;

  static {
    MAGIC_BLOCK = 40 + 2;
    STRING_BLOCK = "Am i a constant ?";
  }

  @Benchmark
  public int magic() {
    return MAGIC;
  }

  @Benchmark
  public int magic_block() {
    return MAGIC_BLOCK;
  }

  @Benchmark
  public String string() {
    return STRING;
  }

  @Benchmark
  public String string_block() {
    return STRING_BLOCK;
  }
}
*/
