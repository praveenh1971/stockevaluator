package stock.quest.india;

import java.io.BufferedReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleStockTickerParser {
	ArrayList<StockTicker> temp = new ArrayList<StockTicker>();
	ArrayList<StockTicker> parsePage(BufferedReader rd){
		temp.clear();
		StringBuilder  sb = new StringBuilder();
		String line = null;
        try {
			while ((line = rd.readLine()) != null) {
				if (line.matches("<th.*>Volume"))
					break;
				sb.append(line + '\n');
			}
			rd.readLine();
			ArrayList<StockTicker> tickers = new ArrayList<StockTicker>();
			
			Pattern pattern = Pattern.compile("<td.*>([\\s\\S]+)");
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
			while ((line = rd.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				String dateStr = null;
				String close = null;
				String volume = null;
				Date date = null;
				if (matcher.matches())
					dateStr = matcher.group(1);
				rd.readLine();
				rd.readLine();
				rd.readLine();
				line = rd.readLine();
				matcher = pattern.matcher(line);
				if (matcher.matches())
					close = matcher.group(1);
				line = rd.readLine();
				matcher = pattern.matcher(line);
				if (matcher.matches())
					volume = matcher.group(1);
				if (dateStr != null && close != null && volume != null) {
					try {
						date = dateFormat.parse(dateStr);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					close = close.replaceAll(",", "");
					double closeVal = Double.parseDouble(close);
					volume = volume.replaceAll(",", "");
					long volumeVal = Long.parseLong(volume);

					StockTicker stockTicker = new StockTicker(date, closeVal,
							volumeVal);
					temp.add(stockTicker);

				}
				String str = rd.readLine(); // skip tr
				if (str.matches("</table>"))
					break;
			}
			int size = temp.size();
			for (int i = 0 ; i < size ; i++){
				tickers.add(temp.get(size-i-1));
			}
//			for (StockTicker ticker : tickers) {
//				System.out.println(ticker);
//			}

			return tickers;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
                  

		
	}
}
