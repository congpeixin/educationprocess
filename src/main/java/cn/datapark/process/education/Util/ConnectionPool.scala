package cn.datapark.process.education.Util

import java.sql.{Connection, ResultSet}
import com.jolbox.bonecp.{BoneCP, BoneCPConfig}
import org.slf4j.LoggerFactory

object ConnectionPool {

  val logger = LoggerFactory.getLogger(this.getClass)
  private val connectionPool = {
    try{
      Class.forName("com.mysql.jdbc.Driver")
      val config = new BoneCPConfig()
      config.setJdbcUrl("jdbc:mysql://192.168.39.18:3306/datapark?useUnicode=true&characterEncoding=UTF-8")
      config.setUsername("root")
      config.setPassword("123456")
      config.setMinConnectionsPerPartition(2)
      config.setMaxConnectionsPerPartition(5)
      config.setPartitionCount(3)
      config.setCloseConnectionWatch(true)
      config.setLogStatementsEnabled(true)
      Some(new BoneCP(config))
    } catch {
      case exception:Exception=>
        logger.warn("Error in creation of connection pool"+exception.printStackTrace())
        None
    }
  }

  def getConnection:Option[Connection] ={
    connectionPool match {
      case Some(connPool) => Some(connPool.getConnection)
      case None => None
    }
  }

  def closeConnection(connection:Connection): Unit = {
    if(!connection.isClosed) connection.close()
  }
}
