package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class OrdersProcessor {
	
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		TreeMap<String, Integer> itemsCount = new TreeMap<>();
		Income totalIncome = new Income(0);
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter item's data file name: ");
		File itemFile = new File(scanner.next());
		System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
		String multiple = scanner.next();
		boolean multipleThreads = false;
		if (multiple.equals("y")) {
			multipleThreads = true;
		}
		System.out.println("Enter number of orders to process: ");
		int numberOfOrders = scanner.nextInt();
		System.out.println("Enter order's base filename: ");
		String baseFile = scanner.next();
		System.out.println("Enter result's filename: ");
		String resultFile = (scanner.next());
		long startTime = System.currentTimeMillis();
		scanner.close();
		String bill = "";
		if (multipleThreads == false) {
			ArrayList<ClientOrder> clients = new ArrayList<ClientOrder>();
			for (int i = 1; i <= numberOfOrders; i++) {
				File filename = new File(baseFile + i + ".txt");
				clients.add(readItems(itemFile, filename, resultFile));
				bill += clients.get(i-1).data();
				System.out.println("Reading order for client with id: " + clients.get(i-1).getClientId());
			}
			writeToFile(resultFile, itemFile, clients, bill);
		} else {
			ArrayList<Thread> orderThreads = new ArrayList<Thread>();
			ArrayList<ClientOrder> orders = new ArrayList<ClientOrder>();
			for (int i = 1; i <= numberOfOrders; i++) {
				ClientOrder order = new ClientOrder(itemFile, new File(baseFile + i + ".txt"));
				orderThreads.add(new Thread(order));
				orders.add(order);
			}
			for (Thread thread : orderThreads) {
				thread.start();
			}
			for (Thread thread : orderThreads) {
				thread.join();
			}
			orderThreads.clear();
			for (ClientOrder order : orders) {
				ProcessClientOrder processedOrder = new ProcessClientOrder(order, itemsCount, totalIncome);
				orderThreads.add(new Thread(processedOrder));
				bill += order.data();
			}
			for (Thread thread : orderThreads) {
				thread.start();
			}
			for (Thread thread : orderThreads) {
				thread.join();
			}
			writeToFileThreaded(resultFile, itemFile, orders, bill, itemsCount);
		}
		
		long endTime = System.currentTimeMillis();
		long timeElapsed = endTime - startTime;

		System.out.println("Processing time (msec): " + timeElapsed);
		System.out.println("Results can be found in the file: " + resultFile);
	}
	
	public static ClientOrder readItems(File itemFile, File baseFile, String resultFile) throws FileNotFoundException {
		Map<Integer, ClientOrder> clients = new TreeMap<Integer, ClientOrder>();
		Map<String, Item> items = new TreeMap<String, Item>();
		
		Scanner itemScanner = new Scanner(itemFile);

		while (itemScanner.hasNext()) {
			String itemName = itemScanner.next();
			double cost = itemScanner.nextDouble();
			Item item = new Item(itemName, cost);
			items.put(itemName, item);
		}
		itemScanner.close();
		
		Scanner clientScanner = new Scanner(baseFile);
		clientScanner.next();
		int clientId = clientScanner.nextInt();
		ClientOrder client = new ClientOrder(clientId);
		
		while (clientScanner.hasNext()) {
			String itemName = clientScanner.next();
			if (items.get(itemName) != null) {
				double cost = items.get(itemName).getCost();
				client.addItem(itemName, cost);
			}
		}
		
		clients.put(clientId, client);
			
		clientScanner.close();
		
		return client;
	}
	
	public static boolean writeToFileThreaded(String filename, File itemFile, ArrayList<ClientOrder> clients, String bill, TreeMap<String, Integer> itemsCount) {
		try {
			FileWriter file = new FileWriter(filename);
			file.write(bill);
			Map<String, Item> items = setItemsMap(itemFile);
			file.write("***** Summary of all orders *****");
			Double grandTotal = 0.0;
			Set<String> itemSet = itemsCount.keySet();
			for (String itemName : itemSet) {
				file.write("\nSummary - Item's name: " + itemName + ", Cost per item: " + NumberFormat.getCurrencyInstance().format(items.get(itemName).getCost()) + ", Number sold: " + itemsCount.get(itemName) + ", Item's Total: " + NumberFormat.getCurrencyInstance().format(items.get(itemName).getCost()*itemsCount.get(itemName)));
				grandTotal += itemsCount.get(itemName)*items.get(itemName).getCost();
			}
			file.write("\nSummary Grand Total: " + NumberFormat.getCurrencyInstance().format(grandTotal));
			file.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static boolean writeToFile(String filename, File itemFile, ArrayList<ClientOrder> clients, String bill) {
		try {
			FileWriter file = new FileWriter(filename);
			file.write(bill);
			Map<String, Integer> quantityPerItem= new TreeMap<String, Integer>();
			Map<String, Item> items = setItemsMap(itemFile);
			Set<String> itemSet = items.keySet();
			for (String itemName : itemSet) {
				quantityPerItem.put(itemName, 0);	
			}
			for (ClientOrder client : clients) {
				for (String itemName : itemSet) {
					quantityPerItem.put(itemName, quantityPerItem.get(itemName) + client.totalQuantity(itemName));
				}
			}
			file.write("***** Summary of all orders *****");
			Double grandTotal = 0.0;
			for (String itemName : itemSet) {
				file.write("\nSummary - Item's name: " + itemName + ", Cost per item: " + NumberFormat.getCurrencyInstance().format(items.get(itemName).getCost()) + ", Number sold: " + quantityPerItem.get(itemName) + ", Item's Total: " + NumberFormat.getCurrencyInstance().format(items.get(itemName).getCost()*quantityPerItem.get(itemName)));
				grandTotal += quantityPerItem.get(itemName)*items.get(itemName).getCost();
			}
			file.write("\nSummary Grand Total: " + NumberFormat.getCurrencyInstance().format(grandTotal));
			file.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static Map<String, Item> setItemsMap(File itemFile) throws FileNotFoundException{
		Map<String, Item> items = new TreeMap<String, Item>();
		
		Scanner itemScanner = new Scanner(itemFile);

		while (itemScanner.hasNext()) {
			String itemName = itemScanner.next();
			double cost = itemScanner.nextDouble();
			Item item = new Item(itemName, cost);
			items.put(itemName, item);
		}
		itemScanner.close();
		return items;
	}
	
	
}