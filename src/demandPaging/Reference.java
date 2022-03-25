/*
 * John Bidlack 
 * CMSC 412 6380
 * 3/6/22
 */
package demandPaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

// class to execute all algorithms
public class Reference {
	Scanner sc = new Scanner(System.in);
	private String[] ref;

// constructor
	public Reference() {
		
	}
// method to manually enter reference string	
	protected void readRef() throws IllegalArgumentException{
		
		try {
		System.out.println("Please enter your reference string, separated by a space.");
		String numbs = sc.nextLine();
		String[] numbList = numbs.split(" ");
		// adding 1 to the length of the array to hold the label
		ref = new String[numbList.length+1];
		ref[0] = "Reference Frame : ";
		// adding the reference string to the array with the offset after titling
		for (int i = 0; i<numbList.length; i++) {
			if(!Character.isDigit(numbList[i].charAt(0)) || numbList[i].length() > 1) {
				throw new IllegalArgumentException();
			}
			else {
				ref[i+1] = numbList[i];
			}
		}
		} catch (IllegalArgumentException e) {
			System.out.println("Reference String must only contain numbers "
					+ "between 0 and 9");
			readRef();
		}
	}
	
	// method to randomly generate a reference string of a length chosen by the user
	protected void randRef() {
		Random rand = new Random();
		try {
			System.out.println("Please enter the length of the reference string you would like generated");

			int len = sc.nextInt();
			ref = new String[len+1];
			
			ref[0] = "Reference Frame : ";
			
			
			for (int i = 1; i < ref.length; i++) {
				ref[i] = String.valueOf(rand.nextInt(10));
			}
		}
		
		catch(IllegalFormatException e) {
			System.out.println("An error has occurred.");
			randRef();
		} 

	}
	
	// a method to print the reference string
	protected void printRef(){
		try {
			if(ref.length == 0 || ref == null) {
				throw new Exception();
			}
			else {
				for (int i = 0; i<ref.length; i++) {
					System.out.print(ref[i] + " ");
				}
				System.out.println("");
			}
		}
		catch (Exception e) {
			System.out.println("An error has occured.");
		}
	}
	
	// method for the FIFO algorithm
	protected void fifo() throws IOException {
		String[] faults = new String[ref.length];
		String[] victim = new String[ref.length];
		String[][] chart = buildChart();
		int faultCount = 0;
		int index = 1;
		int current = 0;
		faults[0] = "Page Faults:       ";
		victim[0] = "Victim Frames:     ";
		//created an arraylist to store the current frames page contents
		ArrayList<String> curr = new ArrayList<String>(chart.length);
		
		if(ref.length == 0 || ref == null) {
			throw new IOException();
		}
		
		for (int i = 1; i < ref.length; i++) {
			// if the current virtual memory value is not found and the pages are not full, 
			// the value is added to the first empty page
			if (!curr.contains(ref[i])){
				if (curr.size() < chart.length) {
					curr.add(ref[i]);
					faults[i] = "F";
					faultCount++;
				}
				// if the pages are all full, the oldest frame (designated by "current"
				// is chosen as the victim
				else {
					if (current >= chart.length) {
						current = 0;
					}						
					victim[i] = curr.get(current);
					curr.set(current, ref[i]);
					// current is set to the new "oldest" frame which is the next frame
					current++;
					faults[i] = "F";
					faultCount++;
				}
				
			}
			// if the element is found in the pages, no fault is generated
			else {

					faults[i] = " ";
				}
			// the table is filled in with its newest value
			for (int j = 0; j < curr.size(); j++) {
				chart[j][i] = curr.get(j);

			}
		}
		//the table is printed along with the total number of faults
		printTable(chart, index, faults, victim);
		System.out.println("Total Faults: " + faultCount);
		System.out.println("");
	}
	
	// method for OPT algorithm
	protected void opt() throws IOException{
		int current = 0;
		int faultCount = 0;
		int max = -1;
		int start = 1;
		String vict = null;
		String[][] chart = buildChart();
		String[] faults = new String[ref.length];
		String[] victim = new String[ref.length];
		int index;
		
		faults[0] = "Page Faults:       ";
		victim[0] = "Victim Frames:     ";
		ArrayList<String> curr = new ArrayList<String>(chart.length);
		ArrayList<String> reference = new ArrayList<String>();
		
		if(ref.length == 0 || ref == null) {
			throw new IOException();
		}
		// an arraylist is created with all the elements of the reference string 
		for (String i : ref) {
			reference.add(i);
		}
		
		for (int i = 1; i < ref.length; i++) {
			// as before if the element is not found and the pages have space, it is added
			if (!curr.contains(ref[i])){
				if (curr.size() < chart.length) {
					curr.add(current, ref[i]);
					reference.remove(ref[i]);
					++current;
					faults[i] = "F";
					faultCount++;
				}
				// if there is no free page, the first element of the reference string is added as a temporary string
				else {
					faults[i] = "F";
					faultCount++;
					String temp = reference.get(1);
					//once stored as temp,. the element is removed from the arraylist
					reference.remove(1);
					// each element currently occupying the pages is checked for its next occurrence
					for (String c: curr) {
						index = reference.indexOf(c);
						// if no further elements of a value are found, the element is chosen as the victim frame
						if (index == -1) {
							victim[i] = c;
							vict = c;
							break;
						}
						// else the element farthest away is chosen as the victim
						if (index > max) {
							victim[i] = c;
							vict = c;
							max = index;
						}
					}
					// the elements are swapped and the max is reset
					curr.set(curr.indexOf(vict), temp);
					max = -1;
				}
			}
			// if the element exists within the pages, no fault is found
			else {
					faults[i] = " ";
					reference.remove(1);
				}

			for (int j = 0; j < curr.size(); j++) {
				chart[j][i] = curr.get(j);
			}
		}
		printTable(chart, start, faults, victim);
		System.out.println("Total Faults: " + faultCount);
		System.out.println("");
	}

	// method for LRU algorithm
	protected void lru() throws IOException{
		int current = 0;
		int faultCount = 0;
		int max = -1;
		int start = 1;
		String[][] chart = buildChart();
		String[] faults = new String[ref.length];
		String[] victim = new String[ref.length];
		int[] count = new int[chart.length];
		int index = 0;

		faults[0] = "Page Faults:       ";
		victim[0] = "Victim Frames:     ";
		ArrayList<String> curr = new ArrayList<String>(chart.length);
		
		if(ref.length == 0 || ref == null) {
			throw new IOException();
		}
		/* to attempt to prevent repitiion, I'll only note the differences between this and other 
		 * alogorithms from here out
		 */
		for (int i = 1; i < ref.length; i++) {
			if (!curr.contains(ref[i])){
				if (curr.size() < chart.length) {
					curr.add(ref[i]);
					// a list is kept of the occurences of each element found in the pages
					for( int j = 0; j<count.length; j++) {
						count[j]++;
					}
					count[current]= 1;
					++ current;
					faults[i] = "F";
					faultCount++;
				}
				
				else {
					max = -1;
					index = 0;
					faults[i] = "F";
					faultCount++;
					// search the above mentioned array to find the element with the highest occurance
					// this element is the least recently used and chosen as the victim 
					for( int j = 0; j<count.length; j++) {
						if(count[j] > max) {
							max = count[j];
							index = j;
						}
					}				
					victim[i] = curr.get(index);
					curr.set(index, ref[i]);
					for( int j = 0; j<count.length; j++) {
						count[j]++;
					}
					count[curr.indexOf(ref[i])] = 1;
				}
			}
			else {
					faults[i] = " ";
					// the count for each element is increased
					for( int j = 0; j<count.length; j++) {
						count[j]++;
					}
					// since the element was found, it is no longer the least recently used
					// so the count is reset to 1
					count[curr.indexOf(ref[i])] = 1;
				}

			for (int j = 0; j < curr.size(); j++) {
				chart[j][i] = curr.get(j);
			}
		}
		printTable(chart, start, faults, victim);
		System.out.println("Total Faults: " + faultCount);
		System.out.println("");
	}
	
	protected void lfu() throws IOException{
		int current = 0;
		int faultCount = 0;
		int count;
		Integer min;
		int start = 1;
		String[][] chart = buildChart();
		String[] faults = new String[ref.length];
		String[] victim = new String[ref.length];
		int index;

		faults[0] = "Page Faults:       ";
		victim[0] = "Victim Frames:     ";
		ArrayList<String> curr = new ArrayList<String>(chart.length);
		HashMap<String, Integer> lfuCount = new HashMap<String, Integer>();
		
		if(ref.length == 0 || ref == null) {
			throw new IOException();
		}
		
		for (int i = 1; i < ref.length; i++) {
			if (!curr.contains(ref[i])){
				if (curr.size() < chart.length) {
					curr.add(current, ref[i]);
					// the element is added to the hashmap with a count of 1
					lfuCount.put(ref[i], 1);
					++current;
					faults[i] = "F";
					faultCount++;
				}
				else {
					// the min is chosen as the count of the first element added
					min = lfuCount.get(curr.get(0));
					index = 0;
					faults[i] = "F";
					faultCount++;
					// if the count of the current element is found to be less than the others, the min 
					// is set to that count, and that elements index is selected
					for (int j = 0; j < curr.size(); j++) {
						if(lfuCount.get(curr.get(j))< min) {
							min = lfuCount.get(curr.get(j));
							index = j;
						}
					}
					
					victim[i] = curr.get(index);
					curr.set(index, ref[i]);
					// the hashmap is searched for the reference string element. If found, its count is 
					// incremented, otherwise it is added with a count of 1
					if(lfuCount.containsKey(ref[i])) {
						count = lfuCount.get(ref[i]);
						count++;
						lfuCount.put(ref[i], count);
					}
					else {
						lfuCount.put(ref[i], 1);
					}
				}
			}
			else {
					faults[i] = " ";
					// the count of the element is then incremented
					count = lfuCount.get(ref[i]);
					count++;
					lfuCount.put(ref[i], count);
				}

			for (int j = 0; j < curr.size(); j++) {
				chart[j][i] = curr.get(j);
			}
		}
		printTable(chart, start, faults, victim);
		System.out.println("Total Faults: " + faultCount);
		System.out.println("");
		
	}
	
	// method to build the table
	private String[][] buildChart() throws IOException{
		try {
			Scanner sc = new Scanner(System.in);
			
			if(ref.length == 0 || ref == null) {
				throw new IOException();
			}
			
			// User is prompted to indicate the number of pages
			System.out.println("How many physical frames are present? (Up to 8)");
			int size = sc.nextInt();
			if(size > 8) {
				throw new IllegalArgumentException();
			}
			if (!Character.isDigit(size)) {
				throw new InputMismatchException();
			}
			
			// the new table is created using the length of the reference string 
			// and the users entered number of pages
			String[][] chart = new String[size][ref.length];
			for (int i = 0; i<chart.length; i++) {
				chart[i][0] = "Physical Frame " + i + ":  ";
			}
			return chart;
		} catch (IllegalArgumentException e) {
			System.out.println("Physical pages must be between 2 and 8");
			buildChart();
		} catch (InputMismatchException e) {
			System.out.println("Value must be a number between 2 and 8.");
			buildChart();
		}
		return null;
		
	}
	
	// method to print the table
	private void printTable(String[][] table, int index, String[] faults, String[] victim){
		Scanner sc = new Scanner(System.in);
		String sel;

		if (index <= table[0].length) {
			for (int i = 0; i<table.length; i++) {
				for (int j=0; j<index; j++) {
					if(table[i][j] == null) {
						table[i][j] = " ";
					}
					System.out.print(table[i][j] + " ");	
				}
				System.out.println("");
			}
			System.out.println("");
			for(int f = 0; f<index; f++) {
				if(faults[f] == null) {
					faults[f] = " ";
				}
				System.out.print(faults[f] + " ");
			}
			System.out.println("");
			for(int v = 0; v<index; v++) {
				if(victim[v] == null) {
					victim[v] = " ";
				}
				System.out.print(victim[v] + " ");
			}
			System.out.println("");
			
			do {
				System.out.println("");
				System.out.println("Press F to continue:");
				sel = sc.next();
				System.out.println("");
				
			} while (!sel.equalsIgnoreCase("f"));
			
			
				index++;
				printTable(table, index, faults, victim);
		}                                                                                                         
	}
	
	
}

