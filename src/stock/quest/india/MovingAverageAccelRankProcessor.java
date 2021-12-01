package stock.quest.india;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class MovingAverageAccelRankProcessor implements StockStatisticsCollector{
	int movingDays;
	ArrayList<StockRank> rankings = new ArrayList<StockRank>();
	public MovingAverageAccelRankProcessor(int movingDays) {
		super();
		this.movingDays = movingDays;
	}
	public void print(){
		for (StockRank rank : rankings){
			System.out.println(rank.getData().getName() + " rank" +  rank.getRank() + "prev = " +  rank.getPrevData()  +" Performance " + rank.getPerformance());
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
		long daysBack = 120*60*60*24*1000L;
		long noOfDaysToCompare = 120*60*60*24*1000L;
		long noOfDaysToPrev = 60*60*60*24*1000L;
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
				
				AverageData dataPrev1 = average1.getMovingAvg(comparePrev);
				AverageData dataPrev2 = average2.getMovingAvg(comparePrev);
				
				
				if (data1 == null) {
					return 1;
				}
				if (data2== null) {
					return -1;
				}
				boolean isAccel1 = data1.getSlope() > (dataPrev1 != null ?  dataPrev1.getSlope() : 0);
				boolean isAccel2 = data2.getSlope() > (dataPrev2 != null ?  dataPrev2.getSlope() : 0);
				if ((isAccel1 && isAccel2) || (!isAccel1 && !isAccel2)){
					double slope1 = (( data1.getSlope() + (dataPrev1 != null ?  dataPrev1.getSlope() : 0)) / 2);
					double slope2 = (( data2.getSlope() + (dataPrev2 != null ?  dataPrev2.getSlope() : 0)) / 2);
					
					
					if (slope1 > slope2) {
						return -1;
					}
					if (slope1 < slope2) {
						return 1;
					}
				}
				if (isAccel1) {
					return -1;
				}
				if (isAccel2) {
					return 1;
				}
				
				return 0;
			}
			
		});
		for (int i = 0 ; i < datas.length ; i++ ){
			MovingAverageAnalyzer average = datas[i].getMovingAverage(movingDays);
			AverageData averageData1 =	average.getMovingAvg(compareDate);
			rankings.add(new StockRank(datas[i], i +1, averageData1, average.getMovingAvg(compareDate2)));
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
