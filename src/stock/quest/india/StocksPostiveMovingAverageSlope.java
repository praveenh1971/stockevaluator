package stock.quest.india;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class StocksPostiveMovingAverageSlope implements StockStatisticsCollector{
	private AverageData[] averageDataArray = new AverageData[0];
	HashMap<Date, AverageData> hashMap = new HashMap<Date, AverageData>();
	private int MovingAvg;
	private int MovingAvgMajor;
	private int count = 0;
	public StocksPostiveMovingAverageSlope(int movingAvg, int major) {
		super();
		MovingAvg = movingAvg;
		this.MovingAvgMajor = major;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "PercentageStocksAboveMovingAverage " + MovingAvg;
	}

	@Override
	public void process(ArrayList<StockData> arrayList) {
		count = arrayList.size();
		for (StockData data : arrayList){
			 MovingAverageAnalyzer analyzer  = data.getMovingAverage(MovingAvg);
			 MovingAverageAnalyzer analyzerMajor = data.getMovingAverage(MovingAvgMajor);
			 if (analyzer == null || analyzer.getMovingAvgList() == null) {
				 System.err.println("Missing DMA for " + data.getName());
				 count--;
				 
			 }
			 ArrayList<AverageData>  arrayList2 = analyzer.getMovingAvgList();
			
			 for (AverageData averageData : arrayList2){
				AverageData data1 = hashMap.get( averageData.getDate());
				if (data1 == null) {
					data1 = new AverageData( averageData.getDate(), 0.0, null);
					hashMap.put( averageData.getDate(), data1);
				}
				double d = data1.getValue(); //
				double per  = data1.getSlope();
				if (averageData.getSlope() > 0 ){
						data1.setValue(d+1);
				}
			}
		}
	   Collection<AverageData> averageDatas = 	 hashMap.values();
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
		// TODO Auto-generated method stub
		System.out.println(" % positive slope " + MovingAvg);
		for (AverageData data : averageDataArray){
			String str = String.format("%1$tm %1$te, %1$tY ;%2$4.2f", data.getDate(), ( data.getValue() / count) * 100);

			System.out.println(str);
		}
		
	}

}
