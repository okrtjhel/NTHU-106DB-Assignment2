package org.vanilladb.bench.as2.rte.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vanilladb.bench.remote.SutResultSet;
import org.vanilladb.bench.remote.jdbc.VanillaDbJdbcResultSet;
import org.vanilladb.bench.rte.jdbc.JdbcJob;

public class As2UpdatePriceJob implements JdbcJob {
	private static Logger logger = Logger.getLogger(As2UpdatePriceJob.class
			.getName());

	@Override
	public SutResultSet execute(Connection conn, Object[] pars) throws SQLException {
		// Parse parameters
		int updateCount = (Integer) pars[0];
		int[] itemIds = new int[updateCount];
		double[] updatePrices = new double[updateCount];
		for (int i = 0; i < updateCount; i++) {
			itemIds[i] = (Integer) pars[2*i + 1];
			updatePrices[i] = (Double) pars[2*i + 2];
		}
		
		// Output message
		StringBuilder outputMsg = new StringBuilder("[");
		
		// Execute logic
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = null;
			for (int i = 0; i < updateCount; i++) {
				// Select the data
				String select_sql = "SELECT i_name, i_price FROM item WHERE i_id = " + itemIds[i];
				rs = statement.executeQuery(select_sql);
				rs.beforeFirst();
				if (rs.next()) {
					outputMsg.append(String.format("('%s', %s), ", rs.getString("i_name"), rs.getString("i_price")));
				} else
					throw new RuntimeException("cannot find the record with i_id = " + itemIds[i]);
				rs.close();
				
				// Update the prices
				
				String update_sql = "UPDATE item SET i_price = " + updatePrices[i] + " WHERE i_id = " + itemIds[i];
				statement.executeUpdate(update_sql);
				
			}
			conn.commit();
			
			outputMsg.deleteCharAt(outputMsg.length() - 2);
			outputMsg.append("]");
			
			return new VanillaDbJdbcResultSet(true, outputMsg.toString());
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning(e.toString());
			return new VanillaDbJdbcResultSet(false, "");
		}
	}

}
