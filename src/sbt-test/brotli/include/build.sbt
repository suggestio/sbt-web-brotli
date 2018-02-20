val root = (project in file(".")).enablePlugins(SbtWeb)

pipelineStages := Seq(brotli)

// set an include filter for js files only

includeFilter in brotli := "*.js"

// for checking that the produced pipeline mappings are correct

val expected = Set(
  "css", "css/a.css",
  "js", "js/a.js", "js/a.js.br"
) map(_.replace("/", java.io.File.separator))

val checkMappings = taskKey[Unit]("check the pipeline mappings")

checkMappings := {
  val mappings = WebKeys.pipeline.value
  val paths = (mappings map (_._2)).toSet
  if (paths != expected) sys.error(s"Expected $expected but pipeline paths are $paths")
}
