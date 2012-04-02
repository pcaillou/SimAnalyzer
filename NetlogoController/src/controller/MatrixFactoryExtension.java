package controller;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.exceptions.MatrixException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class MatrixFactoryExtension extends MatrixFactory {

	public static void exportToJDBC(String url, String username, String password, Matrix matrix, String tablename) throws ClassNotFoundException, SQLException, MatrixException, IOException
	{
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = (Connection) DriverManager.getConnection(url, username, password);
		Statement stmt = (Statement) conn.createStatement();
		for(int i=0;i<matrix.getRowCount();i++)
		{
			Matrix row = matrix.selectRows(Ret.LINK,i);		
			String sql = "insert into "+tablename+" values(";
			for(int j=0;j<matrix.getColumnCount();j++)
            {
                boolean res = true;
                for(int n=0;n<matrix.getRowCount();n++)
        		{
                    try{
                    	Double.parseDouble(matrix.getAsString(n,j));
                    }catch(Exception   ex){
                        res   =   false;
                    }
        		}
                if(j!=matrix.getColumnCount()-1 )
                {
                	if(res)
                		sql = sql+row.getAsDouble(0,j)+",";
                    else
                	    sql = sql+"'"+row.getAsString(0,j)+"',";
                }
                else
                {
                	if(res)
                		sql = sql+row.getAsDouble(0,j)+")";
                    else
                	    sql = sql+"'"+row.getAsString(0,j)+"')";
                }
            }
			stmt.executeUpdate(sql);
		}
		
		
	}

}
