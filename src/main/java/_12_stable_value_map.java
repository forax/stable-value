static class Database {}

private static final Map<String, Database> STABLE_MAP =
    StableValue.map(Set.of("A", "B"), key -> new Database());

public static Map<String, Database> getLazyDatabaseMap() {
  return STABLE_MAP;
}

void main() {
  var map = getLazyDatabaseMap();
  System.out.println(map.get("A"));    // constant ?
}


