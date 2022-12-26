// see: http://www.lihaoyi.com/post/HowtoworkwithHTTPJSONAPIsinScala.html

// ===== How to work with HTTP JSON APIs ====================

// ----- The Task: Github Issue Migrator --------------------

// ----- Step 1: Read Write Access --------------------

val githubToken = "????????????????????????????????????????"

val username = "hermannhueck"
val repo     = "exploring-lihaoyi-libs"
val repoUrl  = s"https://api.github.com/repos/$username/$repo"

// requests.post(
//   s"$repoUrl/issues",
//   data = ujson.Obj("title" -> "hello").render(),
//   headers = Map(
//     "Authorization" -> s"token $githubToken",
//     "Content-Type"  -> "application/json"
//   )
// )

// requests.post(
//   s"$repoUrl/issues/1/comments",
//   data = ujson.Obj("body" -> "world").render(),
//   headers = Map(
//     "Authorization" -> s"token $githubToken",
//     "Content-Type"  -> "application/json"
//   )
// )

// ----- Step 2: Fetching Existing Issues --------------------

// val resp = requests.get(s"$repoUrl/issues", headers = Map("Authorization" -> s"token $githubToken"))

// resp.text()

// val parsed = ujson.read(resp.text())

// parsed.render(indent = 4)

// ----- Step 3: Pagination --------------------

val millRepoUrl = "https://api.github.com/repos/lihaoyi/mill"

// val millIssues = requests.get(s"$repoUrl/issues?state=all", headers = Map("Authorization" -> s"token $githubToken"))

// millIssues.text()

// val millIssuesParsed = ujson.read(millIssues.text())

// millIssuesParsed.render(indent = 4)

// millIssuesParsed.arr.length

val millIssuesJsonFile     = "mill-issues.json"
val millIssuesJsonTextFile = "mill-issues.json.txt"

def getAllIssues(repoUrl: String) = {
  var done      = false
  var page      = 1
  val responses = collection.mutable.Buffer.empty[ujson.Value]
  while (!done) {
    println("page " + page + "...")
    val resp   = requests.get(
      s"$repoUrl/issues?state=all&page=" + page,
      headers = Map("Authorization" -> s"token $githubToken")
    )
    os.write.append(os.pwd / millIssuesJsonTextFile, resp.text())
    val parsed = ujson.read(resp.text()).arr

    if (parsed.length == 0) done = true
    else responses.appendAll(parsed)

    page += 1
  }
  // os.write(responses)
  responses
}

val issuesAndPullRequests = getAllIssues(millRepoUrl).toList

// os.read(os.pwd / millIssuesJsonFile)

// os.read.stream(os.pwd / millIssuesJsonFile).map(ujson.read(_).arr).filter(!_.obj.contains("pull_request")).toList

// def getAllIssuesFromFile(repoUrl: String) = {
//   var done      = false
//   var page      = 1
//   val responses = collection.mutable.Buffer.empty[String]
//   while (!done) {
//     println("page " + page + "...")
//     val resp   = requests.get(
//       s"$repoUrl/issues?state=all&page=" + page,
//       headers = Map("Authorization" -> s"token $githubToken")
//     )
//     os.write.append(os.pwd / millIssuesJsonFile, resp.text())
//     val parsed = ujson.read(resp.text()).arr

//     if (parsed.length == 0) done = true
//     else responses.appendAll(parsed)

//     page += 1
//   }
//   responses
// }

// ----- Step 4: Picking the data we want --------------------

val issues = issuesAndPullRequests.filter(!_.obj.contains("pull_request"))

issues.length

val issueData =
  for (issue <- issues)
    yield (
      issue("number").num.toInt,
      issue("title").str,
      // issue("body").str,
      issue("user")("login").str
    )

// ----- Step 5: Issue Comments --------------------

// Tutorial incomplete
// see: http://www.lihaoyi.com/post/HowtoworkwithHTTPJSONAPIsinScala.html
