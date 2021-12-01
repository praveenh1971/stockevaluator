package stock.quest.india;

import java.io.BufferedReader;
import java.util.ArrayList;
//9449757068  -- hondappanavar
public class StockListReader {
	private ArrayList<StockData> stockList = new ArrayList<StockData>();
	public ArrayList<StockData> getStockList() {
		return stockList;
	}
	public void read(BufferedReader reader){
		String line;
		try {
		
			while ((line = reader.readLine()) != null) {
				String[] values =  line.split(";");
				if (values.length < 2){
					System.out.println(" ERROR in LINE  " + line);
					continue;
				}
				StockData data = new StockData(values[0], values[1],null);
				stockList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
