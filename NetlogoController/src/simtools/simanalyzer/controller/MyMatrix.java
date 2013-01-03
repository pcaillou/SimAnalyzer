package simtools.simanalyzer.controller;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;

public class MyMatrix {

	public static Matrix appendHorizontally(Matrix m1, Matrix m2)
	{
		return appendHorizontally( m1, m2, false);
		
	}
	public static DenseDoubleMatrix2D appendHorizontallyDD(DenseDoubleMatrix2D m1, DenseDoubleMatrix2D m2)
	{
		return appendHorizontallyDD( m1, m2, false);
		
	}	
	
	public static Matrix appendHorizontally(Matrix m1, Matrix m2, boolean m2labels)
	{
		Matrix res;
		Matrix n2 = m1.appendHorizontally(m2);	
		res=n2.subMatrix(Ret.NEW, 0, 0, n2.getRowCount()-1, n2.getColumnCount()-1);
		if (m2labels)
		{
			n2 = m2.appendHorizontally(m1).appendHorizontally(m2);	
			res=n2.subMatrix(Ret.NEW, 0, m2.getColumnCount(), n2.getRowCount()-1, n2.getColumnCount()-1);
		}
		for(long column =0; column<m1.getColumnCount(); column++ ){
			res.setColumnLabel(column, m1.getColumnLabel(column));}
		
		for(long column =0; column<m2.getColumnCount(); column++ ){
			long nc=m1.getColumnCount()+column;
			res.setColumnLabel(nc, m2.getColumnLabel(column));
		}
		
		for(long row =0; row<m1.getRowCount(); row++ ){
			if (m2labels)
			{
				res.setRowLabel(row, m2.getRowLabel(row));
			}
			else
			{
				res.setRowLabel(row, m1.getRowLabel(row));
			}
		}
		return res;
		
	}
	public static DenseDoubleMatrix2D appendHorizontallyDD(DenseDoubleMatrix2D m1, DenseDoubleMatrix2D m2, boolean m2labels)
	{
		DenseDoubleMatrix2D res;
		DenseDoubleMatrix2D n2 = (DenseDoubleMatrix2D) m1.appendHorizontally(m2);	
		res=(DenseDoubleMatrix2D) n2.subMatrix(Ret.NEW, 0, 0, n2.getRowCount()-1, n2.getColumnCount()-1);
		if (m2labels)
		{
			n2 = (DenseDoubleMatrix2D) m2.appendHorizontally(m1).appendHorizontally(m2);	
			res=(DenseDoubleMatrix2D) n2.subMatrix(Ret.NEW, 0, m2.getColumnCount(), n2.getRowCount()-1, n2.getColumnCount()-1);
		}
		for(long column =0; column<m1.getColumnCount(); column++ ){
			res.setColumnLabel(column, m1.getColumnLabel(column));}
		
		for(long column =0; column<m2.getColumnCount(); column++ ){
			long nc=m1.getColumnCount()+column;
			res.setColumnLabel(nc, m2.getColumnLabel(column));
		}
		
		for(long row =0; row<m1.getRowCount(); row++ ){
			if (m2labels)
			{
				res.setRowLabel(row, m2.getRowLabel(row));
			}
			else
			{
				res.setRowLabel(row, m1.getRowLabel(row));
			}
		}
		return res;
		
	}
	
}
