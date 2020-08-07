package org.vanilladb.bench.server.procedure.as2;

import org.vanilladb.bench.server.param.as2.As2UpdatePriceProcParamHelper;
import org.vanilladb.bench.server.procedure.BasicStoredProcedure;
import org.vanilladb.core.query.algebra.Plan;
import org.vanilladb.core.query.algebra.Scan;
import org.vanilladb.core.server.VanillaDb;

public class As2UpdatePriceProc extends BasicStoredProcedure<As2UpdatePriceProcParamHelper> {
	
	public As2UpdatePriceProc() {
		super(new As2UpdatePriceProcParamHelper());
	}

	@Override
	protected void executeSql() {
		for (int idx = 0; idx < paramHelper.getReadCount(); idx++) {
			// Select data
			int iid = paramHelper.getReadItemId(idx);
			Plan p = VanillaDb.newPlanner().createQueryPlan(
					"SELECT i_name, i_price FROM item WHERE i_id = " + iid, tx);
			Scan s = p.open();
			s.beforeFirst();
			if (s.next()) {
				String name = (String) s.getVal("i_name").asJavaVal();
				double price = (Double) s.getVal("i_price").asJavaVal();

				paramHelper.setItemName(name, idx);
				paramHelper.setItemPrice(price, idx);
			} else
				throw new RuntimeException("Could not find item record with i_id = " + iid);

			s.close();
			
			// Update prices
			double price = paramHelper.getUpdatePrice(idx);
			Plan pp = VanillaDb.newPlanner().createQueryPlan(
					"UPDATE item SET i_price = " + price + " WHERE i_id = " + iid, tx);
			Scan ss = pp.open();
			ss.close();
		}
	}
}
