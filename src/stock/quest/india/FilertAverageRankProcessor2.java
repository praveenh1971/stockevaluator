package stock.quest.india;

import java.io.BufferedWriter;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class FilertAverageRankProcessor2 implements StockStatisticsCollector{
	private static final long WEEK = 5*60*60*24*1000L;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
	int movingDays;
	ArrayList<StockRank> rankings = new ArrayList<StockRank>();
	public FilertAverageRankProcessor2(int movingDays) {
		super();
		this.movingDays = movingDays;
	}
	public void print(){
		for (StockRank rank : rankings){
			String str = String.format("%1$s rank-> %2$d data ->%3$s performance-> %4$4.2f" , rank.getData().getName(), rank.getRank(),rank.getPrevData() ,rank.getPerformance() );

			System.out.println(str);
			//	System.out.println(rank.getData().getName() + " rank" +  rank.getRank() + " cur = " + rank.getCurrenntData());

		}
		for (int j = 0 ; j < rankings.size();j++){
			double averagePer = 0.0;
			int start = j;
			int up = 0;
			for (int i = 0 ; i < 25 && j < rankings.size() ; i++ ,j++){
				double per = rankings.get(j).getPerformance();
				if (per < -8.0) per = -8.0;
				averagePer +=  per;
				up +=  (rankings.get(j).getPerformance() > 0) ? 1 :0 ;
			}
			int end = j;
			System.out.println(" Performance from = "  + start + " end = " + end + " performance " +  averagePer /(end -start)  + " up = "  +up  );
			
		}
	}
	@Override
	public void process(ArrayList<StockData> arrayList) {
		Date date = new Date();
		long daysBack = 1*60*60*24*1000L;
		long noOfDaysToCompare = 90*60*60*24*1000L;
		long noOfDaysToPrev = 60*60*60*24*1000L;
		Date date2 = date; 
		try {
			date2 = dateFormat.parse("11192012");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Step back 50 days.
		final Date compareDate = date2;//new Date(date.getTime() - daysBack);
		final Date comparePrev = new Date(compareDate.getTime() - noOfDaysToPrev);

		final Date compareDate2 = date;// new Date(compareDate.getTime() + noOfDaysToCompare);
		
		System.out.println(" Date to compare " + compareDate + " to " + compareDate2);
		StockData[] datas = arrayList.toArray(new StockData[]{});
		Arrays.sort(datas,new Comparator<StockData>() {
			@Override
			public int compare(StockData o1, StockData o2) {
				MovingAverageAnalyzer average1 = o1.getMovingAverage(movingDays);
//				MovingAverageAnalyzer average201 = o1.getMovingAverage(20);
				MovingAverageAnalyzer average2 = o2.getMovingAverage(movingDays);
//				MovingAverageAnalyzer average202 = o2.getMovingAverage(20);
				int noOfDays1 = 0;
				int noOfDays2 = 0;
				AverageData data1 = average1.getMovingAvg(compareDate);
				AverageData data2 = average2.getMovingAvg(compareDate);
				
				if (data1 == null) {
					return 1;
				}
				if (data2== null) {
					return -1;
				}
				if (data1.getSlope() > 0 && data2.getSlope() > 0 ){
//					noOfDays1 = getNoOfDays(data1);  
//					noOfDays2 = getNoOfDays(data2); 
//
//					if (noOfDays1 > noOfDays2) 	{
//						return -1;
//					}
//					if (noOfDays1 < noOfDays2) 	{
//						return 1;
//					}
//					if (noOfDays1 > 0 && noOfDays2 > 0)
//						return noOfDays1 - noOfDays2;
				}
				if (data1.getSlope() > data2.getSlope()){
					return -1;
				}
				if (data1.getSlope() < data2.getSlope()){
					return 1;
				}

				return 0;
			}
			
		});
		System.out.println(" Filtering " + datas.length + " STOCKS ***********88888");
		for (int i = 0 ; i < datas.length ; i++ ){
			MovingAverageAnalyzer average = datas[i].getMovingAverage(movingDays);
			AverageData averageData1 =	average.getMovingAvg(compareDate);
			if (shouldAdd( average,datas[i],compareDate )){
					System.out.println("filtering " + datas[i].getName() + " rank " + (i+1) + " PASSEDDDDDD****");
					rankings.add(new StockRank(datas[i], i +1, averageData1, average.getMovingAvg(compareDate2)));
			}
		}
		
		// Apply filters
		
	}
private int getNoOfDays(AverageData data){
	double percentage = ((data.getStockTicker().getClose() -  data.getValue()) /  data.getValue())*100;
	
	int noOfDays1 = (int) (percentage / data.getSlope());  
	return noOfDays1;

}
	private boolean shouldAdd(MovingAverageAnalyzer average,StockData data, Date compareDate) {
		
		MovingAverageAnalyzer average100 = data.getMovingAverage(100);
		MovingAverageAnalyzer average20 = data.getMovingAverage(20);
		MovingAverageAnalyzer average50 = data.getMovingAverage(50);

		
		// Price should be above 100 SMA
		AverageData averageData100 = average100.getMovingAvg(compareDate);

		if (average100 == null || averageData100== null) {
			System.out.println( data.getName() + "rejected because MISSING 100 SMA " + data.getName());

			return false;
		}

		AverageData averageData20 = average20.getMovingAvg(compareDate);
		AverageData averageData50 = average50.getMovingAvg(compareDate);
		
		if (averageData100.getStockTicker().getClose() < averageData100.getValue() ) {
			System.out.println(data.getName() + " price below 100 DMA " + "close " + averageData100);

			return false;
		}

		if (averageData100.getSlope() < 0) {
			System.out.println(data.getName() + "100 SMA slope negative" + averageData100 );
			return false;
		}

		// 20 avg below 50 and, price below 50 and 20 slope up. 50 also up.
		
		if ( averageData20.getStockTicker().getClose() > averageData20.getValue() && averageData20.getSlope() > 0) {
			if (averageData50.getSlope() < averageData20.getSlope()) {
				// 20 Slope was negative in last 20 days.
				
			}
			return true;
		}
		return false;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MovingAverageRank " + movingDays ;
	}
	@Override
	public void save(BufferedWriter bufferedWriter) {
		// TODO Auto-generated method stub
		
	}

}
