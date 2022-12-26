// see: http://www.lihaoyi.com/post/HowtoworkwithFilesinScala.html

// ===== Paths ====================

// ----- Constructing Paths --------------------

os.pwd: os.Path
os.pwd.wrapped: java.nio.file.Path

os.root

os.home

os.pwd / "post"

os.home / "Github" / "blog"

// os.home / "Github/blog"
// os.PathError$InvalidSegment: [Github/blog] is not a valid path segment.
// [/] is not a valid character to appear in a path segment.

os.pwd

os.pwd / os.up

os.pwd / os.up / os.up

os.pwd / os.up / os.up / os.up

os.pwd / os.up / os.up / os.up / os.up

os.Path("/")

// os.Path("post")
// java.lang.IllegalArgumentException: requirement failed: post is not an absolute path

os.Path("post", base = os.pwd)

os.Path("../Ammonite", base = os.pwd)

// ----- Relative Paths --------------------

os.RelPath("post")

os.RelPath("../hello/world")

val postFolder = os.RelPath("post")

os.pwd / postFolder

val helloWorldFolder = os.RelPath("../hello/world")

os.home / helloWorldFolder

val githubPath = os.Path("/Users/lihaoyi/Github")

val usersPath = os.Path("/Users")

githubPath.relativeTo(usersPath)

usersPath.relativeTo(githubPath)

// ----- Paths are always canonicalized --------------------

val githubPathOne = os.Path("/Users/lihaoyi/Github/../Github")

val githubPathTwo = os.Path("/Users/lihaoyi/Github/../Github/../Github")

githubPathOne == githubPathTwo

// import munit.Assertions._
// assertEquals(githubPathOne, githubPathTwo)

os.Path("/Users/lihaoyi////Github/")

os.Path("/Users/lihaoyi/Github") == os.Path("/Users/lihaoyi////Github/")

val helloPathOne = os.RelPath("../hello/world")

val helloPathTwo = os.RelPath("../hello/../hello/world//../world")

helloPathOne == helloPathTwo

// ----- Type-safe extension --------------------

val githubPath2 = os.Path("/Users/lihaoyi/Github")

val postPath2 = os.RelPath("post")

githubPath2 / postPath2

postPath2 / postPath2

// does not compile
// githubPath2 / githubPath2

// does not compile
// postPath2 / githubPath2
