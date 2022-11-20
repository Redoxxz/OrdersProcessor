package processor;

import java.util.TreeMap;


public class ProcessClientOrder implements Runnable {
	private TreeMap<String, Integer> itemsCount;
	private Income totalIncome;
	private ClientOrder order;

	public ProcessClientOrder(ClientOrder order, TreeMap<String, Integer> itemsCount, Income totalIncome) {
		this.itemsCount = itemsCount;
		this.totalIncome = totalIncome;
		this.order = order;
	}

	public void run() {
		int totalOrder = 0;
		for (Item item : order) {
			String itemName = item.getItemName();
			double cost = item.getCost();
			totalOrder += cost;
			/* Keeping track of number of items count */
			synchronized (itemsCount) {
				Integer number = itemsCount.get(itemName);
				if (number == null) {
					itemsCount.put(itemName, order.totalQuantity(itemName));
				} else {
					itemsCount.put(itemName, number + order.totalQuantity(itemName));
				}
			}

		synchronized(totalIncome) {
			totalIncome.add(totalOrder);
		}
		}
	}
	
	public int quantity(String itemName) {
		return itemsCount.get(itemName);
	}
	
	public String getTotalIncome() {
		return totalIncome.toString();
	}
}
