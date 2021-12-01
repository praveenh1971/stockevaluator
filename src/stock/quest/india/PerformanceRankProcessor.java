package stock.quest.india;

import java.io.BufferedWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class PerformanceRankProcessor implements StockStatisticsCollector{
	int movingDays;
	ArrayList<StockRank> rankings = new ArrayList<StockRank>();
	public PerformanceRankProcessor(int movingDays) {
		super();
		this.movingDays = movingDays;
	}
	public void print(){
		for (StockRank rank : rankings){
			System.out.println(rank.getData().getName() + " rank" +  rank.getRank() + "prevPer= " + rank.getPrevPerformance()   +" Performance " + rank.getPerformance());
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
		long noOfDaysToCompare = 1*60*60*24*1000L;
		long noOfDaysToPrev = 10*60*60*24*1000L;
		// Step back 50 days.
		final Date compareDate = new Date(date.getTime() - daysBack);
		final Date comparePrev = new Date(compareDate.getTime() - noOfDaysToPrev);

		final Date compareDate2 = new Date(compareDate.getTime() + noOfDaysToCompare);
		
		System.out.println(" Date to compare " + compareDate + " to " + compareDate2);
		StockData[] datas = arrayList.toArray(new StockData[]{});
		Arrays.sort(datas,new Comparator<StockData>() {
			@Override
			public int compare(StockData o1, StockData o2) {
				MovingAverageAnalyzer average1 = o1.getMovingAverage(movingDays);
//				MovingAverageAnalyzer average201 = o1.getMovingAverage(20);
				
				
				MovingAverageAnalyzer average2 = o2.getMovingAverage(movingDays);
//				MovingAverageAnalyzer average202 = o2.getMovingAverage(20);

				AverageData data1 = average1.getMovingAvg(compareDate);
				AverageData data2 = average2.getMovingAvg(compareDate);

				AverageData dataLast1 = average1.getMovingAvg(comparePrev);
				AverageData dataLast2 = average2.getMovingAvg(comparePrev);
				
				if (dataLast1 == null) {
					return 1;
				}
				if (dataLast2== null) {
					return -1;
					
				}
				double perfo1 = (data1.getStockTicker().getClose() - dataLast1.getStockTicker().getClose()) / dataLast1.getStockTicker().getClose();
				double perfo2 = (data2.getStockTicker().getClose() - dataLast2.getStockTicker().getClose()) / dataLast2.getStockTicker().getClose();
				
				if (perfo1 > perfo2){
					return -1;
				}
				if (perfo1 < perfo2){
					return 1;
				}

				return 0;
			}
			
		});
		for (int i = 0 ; i < datas.length ; i++ ){
			MovingAverageAnalyzer average = datas[i].getMovingAverage(movingDays);
			AverageData averageData1 =	average.getMovingAvg(compareDate);
			AverageData dataLast1 = average.getMovingAvg(comparePrev);
			rankings.add(new StockRank(datas[i], i +1, averageData1, average.getMovingAvg(compareDate2), dataLast1));
		}
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
