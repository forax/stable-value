static class Database {}

private Database db;
private final Object LOCK = new Object();

public Database getLazyDatabase() {
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


