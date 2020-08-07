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

public class As2TestbedLoaderJob implements JdbcJob {
	private static Logger logger = Logger.getLogger(As2UpdatePriceJob.class
			.getName());

	@Override
	public SutResultSet execute(Connection conn, Object[] pars) throws SQLException {
		int iid, iimid;
		String iname, idata;
		double iprice;
		String sql;
		
		// Output message
		StringBuilder outputMsg = new StringBuilder("[");
		
		// Execute logic
		try {
			Statement stat = conn.createStatement();
			for (int i = 1; i <= As2Constants.NUM_ITEMS; i++) {
				iid = i;
	
				// Deterministic value generation by item id
				iimid = iid % (As2Constants.MAX_IM - As2Constants.MIN_IM) + As2Constants.MIN_IM;
				iname = String.format("%0" + As2Constants.MIN_I_NAME + "d", iid);
				iprice = (iid % (int) (As2Constants.MAX_PRICE - As2Constants.MIN_PRICE)) + As2Constants.MIN_PRICE;
				idata = String.format("%0" + As2Constants.MIN_I_DATA + "d", iid);
				
				sql = "INSERT INTO item(i_id, i_im_id, i_name, i_price, i_data) VALUES (" + iid + ", " + iimid + ", '"
						+ iname + "', " + iprice + ", '" + idata + "' )";
				
				stat.executeUpdate(sql);
			}
			conn.commit();
			
			outputMsg.append("Finish Loading Testbed");
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
