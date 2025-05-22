static class Database {}

private static final StableValue<Database> STABLE_VALUE = StableValue.of();

public static Database getLazyDatabase() {
  return STABLE_VALUE.orElseSet(() -> new Database());
}

void main() {
  System.out.println(getLazyDatabase());    // constant ?
}


