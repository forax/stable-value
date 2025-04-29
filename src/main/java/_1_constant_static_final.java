private static final int MAGIC = 40 + 2;
private static final String STRING = "Am i a constant ?";

void main() {
  System.out.println(42);                   // constant ?
  System.out.println("Am i a constant ?");  // constant ?

  System.out.println(MAGIC);   // constant ?
  System.out.println(STRING);  // constant ?
}



// void main();
//    descriptor: ()V
//    flags: (0x0000)
//    Code:
//      stack=2, locals=1, args_size=1
//         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//         3: bipush        42
//         5: invokevirtual #13                 // Method java/io/PrintStream.println:(I)V
//         8: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//        11: ldc           #19                 // String Am i a constant ?
//        13: invokevirtual #21                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
//        16: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//        19: bipush        42
//        21: invokevirtual #13                 // Method java/io/PrintStream.println:(I)V
//        24: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//        27: ldc           #19                 // String Am i a constant ?
//        29: invokevirtual #21                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
//        32: return