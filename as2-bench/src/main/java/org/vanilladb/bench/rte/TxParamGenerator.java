package org.vanilladb.bench.rte;

import org.vanilladb.bench.TransactionType;
import org.vanilladb.bench.as2.As2TransactionType;

public interface TxParamGenerator<T extends TransactionType> {
	
	T getTxnType();
	
	void setTxnType(As2TransactionType tt);

	Object[] generateParameter();
	
}
