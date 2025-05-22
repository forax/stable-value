import java.util.ArrayList;
import java.util.List;

class _13_constant_static_block_list {
  private static final List<String> LIST_OF;
  private static final List<String> ARRAYLIST;

  static {
    LIST_OF = List.of("John");
    var arrayList = new ArrayList<String>();
    arrayList.add("John");
    ARRAYLIST = arrayList;
  }

  void main() {
    System.out.println(LIST_OF.getFirst());    // constant ?
    System.out.println(ARRAYLIST.getFirst());  // constant ?
  }
}

