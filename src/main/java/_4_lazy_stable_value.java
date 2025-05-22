static class Database {}

private final StableValue<Database> dbStableValue = StableValue.of();

public Database getLazyDatabase() {
  return dbStableValue.orElseSet(() -> new Database());
}

void main() {
  System.out.println(getLazyDatabase());
}


