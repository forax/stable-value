static class Database {}

private static final List<Database> STABLE_LIST =
    StableValue.list(10, i -> new Database());

public static List<Database> getLazyDatabaseList() {
  return STABLE_LIST;
}

void main() {
  var list = getLazyDatabaseList();
  System.out.println(list.getFirst());    // constant ?
}


