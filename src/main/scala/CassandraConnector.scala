import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetSocketAddress
import java.util.UUID
import java.time.Instant

object CassandraConnection {
    lazy val session: CqlSession = CqlSession.builder()
        .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
        .withLocalDatacenter("datacenter1")
        .build()

    def createUser(email: String, password: String): Int = {
        try {
            val query =
                """INSERT INTO chat_app.user (userid, email, password, date_created_timestamp) 
                    |VALUES (?, ?, ?, ?);""".stripMargin

            val userId = UUID.randomUUID()
            val timestamp = Instant.now()

            session.execute(query, userId, email, password, timestamp)
            200
        } catch {
            case e: Exception if e.getMessage.contains("already exists") => 409
            case e: Exception => 500
        }
    }

    def loginUser(email: String, password: String): Int = {
        try {
            val query =
                """SELECT userid FROM chat_app.user WHERE email = ? AND password = ? ALLOW FILTERING;"""
            val resultSet = session.execute(query, email, password)
            val row = resultSet.one()

            if (row != null) 200 else 401
        } catch {
            case _: Exception => 500
        }
    }
}
