package stock.quest.india;
public class StockRank
{
	private StockData data;
	private int rank;
	private AverageData prev;
	private AverageData current;
	private AverageData comparePrev;
	
	public AverageData getComparePrev() {
		return comparePrev;
	}
	public AverageData getPrevData() {
		return prev;
	}
	public double getPerformance() {
		if (prev != null && current != null)
		return ((current.getStockTicker().getClose() - prev.getStockTicker().getClose())) / (prev.getStockTicker().getClose() ) * 100 ;
		return Double.NaN;
	}
	public double getPrevPerformance() {
		if (prev != null && comparePrev != null)
		return ((prev.getStockTicker().getClose() - comparePrev.getStockTicker().getClose())) / (comparePrev.getStockTicker().getClose() ) * 100 ;
		return Double.NaN;
	}

	public AverageData getCurrenntData() {
		return current;
	}
	public StockRank(StockData data, int rank, AverageData prev, AverageData today, AverageData comparePrev) {
		super();
		this.data = data;
		this.rank = rank;
		this.prev = prev;
		this.current = today;
		this.comparePrev = comparePrev;
	}

	public StockRank(StockData data, int rank, AverageData prev, AverageData today) {
		super();
		this.data = data;
		this.rank = rank;
		this.prev = prev;
		this.current = today;
	}
	public StockData getData() {
		return data;
	}
	public int getRank() {
		return rank;
	}
}
