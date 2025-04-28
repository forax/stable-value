void main() {
  // high level
  var supplier = StableValue.supplier(() -> 3);
  //IO.println(supplier);
  //IO.println(supplier.get());
  //IO.println(supplier);

  // low-level
  var stableValue = StableValue.<Integer>of();
  var value = stableValue.orElseSet(() -> 3);
  //IO.println(value);

  // debug or stupid
  //IO.println(stableValue.isSet());

  // stable list ?
  var list = StableValue.list(10, index -> "" + index);
  IO.println(list.get(3));

  // stable map ?
  var map =
      StableValue.map(Set.of("foo", "foobar"), key -> key.length());
  IO.println(map.get("foo"));
}
