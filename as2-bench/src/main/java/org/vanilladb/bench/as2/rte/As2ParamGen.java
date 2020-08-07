package org.vanilladb.bench.as2.rte;

import java.util.LinkedList;

import org.vanilladb.bench.as2.As2TransactionType;
import org.vanilladb.bench.as2.As2Constants;
import org.vanilladb.bench.rte.TxParamGenerator;
import org.vanilladb.bench.util.RandomValueGenerator;

public class As2ParamGen implements TxParamGenerator<As2TransactionType> {
	
	private static final int READ_COUNT = 10;
	private As2TransactionType tt;
	
	@Override
	public As2TransactionType getTxnType() {
		return this.tt;
	}
	
	@Override
	public void setTxnType(As2TransactionType tt) {
		this.tt = tt;
	}

	@Override
	public Object[] generateParameter() {
		RandomValueGenerator rvg = new RandomValueGenerator();
		LinkedList<Object> paramList = new LinkedList<Object>();
		
		paramList.add(READ_COUNT);
		for (int i = 0; i < READ_COUNT; i++)
			paramList.add(rvg.number(1, As2Constants.NUM_ITEMS));

		return paramList.toArray();
	}

}

