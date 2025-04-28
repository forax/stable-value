
static class Database {}

private static volatile Database DB;
private static final Object LOCK = new Object();

public static Database getLazyDatabase() {
  if (DB != null) {
    return DB;
  }
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


