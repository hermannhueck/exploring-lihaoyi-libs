// ===== Streaming =================================================
// see: - https://com-lihaoyi.github.io/PPrint/

val large = Seq.fill(100000)("ABCDE" * 1000)

// println(large)
// java.lang.OutOfMemoryError: Java heap space

pprint.pprintln(large)

val large2 = Seq.tabulate(100000)("ABCDE" + _)

// pprint.log(large2, height = 20)
// throws java.lang.NullPointerException in Scala 3
pprint.pprintln(large2, height = 20)

pprint.tokenize(large)
