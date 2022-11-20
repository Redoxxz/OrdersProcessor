package processor;

import java.util.Map;
import java.util.TreeMap;

public class Item {
	private String itemName;
	private double cost;
	private int totalQuantity;
	private Map<Integer, Integer> clientQuantity;
	

	public Item(String itemName, double cost) {
		this.itemName = itemName;
		this.cost = cost;
		totalQuantity = 0;
		clientQuantity = new TreeMap<Integer, Integer>();
	}
	
	public void addQuantity(int clientId){
		if (clientQuantity.containsKey(clientId) == false) {
			clientQuantity.put(clientId, 1);
			totalQuantity++;
		} else {
			clientQuantity.put(clientId, clientQuantity.get(clientId) + 1);
			totalQuantity++;
		}
		
	}
	
	public int getQuantity(int clientId) {
		return clientQuantity.get(clientId);
	}

	public double getCost() {
		return cost;
	}

	public String getItemName() {
		return itemName;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}
}
