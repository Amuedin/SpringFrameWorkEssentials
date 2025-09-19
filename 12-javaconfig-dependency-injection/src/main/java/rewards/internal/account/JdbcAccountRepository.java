package rewards.internal.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;



import org.springframework.dao.EmptyResultDataAccessException;

import common.money.MonetaryAmount;
import common.money.Percentage;

/**
 * Loads accounts from a data source using the JDBC API.
 */
public class JdbcAccountRepository implements AccountRepository {

	
	//@Autowired, aqui es inútil, puesto que la instancia la estoy creando en RewardsConfig
	//Aqui no se está ni se debe estar produciendo ningún tipo de inyección por parte de Spring
	private DataSource dataSource;

	//Parece mas adecuado inyectar por constructor, pero en la practica lo hace asi
	/*public JdbcAccountRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}*/

	/**
	 * Sets the data source this repository will use to load accounts.
	 * @param dataSource the data source
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Account findByCreditCard(String creditCardNumber) {
		
		String sql = "select a.ID as ID, a.NUMBER as NUMBER, a.NAME as ACCOUNT_NAME, c.NUMBER as CREDIT_CARD_NUMBER, " +
			"	b.NAME as BENEFICIARY_NAME, b.ALLOCATION_PERCENTAGE as BENEFICIARY_ALLOCATION_PERCENTAGE, b.SAVINGS as BENEFICIARY_SAVINGS " +
			"from T_ACCOUNT a, T_ACCOUNT_CREDIT_CARD c " +
			"left outer join T_ACCOUNT_BENEFICIARY b " +
			"on a.ID = b.ACCOUNT_ID " +
			"where c.ACCOUNT_ID = a.ID and c.NUMBER = ?";

		
		String sqlOracle = "SELECT a.ID AS ID, a.ACCOUNT_NUMBER AS ACCOUNT_NUMBER,a.NAME AS ACCOUNT_NAME,c.CARD_NUMBER AS CREDIT_CARD_NUMBER,b.NAME AS BENEFICIARY_NAME,b.ALLOCATION_PERCENTAGE AS BENEFICIARY_ALLOCATION_PERCENTAGE,b.SAVINGS AS BENEFICIARY_SAVINGS FROM T_ACCOUNT a JOIN T_ACCOUNT_CREDIT_CARD c ON c.ACCOUNT_ID = a.ID LEFT OUTER JOIN T_ACCOUNT_BENEFICIARY b ON a.ID = b.ACCOUNT_ID WHERE c.CARD_NUMBER = ?";
		
		Account account = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			//Validamos a que base de datos se conecta
			conn = dataSource.getConnection();
			System.out.println("DB Product: " + conn.getMetaData().getDatabaseProductName());
			if(conn.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle")){
				ps = conn.prepareStatement(sqlOracle);
			}else{
				ps = conn.prepareStatement(sql);
			}

			ps.setString(1, creditCardNumber);
			rs = ps.executeQuery();
			account = mapAccount(rs, conn);
		} catch (SQLException e) {
			throw new RuntimeException("SQL exception occurred finding by credit card number", e);
		} finally {
			if (rs != null) {
				try {
					// Close to prevent database cursor exhaustion
					rs.close();
				} catch (SQLException ex) {
				}
			}
			if (ps != null) {
				try {
					// Close to prevent database cursor exhaustion
					ps.close();
				} catch (SQLException ex) {
				}
			}
			if (conn != null) {
				try {
					// Close to prevent database connection exhaustion
					conn.close();
				} catch (SQLException ex) {
				}
			}
		}
		return account;
	}

	public void updateBeneficiaries(Account account) {
		String sql = "update T_ACCOUNT_BENEFICIARY SET SAVINGS = ? where ACCOUNT_ID = ? and NAME = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			
			ps = conn.prepareStatement(sql);
			for (Beneficiary beneficiary : account.getBeneficiaries()) {
				ps.setBigDecimal(1, beneficiary.getSavings().asBigDecimal());
				ps.setLong(2, account.getEntityId());
				ps.setString(3, beneficiary.getName());
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			throw new RuntimeException("SQL exception occurred updating beneficiary savings", e);
		} finally {
			if (ps != null) {
				try {
					// Close to prevent database cursor exhaustion
					ps.close();
				} catch (SQLException ex) {
				}
			}
			if (conn != null) {
				try {
					// Close to prevent database connection exhaustion
					conn.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	/**
	 * Map the rows returned from the join of T_ACCOUNT and T_ACCOUNT_BENEFICIARY to an fully-reconstituted Account
	 * aggregate.
	 * @param rs the set of rows returned from the query
	 * @return the mapped Account aggregate
	 * @throws SQLException an exception occurred extracting data from the result set
	 * Validamos también para Oracle
	 */
	private Account mapAccount(ResultSet rs, Connection conn) throws SQLException {
		Account account = null;
		while (rs.next()) {
			if (account == null) {
				if (conn.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle")) {
					String numberOracle = rs.getString("ACCOUNT_NUMBER");
					String nameOracle = rs.getString("ACCOUNT_NAME");
					account = new Account(numberOracle, nameOracle);
					account.setEntityId(rs.getLong("ID"));
				} else {
					String number = rs.getString("NUMBER");// "ACCOUNT_NUMBER" para oracle, NUMBER no se puede usar como nombre de columna en Oracle
					String name = rs.getString("NAME");// "ACCOUNT_NAME" para Oracle
					account = new Account(number, name);
					// set internal entity identifier (primary key)
					account.setEntityId(rs.getLong("ID"));
				}
			}
			Beneficiary b = mapBeneficiary(rs);
			if (b != null) {
				account.restoreBeneficiary(b);
			}
		}
		if (account == null) {
			// no rows returned - throw an empty result exception
			throw new EmptyResultDataAccessException(1);
		}
		return account;
	}

	/**
	 * Maps the beneficiary columns in a single row to an AllocatedBeneficiary object.
	 * @param rs the result set with its cursor positioned at the current row
	 * @return an allocated beneficiary
	 * @throws SQLException an exception occurred extracting data from the result set
	 */
	private Beneficiary mapBeneficiary(ResultSet rs) throws SQLException {
		String name = rs.getString("BENEFICIARY_NAME");
		if (name == null) {
			// apparently no beneficiary for this 
			return null;
		}		
		MonetaryAmount savings = MonetaryAmount.valueOf(rs.getString("BENEFICIARY_SAVINGS"));
		Percentage allocationPercentage = Percentage.valueOf(rs.getString("BENEFICIARY_ALLOCATION_PERCENTAGE"));
		return new Beneficiary(name, allocationPercentage, savings);
	}
}