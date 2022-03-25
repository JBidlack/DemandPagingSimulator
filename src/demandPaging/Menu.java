package demandPaging;

import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.Scanner;

public class Menu {
	Reference r = new Reference();
	
	public Menu() {
		int sel;
		
		do {
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Demand Paging Simulator");
		System.out.println("");
		System.out.println("0 - Exit");
		System.out.println("1 - Read refference string");
		System.out.println("2 - Generate reference string");
		System.out.println("3 - Display current reference string");
		System.out.println("4 - Simulate FIFO");
		System.out.println("5 - Simulate OPT");
		System.out.println("6 - Simulate LRU");
		System.out.println("7 - Simulate LFU");
		System.out.println("");
		System.out.println("Please make your selection: ");
		
		sel = sc.nextInt();
		
		Selection(sel);
		} while (sel!=0);

	}
	
	private void Selection(int sel) {
		
		switch(sel) {
			case 0:
				System.out.println("Thank you for using the Demand Paging Simulator!");
				System.exit(0);
				
			case 1:
				r.readRef();
				break;
			case 2:
				r.randRef();
				break;
			case 3:
				r.printRef();
				break;
			case 4:
				try {
					r.fifo();
				} catch (IOException e) {
					System.out.println("No reference string exists!");
				}
				break;
			case 5:
				try {
					r.opt();
				} catch (IOException e) {
					System.out.println("No reference string exists!");
				}
				break;
			case 6:
				try {
					r.lru();
				} catch (IOException e) {
					System.out.println("No reference string exists!");
				}
				break;
			case 7:
				try {
					r.lfu();
				} catch (IOException e) {
					System.out.println("No reference string exists!");
				}
				break;
			default:
				System.out.println("Invalid selection. Please try again!");
				System.out.println("");
				
		}
	}
	
	
	
	
}
