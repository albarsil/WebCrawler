/**
 * 
 */
package webcrawler.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author  Allan de Barcelos Silva
 *
 */
public class Database {

	private static Database database;

	private static final String TABLE = "VisitedPages";

	private Database(){

	}

	public static Database getInstance(){
		if(database == null)
			database = new Database();

		return database;
	}


	/**
	 * Connect to the test.db database
	 *
	 * @return the Connection object
	 */
	private Connection connect() {
		// SQLite connection string

		String url = "jdbc:sqlite:" + new File("db.sqlite").getAbsolutePath();
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public void clear() throws SQLException{
		Connection con = this.connect();
		con.prepareStatement("DELETE FROM "+ TABLE).execute();
		con.close();
	}

	/**
	 * Insert a new row into the warehouses table
	 *
	 * @param name
	 * @param capacity
	 * @throws SQLException 
	 */
	public void insert(String address, int depth) throws SQLException {
		String sql = "INSERT INTO " + TABLE + " (address, depth) VALUES(?, ?)";

		Connection conn = this.connect();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, address);
		pstmt.setInt(2, depth);

		pstmt.executeUpdate();
		pstmt.close();
		conn.close();
	}

	/**
	 * select all rows in the warehouses table
	 * @throws SQLException 
	 */
	public boolean contains(String address) throws SQLException{
		String sql = "SELECT COUNT(Address) AS total FROM " + TABLE + " WHERE " + TABLE + ".Address LIKE \'" + address + "\'";
		boolean exists = false;

		Connection conn = this.connect();
		Statement stmt  = conn.createStatement();
		ResultSet rs    = stmt.executeQuery(sql);

		exists = rs.getInt("total") > 0 ? true : false;

		rs.close();
		stmt.close();

		return exists;
	}

	private int getLastDepth(){
		String sql = "select distinct depth from " + TABLE + " order by depth desc";
		int lastDepth = 0;

		try (Connection conn = this.connect();
				Statement stmt  = conn.createStatement();
				ResultSet rs   = stmt.executeQuery(sql)){

			lastDepth = rs.getInt("depth");

			stmt.executeQuery("");

			rs.close();
			stmt.close();
			conn.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return lastDepth;
	}   

}
