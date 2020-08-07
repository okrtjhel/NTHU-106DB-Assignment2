package org.vanilladb.bench;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*; // Added by TY

import org.vanilladb.bench.util.BenchProperties;

public class StatisticMgr {
	private static Logger logger = Logger.getLogger(StatisticMgr.class.getName());

	private static final String OUTPUT_DIR;

	private List<TxnResultSet> resultSets = new ArrayList<TxnResultSet>();

	private static long benchStartTime;

	private static int GRANULARITY;

	private static TreeMap<Long, ArrayList<Long>> latencyHistory;

	static {
		File defaultDir = new File(System.getProperty("user.home"), "benchmark_results");
		OUTPUT_DIR = BenchProperties.getLoader().getPropertyAsString(StatisticMgr.class.getName() + ".OUTPUT_DIR",
				defaultDir.getAbsolutePath());

		// Create the directory if that doesn't exist
		File dir = new File(OUTPUT_DIR);
		if (!dir.exists())
			dir.mkdir();
		benchStartTime = System.nanoTime();
		GRANULARITY = BenchProperties.getLoader().getPropertyAsInteger(StatisticMgr.class.getName() + ".GRANULARITY",
				5000);
		latencyHistory = new TreeMap<Long, ArrayList<Long>>();
	}

	private List<TransactionType> allTxTypes;

	public StatisticMgr(Collection<TransactionType> txTypes) {
		allTxTypes = new LinkedList<TransactionType>(txTypes);
	}

	public synchronized void processTxnResult(TxnResultSet trs) {
		resultSets.add(trs);
	}

	public synchronized void processBatchTxnsResult(TxnResultSet... trss) {
		for (TxnResultSet trs : trss)
			resultSets.add(trs);
	}
	
	public synchronized void outputReport() {
		HashMap<TransactionType, TxnStatistic> txnStatistics = new HashMap<TransactionType, TxnStatistic>();
		
		for (TransactionType type : allTxTypes)
			txnStatistics.put(type, new TxnStatistic(type));

		try {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
			String timeString = formatter.format(Calendar.getInstance().getTime());
			String fileName = timeString + ".csv";

			File dir = new File(OUTPUT_DIR);
			File outputFile = new File(dir, fileName);
			FileWriter wrFile = new FileWriter(outputFile);
			BufferedWriter bwrFile = new BufferedWriter(wrFile);
			
			int committedCount = 0;

			/* Modify by TY ... */ 
			
			// Report fields ... 
			bwrFile.write("time(sec), throughput(txs), avg_latency(ms), min(ms), max(ms), 25th_lat(ms), median_lat(ms), 75th_lat(ms)");
			bwrFile.newLine();

			// read all txn resultset
			for (TxnResultSet resultSet : resultSets) {
				if (resultSet.isTxnIsCommited()) {
					TxnStatistic txnStatistic = txnStatistics.get(resultSet.getTxnType());
					if (txnStatistic != null)
						txnStatistic.addTxnResponseTime(resultSet.getTxnResponseTime());
					addTxnLatency(resultSet);
					committedCount++;
				} 
			}

			// output summary for each transaction type
			for (Map.Entry<Long, ArrayList<Long>> record : latencyHistory.entrySet()) {
				bwrFile.write(analyzeRecord(record));
				bwrFile.newLine();
			}
			
			// TOTAL
			double avgResTimeMs = 0;
			
			if (committedCount > 0) {
				for (TxnResultSet rs : resultSets) {
					if (rs.isTxnIsCommited())
						avgResTimeMs += rs.getTxnResponseTime() / committedCount;
				}
			}

			bwrFile.write(String.format("Total %d Aborted %d Commited %d avg Commited latency: %d ms",
					resultSets.size(), resultSets.size() - committedCount, committedCount, Math.round(avgResTimeMs / 1000000)));

			bwrFile.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (logger.isLoggable(Level.INFO))
			logger.info("Finnish creating tpcc benchmark report");
	}

	/* Modify ... */
	
	private String analyzeRecord(Map.Entry<Long, ArrayList<Long>> record) {
		
		Long[] recordData = record.getValue().toArray(new Long[record.getValue().size()]);
		
		//Sort
		Arrays.sort(recordData);
		
		int recordLen = record.getValue().size();
		
		long recordQ, record2Q, record3Q;
		long recordMin, recordMax, recordAVG;
		long recordThroughput;
		long recordTime;
		
		recordTime = record.getKey();
		
		recordThroughput = record.getValue().size();
		
		recordAVG = 0;
		for (Long i : recordData ) {
			recordAVG += i;
		}
		recordAVG /= recordLen;
		
		recordMin = Collections.min(record.getValue());
		
		recordMax = Collections.max(record.getValue());
		
		if (recordLen % 4 == 0 ) {
			recordQ  = (recordData[recordLen/4] + recordData[recordLen/4+1]) / 2;
			record3Q = (recordData[recordLen*3/4] + recordData[recordLen*3/4+1]) / 2;
		} else {
			recordQ  =  recordData[recordLen/4+1];
			record3Q =  recordData[recordLen*3/4+1];
		}
		
		if (recordLen % 2 == 0 ) {
			record2Q =  (recordData[recordLen/2] + recordData[recordLen/2+1]) / 2;
		} else {
			record2Q =   recordData[recordLen/2+1];
		}
		
		return Long.toString(recordTime) + ',' + Integer.toString(recordLen) + ',' + Long.toString(recordAVG) + ',' + Long.toString(recordMin)
		+ ',' + Long.toString(recordMax) + ',' + Long.toString(recordQ) + ',' + Long.toString(record2Q) + ',' + Long.toString(record3Q);
	}
	
	public void addTxnLatency(TxnResultSet rs) {
		long t = TimeUnit.NANOSECONDS.toMillis(rs.getTxnEndTime() - benchStartTime);
		t = (((t - BenchmarkerParameters.WARM_UP_INTERVAL) / GRANULARITY) * GRANULARITY
				+ BenchmarkerParameters.WARM_UP_INTERVAL) / 1000;

		if (!latencyHistory.containsKey(t))
			latencyHistory.put(t, new ArrayList<Long>());

		latencyHistory.get(t).add(TimeUnit.NANOSECONDS.toMillis(rs.getTxnResponseTime()));
	}
	

	private static class TxnStatistic {
		private TransactionType mType;
		private int txnCount = 0;
		private long totalResponseTimeNs = 0;

		public TxnStatistic(TransactionType txnType) {
			this.mType = txnType;
		}

		public TransactionType getmType() {
			return mType;
		}

		public void addTxnResponseTime(long responseTime) {
			txnCount++;
			totalResponseTimeNs += responseTime;
		}

		public int getTxnCount() {
			return txnCount;
		}

		public long getTotalResponseTime() {
			return totalResponseTimeNs;
		}
	}
}
