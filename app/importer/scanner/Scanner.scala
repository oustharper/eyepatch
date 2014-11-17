package importer.scanner

import java.nio.file.{DirectoryStream, Files, FileSystems, Path}
import scala.collection.JavaConverters._
import scala.util.matching.Regex.Match

case class EpisodeFile(
  path: Path,
  showNameTokens: Seq[String],
  episodeNameTokens: Seq[String],
  seasonNumber: Int,
  episodeNumber: Int
)

object Scanner {
  private val xEpisodeRegex = """^(.*)(\d+)x(\d+)(.*)\.(.*)$""".r
  private val wordRegex = """\w+""".r


  def getDirectoryEntries(path: Path): Iterable[Path] = {
    try {
      Files.newDirectoryStream(path).asScala flatMap { entry =>
        if (entry.toFile.isDirectory)
          getDirectoryEntries(entry.toAbsolutePath)
        else
          Iterable(entry)
      }
    } catch {
      case t: Throwable => Iterable.empty
    }
  }

  def findEpisodesInDir(dir: String) = {
    (getDirectoryEntries(FileSystems.getDefault.getPath(dir)) flatMap { path =>
      xEpisodeRegex.findFirstMatchIn(path.getFileName.toString) map { ep: Match =>
        val showNameTokens = wordRegex.findAllMatchIn(ep.group(1)).toList.map(_.matched)
        val episodeNameTokens = wordRegex.findAllMatchIn(ep.group(4)).toList.map(_.matched)
        val seasonNumber = ep.group(2).toInt
        val episodeNumber = ep.group(3).toInt
        EpisodeFile(
          path = path,
          showNameTokens = showNameTokens,
          episodeNameTokens = episodeNameTokens,
          seasonNumber = seasonNumber,
          episodeNumber = episodeNumber
        )
      }
    }).foreach {ef => println(ef)}
  }
}
