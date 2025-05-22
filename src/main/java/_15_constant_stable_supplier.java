static class Database {}

private static final Supplier<Database> SUPPLIER = StableValue.supplier(() -> new Database());

public static Database getLazyDatabase() {
  return SUPPLIER.get();
}

void main() {
  System.out.println(getLazyDatabase());    // constant ?
}


