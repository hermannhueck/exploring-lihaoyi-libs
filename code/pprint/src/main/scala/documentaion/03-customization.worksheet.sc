// ===== Customization =============================================
// see: - https://com-lihaoyi.github.io/PPrint/

pprint.pprintln(Math.PI)

val pprint2 =
  pprint.copy(
    additionalHandlers = { case value: Double =>
      pprint.Tree.Literal(f"$value%1.5f") /* 5 digit precision */
    }
  )

pprint2.pprintln(Math.PI)
