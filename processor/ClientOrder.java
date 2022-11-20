package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class ClientOrder implements Iterable<Item>, Runnable{
	private int clientId;
	private Map<String, Item> items;
	private ArrayList<Item> itemsList;
	private File itemFile;
	private File baseFile;

	public ClientOrder(int clientId) {
		items = new TreeMap<String, Item>();
		itemsList = new ArrayList<Item>();
		this.clientId = clientId;
	}
	
	public ClientOrder(File itemFile, File baseFile) {
		items = new TreeMap<String, Item>();
		itemsList = new ArrayList<Item>();
		this.itemFile = itemFile;
		this.baseFile = baseFile;
	}

	public void addItem(String itemName, double cost) {
		Item item = new Item(itemName, cost);
		if (items.get(itemName) != null) {
			items.get(itemName).addQuantity(clientId);
		} else {
			itemsList.add(item);
			items.put(itemName, item);
			items.get(itemName).addQuantity(clientId);
		}
	}
	
	public String data() {
		String data = "";
		Set<String> itemSet = items.keySet();
		double total = 0.0;
		data += "----- Order details for client with Id: " + clientId + " -----";
		for (String item: itemSet) {
			data += ("\nItem's name: " + item + ", Cost per item: " + NumberFormat.getCurrencyInstance().format(items.get(item).getCost()) + ", Quantity: " + items.get(item).getQuantity(clientId) + ", Cost: " + NumberFormat.getCurrencyInstance().format(items.get(item).getCost()*items.get(item).getQuantity(clientId)));
			total += (items.get(item).getCost()*items.get(item).getQuantity(clientId));
			
		}
		data += "\nOrder Total: " + NumberFormat.getCurrencyInstance().format(total) + "\n";
		return data;
	}
	
	public int totalQuantity(String itemName){
		if (items.containsKey(itemName)) {
			return items.get(itemName).getQuantity(clientId);
		}
		return 0;
	}
	

	public int getClientId() {
		return clientId;
	}

	public Iterator<Item> iterator() {
		return itemsList.iterator();
	}

	@Override
	public void run() {
		Map<String, Item> items = new TreeMap<String, Item>();
		
		Scanner itemScanner;
		try {
			itemScanner = new Scanner(itemFile);
			while (itemScanner.hasNext()) {
				String itemName = itemScanner.next();
				double cost = itemScanner.nextDouble();
				Item item = new Item(itemName, cost);
				items.put(itemName, item);
			}
			itemScanner.close();
			
			Scanner clientScanner = new Scanner(baseFile);
			clientScanner.next();
			clientId = clientScanner.nextInt();
			
			while (clientScanner.hasNext()) {
				String itemName = clientScanner.next();
				if (items.get(itemName) != null) {
					double cost = items.get(itemName).getCost();
					addItem(itemName, cost);
				}
			}
			clientScanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
