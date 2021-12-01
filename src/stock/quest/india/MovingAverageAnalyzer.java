package stock.quest.india;

import java.util.ArrayList;
import java.util.Date;

public class MovingAverageAnalyzer {
	
	private int movingAvgDays;
	public int getMovingAvgDays() {
		return movingAvgDays;
	}

	private ArrayList<AverageData> mvgAvArrayList;
	private int slopeDays;
	public MovingAverageAnalyzer(int movingAvgDays, int slopeDays){
		super();
		this.movingAvgDays = movingAvgDays;
		this.slopeDays = slopeDays;
	}

	public ArrayList<AverageData> getMovingAvgList() {
		return mvgAvArrayList;
	}
	public AverageData getMovingAvg(Date date) {
		 if (date != null) {
			for (AverageData averageData : mvgAvArrayList){
				if (averageData.getDate().after(date)){
					return averageData;
				}
			}
		 }
		return mvgAvArrayList.size() > 0 ? mvgAvArrayList.get(mvgAvArrayList.size() -1) : null;
	}

	public void compute( ArrayList<StockTicker> list){
		// First compute Moving Avg values
		mvgAvArrayList = new ArrayList<AverageData>();
		double mvgAvg = 0;
		double total = 0;
		// First add first X days
		int cnt = 0;
		int size = list.size();
		Date dte = null;
		StockTicker stockTicker = null;
		if (movingAvgDays > size ) return ; // Can't compute slopes
		for ( ;cnt < movingAvgDays ; cnt++){
			 stockTicker = list.get(cnt);
			total += stockTicker.getClose();
			dte = stockTicker.getDate();
		}
		mvgAvg = total / movingAvgDays;
		mvgAvArrayList.add(new AverageData(dte, mvgAvg, stockTicker));
		int last = 0;
		for ( ;cnt < list.size() ; cnt++,last++){
			stockTicker = list.get(cnt);
			StockTicker prev = list.get(last);
			double add = stockTicker.getClose();
			double subtract = prev.getClose();
			dte = stockTicker.getDate();
			mvgAvg += ((add-subtract) / movingAvgDays);
			mvgAvArrayList.add(new AverageData(dte, mvgAvg, stockTicker));
		}
		// Slopes
		 size = mvgAvArrayList.size();
		 int i = 0;
		for ( ; i + slopeDays < size ; i++){
			AverageData averageData1 = mvgAvArrayList.get(i + slopeDays);
			AverageData averageData2 = mvgAvArrayList.get(i);
			
			double d = (averageData1.getValue() - averageData2.getValue())/averageData2.getValue();
			dte = averageData1.getDate();
			averageData1.setSlope(d/slopeDays*100);
		}
		return ;
		
	}// 

	@Override
	public String toString() {
		StringBuffer buffer =  new StringBuffer();
		
		buffer.append("moving average " + movingAvgDays  + " ");
		for (AverageData d : mvgAvArrayList){
			buffer.append(d + " ,");
		}
		buffer.append("\n");
		buffer.append("Slopes average " + slopeDays);
		buffer.append("\n");
		return buffer.toString();
	}

	public AverageData getLastAverage() {
		if (mvgAvArrayList.size() > 0)
			 return mvgAvArrayList.get(mvgAvArrayList.size() -1);
		return null;
	}
	
}
 class AverageData{
	private Date date;
	private StockTicker stockTicker;
	private Double value ;
	private double slope;
	public AverageData(Date date, Double value, StockTicker stockTicker) {
		super();
		this.date = date;
		this.value = value;
		this.stockTicker = stockTicker;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public StockTicker getStockTicker() {
		return stockTicker;
	}
	public double getSlope() {
		return slope;
	}
	public void setSlope(double slope) {
		this.slope = slope;
	}

	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	@Override
	public String toString() {
		String str = String.format("%1$tm %1$te v[%2$4.2f],s[%3$4.5f],c[%4$4.2f]", date, value,slope,stockTicker != null ? stockTicker.getClose(): Double.NaN);
		return str;
	}
	
}