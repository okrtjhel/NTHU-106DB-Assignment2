package org.vanilladb.bench.as2.rte;

import org.vanilladb.bench.TxnResultSet;

import org.vanilladb.bench.as2.As2TransactionType;
import org.vanilladb.bench.as2.rte.jdbc.As2JdbcExecutor;
import org.vanilladb.bench.remote.SutConnection;
import org.vanilladb.bench.remote.SutResultSet;
import org.vanilladb.bench.rte.TransactionExecutor;
import org.vanilladb.bench.rte.jdbc.JdbcExecutor;

import java.util.Objects;

public class As2TxExecutor extends TransactionExecutor<As2TransactionType> {
	
	private As2JdbcExecutor jdbcExecutor = new As2JdbcExecutor();
	private As2TransactionType tt = null;

	public As2TxExecutor(As2ParamGen pg) {
		this.pg = pg;
		this.tt = As2TransactionType.READ_ITEM;
		this.pg.setTxnType(tt);
	}
	
	public As2TxExecutor(As2UpdatePriceParamGen pg) {
		this.pg = pg;
		this.tt = As2TransactionType.UPDATE_ITEM;
	}
	
	public As2TxExecutor(As2ParamGen pg, As2TransactionType tt) {
		this.pg = pg;
		this.tt = tt;
		this.pg.setTxnType(tt);
	}

	public TxnResultSet execute(SutConnection conn) {
		try {
			TxnResultSet rs = new TxnResultSet();
			rs.setTxnType(pg.getTxnType());

			// generate parameters
			Object[] params = pg.generateParameter();

			// send txn request and start measure txn response time
			long txnRT = System.nanoTime();

			if (Objects.isNull(params)) {
				System.out.println("hello params");
			}
			SutResultSet result = executeTxn(conn, params);

			// measure txn response time
			txnRT = System.nanoTime() - txnRT;

			// display output
			if (TransactionExecutor.DISPLAY_RESULT)
				System.out.println(pg.getTxnType() + " " + result.outputMsg());

			rs.setTxnIsCommited(result.isCommitted());
			rs.setOutMsg(result.outputMsg());
			rs.setTxnResponseTimeNs(txnRT);
			rs.setTxnEndTime();

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	protected JdbcExecutor<As2TransactionType> getJdbcExecutor() {
		return jdbcExecutor;
	}
}
