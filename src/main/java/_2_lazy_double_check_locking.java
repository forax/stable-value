static class Database {}

private volatile Database db;
private final Object LOCK = new Object();

public Database getLazyDatabase() {
  if (db != null) {
    return db;
  }
  synchronized (LOCK) {
    if (db != null) {
      return db;
    }
    return db = new Database();
  }
}

void main() {
  System.out.println(getLazyDatabase());
}


