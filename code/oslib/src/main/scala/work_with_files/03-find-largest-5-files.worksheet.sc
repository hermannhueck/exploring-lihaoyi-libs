// see: http://www.lihaoyi.com/post/HowtoworkwithFilesinScala.html

// ===== Use Case: Find Largest 5 Files ====================

val allPaths = os.walk(os.pwd)

val allFiles = allPaths.filter(os.isFile)

val sizedFiles = allFiles.map(path => (os.size(path), path))

val fiveLargestFiles =
  sizedFiles
    .sortBy(_._1)
    .takeRight(5)
    .reverse

val fiveLargestFiles2 =
  os
    .walk(os.pwd)
    .filter(os.isFile)
    .map(path => (os.size(path), path))
    .sortBy(_._1)
    .takeRight(5)
    .reverse

fiveLargestFiles2
  .map { case size -> path => size -> path.relativeTo(os.pwd) }
  .map { case size -> path => f"$size%9d -- $path" }
  .foreach(println)
