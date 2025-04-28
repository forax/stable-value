static class Database {}

private static Database DB;
private static final Object LOCK = new Object();

public static Database getLazyDatabase() {
  synchronized (LOCK) {
    if (DB != null) {
      return DB;
    }
    return DB = new Database();
  }
}

void main() {
  System.out.println(getLazyDatabase());    // constant ?
}


