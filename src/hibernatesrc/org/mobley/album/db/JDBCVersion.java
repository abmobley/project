package org.mobley.album.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

public class JDBCVersion
{

   public static void main(String[] args) throws SQLException
   {
      OracleDataSource ods = new OracleDataSource();
      ods.setURL("jdbc:oracle:thin:hr/DrtWe41@localhost:1521/XE");
      Connection con = ods.getConnection();
      
      DatabaseMetaData metaData = con.getMetaData();
      System.out.println("JDBC driver version is " +  metaData.getDriverVersion());
      
      con.close();
   }

}
