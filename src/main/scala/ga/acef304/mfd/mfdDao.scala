package ga.acef304.mfd

import com.mongodb.casbah.MongoConnection
import salat.dao.SalatDAO
import salat.global.ctx


case class Ticker(id: String, groupId: String, name: String)

object TickerDAO extends SalatDAO[Ticker, Int](collection =
  MongoConnection()("test_db")("test_coll"))
