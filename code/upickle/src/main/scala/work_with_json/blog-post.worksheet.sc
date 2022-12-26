// see: http://www.lihaoyi.com/post/HowtoworkwithJSONinScala.html

// ===== How to work with JSON ====================

// ----- Download Ammonite releases JSON to local file --------------------

val ammoniteReleasesUrl = "https://api.github.com/repositories/29178282/releases" // Ammonite releases url

val ammoniteReleasesFile = "ammonite-releases-from-github.json"
val ammoniteReleasesPath = os.pwd / "files" / ammoniteReleasesFile

// os.proc("curl", ammoniteReleasesUrl).call(stdout = ammoniteReleasesPath)

os.exists(ammoniteReleasesPath)
assert(os.exists(ammoniteReleasesPath))

// ----- Process JSON from Ammonite releases JSON file --------------------

// ----- Extracting Values from JSON --------------------

val jsonString = os.read(ammoniteReleasesPath)

val data = ujson.read(jsonString)

data(0)
data(0)("url")
data(0)("author")("login")
data(0)("author")("id")

data(0).obj
data(0).obj.keys
data(0).obj.size

data(0)("url").str
data(0)("author")("id").num
data(0)("author")("id").num.toInt

// ----- Generating JSON --------------------

val output = ujson.Arr(
  ujson.Obj("hello" -> ujson.Str("world"), "answer" -> ujson.Num(42)),
  ujson.Bool(true)
)

val output2 = ujson.Arr(
  ujson.Obj("hello" -> "world", "answer" -> 42),
  true
)

output == output2
assert(output == output2)

ujson.write(output)

ujson.write(output, indent = 4)

// ----- Modifying JSON --------------------

println(output)

output(0)("hello") = "goodbye"
println(output)

output(0)("tags") = ujson.Arr("awesome", "yay", "wonderful")
println(output)

output(0).obj.remove("hello")
println(output)

output.arr.append(123)
println(output)

output.arr.clear()
println(output)

// ----- Traversing JSON --------------------

ujson.write(data, indent = 4)

// To traverse over the tree structure of the ujson.Value, we can use a recursive function.
// For example, here is one that recurses over data and collects all the ujson.Str nodes in the JSON structure:
def traverse(v: ujson.Value): Iterable[String] = v match {
  case a: ujson.Arr => a.arr.flatMap(traverse)
  case o: ujson.Obj => o.obj.values.flatMap(traverse)
  case s: ujson.Str => Seq(s.str)
  case _            => Nil
}

traverse(data)

// We could also modify the ujson.Value during traversal. Here's a function that recurses over data
// and removes all key-value pairs where the value is a string starting with https://:
def traverse2(v: ujson.Value): Boolean = v match {
  case a: ujson.Arr =>
    a.arr.foreach(traverse2)
    true
  case o: ujson.Obj =>
    o.obj.filterInPlace { case (k, v) => traverse2(v) }
    true
  case s: ujson.Str =>
    !s.str.startsWith("https://")
  case _            =>
    true
}

traverse2(data)

ujson.write(data, indent = 4)

// ----- Clean up local Ammonite releases JSON file --------------------

import upickle.default._

ujson.write(data(0)("author"), indent = 4)

case class Author(login: String, id: Int, site_admin: Boolean)

given ReadWriter[Author] = macroRW[Author]

val author = read[Author](data(0)("author"))

author.login
author.id
author.site_admin

case class Author2(login: String, id: Int, @upickle.implicits.key("site_admin") siteAdmin: Boolean)

given ReadWriter[Author2] = upickle.default.macroRW[Author2]

val author2 = upickle.default.read[Author2](data(0)("author"))

author2.login
author2.id
author2.siteAdmin

upickle.default.write(author)
upickle.default.write(author2)

// You can also deserialize to Seqs and other builtin data structures. Here we read assets,
// which is a JSON array of objects, into a Scala Seq[Asset]:

ujson.write(data(0)("assets"), indent = 4)

case class Asset(id: Int, name: String)

given ReadWriter[Asset] = upickle.default.macroRW[Asset]

val assets = upickle.default.read[Seq[Asset]](data(0)("assets"))

// You can also deserialize nested case classes:

case class Uploader(id: Int, login: String, `type`: String)

case class Asset2(id: Int, name: String, uploader: Uploader)

given ReadWriter[Uploader] = upickle.default.macroRW[Uploader]

given ReadWriter[Asset2] = upickle.default.macroRW[Asset2]

val assets2 = upickle.default.read[Seq[Asset2]](data(0)("assets"))

// If you wish to store a dynamically typed JSON field within your case class, simply label it as ujson.Value:

case class Asset3(id: Int, name: String, uploader: ujson.Value)

given ReadWriter[Asset3] = upickle.default.macroRW[Asset3]

val assets3 = upickle.default.read[Seq[Asset3]](data(0)("assets"))

assets3(0).uploader
assets3(0).uploader.obj
assets3(0).uploader.obj.keys

// Lastly, all our Scala data types can be converted back to JSON strings using upickle.default.write:

upickle.default.write(assets)
upickle.default.write(assets2)
upickle.default.write(assets3)

// ----- Clean up local Ammonite releases JSON file --------------------

// os.remove(ammoniteReleasesPath)
// os.exists(ammoniteReleasesPath)
