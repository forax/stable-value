static class Database {}

private static Database DB;
private static final Object LOCK = new Object();

public static Database getLazyDatabase() {
  enum Holder {
    ;
    private static final Database DB = new Database();
  }
  return Holder.DB;
}

void main() {
  System.out.println(getLazyDatabase());    // constant ?
}


