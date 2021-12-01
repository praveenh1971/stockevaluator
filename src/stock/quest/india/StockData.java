package stock.quest.india;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class StockData {
private String name;
private String symbol;
private ArrayList<StockTicker> stockTickers;
private boolean isModified;
public boolean isModified() {
	return isModified;
}


public String getName() {
	return name;
}
public String getSymbol() {
	return symbol;
}

private ArrayList<MovingAverageAnalyzer> movingAverages;
	public StockData(String name, String symbol, ArrayList<StockTicker> stockTickers) {
		super();
		this.stockTickers = stockTickers;
		this.name = name;
		this.symbol = symbol;
		movingAverages = new ArrayList<MovingAverageAnalyzer>();
	}
	public MovingAverageAnalyzer getMovingAverage(int average){
		for (MovingAverageAnalyzer analyzer : movingAverages){
			if (analyzer.getMovingAvgDays() == average) 
				return analyzer;
		}
		return null;
		
	}
  public void addMovingAverage(int average, int slope){
	  MovingAverageAnalyzer analyzer = new MovingAverageAnalyzer(average,slope);
	  analyzer.compute(stockTickers);
	  movingAverages.add(analyzer);
  }
  public void write(BufferedWriter bufferedWriter){
	  try {
		bufferedWriter.write(name + " ; " + symbol + " \n");
		StockTicker prev = null;
		for (StockTicker stockTicker : stockTickers){
			if (prev != null && prev.getDate().equals(stockTicker.getDate())){
				continue ; // Skip duplicates
			}
			stockTicker.write(bufferedWriter);
			prev = stockTicker;
		}
		bufferedWriter.flush();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  public void read(BufferedReader bufferedReader){
	  try {
		 String line =  bufferedReader.readLine();
		 if (line != null){
			String[] data = line.split(";");
			name = data[0].trim();
			symbol = data[1].trim();
		 }
		 stockTickers = new ArrayList<StockTicker>();
		 StockTicker lastTicker = null;
		 while ((line =  bufferedReader.readLine()) != null){
			 if (!line.trim().isEmpty()){
				 StockTicker stockTicker = new StockTicker(line);
				 if (!stockTickers.contains(stockTicker))
					 stockTickers.add(stockTicker);
				 lastTicker = stockTicker;
			 }
		 }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  public Date getDateLastPrice(){
	  if (stockTickers != null && stockTickers.size() > 0 ){
		 return stockTickers.get(stockTickers.size() - 1).getDate();
	  }
	  return null;
  }
 

  public ArrayList<StockTicker> getStockTickers() {
		return stockTickers;
	}
	public void appendStockTickers(ArrayList<StockTicker> stockTickersNew) {
		if (this.stockTickers == null || stockTickers.isEmpty())
			this.stockTickers = stockTickersNew;
		else {
			StockTicker stockTicker = this.stockTickers.get(stockTickers.size() - 1);
			int index = stockTickersNew.lastIndexOf(stockTicker);
			if (index >= 0 && index + 1 < stockTickersNew.size()){
				// Copy the rest.// check the next one 
				
				this.stockTickers.addAll(stockTickersNew.subList(index + 1, stockTickersNew.size()));
			}else {
				stockTickers.addAll(stockTickersNew);
			}
		}
		isModified = true;
	}

  public void setStockTickers(ArrayList<StockTicker> stockTickers) {
		this.stockTickers = stockTickers;
	}
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer(1000);
		buffer.append("Stock = " + name + " \n");
		for (StockTicker stockTicker : stockTickers){
			buffer.append(stockTicker.toString() + " \n");
		}
		for (MovingAverageAnalyzer analyzer : movingAverages){
			buffer.append(analyzer.toString());
		}
		buffer.append("\n");
		return buffer.toString();
	}
	
	
}
