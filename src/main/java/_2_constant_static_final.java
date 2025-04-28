private static final int MAGIC = 42;
private static final String STRING = "Am i a constant ?";

void main() {
  System.out.println(MAGIC);   // constant ?
  System.out.println(STRING);  // constant ?
}



//  void main();
//    descriptor: ()V
//    flags: (0x0000)
//    Code:
//      stack=2, locals=1, args_size=1
//         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//         3: bipush        42
//         5: invokevirtual #15                 // Method java/io/PrintStream.println:(I)V
//         8: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//        11: ldc           #21                 // String Am i a constant ?
//        13: invokevirtual #23                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
//        16: return
