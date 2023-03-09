package com.searchupload.aws.serviceimpl;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SqlServiceImplTest {

	String dburl = "jdbc:postgresql://postgresdb.cmpiumy6lzsj.ap-south-1.rds.amazonaws.com:5432/myDatabase";
	String user = "postgres";
	String pass = "postgres";
	Connection con = null;
	
	@Test
	public void testConnect() throws SQLException {
		con = DriverManager.getConnection(dburl, user, pass);
		Assert.assertNotNull(con);
		con.close();
	}
	
	@Test
	public void testCreateTableindb() throws SQLException {
		con = DriverManager.getConnection(dburl, user, pass);
		String createsql = "CREATE TABLE IF NOT EXISTS SAMPLE(name VARCHAR(255));";
		String checktable = "SELECT * FROM SAMPLE;";
		Statement stmt = con.createStatement();
		stmt.execute(createsql);
		boolean check = stmt.execute(checktable);
		
		Assert.assertEquals(true,check);
	}
	
	@Test
	public void testInsertdata() throws SQLException {
		con = DriverManager.getConnection(dburl, user, pass);
		String createsql = "CREATE TABLE IF NOT EXISTS SAMPLE(name VARCHAR(255));";
		String deletesql = "DROP TABLE SAMPLE";
		String insertsql = "INSERT INTO SAMPLE VALUES(?)";
		String checktable = "SELECT * FROM SAMPLE;";
		Statement stmt = con.createStatement();
		stmt.execute(createsql);
		PreparedStatement pstmt = con.prepareStatement(insertsql);
		pstmt.setString(1, "Sample_Entry");
		int i = pstmt.executeUpdate();
		
		Assert.assertEquals(1, i);
		
		stmt.execute(deletesql);
		
	}

}
