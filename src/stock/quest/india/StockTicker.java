package stock.quest.india;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class StockTicker implements Comparable<StockTicker> {
	private Date date;
	private double close;
	private  long volume;
	private static  SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public StockTicker(Date date, double close, long volume) {
		super();
		this.date = date;
		this.close = close;
		this.volume = volume;
	}
	public StockTicker() {
		// TODO Auto-generated constructor stub
	}
	public StockTicker(String line) { // Read the saved data
		String[] data =  line.split(";");
		try {
			date= dateFormat.parse(data[0].trim());
			close= Double.parseDouble(data[1].trim());
			volume= Long.parseLong(data[2].trim());
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public String toString() {
		String str = String.format("date %1$tm %1$te = %2$4.2f ,", date, close);
		return str;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StockTicker other = (StockTicker) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}
	@Override
	public int compareTo(StockTicker o) {
		return (int) (this.getDate().getTime() - o.getDate().getTime());
		
	}
	public void write(BufferedWriter bufferedWriter) throws IOException {
			String sDate = dateFormat.format(date);
			String str = String.format("%1$s ; %2$4.2f ;%3$d\n",sDate , close, volume);
		bufferedWriter.write(str);
		
	}
	public static void main(String[] args) {
		final CountDownLatch helloALatch = new CountDownLatch(1);
		final CountDownLatch helloBLatch = new CountDownLatch(1);
		final CountDownLatch goodByeALatch = new CountDownLatch(1);
			
		Thread threadA = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(" Hello A" );
				helloALatch.countDown();
				try {
					helloBLatch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(" Good Bye A" );	
				goodByeALatch.countDown();
			}
		});
		
		Thread threadB = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					helloALatch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(" Hello B" );
				helloBLatch.countDown();
				try {
					goodByeALatch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(" Good Bye B" );	
			}
		});
		threadB.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		threadA.start();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
