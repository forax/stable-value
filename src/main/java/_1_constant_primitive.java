void main() {
  System.out.println(3);       // constant ?
  System.out.println(40 + 2);  // constant ?
}



// void main();
//    descriptor: ()V
//    flags: (0x0000)
//    Code:
//      stack=2, locals=1, args_size=1
//         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//         3: iconst_3
//         4: invokevirtual #13                 // Method java/io/PrintStream.println:(I)V
//         7: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
//        10: bipush        42
//        12: invokevirtual #13                 // Method java/io/PrintStream.println:(I)V
//        15: return