import module java.base;

class _2_constant_static_block {
  private static final int MAGIC;
  private static final String STRING;

  static {
    MAGIC = 42;
    STRING = "Am i a constant ?";
  }

  void main() {
    System.out.println(MAGIC);   // constant ?
    System.out.println(STRING);  // constant ?
  }
}



// void main();
//    descriptor: ()V
//    flags: (0x0000)
//    Code:
//      stack=2, locals=1, args_size=1
//         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//         3: getstatic     #13                 // Field MAGIC:I
//         6: invokevirtual #19                 // Method java/io/PrintStream.println:(I)V
//         9: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//        12: getstatic     #25                 // Field STRING:Ljava/lang/String;
//        15: invokevirtual #29                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
//        18: return