package stock.quest.india;

import java.io.BufferedWriter;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class FilertAverageRankProcessor implements StockStatisticsCollector{
	private static final long WEEK = 5*60*60*24*1000L;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
	int movingDays;
	ArrayList<StockRank> rankings = new ArrayList<StockRank>();
	public FilertAverageRankProcessor(int movingDays) {
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
			date2 = dateFormat.parse("01012014");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			date = dateFormat.parse("01032013");
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

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
				if (data1.getSlope() > data2.getSlope()){
					return -1;
				}
				if (data1.getSlope() < data2.getSlope()){
					return 1;
				}

				return 0;
			}
			
		});
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
		
//		//	System.out.println(data.getName() +  " " + averageData100 + " noOfDays1 " +  getNoOfDays(averageData100) );
//		double dev = Math.abs((averageData20.getValue() - averageData50.getValue()) / averageData50.getValue() ) * 100;
//		if (dev > averageData100.getSlope()*30) {
//			System.out.println(data.getName() + "50 SMA is  FAR to 20 SMA "  + " OFF " + dev);
//			return false;
//		}
		System.out.println(data.getName() + " 20 SMA " + averageData20 + " 50 SMA " + averageData50 + " 100 sma " + averageData100);
		
		// slowing 20 average 
			int reason = 0;
			boolean add = false;
			Date prev = new Date(compareDate.getTime() - WEEK);
			Date prev2w = new Date(compareDate.getTime() - 2*WEEK);
			Date prev1m = new Date(compareDate.getTime() - 6*WEEK);
			AverageData average20p = average20.getMovingAvg(prev);
			AverageData average50p = average50.getMovingAvg(prev);
			AverageData average100p = average100.getMovingAvg(prev);
			
			AverageData average20p2w = average20.getMovingAvg(prev2w);
			AverageData average50p2w = average50.getMovingAvg(prev2w);
			AverageData average20p1m = average20.getMovingAvg(prev1m);
			AverageData average50p1m = average50.getMovingAvg(prev1m);
			AverageData average100p2w = average100.getMovingAvg(prev2w);
			double curDiff = averageData20.getValue() - averageData50.getValue();
			double prevDiff = average20p.getValue() - average50p.getValue();
			double prevDiff2w = average20p2w.getValue() - average50p2w.getValue();
			double prevDiff1m = average20p1m.getValue() - average50p1m.getValue();

			
//			if (averageData100.getStockTicker().getClose() < averageData100.getValue() ) {
//				System.out.println(data.getName() + " price below 100 DMA " + "close " + averageData100);
//				// BREAK OUTS AFTER A LONG SIDE WAYS
//				// 50 day and 20 day are very close to each other for a month and suddenly price jumped 15% more than 20 day
//				return false;
//			}
			
		
			if (averageData100.getSlope() < 0) {
				System.out.println(data.getName() + "100 SMA slope negative" + averageData100 );
				if (  averageData50.getStockTicker().getClose() > averageData50.getValue()*1.05) {
					if ( average50p1m.getSlope() < 0.01 && average50p2w.getSlope() < 0 
							&& (average50p2w.getSlope() > average50p1m.getSlope()) 
							&& (average50p.getSlope() > average50p2w.getSlope())
							&& (averageData50.getSlope() > average50p.getSlope())
							&& (averageData50.getSlope() > -0.03)
							) {
						reason = 1;
						add = true;
					}
				}
				return false;
			}

			if (averageData20.getSlope() > 0 && averageData50.getSlope() > 0){
				// Almost parallel
				System.out.println("prevDiff " + prevDiff +  " curDiff " + curDiff);
				if (prevDiff > 0 && curDiff > 0  && (((prevDiff / curDiff ) > 0.90) && ((prevDiff / curDiff ) < 1.1) )){
					reason = 2;
					add = true;
				}
				if (prevDiff > 0 && curDiff > 0  && ( curDiff > prevDiff) && (prevDiff / curDiff) > 0.5){
					reason = 3;
					add = true;
				}
				

			}

			if ( averageData20.getStockTicker().getClose() < averageData20.getValue()*1.1 && averageData20.getStockTicker().getClose() > averageData20.getValue()) {
				if ( average20p.getSlope() < 0.01 || average20p2w.getSlope() < 0 && (averageData20.getSlope() > average20p2w.getSlope())) {
					reason = 5;
					add = true;
				}
			}
			// Ge
			if (averageData20.getValue() > averageData50.getValue()  && averageData50.getValue() > averageData100.getValue()) {
				reason = 6;
				add = true;
				
			}

			//Flatter Consolidation...
			if (isClosing(curDiff, prevDiff ) && isClosing(prevDiff, prevDiff2w) ){
				System.out.println(data.getName() + " 20 SMA and 50 SMA NARROWING");
			//	return true;
			}
			if (isVeryClose(average20p.getValue(), average50p.getValue(), 0.05)
					&& isVeryClose(average20p2w.getValue(), average50p2w.getValue(), 0.05)
					&& isVeryClose(average20p1m.getValue(), average50p1m.getValue(), 0.05)
					&& isVeryClose(average100p2w.getValue(), average50p2w.getValue(), 0.1)
					){
				System.out.println(data.getName() + " 20 SMA and 50 SMA are VERY CLOSE" + averageData100);
				// Check if break out
				if (averageData20.getStockTicker().getClose() / averageData20.getValue() > 1.1) {
					reason = 4;
					add = true;
				}
				
			}
	
			
		switch (reason)
		{
		case 1:
			System.out.println(data.getName() + " 50 SMA was slope down, BREAKOUT 50 SMA");
			break;
		case 2:
			System.out.println(data.getName() + " 20 SMA and 50 SMA are parellel");
			break;

		case 3:
			System.out.println(data.getName() + " 20 SMA and 50 SMA are WIDENING");
			break;
		case 4:
			System.out.println(data.getName() + " LONG BREAKOUT");
			break;
		case 5:
			System.out.println(data.getName() + " 20 SMA was slope down, price crossed");
			break;
		case 6:
			System.out.println(data.getName() + " 20 SMA > 50 SMA > 100 SMA");
			break;
		
		}

		return add;
	}
	private boolean isClosing(double d1, double d2) { // going from +ve to -ve.
		if ( d1 < d2) return true;
		return false;
		
	}
	private boolean isVeryClose(double d1, double d2, double dev) { // going from +ve to -ve.
		double d = d1/d2;
		if (d > (1-dev) && d < (1+dev)) return true;
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
	public static void main(String[] args) {
		
	}
}
