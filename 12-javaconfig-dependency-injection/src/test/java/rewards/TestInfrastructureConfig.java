package rewards;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import config.RewardsConfig;
import config.RewardsOracleConfig;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * TODO-06: Study this configuration class used for testing
 * - It contains a @Bean method that returns DataSource.
 * - It also creates and populates in-memory HSQL database tables
 *   using two SQL scripts.
 * - Note that the two scripts are located under the
 *   'src/main/resources/rewards/testdb' directory of
 *   the '00-rewards-common' project
 * - Do not modify this method.
 *
 * TODO-07: Import your application configuration file (RewardsConfig)
 * - Now the test code should have access to all the beans defined in
 *   the RewardsConfig configuration class
 *
 * TODO-08: Create a new JUnit 5 test class
 * - Call it RewardNetworkTests
 * - Create it in the same package this configuration class is located.
 * - Ask for a setUp() method to be generated within your IDE.
 *
 * NOTE: The appendices at the bottom of the course Home Page includes
 * a section on creating JUnit tests in an IDE.
 *
 * TODO-09: Make sure the setUp() method in the RewardNetworkTests class is annotated with @BeforeEach.
 * - In the setUp() method, create an application context using
 *   this configuration class - use run(..) static method of
 *   the SpringApplication class
 * - Then get the 'rewardNetwork' bean from the application context
 *   and assign it to a private field for use later.
 *
 * TODO-10: We can test the setup by running an empty test.
 * - If your IDE automatically generated a @Test method, rename it
 *   testRewardForDining. Delete any code in the method body.
 * - Otherwise add a testRewardForDining method & annotate it with
 *   @Test (make sure the @Test is from org.junit.jupiter.api.Test ).
 * - Run the test. If your setup() is working, you get a green bar.
 *
 * TODO-11: Finally run a real test.
 * - Copy the unit test (the @Test method) from
 *   RewardNetworkImplTests#testRewardForDining() under
 *   rewards.internal test package - we are testing
 *   the same code, but using a different setup.
 * - Run the test - it should pass if you have configured everything
 *   correctly. Congratulations, you are done.
 * - If your test fails - did you miss the import in TO DO 7 above?
 *
 */
@Configuration
@Import(RewardsConfig.class)
public class TestInfrastructureConfig {

	/**
	 * Creates an in-memory "rewards" database populated
	 * with test data for fast testing
	 */
	@Bean
	@Qualifier("hsqlDataSource")
	public DataSource dataSourceHsql() throws SQLException {
		return (new EmbeddedDatabaseBuilder()) //
				.addScript("classpath:rewards/testdb/schema.sql") //
				.addScript("classpath:rewards/testdb/data.sql") //
				.build();

	}

	/**
	 * Connect to an Oracle database locally using an URL and credentials
	 * 
	 */
	@Bean
	@Qualifier("oracleDataSource")
	public DataSource dataSourceOracle() throws SQLException {
		
		OracleDataSource dataSource = new OracleDataSource();
		dataSource.setURL("jdbc:oracle:thin:@//localhost:1521/INVENTARIO");
		dataSource.setUser("user123");
		dataSource.setPassword("pass123");
		return new DelegatingDataSource(dataSource) {

			//Inicialmente se creó el esquema de la pdb desde otro usuario(springuser). Al dar acceso al
			//nuevo usuario(user123) se le ha otorgado el privilegio alter session,
			//y a través de la siguiente sentencia permitimos que use el esquema del usuario que creó la pdb.
			@Override
			public Connection getConnection() throws SQLException {
				Connection conn = super.getConnection();
				conn.createStatement().execute("ALTER SESSION SET CURRENT_SCHEMA = springuser");
				return conn;
			}

		};
	}
}
