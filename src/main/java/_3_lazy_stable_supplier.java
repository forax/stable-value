static class Database {}

private final Supplier<Database> dbSupplier = StableValue.supplier(() -> new Database());

public Database getLazyDatabase() {
  return dbSupplier.get();
}

void main() {
  System.out.println(getLazyDatabase());
}


