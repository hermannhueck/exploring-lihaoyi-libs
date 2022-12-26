// see: http://www.lihaoyi.com/post/HowtoworkwithSubprocessesinScala.html

// ===== How to work with Subprocesses ====================

// ----- os.proc.call --------------------

/*
The most common thing to do with subprocesses is to spawn one and wait for it to complete.
This is done through the os.proc.call function:

os.proc(command: os.Shellable*)
  .call(cwd: Path = null,
        env: Map[String, String] = null,
        stdin: ProcessInput = Pipe,
        stdout: ProcessOutput = Pipe,
        stderr: ProcessOutput = Pipe,
        mergeErrIntoOut: Boolean = false,
        timeout: Long = Long.MaxValue,
        check: Boolean = true,
        propagateEnv: Boolean = true): os.CommandResult

os.proc.call takes a lot of optional parameters, but at its simplest you simply pass in the command you want to execute:
 */

val lsResult = os.proc("ls", "-l", "lollipop").call(check = false)

lsResult.exitCode

lsResult.err.text()

lsResult.out.text()

val gitCmd    = os.root / "usr" / "local" / "bin" / "git"
val gitStatus = os.proc(gitCmd, "status").call()

gitStatus.exitCode

gitStatus.err.text()

gitStatus.out.text()

// ----- Find distinct contributors in a Git repo history --------------------

val gitLog = os.proc("git", "log").call().out.text()

val authorLines = os.proc("grep", "Author: ").call(stdin = gitLog).out.lines()

authorLines.distinct

// ----- Remove non-master branches from a Git Repo --------------------

os.proc("git", "branch", "feature_branch").call()
os.proc("git", "branch", "bugfix_branch").call()

val gitBranches = os.proc("git", "branch").call().out.lines()

val otherBranches = gitBranches.filter(_.startsWith("  ")).map(_.trim)

otherBranches.foreach { branch =>
  os.proc("git", "branch", "-D", branch).call()
}

val gitBranches2 = os.proc("git", "branch").call().out.lines()
gitBranches2.head == "* main"

// ----- Curl to a local file --------------------

val millReleasesUrl = "https://api.github.com/repositories/107214500/releases" // mill releases url

val millReleasesFile   = "mill-releases-from-github.json"
val millReleasesFileGz = millReleasesFile + ".gz"
val millReleasesPath   = os.pwd / millReleasesFile
val millReleasesPathGz = os.pwd / millReleasesFileGz

os.proc("curl", millReleasesUrl).call(stdout = millReleasesPath)

val releaseNameLines =
  os
    .proc("grep", """"name": """, millReleasesPath)
    .call()
    .out
    .lines()

val releaseNames = releaseNameLines.map { line =>
  val name = line.split(":")(1).trim
  name.substring(1, name.length - 2)
}

// ----- Curl to a local file --------------------

os.proc("gzip").call(stdin = millReleasesPath, stdout = millReleasesPathGz)

os.proc("ls", "-lh", millReleasesFile, millReleasesFileGz).call().out.text()

os.remove(millReleasesPath)
os.remove(millReleasesPathGz)

os.exists(millReleasesPath)
os.exists(millReleasesPathGz)

// ----- os.proc.spawn --------------------

/*
While os.proc.call allows you to pass concrete input data and receive concrete output data from a subprocess,
and allows some degree of streaming input and output, it has one core limitation: the spawned subprocess must
terminate before os.proc.call returns. This means you cannot use it to set up pipelines where two or more
processes are running in parallel and feeding data into each other to process, or to start a subprocess that runs
in the background for you to interact with. For these use cases, you need os.proc.spawn:

os.proc(command: os.Shellable*)
  .spawn(cwd: Path = null,
         env: Map[String, String] = null,
         stdin: os.ProcessInput = os.Pipe,
         stdout: os.ProcessOutput = os.Pipe,
         stderr: os.ProcessOutput = os.Pipe,
         mergeErrIntoOut: Boolean = false,
         propagateEnv: Boolean = true): os.SubProcess

os.proc.spawn takes a similar set of arguments as os.proc.call, but instead of returning a completed os.CommandResult,
it instead returns a os.SubProcess object. This represents a subprocess that may or may not have completed,
and you can interact with.
 */

// ----- Streaming distinct contributors in a Git repo history --------------------

val gitLog2     = os.proc("git", "log").spawn()
val grepAuthor2 = os.proc("grep", "Author: ").spawn(stdin = gitLog2.stdout)

val authors2 = grepAuthor2.stdout.lines().distinct

val gitLog3     = os.proc("git", "log").spawn()
val grepAuthor3 = os.proc("grep", "Author: ").spawn(stdin = gitLog3.stdout)

val authors3 = collection.mutable.LinkedHashSet.empty[String]
while (grepAuthor3.stdout.available() > 0 || grepAuthor3.isAlive()) {
  val author = grepAuthor3.stdout.readLine()
  // println(author)
  authors3.add(author)
}
authors3

// ----- Streaming download-process-reupload --------------------

val download = os.proc("curl", millReleasesUrl).spawn()

val gzip = os.proc("gzip").spawn(stdin = download.stdout)

val upload =
  os
    .proc(
      "curl",
      "-X",
      "PUT",
      "-H",
      "Content-Type:application/octet-stream",
      "-d",
      "@-",
      "https://httpbin.org/anything"
    )
    .spawn(stdin = gzip.stdout)

val contentLength =
  upload.stdout
    .lines()
    .filter(_.contains("Content-Length"))
    .head
    .split(":")(1)
    .trim
    .drop(1)
    .dropRight(2)
    .toInt

// ----- Interacting with a Python process --------------------

/*
val sub = os.proc("python", "-u", "-c", "while True: print(eval(raw_input()))").spawn()

sub.stdin.write("1 + 2")

sub.stdin.writeLine("+ 4")

sub.stdin.flush()

sub.stdout.readLine()

sub.stdin.write("'1' + '2'")

sub.stdin.writeLine("+ '4'")

sub.stdin.flush()

sub.stdout.readLine()

sub.stdin.write("1 * 2".getBytes)

sub.stdin.write("* 4\n".getBytes)

sub.stdin.flush()

val res = sub.stdout.read()

res.toChar

sub.isAlive()

sub.destroy()

sub.isAlive()
 */
