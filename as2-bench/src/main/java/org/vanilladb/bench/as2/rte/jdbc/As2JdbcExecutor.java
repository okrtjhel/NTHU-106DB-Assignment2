package org.vanilladb.bench.as2.rte.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.vanilladb.bench.as2.As2TransactionType;
import org.vanilladb.bench.remote.SutResultSet;
import org.vanilladb.bench.rte.jdbc.JdbcExecutor;

public class As2JdbcExecutor implements JdbcExecutor<As2TransactionType> {

	@Override
	public SutResultSet execute(Connection conn, As2TransactionType txType, Object[] pars)
			throws SQLException {
		switch (txType) {
		case READ_ITEM:
			return new As2ReadItemJob().execute(conn, pars);
		case UPDATE_ITEM:
			return new As2UpdatePriceJob().execute(conn, pars);
		case TESTBED_LOADER:
			return new As2TestbedLoaderJob().execute(conn,pars);
		case SCHEMA_BUILDER:
			return new As2SchemaBuilderJob().execute(conn, pars);
		default:
			throw new UnsupportedOperationException(
					String.format("no JDCB implementation for '%s'", txType));
		}
	}

}
