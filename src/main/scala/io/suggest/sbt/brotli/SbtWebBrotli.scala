package io.suggest.sbt.brotli

import java.io.{BufferedInputStream, BufferedOutputStream, FileInputStream, FileOutputStream}
import java.nio.file.Files

import sbt._
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.Keys._
import org.meteogroup.jbrotli._
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader

import scala.annotation.tailrec

object Import {

  val brotli = TaskKey[Pipeline.Stage]("brotli-compress", "Add brotli-compressed files to asset pipeline.")

}

object SbtWebBrotli extends AutoPlugin {

  BrotliLibraryLoader.loadBrotli()

  private val READ_BUFFER_SIZE_BYTES = 4096

  override def requires = SbtWeb

  override def trigger = AllRequirements

  val autoImport = Import

  import SbtWeb.autoImport._
  import WebKeys._
  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    includeFilter in brotli := "*.html" || "*.css" || "*.js",
    excludeFilter in brotli := HiddenFileFilter || "*.woff" || "*.woff2" || "*.gz",
    target in brotli := webTarget.value / brotli.key.label,
    deduplicators += SbtWeb.selectFileFrom((target in brotli).value),
    brotli := brotliFiles.value
  )

  def brotliFiles: Def.Initialize[Task[Pipeline.Stage]] = Def.task {
    val targetDir = (target in brotli).value
    val include = (includeFilter in brotli).value
    val exclude = (excludeFilter in brotli).value
    mappings =>
      val brotliMappings = for {
        (file, path) <- mappings.iterator
        if !file.isDirectory && include.accept(file) && !exclude.accept(file)
      } yield {
        val brotliPath = path + ".br"
        val brotliFile = targetDir / brotliPath
        brotliFile.getParentFile.mkdirs()

        val streamCompressor = new BrotliStreamCompressor( Brotli.DEFAULT_PARAMETER )
        val input = new BufferedInputStream( new FileInputStream( file ) )
        val output = new BufferedOutputStream( new FileOutputStream(brotliFile, false) )

        try {
          val buf = Array.ofDim[Byte]( READ_BUFFER_SIZE_BYTES )

          @tailrec
          def __processChunk(): Unit = {
            val bytesRead = input.read( buf )

            val compressedBytes = if (bytesRead < 0) {
              streamCompressor.compressArray(Array.empty, true)
            } else {
              streamCompressor.compressArray(buf, 0, bytesRead, false)
            }
            output.write(compressedBytes)

            if (bytesRead != -1)
              __processChunk()
          }

          __processChunk()


          input.read()
        } finally {
          streamCompressor.close()
          output.close()
          input.close()
        }

        (brotliFile, brotliPath)
      }
      mappings ++ brotliMappings
  }
}
