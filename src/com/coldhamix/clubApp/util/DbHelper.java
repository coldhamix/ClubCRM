package com.coldhamix.clubApp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHelper {
	
	/**
	 * Create a table for students entities if it does not exist
	 * @see com.coldhamix.clubApp.entities.Student
	 */
	private static final String CREATE_STUDENTS_TABLE = 
										"CREATE TABLE if not exists [students] ( " +
										"[id] INTEGER NOT NULL PRIMARY KEY," + 
										"[name] TEXT  NOT NULL, " + 
										"[birthday] INTEGER NOT NULL, " + 
										"[phone] TEXT, " + 
										"[parentName] TEXT, " + 
										"[institution] TEXT, " + 
										"[archive] BOOLEAN NOT NULL" +
										")";

	/**
	 * Create a table for lessons entities if it does not exist
	 * @see com.coldhamix.clubApp.entities.Course
	 */
	private static final String CREATE_LESSONS_TABLE = 
										"CREATE TABLE if not exists [lessons] ( " +
										"[id] INTEGER NOT NULL PRIMARY KEY, " + 
										"[title] TEXT NOT NULL, " +
										"[price] FLOAT NOT NULL, " +
										"[archive] BOOLEAN NOT NULL" +
										")";
	
	/**
	 * Create a table for groups entities if it does not exist
	 * @see com.coldhamix.clubApp.entities.Group
	 */
	private static final String CREATE_GROUPS_TABLE = 
										"CREATE TABLE if not exists [groups] ( " +
										"[id] INTEGER NOT NULL PRIMARY KEY, " +
										"[lessonId] INTEGER, " +
										"[archive] BOOLEAN NOT NULL" +
										")";

	/**
	 * Create a table for visits entities if it does not exist
	 * @see com.coldhamix.clubApp.entities.Visit
	 */
	private static final String CREATE_VISITS_TABLE = 
										"CREATE TABLE if not exists [visits] ( " +
										"[id] INTEGER NOT NULL PRIMARY KEY, " + 
										"[studentId] INTEGER, " +
										"[groupId] INTEGER, " +
										"[timestamp] INTEGER," +
										"[archive] BOOLEAN NOT NULL" +
										")";

	/**
	 * Create a table for members entities if it does not exist
	 * @see com.coldhamix.clubApp.entities.Member
	 */
	private static final String CREATE_MEMBERS_TABLE = 
										"CREATE TABLE if not exists [members] ( " +
										"[id] INTEGER NOT NULL PRIMARY KEY, " + 
										"[studentId] INTEGER, " +
										"[groupId] INTEGER, " +
										"[archive] BOOLEAN NOT NULL" +
										")";

	/**
	 * Create a table for payments entities if it does not exist
	 * @see com.coldhamix.clubApp.entities.Payment
	 */
	private static final String CREATE_PAYMENTS_TABLE = 
										"CREATE TABLE if not exists [payments] ( " +
										"[id] INTEGER NOT NULL PRIMARY KEY, " + 
										"[studentId] INTEGER, " +
										"[timestamp] INTEGER, " +
										"[payedFor] INTEGER," + 
										"[archive] BOOLEAN NOT NULL" +
										")";
	
	/**
	 * Stores current database connection
	 */
	private static Connection currentConnection = null;
	
	/**
	 * Create table using default connection
	 * @return Whether created successfully or not
	 */
	public static boolean createTables()
	{
		return createTables(currentConnection);
	}
	
	/**
	 * Create tables
	 * @param c Database connection
	 * @return Whether created successfully or not
	 */
	public static boolean createTables(Connection c)
	{
		try {
			Statement stmt = c.createStatement();
			
			// create tables
			stmt.execute(CREATE_STUDENTS_TABLE);
			stmt.execute(CREATE_LESSONS_TABLE);
			stmt.execute(CREATE_GROUPS_TABLE);
			stmt.execute(CREATE_MEMBERS_TABLE);
			stmt.execute(CREATE_VISITS_TABLE);
			stmt.execute(CREATE_PAYMENTS_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Connect to the sqlite database
	 * @param dbName Database name
	 * @return Whether connected successfully or not
	 */
	public static Connection connect(String dbName)
	{		
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC"); // if class does not exist, this line throws an exception
			c = DriverManager.getConnection("jdbc:sqlite:" + dbName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		currentConnection = c;		
		return c;
	}
	
	/**
	 * @return Current connection
	 */
	public static Connection getConnection() {
		return currentConnection;
	}
}
