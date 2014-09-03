package com.sjsu.db2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcDb2 
{
  //public static void main(String[] args) 
  public void db2load(String user, String password, String database, String filename)
	{
    String urlPrefix = "jdbc:db2:";
    //String url;
   // String user;
   // String password;
    
    String empNo;                                                              
    Connection con;
    Statement stmt;
    ResultSet rs;
   
System.out.println(filename);
    /*if (args.length!=2)
    {
      //System.err.println ("Invalid value. First argument appended to "+
      // "jdbc:db2: must specify a valid URL.");
      System.err.println ("First argument must be a valid user ID.");
      System.err.println ("Second argument must be the password for the user ID.");
      System.exit(1);
    }*/
   database = urlPrefix + "//127.0.0.1:50000/DB2TEMP";
    
    try 
    {                                                                        

      Class.forName("com.ibm.db2.jcc.DB2Driver");                              
      System.out.println("**** Loaded the JDBC driver");


      con = DriverManager.getConnection (database, user, password);                 

      con.setAutoCommit(false);
      System.out.println("**** Created a JDBC connection to the data source");

      stmt = con.createStatement();                                            
      rs = stmt.executeQuery("SELECT ID FROM EMPLOYEE");                    

      while (rs.next()) {
        empNo = rs.getString(1);
        System.out.println("Employee number = " + empNo);
      }

      rs.close();
      stmt.close();
      System.out.println("**** Closed JDBC Statement");
      con.commit();
      con.close();                                                             

    }
    
    catch (ClassNotFoundException e)
    {
      System.err.println("Could not load JDBC driver");
      System.out.println("Exception: " + e);
      e.printStackTrace();
    }

    catch(SQLException ex)                                                      
    {
      System.err.println("SQLException information");
      while(ex!=null) {
        System.err.println ("Error msg: " + ex.getMessage());
        System.err.println ("SQLSTATE: " + ex.getSQLState());
        System.err.println ("Error code: " + ex.getErrorCode());
        ex.printStackTrace();
        ex = ex.getNextException(); // For drivers that support chained exceptions
      }
    }
  }  // End main
}    // End EzJava