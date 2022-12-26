// ===== Getting Started ============================================
// see: - https://com-lihaoyi.github.io/PPrint/

// ----- pprint.pprintln --------------------------------------------

pprint.pprintln(new Object())

pprint.pprintln(Seq(1, 2, 3), width = 5) // force wrapping

pprint.pprintln(Seq(1, 2, 3), width = 6, height = 3) // truncate

// ----- pprint.log -------------------------------------------------

// class Foo {
//   def bar(grid: Seq[Seq[Int]]) = {
//     // throws java.lang.NullPointerException in Scala 3
//     pprint.log(grid)
//   }
// }

// new Foo().bar(Seq(0 until 10, 10 until 20, 20 until 30))
