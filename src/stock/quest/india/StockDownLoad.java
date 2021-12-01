package stock.quest.india;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class StockDownLoad {
	static String index = "SP500";
	static String prefix ="";
	public ArrayList<StockTicker> downLoad(String ticker, int days){
	      HttpURLConnection connection = null;
	      OutputStreamWriter wr = null;
	      BufferedReader rd  = null;
	      StringBuilder sb = null;
	      String line = null;
	      URL serverAddress = null;
	      String initialUrl = "http://www.google.com/finance/historical?";
	      int num = days > 200?  200 : days;
	      int start = 0;
	     ArrayList<StockTicker> tickers = new ArrayList<StockTicker>();
//getMovingAvgList
	      try {
	    	  boolean isComplete = false;
	    	  while (!isComplete){
		          //serverAddress = new URL("http://www.google.com/finance/historical?q=BOM%3A532286&startdate=Jun%206%2C%202011&enddate=Nov%2012%2C%202012&start=1&num=400");
	    		  String url = initialUrl+"q=" + ticker +  "&start=" + start + "&num="+num;
	    		  System.out.println(" Querying *** " + url);
	    		  serverAddress = new URL(url );
	    		  //set up out communications stuff
		          connection = null;
		          //Set up the initial connection
		          connection = (HttpURLConnection)serverAddress.openConnection();
		          connection.setRequestMethod("GET");
		          connection.setDoOutput(true);
		          connection.setReadTimeout(10000);
		                    
		          connection.connect();
		          //read the result from the server
		          rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		          GoogleStockTickerParser googleStockTickerParser = new GoogleStockTickerParser();
		         ArrayList<StockTicker> ret = googleStockTickerParser.parsePage(rd);
		         if (ret != null){
		        	 tickers.addAll(0,ret);
		        	start += num;
		        	if (start >= days) {
		        		isComplete = true;
		        		break;
		        	}
		        	if ( start + num > days   ){
		        			num = days - start;
		        	}
		         }else {
		        	 isComplete = true;
		         }
	    		  
	    	  }
	      } catch (MalformedURLException e) {
	          e.printStackTrace();
	      } catch (ProtocolException e) {
	          e.printStackTrace();
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      finally
	      {
	          //close the connection, set all objects to null
	          connection.disconnect();
	          rd = null;
	          sb = null;
	          wr = null;
	          connection = null;
	      }
		return tickers;
		
	}
	public static void main(String[] args) {
		String 	 ticker = "BOM%3A500285";
		StockDownLoad downLoad = new StockDownLoad();
		try {//C:\Users\arnav\Documents
			BufferedReader bufferedReader =  new BufferedReader(new FileReader("C://stockquest//" + index + "//" + index + ".txt"));
			StockListReader listReader = new StockListReader();
			listReader.read(bufferedReader);
			ArrayList<StockData> stockList = listReader.getStockList();
			// Read Archive.
			readArchive(stockList);	

			for (StockData data : stockList){
				downLoad(downLoad, data);
			}
			for (StockData data : stockList){
				data.addMovingAverage(20, 3);
				data.addMovingAverage(50, 5);
				data.addMovingAverage(100, 20);
				data.addMovingAverage(10, 3);
				data.addMovingAverage(200, 20);

			}
			// Dump 50 Day moving slope
			writeArchive(stockList);
			
			// PRint 50 day ranking and 20 day ranking
			StockStatisticsCollector averageRankProcessor50 = new FilertAverageRankProcessor(200);
			averageRankProcessor50.process(stockList);
			averageRankProcessor50.process(stockList);
			averageRankProcessor50.print();
			StockStatisticsCollector percentageAbove100 = new StocksAboveMovingAverage(100);
			percentageAbove100.process(stockList);
			percentageAbove100.print();
//			
//			StockStatisticsCollector percentageAbove50 = new StocksAboveMovingAverage(50);
//			percentageAbove50.process(stockList);
//			percentageAbove50.print();
//
//			StockStatisticsCollector percentageAbove20 = new StocksAboveMovingAverage(20);
//			percentageAbove20.process(stockList);
//			percentageAbove20.print();


//			StocksPercentageMvgAvgCrossOver averageSlope =  new StocksPercentageMvgAvgCrossOver(50,200);
//			averageSlope.process(stockList);
//			averageSlope.print();
////			
//			StocksPostiveMovingAverageSlope pslop = new StocksPostiveMovingAverageSlope(20, 100);
//			pslop.process(stockList);
//			pslop.print();
//			StocksPostiveMovingAverageSlope pslop50 = new StocksPostiveMovingAverageSlope(50, 100);
//			pslop50.process(stockList);
//			pslop50.print();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
	}
	private static void downLoad(StockDownLoad downLoad, StockData data) {
		String ticker;
		System.out.println("Down Loading " + data.getName() );
		ticker =  prefix + data.getSymbol();
		Date lastDownload = data.getDateLastPrice();
		int daysToLoad = 400;
		if (lastDownload != null) {
			Date date = new Date(); // now
			long time = date.getTime();
			long last = lastDownload.getTime();
			daysToLoad = (int) ((time - last) / (24*60*60*1000));
			System.out.println("daysToLoad  = " + daysToLoad);
			if (daysToLoad <= 1){
				daysToLoad = 1;
			}
		}
		if (daysToLoad > 0){
			ArrayList<StockTicker > arrayList = downLoad.downLoad(ticker, daysToLoad);
			System.out.println("DownLoaded  " + data.getName() + "  size = " + arrayList.size());
			data.appendStockTickers(arrayList);
		}
		//System.out.println(data.toString());
	}
	private static void writeArchive(ArrayList<StockData> stockList) {
		// write Archive.
		for (StockData data : stockList){
			 if (data.isModified()) {
					String sfile = "C://stockquest//" + index + "//archive//" + data.getSymbol() + ".txt";
					File file = new File(sfile);
					try {
						if (!file.exists())
							file.createNewFile();
						BufferedWriter writer;

						writer = new BufferedWriter(new FileWriter(file));
						System.out.println("SAVING  " + data.getName() );

						data.write(writer);
						writer.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	  			 
   			   }
			// Dump to a file.
			}
	}
	private static void readArchive(ArrayList<StockData> stockList)
			throws FileNotFoundException {
		for (StockData data : stockList){
			// Dump to a file.
				String sfile = "C://stockquest//" + index + "//archive//"  + data.getSymbol() + ".txt";
				File file = new File(sfile);
				if (file.exists()){
					data.read(new BufferedReader(new FileReader(file)));
				}
			}
	}
}
