package org.mobley.album;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBUtils {

	private static Map<String,Connection> connections = new HashMap<String,Connection>();
	
	public static void executeQuery(ExecuteQueryCBH cbh) {
		executeQuery(cbh,"music");
	}
	
	public static void executeQuery(ExecuteQueryCBH cbh, String dbName) {

		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = DBUtils.getConnection(dbName).createStatement();
			rs = stmt.executeQuery(cbh.getQuery());
			cbh.processResultSet(rs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void executePreparedStatement(PreparedStatementCBH cbh,
			boolean getAIValue, boolean batch) throws Exception {
		executePreparedStatement(cbh,getAIValue,batch,"music");
	}

	public static void executePreparedStatement(PreparedStatementCBH cbh,
			boolean getAIValue, boolean batch, String dbName) throws Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (getAIValue) {
				stmt = DBUtils.getConnection(dbName).prepareStatement(cbh.getQuery(),
						Statement.RETURN_GENERATED_KEYS);
			} else {
				stmt = DBUtils.getConnection(dbName).prepareStatement(cbh.getQuery());
			}
			cbh.prepareStatement(stmt);
			if(batch) {
				stmt.executeBatch();
			} else {
			    if(stmt.execute()) {
			    	cbh.processResultSet(stmt.getResultSet());
			    }
			}
			if (getAIValue) {
				rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					cbh.setAutoIncrementKey(rs.getInt(1));
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static int executeUpdate(String statement) {
		return executeUpdate(statement, "music");
	}
	
	public static int executeUpdate(String statement, String dbName) {

		Statement stmt = null;
		int result = 0;
		try {
			stmt = DBUtils.getConnection(dbName).createStatement();
			result = stmt.executeUpdate(statement);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static Connection getConnection() {
		return getConnection("music");
	}

	public static Connection getConnection(String dbName) {
		Connection conn = connections.get(dbName);
		if (conn == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String s = "jdbc:mysql://localhost/" + dbName + "?user=amobley&password=d5yHeaeK";
				conn = DriverManager
						.getConnection(s);
				connections.put(dbName, conn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conn;
	}

	public static void startTransaction(String dbName) throws SQLException {
		Connection conn = getConnection(dbName);
		conn.setAutoCommit(false);
	}

	public static void commitTransaction(String dbName) throws SQLException {
		Connection conn = getConnection(dbName);
		conn.commit();
	}

	public static void rollbackTransaction(String dbName) throws SQLException {
		Connection conn = getConnection(dbName);
		conn.rollback();
	}

	public static interface ExecuteQueryCBH {
		public void processResultSet(ResultSet rs) throws SQLException;

		public String getQuery();
	}

	public static interface PreparedStatementCBH {
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException;

		public String getQuery();

		public void setAutoIncrementKey(int key);

		public int getAutoIncrementKey();
		
		public void processResultSet(ResultSet rs) throws SQLException;
	}
}
