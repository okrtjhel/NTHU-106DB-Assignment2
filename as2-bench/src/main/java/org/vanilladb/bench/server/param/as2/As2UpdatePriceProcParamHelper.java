package org.vanilladb.bench.server.param.as2;

import org.vanilladb.core.remote.storedprocedure.SpResultSet;
import org.vanilladb.core.sql.DoubleConstant;
import org.vanilladb.core.sql.IntegerConstant;
import org.vanilladb.core.sql.Schema;
import org.vanilladb.core.sql.Type;
import org.vanilladb.core.sql.VarcharConstant;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;
import org.vanilladb.core.sql.storedprocedure.StoredProcedureParamHelper;

public class As2UpdatePriceProcParamHelper extends StoredProcedureParamHelper {

	// Parameters
	private int updateCount;
	private int[] readItemId;
	private double[] updatePrices;

	// Results
	private String[] itemName;
	private double[] itemPrice;

	public int getReadCount() {
		return updateCount;
	}

	public int getReadItemId(int index) {
		return readItemId[index];
	}
	
	public double getUpdatePrice(int index) {
		return updatePrices[index];
	}

	public void setItemName(String s, int idx) {
		itemName[idx] = s;
	}

	public void setItemPrice(double d, int idx) {
		itemPrice[idx] = d;
	}

	@Override
	public void prepareParameters(Object... pars) {

		// Show the contents of paramters
		// System.out.println("Params: " + Arrays.toString(pars));

		int indexCnt = 0;

		updateCount = (Integer) pars[indexCnt++];
		readItemId = new int[updateCount];
		updatePrices = new double[updateCount];
		itemName = new String[updateCount];
		itemPrice = new double[updateCount];

		for (int i = 0; i < updateCount; i++) {
			readItemId[i] = (Integer) pars[indexCnt++];
			updatePrices[i] = (Double) pars[indexCnt++];
		}
	}

	@Override
	public SpResultSet createResultSet() {
		Schema sch = new Schema();
		Type statusType = Type.VARCHAR(10);
		Type intType = Type.INTEGER;
		Type itemPriceType = Type.DOUBLE;
		Type itemNameType = Type.VARCHAR(24);
		sch.addField("status", statusType);
		sch.addField("rc", intType);
		int l = itemName.length;
		for (int i = 0; i < l; i++) {
			sch.addField("i_name_" + i, itemNameType);
			sch.addField("i_price_" + i, itemPriceType);
		}

		SpResultRecord rec = new SpResultRecord();
		String status = isCommitted ? "committed" : "abort";
		rec.setVal("status", new VarcharConstant(status, statusType));
		rec.setVal("rc", new IntegerConstant(l));
		for (int i = 0; i < l; i++) {
			rec.setVal("i_name_" + i, new VarcharConstant(itemName[i], itemNameType));
			rec.setVal("i_price_" + i, new DoubleConstant(itemPrice[i]));
		}

		return new SpResultSet(sch, rec);
	}
}
