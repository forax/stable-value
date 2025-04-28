package stable;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class StableValueTest {

  @Nested
  public class SupplierTests {

    @Test
    public void supplierComputesValueOnlyOnce() {
      var counter = new AtomicInteger();
      var stableSupplier = StableValue.supplier(counter::incrementAndGet);

      assertEquals(1, stableSupplier.get());
      assertEquals(1, stableSupplier.get());
      assertEquals(1, stableSupplier.get());
      assertEquals(1, counter.get());
    }

    @Test
    public void supplierThrowsExceptionForCyclicDefinition() {
      var cyclicSupplier = new ArrayList<Supplier<Integer>>();
      cyclicSupplier.add(StableValue.supplier(() -> cyclicSupplier.getFirst().get() + 1));

      assertThrows(IllegalStateException.class, () -> cyclicSupplier.getFirst().get());
    }

    @Test
    public void supplierRelaysExceptions() {
      var expected = new RuntimeException("Expected exception");
      var stableSupplier = StableValue.supplier(() -> {
        throw expected;
      });

      var actual = assertThrows(RuntimeException.class, stableSupplier::get);
      assertSame(expected, actual);
    }

    @Test @Disabled
    public void supplierRejectsNullValues() {
      var stableSupplier = StableValue.supplier(() -> null);

      assertThrows(NullPointerException.class, stableSupplier::get);
    }

    @Test
    public void supplierIsThreadSafe() {
      var threadCount = 10;
      var counter = new AtomicInteger();
      var startLatch = new CountDownLatch(1);
      var doneLatch = new CountDownLatch(threadCount);

      var stableSupplier = StableValue.supplier(() -> {
        try {
          Thread.sleep(100); // Simulate work
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
        return counter.incrementAndGet();
      });

      var results = ConcurrentHashMap.<Integer>newKeySet();

      try(var executor = Executors.newFixedThreadPool(threadCount)) {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
          for (var i = 0; i < threadCount; i++) {
            executor.submit(() -> {
              try {
                startLatch.await(); // Wait for all threads to be ready
                var result = stableSupplier.get();
                results.add(result);
              } catch (InterruptedException e) {
                throw new AssertionError(e);
              } finally {
                doneLatch.countDown();
              }
            });
          }

          startLatch.countDown(); // Start all threads
          doneLatch.await(); // Wait for all threads to complete
        });
      }

      assertAll(
          () -> assertEquals(1, counter.get()),
          () -> assertEquals(1, results.size()),
          () -> assertEquals(1, results.iterator().next())
      );
    }
  }


  @Nested
  public class ListTests {

    @Test
    public void listComputesValuesLazily() {
      var computationCount = new AtomicInteger();
      var list = StableValue.list(10, index -> {
        computationCount.incrementAndGet();
        return index * 2;
      });

      assertAll(
          () -> assertEquals(10, list.size()),
          () -> assertEquals(0, computationCount.get()) // No elements accessed yet
      );

      // Access some elements
      assertEquals(0, list.get(0));
      assertEquals(6, list.get(3));
      assertEquals(2, computationCount.get()); // Only accessed elements computed
    }

    @Test
    public void listComputesElementsOnlyOnce() {
      var computationCounts = IntStream.range(0, 5)
          .boxed()
          .collect(Collectors.toMap(i -> i, _ -> new AtomicInteger()));

      var list = StableValue.list(5, index -> {
        computationCounts.get(index).incrementAndGet();
        return index * 3;
      });

      // Access all elements multiple times
      for (var i = 0; i < 3; i++) {
        for (var j = 0; j < 5; j++) {
          assertEquals(j * 3, list.get(j));
        }
      }

      // Verify each element was computed exactly once
      for(var count : computationCounts.values()) {
        assertEquals(1, count.get());
      }
    }

    @Test
    public void listThrowsExceptionForNegativeSize() {
      assertThrows(IllegalArgumentException.class, () -> StableValue.list(-1, i -> i));
    }

    @Test
    public void listThrowsExceptionForOutOfBounds() {
      var list = StableValue.list(5, i -> i);

      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> list.get(5)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1))
      );
    }

    @Test
    public void listThrowsExceptionForCyclicDefinition() {
      var cyclicList = new Object() {
        final List<Integer> list = StableValue.list(5, i -> {
          if (i == 3) {
            return this.list.get(3) + 1; // Cyclic definition
          }
          return i;
        });
      }.list;

      assertThrows(IllegalStateException.class, () -> cyclicList.get(3));
    }

    @Test
    public void listSupportsConversionToArray() {
      var list = StableValue.list(3, i -> "Item " + i);

      var array = list.toArray();
      var stringArray = list.toArray(new String[0]);
      var generatedArray = list.toArray(String[]::new);

      assertAll(
          () -> assertEquals(3, array.length),
          () -> assertEquals(3, stringArray.length),
          () -> assertEquals(3, generatedArray.length),
          () -> assertEquals("Item 0", array[0]),
          () -> assertEquals("Item 1", stringArray[1]),
          () -> assertEquals("Item 2", generatedArray[2])
      );
    }

    @Test
    public void listIsUnmodifiable() {
      var list = StableValue.list(5, i -> i);

      assertAll(
          () -> assertThrows(UnsupportedOperationException.class, () -> list.add(5)),
          () -> assertThrows(UnsupportedOperationException.class, () -> list.remove(1)),
          () -> assertThrows(UnsupportedOperationException.class, list::clear),
          () -> assertThrows(UnsupportedOperationException.class, () -> list.set(0, 100))
      );
    }

    @Test
    public void listIsThreadSafe() {
      var size = 5;
      var threadCount = 10;
      var computationCounts = IntStream.range(0, size)
          .boxed()
          .collect(Collectors.toMap(i -> i, _ -> new AtomicInteger()));

      var startLatch = new CountDownLatch(1);
      var doneLatch = new CountDownLatch(threadCount);

      var list = StableValue.list(size, index -> {
        try {
          Thread.sleep(100); // Simulate work
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
        return computationCounts.get(index).incrementAndGet();
      });

      assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
        try(var executor = Executors.newFixedThreadPool(threadCount)) {

          for (var i = 0; i < threadCount; i++) {
            var threadIndex = i % size; // Each thread focuses on one element
            executor.submit(() -> {
              try {
                startLatch.await(); // Wait for all threads to be ready
                var _ = list.get(threadIndex);
              } catch (InterruptedException e) {
                throw new AssertionError(e);
              } finally {
                doneLatch.countDown();
              }
            });
          }

          startLatch.countDown(); // Start all threads
          doneLatch.await(); // Wait for all threads to complete
        }
      });

      for(var count : computationCounts.values()) {
        assertEquals(1, count.get());
      }
    }
  }


  @Nested
  public class MapTests {

    @Test
    public void mapComputesValuesLazily() {
      var keys = Set.of("apple", "banana", "cherry");
      var computationCount = new AtomicInteger();

      var map = StableValue.map(keys, key -> {
        computationCount.incrementAndGet();
        return key.length();
      });

      assertAll(
          () -> assertEquals(3, map.size()),
          () -> assertEquals(0, computationCount.get()) // No elements accessed yet
      );

      // Access some elements
      assertEquals(5, map.get("apple"));
      assertEquals(1, computationCount.get()); // Only accessed elements computed

      assertEquals(6, map.get("banana"));
      assertEquals(2, computationCount.get());
    }

    @Test
    public void mapComputesValuesOnlyOnce() {
      var keys = Set.of("a", "b", "c");
      var computationCounts = keys.stream()
          .collect(Collectors.toMap(k -> k, _ -> new AtomicInteger()));

      var map = StableValue.map(keys, key -> {
        computationCounts.get(key).incrementAndGet();
        return key.hashCode();
      });

      // Access all elements multiple times
      for (var i = 0; i < 3; i++) {
        for (var key : keys) {
          assertEquals(key.hashCode(), map.get(key));
        }
      }

      for(var counts : computationCounts.values()) {
        assertEquals(1, counts.get());
      }
    }

    @Test
    public void mapThrowsExceptionForCyclicDefinition() {
      var keys = Set.of("a", "b", "c");
      var cyclicMap = new Object() {
        final Map<String, Integer> map = StableValue.map(keys,key -> {
          if (key.equals("b")) {
            return this.map.get("b") + 1; // Cyclic definition
          }
          return key.hashCode();
        });
      }.map;

      assertThrows(IllegalStateException.class, () -> cyclicMap.get("b"));
    }

    @Test
    public void mapContainsExpectedKeys() {
      var keys = Set.of("a", "b", "c");
      var map = StableValue.map(keys, String::length);

      assertAll(
          () -> assertTrue(map.containsKey("a")),
          () -> assertTrue(map.containsKey("b")),
          () -> assertTrue(map.containsKey("c")),
          () -> assertFalse(map.containsKey("d")),
          () -> assertEquals(Set.of("a", "b", "c"), map.keySet())
      );
    }

    @Test
    public void mapReturnsNullForNonExistentKeys() {
      var keys = Set.of("a", "b", "c");
      var map = StableValue.map(keys, String::length);

      assertNull(map.get("d"));
    }

    @Test
    public void mapViewsWorkCorrectly() {
      var keys = Set.of("a", "bb", "ccc");
      var map = StableValue.map(keys, String::length);

      assertAll(
          () -> assertEquals(Set.of(1, 2, 3), Set.copyOf(map.values())),
          () -> assertEquals(3, map.entrySet().size()),
          () -> assertTrue(map.entrySet().stream()
              .allMatch(entry ->
                  keys.contains(entry.getKey()) && entry.getValue() == entry.getKey().length()))
      );
    }

    @Test
    public void mapIsUnmodifiable() {
      var keys = Set.of("a", "b", "c");
      var map = StableValue.map(keys, String::length);

      assertAll(
          () -> assertThrows(UnsupportedOperationException.class, () -> map.put("d", 1)),
          () -> assertThrows(UnsupportedOperationException.class, () -> map.remove("a")),
          () -> assertThrows(UnsupportedOperationException.class, map::clear)
      );
    }

    @Test
    public void mapIsThreadSafe() {
      var keys = Set.of("a", "b", "c", "d", "e");
      var computationCounts = keys.stream()
          .collect(Collectors.toMap(k -> k, _ -> new AtomicInteger()));

      var threadCount = 10;
      var startLatch = new CountDownLatch(1);
      var doneLatch = new CountDownLatch(threadCount);

      var map = StableValue.map(keys, key -> {
        try {
          Thread.sleep(100); // Simulate work
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
        return computationCounts.get(key).incrementAndGet();
      });
      assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
        try(var executor = Executors.newFixedThreadPool(threadCount)) {
          var keysArray = keys.toArray(new String[0]);

          for (var i = 0; i < threadCount; i++) {
            var threadKey = keysArray[i % keys.size()]; // Each thread focuses on one key
            executor.submit(() -> {
              try {
                startLatch.await(); // Wait for all threads to be ready
                map.get(threadKey);
              } catch (InterruptedException e) {
                throw new AssertionError(e);
              } finally {
                doneLatch.countDown();
              }
            });
          }

          startLatch.countDown(); // Start all threads
          doneLatch.await(); // Wait for all threads to complete
        }
      });

      for (var count : computationCounts.values()) {
        assertEquals(1, count.get());
      }
    }
  }
}