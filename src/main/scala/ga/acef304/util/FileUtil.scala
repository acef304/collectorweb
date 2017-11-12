package ga.acef304.util

import java.io.File

object FileUtil {
  def getFileName(date: String, tickers: Iterable[String]) = {
    ensureFolder(date)
    s"$date/${tickers.mkString(",")}.csv"
  }

  def ensureFolder(path: String): Unit = {
    val directory = new File(path)
    if (! directory.exists()) {
      directory.mkdirs()
    }
  }
}
