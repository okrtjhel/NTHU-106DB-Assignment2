package org.vanilladb.bench.as2;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.vanilladb.bench.Benchmarker;
import org.vanilladb.bench.StatisticMgr;
import org.vanilladb.bench.TransactionType;
import org.vanilladb.bench.as2.rte.As2ParamGen;
import org.vanilladb.bench.as2.rte.As2Rte;
import org.vanilladb.bench.as2.rte.As2TxExecutor;
import org.vanilladb.bench.remote.SutConnection;
import org.vanilladb.bench.remote.SutDriver;
import org.vanilladb.bench.rte.RemoteTerminalEmulator;
import org.vanilladb.bench.rte.TransactionExecutor;

public class As2Benchmarker extends Benchmarker {
	
	public As2Benchmarker(SutDriver sutDriver) {
		super(sutDriver);
	}
	
	public Set<TransactionType> getBenchmarkingTxTypes() {
		Set<TransactionType> txTypes = new HashSet<TransactionType>();
		for (TransactionType txType : As2TransactionType.values()) {
			if (txType.isBenchmarkingTx())
				txTypes.add(txType);
		}
		return txTypes;
	}

	protected void executeLoadingProcedure(SutConnection conn) throws SQLException {
		conn.callStoredProc(As2TransactionType.SCHEMA_BUILDER.ordinal());
		conn.callStoredProc(As2TransactionType.TESTBED_LOADER.ordinal());
	}
	
	protected void executeLoadingJdbc(SutConnection conn) throws SQLException {
		TransactionExecutor<As2TransactionType> BuildExecutor = new As2TxExecutor(new As2ParamGen(), As2TransactionType.SCHEMA_BUILDER);
		BuildExecutor.execute(conn);
		
		TransactionExecutor<As2TransactionType> LoadExecutor = new As2TxExecutor(new As2ParamGen(), As2TransactionType.TESTBED_LOADER);
		LoadExecutor.execute(conn);
	}
	
	protected RemoteTerminalEmulator<As2TransactionType> createRte(SutConnection conn, StatisticMgr statMgr) {
		return new As2Rte(conn, statMgr);
	}
}
