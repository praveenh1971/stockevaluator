package stock.quest.india;

import java.io.BufferedWriter;
import java.util.ArrayList;

public interface StockStatisticsCollector {
	public void process(ArrayList<StockData>  arrayList);
	public String getName();
	public void save(BufferedWriter bufferedWriter);
	public void print();

}
