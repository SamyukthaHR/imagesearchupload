package com.searchupload.aws.serviceimpl;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.searchupload.aws.model.Fileupload;
import com.searchupload.aws.service.SqlService;

@Service
public class SqlServiceImpl implements SqlService{
	private Logger logger = LoggerFactory.getLogger(SqlServiceImpl.class);

	@Value("${db_url}")
	private String dbURL;

	@Value("${user_name}")
	private String user;

	@Value("${pass_word}")
	private String pass;
	
	@Value("${query}")
	private String sql;

	@Override
	public boolean uploadtodb(Fileupload file) {
		 
		   try(Connection conn = connect();
				   Statement stmt = conn.createStatement()) {
			   conn.setAutoCommit(false);
			  logger.info("Connected database successfully...");
		      logger.info("Creating table in given database...");
		      String sqlquery = "CREATE TABLE IF NOT EXISTS IMAGEAWS " +
	                   "(name VARCHAR(255) not NULL, " +
	                   " type VARCHAR(255), " + 
	                   " size INTEGER, " + 
	                   " PRIMARY KEY ( name ))"; 
		      stmt.executeUpdate(sqlquery);
		      conn.commit();
		      logger.info("Created table in given database...");
		      	      
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }
		   
		      	  
		   if(insertfiledetails(file)) {
		   logger.info("Inserted file details to DB");
		   logger.info("Goodbye!");
		   return true;
		   }
		   return false;
		   
	}	   	
	
	public Connection connect() throws SQLException {
        return DriverManager.getConnection(dbURL, user, pass);
    }
	
	public boolean insertfiledetails(Fileupload file) {
        
        int id = 0;

        try (Connection conn = connect();) {
        	conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
        	
            pstmt.setString(1, file.getName());
            pstmt.setString(2, file.getType());
            pstmt.setLong(3, file.getSize());

            int affectedRows = pstmt.executeUpdate();
            id = affectedRows;
            String s = String.valueOf(id);
            logger.info(s);
            }
            catch(SQLException ex)
            {
                conn.rollback();
                conn.setAutoCommit(true);
                logger.info("Rolled back Successfully!");
                throw ex;
            }
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException ex) {
            	logger.error(ex.getMessage());
            	return false;
        }
    }

}
