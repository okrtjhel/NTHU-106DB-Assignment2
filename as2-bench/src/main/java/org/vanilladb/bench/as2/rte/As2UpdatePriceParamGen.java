package org.vanilladb.bench.as2.rte;

import java.util.LinkedList;

import org.vanilladb.bench.as2.As2Constants;
import org.vanilladb.bench.as2.As2TransactionType;
import org.vanilladb.bench.rte.TxParamGenerator;
import org.vanilladb.bench.util.RandomValueGenerator;

public class As2UpdatePriceParamGen implements TxParamGenerator<As2TransactionType> {
	private static final int UPDATE_COUNT = 10;

	@Override
	public As2TransactionType getTxnType() {
		return As2TransactionType.UPDATE_ITEM;
	}

	@Override
	public Object[] generateParameter() {
		RandomValueGenerator rvg = new RandomValueGenerator();
		LinkedList<Object> paramList = new LinkedList<Object>();
		
		paramList.add(UPDATE_COUNT);
		for (int i = 0; i < UPDATE_COUNT; i++) {
			paramList.add(rvg.number(1, As2Constants.NUM_ITEMS));
			paramList.add(rvg.randomDoubleIncrRange(As2Constants.MIN_PRICE, As2Constants.MAX_PRICE, 1.0));
		}

		return paramList.toArray();
	}

	@Override
	public void setTxnType(As2TransactionType tt) {
		// TODO Auto-generated method stub
		
	}
}
