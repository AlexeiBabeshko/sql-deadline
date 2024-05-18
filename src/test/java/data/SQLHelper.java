package data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {

    }
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(System.getProperty("db.url"), "app", "pass");
    }

    @SneakyThrows
    public static String getVerificationCode() {
        var connection = getConnection();
        String queryVerCode = "SELECT code  FROM auth_codes ORDER BY created DESC LIMIT 1";
        return QUERY_RUNNER.query(connection, queryVerCode, new ScalarHandler<>());
    }
    @SneakyThrows
    public static void cleanDB() {
        var connection = getConnection();
        cleanVerificationTable();
        QUERY_RUNNER.execute(connection, "DELETE FROM auth_codes");
        QUERY_RUNNER.execute(connection, "DELETE FROM card_transactions");
        QUERY_RUNNER.execute(connection, "DELETE FROM cards");
        QUERY_RUNNER.execute(connection, "DELETE FROM users");
    }
    @SneakyThrows
    public static void cleanVerificationTable() {
        var connection = getConnection();
        QUERY_RUNNER.execute(connection, "DELETE FROM auth_codes");
    }
}
