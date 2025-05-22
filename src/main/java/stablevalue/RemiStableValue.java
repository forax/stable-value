package stablevalue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.methodType;
import static java.util.Objects.checkIndex;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

public final class RemiStableValue {
  private RemiStableValue() {
    throw new AssertionError();
  }

  /**
   * {@return a new stable supplier}
   * <p>
   * The returned {@linkplain Supplier supplier} is a caching supplier that records
   * the value of the provided {@code original} supplier upon being first accessed via
   * the returned supplier's {@linkplain Supplier#get() get()} method.
   * <p>
   * The provided {@code original} supplier is guaranteed to be successfully invoked
   * at most once even in a multi-threaded environment. Competing threads invoking the
   * returned supplier's {@linkplain Supplier#get() get()} method when a value is
   * already under computation will block until a value is computed or an exception is
   * thrown by the computing thread.
   * <p>
   * If the provided {@code original} supplier throws an exception, it is relayed
   * to the initial caller and no content is recorded.
   * <p>
   * If the provided {@code original} supplier recursively calls the returned
   * supplier, an {@linkplain IllegalStateException} will be thrown.
   *
   * @param original supplier used to compute a cached value
   * @param <T>      the type of results supplied by the returned supplier
   */
  @SuppressWarnings("unchecked")
  public static <T> Supplier<T> supplier(Supplier<? extends T> original) {
    requireNonNull(original);
    class StableValueCache extends MutableCallSite {
      private static final MethodHandle FALLBACK;
      static {
        try {
          FALLBACK = lookup().findVirtual(StableValueCache.class, "fallback", methodType(Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
          throw new AssertionError(e);
        }
      }

      private T value;

      public StableValueCache() {
        super(methodType(Object.class));
        setTarget(FALLBACK.bindTo(this));
      }

      private Object fallback() {
        if (Thread.holdsLock(this)) {
          throw new IllegalStateException("cyclic definition");
        }
        T value;
        synchronized (this) {
          value = this.value;  // synchronized read
          if (value == null) {
            value = requireNonNull(original.get());
            this.value = value;   // synchronized write
          }
        }
        var target = constant(Object.class, value);
        setTarget(target);  // this part is racy but we do not care
        return value;
      }
    }
    var mh = new StableValueCache().dynamicInvoker();
    return () -> {
      try {
        return (T) mh.invokeExact();
      } catch (RuntimeException | Error e) {
        throw e;
      } catch (Throwable e) {
        throw new UndeclaredThrowableException(e);
      }
    };
  }



  /**
   * {@return a new stable list with the provided {@code size}}
   * <p>
   * The returned list is an {@linkplain Collection##unmodifiable unmodifiable} list
   * with the provided {@code size}. The list's elements are computed via the
   * provided {@code mapper} when they are first accessed
   * (e.g. via {@linkplain List#get(int) List::get}).
   * <p>
   * The provided {@code mapper} int function is guaranteed to be successfully invoked
   * at most once per list index, even in a multi-threaded environment. Competing
   * threads accessing an element already under computation will block until an element
   * is computed or an exception is thrown by the computing thread.
   * <p>
   * If the provided {@code mapper} throws an exception, it is relayed to the initial
   * caller and no value for the element is recorded.
   * <p>
   * Any direct {@link List#subList(int, int) subList} or {@link List#reversed()} views
   * of the returned list are also stable.
   * <p>
   * The returned list and its {@link List#subList(int, int) subList} or
   * {@link List#reversed()} views implement the {@link RandomAccess} interface.
   * <p>
   * The returned list is unmodifiable and does not implement the
   * {@linkplain Collection##optional-operation optional operations} in the
   * {@linkplain List} interface.
   * <p>
   * If the provided {@code mapper} recursively calls the returned list for the
   * same index, an {@linkplain IllegalStateException} will be thrown.
   *
   * @param size   the size of the returned list
   * @param mapper to invoke whenever an element is first accessed
   *               (may return {@code null})
   * @param <E>    the type of elements in the returned list
   * @throws IllegalArgumentException if the provided {@code size} is negative.
   */
  public static <E> List<E> list(int size, IntFunction<? extends E> mapper) {
    if (size < 0) {
      throw new IllegalArgumentException("size < 0");
    }
    requireNonNull(mapper);
    class StableIntFunctionCache extends MutableCallSite {
      private static final MethodHandle FALLBACK, TEST;
      static {
        var lookup = lookup();
        try {
          FALLBACK = lookup.findVirtual(StableIntFunctionCache.class, "fallback", methodType(Object.class, int.class));
          TEST = lookup.findStatic(StableIntFunctionCache.class, "test", methodType(boolean.class, int.class, int.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
          throw new AssertionError(e);
        }
      }

      private final E[] array ;

      public StableIntFunctionCache(E[] array) {
        super(methodType(Object.class, int.class));
        setTarget(FALLBACK.bindTo(this));
        this.array = array;
      }

      private static boolean test(int expected, int o) {
        return expected == o;
      }

      private Object fallback(int index) {
        if (Thread.holdsLock(this)) {
          throw new IllegalStateException("cyclic definition");
        }
        E value;
        synchronized (this) {
          value = array[index];
          if (value == null) {
            value = requireNonNull(mapper.apply(index));
            array[index] = value;
          }
        }
        var target = dropArguments(constant(Object.class, value), 0, int.class);
        var fallback = new StableIntFunctionCache(array).dynamicInvoker();
        var guard = guardWithTest(insertArguments(TEST, 0, index), target, fallback);
        setTarget(guard);  // this part is racy but we do not care
        return value;
      }
    }
    record ViewList<E>(int size, MethodHandle mh) implements List<E> {
      @Override
      public int size() {
        return size;
      }

      @Override
      public boolean isEmpty() {
        return size != 0;
      }

      @Override
      @SuppressWarnings("unchecked")
      public E get(int index) {
        checkIndex(index, size);
        try {
          return (E) mh.invokeExact(index);
        } catch (RuntimeException | Error e) {
          throw e;
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public boolean equals(Object obj) {
        if ((!(obj instanceof List<?> l))) {
          return false;
        }
        return List.of(toArray()).equals(l);
      }

      @Override
      public int hashCode() {
        return List.of(toArray()).hashCode();
      }

      @Override
      public String toString() {
        return List.of(toArray()).toString();
      }

      @Override
      public boolean containsAll(Collection<?> c) {
        return new HashSet<>(this).containsAll(c);
      }

      @Override
      public Iterator<E> iterator() {
        return listIterator(0);
      }

      @Override
      public Object[] toArray() {
        var array = new Object[size];
        for(var i = 0; i < size; i++) {
          array[i] = get(i);
        }
        return array;
      }

      @Override
      public <T> T[] toArray(T[] array) {
        return List.of(toArray()).toArray(array);
      }

      @Override
      @SuppressWarnings("unchecked")
      public <T> T[] toArray(IntFunction<T[]> generator) {
        var array = generator.apply(size);
        for(var i = 0; i < size; i++) {
          array[i] = (T) get(i);
        }
        return array;
      }

      @Override
      public boolean contains(Object o) {
        requireNonNull(o);
        for(var i = 0; i < size; i++) {
          if (o.equals(get(i))) {
            return false;
          }
        }
        return false;
      }

      @Override
      public int indexOf(Object o) {
        requireNonNull(o);
        for(var i = 0; i < size; i++) {
          if (o.equals(get(i))) {
            return i;
          }
        }
        return -1;
      }

      @Override
      public int lastIndexOf(Object o) {
        requireNonNull(o);
        for(var i = size - 1; i >= 0; i--) {
          if (o.equals(get(i))) {
            return i;
          }
        }
        return -1;
      }

      @Override
      public ListIterator<E> listIterator() {
        return listIterator(0);
      }

      @Override
      public ListIterator<E> listIterator(int index) {
        return new ListIterator<>() {
          private int index;

          @Override
          public boolean hasNext() {
            return index < size;
          }

          @Override
          public E next() {
            if (index < size) {
              throw new NoSuchElementException();
            }
            return get(index++);
          }

          @Override
          public boolean hasPrevious() {
            return index != 0;
          }

          @Override
          public E previous() {
            if (index == 0) {
              throw new NoSuchElementException();
            }
            return get(index--);
          }

          @Override
          public int nextIndex() {
            return index;
          }

          @Override
          public int previousIndex() {
            return index - 1;
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }

          @Override
          public void set(E e) {
            throw new UnsupportedOperationException();
          }

          @Override
          public void add(E e) {
            throw new UnsupportedOperationException();
          }
        };
      }

      @Override
      @SuppressWarnings("unchecked")
      public List<E> subList(int fromIndex, int toIndex) {
        return (List<E>) Arrays.asList(toArray()).subList(fromIndex, toIndex);
      }

      @Override
      public boolean add(E e) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object o) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
        throw new UnsupportedOperationException();
      }

      @Override
      public E set(int index, E element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void add(int index, E element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public E remove(int index) {
        throw new UnsupportedOperationException();
      }
    }
    @SuppressWarnings("unchecked")
    var array = (E[]) new Object[size];
    var mh = new StableIntFunctionCache(array).dynamicInvoker();
    return new ViewList<>(size, mh);
  }

  /**
   * {@return a new stable map with the provided {@code keys}}
   * <p>
   * The returned map is an {@linkplain Collection##unmodifiable unmodifiable} map whose
   * keys are known at construction. The map's values are computed via the provided
   * {@code mapper} when they are first accessed
   * (e.g. via {@linkplain Map#get(Object) Map::get}).
   * <p>
   * The provided {@code mapper} function is guaranteed to be successfully invoked
   * at most once per key, even in a multi-threaded environment. Competing
   * threads accessing a value already under computation will block until an element
   * is computed or an exception is thrown by the computing thread.
   * <p>
   * If the provided {@code mapper} throws an exception, it is relayed to the initial
   * caller and no value associated with the provided key is recorded.
   * <p>
   * Any direct {@link Map#values()} or {@link Map#entrySet()} views
   * of the returned map are also stable.
   * <p>
   * The returned map is unmodifiable and does not implement the
   * {@linkplain Collection##optional-operations optional operations} in the
   * {@linkplain Map} interface.
   * <p>
   * If the provided {@code mapper} recursively calls the returned map for
   * the same key, an {@linkplain IllegalStateException} will be thrown.
   *
   * @param keys   the (non-null) keys in the returned map
   * @param mapper to invoke whenever an associated value is first accessed
   *               (may return {@code null})
   * @param <K>    the type of keys maintained by the returned map
   * @param <V>    the type of mapped values in the returned map
   * @throws NullPointerException if the provided set of {@code inputs} contains a
   *                              {@code null} element.
   */
  public static <K,V> Map<K,V> map(Set<K> keys, Function<? super K, ? extends V> mapper) {
    requireNonNull(keys);
    requireNonNull(mapper);
    class StableKeyedFunctionCache extends MutableCallSite {
      private static final MethodHandle FALLBACK, TEST;
      static {
        var lookup = lookup();
        try {
          FALLBACK = lookup.findVirtual(StableKeyedFunctionCache.class, "fallback", methodType(Object.class, Object.class));
          TEST = lookup.findStatic(StableKeyedFunctionCache.class, "test", methodType(boolean.class, Object.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
          throw new AssertionError(e);
        }
      }

      private static final Object UNINITIALIZED = new Object();

      private final Map<K, Object> map ;

      public StableKeyedFunctionCache(Map<K, Object> map) {
        super(methodType(Object.class, Object.class));
        setTarget(FALLBACK.bindTo(this));
        this.map = map;
      }

      private static boolean test(Object expected, Object o) {
        return expected == o;
      }

      @SuppressWarnings("unchecked")
      private Object fallback(Object key) {
        if (Thread.holdsLock(this)) {
          throw new IllegalStateException("cyclic definition");
        }
        Object value;
        synchronized (this) {
          value = map.get(key);
          if (value == null) {
            return null;
          }
          if (value == UNINITIALIZED) {
            value = requireNonNull(mapper.apply((K) key));
            map.put((K) key, value);
          }
        }
        var target = dropArguments(constant(Object.class, value), 0, Object.class);
        var guard = guardWithTest(TEST.bindTo(key), target, new StableKeyedFunctionCache(map).dynamicInvoker());
        setTarget(guard);  // this part is racy but we do not care
        return value;
      }
    }
    var keySet = Set.copyOf(keys);
    var map = keySet.stream().collect(toMap(k -> k, _ -> StableKeyedFunctionCache.UNINITIALIZED));
    record ViewMap<K,V>(Set<K> keys, MethodHandle mh) implements Map<K,V> {
      @Override
      public int size() {
        return keys.size();
      }

      @Override
      public boolean isEmpty() {
        return keys.isEmpty();
      }

      @Override
      public boolean equals(Object obj) {
        if ((!(obj instanceof Map<?, ?> m))) {
          return false;
        }
        return Map.copyOf(this).equals(m);
      }

      @Override
      public int hashCode() {
        return Map.copyOf(this).hashCode();
      }

      @Override
      public String toString() {
        return Map.copyOf(this).toString();
      }

      @Override
      public boolean containsKey(Object key) {
        requireNonNull(key);
        return keys.contains(key);
      }

      @Override
      public boolean containsValue(Object value) {
        requireNonNull(value);
        return get(value) != null;
      }

      @Override
      @SuppressWarnings("unchecked")
      public V get(Object key) {
        requireNonNull(key);
        try {
          return (V) mh.invokeExact(key);
        } catch (RuntimeException | Error e) {
          throw e;
        } catch (Throwable e) {
          throw new UndeclaredThrowableException(e);
        }
      }

      @Override
      public Set<K> keySet() {
        return keys;
      }

      @Override
      public Collection<V> values() {
        return new AbstractCollection<V>() {
          @Override
          public int size() {
            return keys.size();
          }

          @Override
          public Iterator<V> iterator() {
            var it = keys.iterator();
            return new Iterator<>() {
              @Override
              public boolean hasNext() {
                return it.hasNext();
              }

              @Override
              public V next() {
                return get(it.next());
              }
            };
          }
        };
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<>() {
          @Override
          public int size() {
            return ViewMap.this.keys.size();
          }

          @Override
          public Iterator<Entry<K, V>> iterator() {
            var it = keys.iterator();
            return new Iterator<>() {
              @Override
              public boolean hasNext() {
                return it.hasNext();
              }

              @Override
              public Entry<K,V> next() {
                var key = it.next();
                return Map.entry(key, get(key));
              }
            };
          }
        };
      }

      @Override
      public V put(K key, V value) {
        throw new UnsupportedOperationException();
      }

      @Override
      public V remove(Object key) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
        throw new UnsupportedOperationException();
      }
    }
    var mh = new StableKeyedFunctionCache(map).dynamicInvoker();
    return new ViewMap<>(keySet, mh);
  }
}
