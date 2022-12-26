// see: http://www.lihaoyi.com/post/HowtoworkwithFilesinScala.html

// ===== Filesystem Operations ====================

// ----- Queries --------------------

os.list(os.pwd)

os.walk(os.pwd)

os.stat(os.pwd)

os.isFile(os.pwd)

os.isDir(os.pwd)

os.isFile(os.pwd / "build.sbt")

os.isDir(os.pwd / "build.sbt")

os.size(os.pwd / "build.sbt")

// ----- Actions --------------------

os.read(os.pwd / ".gitignore")

val tmp = os.pwd / "tmp"

if (!os.exists(tmp))
then os.makeDir(tmp)

if (!os.exists(tmp / "new.txt"))
then os.write(tmp / "new.txt", "Hello World")

os.list(tmp)
os.read(tmp / "new.txt")

if (os.exists(tmp / "new.txt") && !os.exists(tmp / "newer.txt"))
then os.move(tmp / "new.txt", tmp / "newer.txt")

os.list(tmp)
os.read(tmp / "newer.txt")

if (os.exists(tmp / "newer.txt") && !os.exists(tmp / "newer-2.txt"))
then os.copy(tmp / "newer.txt", tmp / "newer-2.txt")

os.list(tmp)
os.read(tmp / "newer-2.txt")

if (os.exists(tmp / "newer.txt"))
then os.remove(tmp / "newer.txt")

os.list(tmp)

if (!os.exists(tmp / "new-folder"))
then os.makeDir(tmp / "new-folder")

os.list(tmp)

os.remove.all(tmp)

os.exists(tmp)

// ----- Streaming --------------------

os.read.lines(os.pwd / ".gitignore").foreach(println)
os.read.lines.stream(os.pwd / ".gitignore").foreach(println)

os.list(os.pwd).foreach(println)
os.list.stream(os.pwd).foreach(println)
