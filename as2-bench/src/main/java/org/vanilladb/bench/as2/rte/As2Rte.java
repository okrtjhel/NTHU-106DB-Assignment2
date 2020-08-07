package org.vanilladb.bench.as2.rte;

import org.vanilladb.bench.StatisticMgr;
import org.vanilladb.bench.as2.As2Constants;
import org.vanilladb.bench.as2.As2TransactionType;
import org.vanilladb.bench.remote.SutConnection;
import org.vanilladb.bench.rte.RemoteTerminalEmulator;
import org.vanilladb.bench.util.RandomValueGenerator;
import org.vanilladb.bench.BenchmarkerParameters;

public class As2Rte extends RemoteTerminalEmulator<As2TransactionType> {
	
	private As2TxExecutor executor;
	private As2TxExecutor readValueExecutor;
	private As2TxExecutor setValueExecutor;

	public As2Rte(SutConnection conn, StatisticMgr statMgr) {
		super(conn, statMgr);
		
		/* Modify by TY ... */ 
		
		readValueExecutor = new As2TxExecutor(new As2ParamGen());
		setValueExecutor = new As2TxExecutor(new As2UpdatePriceParamGen());
		executor = readValueExecutor;
	}
	
	protected As2TransactionType getNextTxType() {
		
		/* Modify by TY ... */ 
		
		RandomValueGenerator rvg = new RandomValueGenerator(); 
		if (rvg.number(1, 10) > BenchmarkerParameters.WRITE_TX_RATE ) {
			executor = readValueExecutor;
			return As2TransactionType.READ_ITEM;
		} else {
			executor = setValueExecutor;
			return As2TransactionType.UPDATE_ITEM;
		}
		
	}
	
	protected As2TxExecutor getTxExeutor(As2TransactionType type) {
		return executor;
	}
}
