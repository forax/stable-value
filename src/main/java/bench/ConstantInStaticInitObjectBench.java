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

// Benchmark                                      Mode  Cnt  Score   Error  Units
// ConstantInStaticInitObjectBench.person         avgt    5  0,423 ± 0,124  ns/op
// ConstantInStaticInitObjectBench.person_record  avgt    5  0,307 ± 0,005  ns/op

// $JAVA_HOME/bin/java -jar target/benchmarks.jar -prof dtraceasm
/*@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "--enable-preview" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ConstantInStaticInitObjectBench {
  static final class Person {
    private final String name;

    Person(String name) {
      this.name = name;
    }
  }

  record PersonRecord(String name) { }

  private static final Person PERSON = new Person("John");
  private static final PersonRecord PERSON_RECORD = new PersonRecord("John");

  @Benchmark
  public String person() {
    return PERSON.name;
  }

  @Benchmark
  public String person_record() {
    return PERSON_RECORD.name;
  }
}
*/
