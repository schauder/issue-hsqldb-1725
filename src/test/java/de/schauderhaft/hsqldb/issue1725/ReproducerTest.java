package de.schauderhaft.hsqldb.issue1725;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * the two test methods are identically, except for order and name.
 * Yet the first one succeeds while the second one fails.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReproducerTest {

	static final JDBCDataSource ds = new JDBCDataSource();

	@BeforeAll
	static void setUp() {
		ds.setURL("jdbc:hsqldb:mem:test");

		try (Connection connection = ds.getConnection()) {

			connection.prepareStatement("create table dummy( id int, text varchar(20))").execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	@Order(100)
	void one() {
		insert();
	}

	@Test
	@Order(200)
	void two() {
		insert();
	}


	private void insert() {
		try (Connection connection = ds.getConnection()) {

			PreparedStatement preparedStatement = connection.prepareStatement("insert into dummy(id, text) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setInt(1, 4711);
			preparedStatement.setString(2, "text1");
			preparedStatement.executeUpdate();

			try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
				while (resultSet.next()) {
					System.out.println("rs element");
				}
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
}
