package org.vanilladb.bench.as2.rte.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vanilladb.bench.as2.As2Constants;
import org.vanilladb.bench.remote.SutResultSet;
import org.vanilladb.bench.remote.jdbc.VanillaDbJdbcResultSet;
import org.vanilladb.bench.rte.jdbc.JdbcJob;

public class As2SchemaBuilderJob implements JdbcJob {
	private static Logger logger = Logger.getLogger(As2UpdatePriceJob.class
			.getName());
	private final String TABLES_DDL = "CREATE TABLE item ( i_id INT, i_im_id INT, i_name VARCHAR(24), "
					+ "i_price DOUBLE, i_data VARCHAR(50) )";
	private final String INDEXES_DDL = "CREATE INDEX idx_item ON item (i_id)";
	
	@Override
	public SutResultSet execute(Connection conn, Object[] pars) throws SQLException {
		
		// Output message
		StringBuilder outputMsg = new StringBuilder("[");
		
		// Execute logic
		try {
			Statement stat = conn.createStatement();
			stat.executeUpdate(TABLES_DDL);
			stat.executeUpdate(INDEXES_DDL);
			
			conn.commit();
			
			outputMsg.append("Finish Building Schema");
			outputMsg.deleteCharAt(outputMsg.length() - 2);
			outputMsg.append("]");
			
			return new VanillaDbJdbcResultSet(true, outputMsg.toString());
		}catch (Exception e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning(e.toString());
			return new VanillaDbJdbcResultSet(false, "");
		}
	}

}
