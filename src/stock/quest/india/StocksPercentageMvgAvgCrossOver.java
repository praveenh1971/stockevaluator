package stock.quest.india;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class StocksPercentageMvgAvgCrossOver implements
		StockStatisticsCollector {

	private AverageData[] averageDataArray = new AverageData[0];
	HashMap<Date, AverageData> hashMap = new HashMap<Date, AverageData>();
	HashMap<Date, AverageData> hashMapResult = new HashMap<Date, AverageData>();
	
	private int movingAvgLow;
	private int movingAvgHigh;
	private int count = 0;
	public StocksPercentageMvgAvgCrossOver(int movingAvgLow, int movingAvgHigh) {
		super();
		this.movingAvgLow = movingAvgLow;
		this.movingAvgHigh = movingAvgHigh;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "PercentageStocksAboveMovingAverage " + movingAvgLow + " - " +  movingAvgHigh;
	}

	@Override
	public void process(ArrayList<StockData> arrayList) {
		count = arrayList.size();
		for (StockData data : arrayList){
			 MovingAverageAnalyzer analyzerHigh  = data.getMovingAverage(movingAvgHigh);
			 MovingAverageAnalyzer analyzerLow  = data.getMovingAverage(movingAvgLow);

			 if (analyzerHigh == null || analyzerHigh.getMovingAvgList() == null) {
				 System.err.println("Missing DMA for " + data.getName());
				 count--;
				 
			 }
			 ArrayList<AverageData>  arrayListHigh = analyzerHigh.getMovingAvgList();
			for (AverageData averageData : arrayListHigh){
				hashMap.put(averageData.getDate(), averageData);
				
			}
			 ArrayList<AverageData>  arrayListLow = analyzerLow.getMovingAvgList();
			for (AverageData averageDataLow : arrayListLow){
				AverageData dataHigh = hashMap.get( averageDataLow.getDate());
				if (dataHigh == null) continue;
				AverageData dataResult=hashMapResult.get(averageDataLow.getDate());
				if (dataResult== null) {
					dataResult = new AverageData( averageDataLow.getDate(), 0.0, null);
					hashMapResult.put( averageDataLow.getDate(), dataResult);
				}
				if (averageDataLow.getValue() > dataHigh.getValue()){
					dataResult.setValue(dataResult.getValue()+1);
				}
			}
	
		}
	   Collection<AverageData> averageDatas = 	 hashMapResult.values();
	   averageDataArray = averageDatas.toArray(new AverageData[0]);
	   Arrays.sort(averageDataArray, new Comparator<AverageData>() {

		@Override
		public int compare(AverageData o1, AverageData o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	});
	}
	public void save(BufferedWriter bufferedWriter) {
		// Step back 50 days.
	
		for (AverageData data : averageDataArray){
			String str = String.format("date= %1$tm %1$te, %1$tY ; value= %2$4.2f\n", data.getDate(), ( data.getValue() / count) * 100);

			try {
				bufferedWriter.write(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void print() {
		System.out.println("% cross " + movingAvgLow + "  Over " + movingAvgHigh);

		// TODO Auto-generated method stub
		for (AverageData data : averageDataArray){
			String str = String.format("%1$tm/%1$te/%1$tY;%2$4.2f", data.getDate(), ( data.getValue() / count) * 100);

			System.out.println(str);
		}
		
	}


}
