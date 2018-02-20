sbt-web-brotli
==========

[sbt-web] plugin for brotli-compressing web assets using [jbrotli] bindings.

Rewritten from [sbt-gzip] sources, thanks to Typesafe/Lightbend.
Some parts of code, docs, tests are copy-pasted with no changes.


Add plugin
----------

Add the plugin to `project/plugins.sbt`. For example:

```scala
resolvers += (
  "bintray-nitram509-jbrotli" at "http://dl.bintray.com/nitram509/jbrotli"
)

// Brotli itself implemented in C, so you need native `.so/.dll` library in your classpath.
// All pre-built arch at https://dl.bintray.com/nitram509/jbrotli/org/meteogroup/jbrotli/
// UNCOMMENT one or more architectures, suitable for your needs:
libraryDependencies ++= Seq(
  //"org.meteogroup.jbrotli" % "jbrotli-native-darwin-x86-amd64" % "0.5.0"
  //"org.meteogroup.jbrotli" % "jbrotli-native-linux-arm32-vfp-hflt" % "0.5.0"
  //"org.meteogroup.jbrotli" % "jbrotli-native-linux-x86-amd64" % "0.5.0"
  //"org.meteogroup.jbrotli" % "jbrotli-native-win32-x86-amd64" % "0.5.0"
  //"org.meteogroup.jbrotli" % "jbrotli-native-win32-x86" % "0.5.0"
)

// PUBLIC REPO NOT READY YET, SORRY. RE-BUILD SOURCES BY HANDS
addSbtPlugin("io.suggest" % "sbt-web-brotli" % "0.5.6-SNAPSHOT")
```

Your project's build file also needs to enable sbt-web plugins. For example with build.sbt:

    lazy val root = (project.in file(".")).enablePlugins(SbtWeb)

As with all sbt-web asset pipeline plugins you must declare their order of execution e.g.:

```scala
pipelineStages := Seq(brotli)
```

Configuration
-------------

### Filters

Include and exclude filters can be provided. For example, to only create
brotli files for `.js` files:

```scala
includeFilter in brotli := "*.js"
```

Or to exclude all `.js` files but include any other files:

```scala
excludeFilter in brotli := "*.js"
```

The '''default''' filters configured like this:

```scala
includeFilter in brotli := "*.html" || "*.css" || "*.js"

excludeFilter in brotli := HiddenFileFilter || "*.woff" || "*.woff2" || "*.gz"
```

If you also using `sbt-gzip`, you may want configure it to ignore brotli-compressed files:
```scala
excludeFilter in gzip := "*.woff" || "*.woff2" || "*.br"
```

License
-------

This code is licensed under the [Apache 2.0 License][apache].

[jbrotli]: https://github.com/MeteoGroup/jbrotli
[sbt-gzip]: https://github.com/sbt/sbt-gzip
[sbt-web]: https://github.com/sbt/sbt-web
[apache]: http://www.apache.org/licenses/LICENSE-2.0.html
