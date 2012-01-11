/*  $Name:  $
 *	$Revision: 1.1.1.1 $
 *	$Date: 2006/05/03 18:55:18 $
 *	$Author: bflagg $
 *	
 *  Copyright 2002 Sight Software, Inc.  
 */
package cqf.bcd.ui.specs;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.JComboBox;


/**
 *  This class provides a set of three inter-related combo box to be used 
 *	as year, month, and date dropdown lists.  
 *
 */
public class DateDropdownLists {
	
	// Constants
        private static final TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");

        private static final String[] MONTH_ARRAY = { "1", "2", "3", "4", "5", 
			"6", "7", "8", "9", "10", "11", "12" };
	private static final String[] DATE_ARRAY = { "1", "2", "3", "4", "5", 
			"6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", 
			"17", "18", "19", "20", "21", "22", "23", "24", "25", "26", 
			"27", "28", "29", "30", "31" };
	
	
	// Variables
	private int startYear = 0;
	private JComboBox choiceYear = null;
	private JComboBox choiceMonth = null;
	private JComboBox choiceDate = null;
	
	private GregorianCalendar dummyDate = new GregorianCalendar();
	
	
	// Constructor
	public DateDropdownLists(int startYear, int endYear, int defaultYear, 
			int defaultMonth, int defaultDate) {
		
		this.startYear = startYear;
		
		// Make the year array
		String[] yearArray = new String[endYear - startYear + 1];
		for (int i = startYear, j = 0; i <= endYear; i++, j++) {
			yearArray[j] = String.valueOf(i);
		}
		
		// Make the three combo box
		choiceYear = new JComboBox(yearArray);
		choiceMonth = new JComboBox(MONTH_ARRAY);
		choiceDate = new JComboBox(DATE_ARRAY);
		
		// Set default values
		// Note that the month is 0-based in GregorianCalendar
		if (defaultYear < startYear) {
			
			// Default year is earlier than the start year
			// Ignore the given value and set the start year as the default
			choiceYear.setSelectedIndex(0);
			
		} else if (defaultYear > endYear) {
			
			// Default year is later than the end year
			// Ignore the given value and set the end year as the default
			choiceYear.setSelectedIndex(yearArray.length - 1);
			
		} else {
			
			choiceYear.setSelectedIndex(defaultYear - startYear);
		}
		
		choiceMonth.setSelectedIndex(defaultMonth - 1);
		choiceDate.setSelectedIndex(defaultDate - 1);
		
		// Register the item listener for date adjustment
		choiceYear.addItemListener(new DateAdjustmentListener());
		choiceMonth.addItemListener(new DateAdjustmentListener());
		
		// Insure the date dropdown list is correct initiallly
		adjustDate();
	}
	
	
	private void adjustDate() {
		
		int currentDateCount = choiceDate.getItemCount();
		boolean lastDateSelected = (getSelectedDate() == currentDateCount);
		
		int selectedMonth = getSelectedMonth();
		if (isBigMonth(selectedMonth)){
			
			if (currentDateCount < 31) {
				
				for (int i = currentDateCount + 1; i < 32; i++) {
					choiceDate.addItem(String.valueOf(i));
				}
			}
			
		} else if (isSmallMonth(selectedMonth)) {
			
			if(currentDateCount > 30) {
				
				choiceDate.removeItemAt(currentDateCount - 1);
				
			} else if (currentDateCount < 30) {
				
				for(int i = currentDateCount + 1; i < 31; i++) {
					choiceDate.addItem(String.valueOf(i));
				}
			}
			
		} else {
			
			// Must be a Feb
			// Check for leap year
			int targetDateCount = 28;
			if (dummyDate.isLeapYear(getSelectedYear())) {
				targetDateCount = 29;
			}
			
			if (currentDateCount < targetDateCount) {
				
				choiceDate.addItem("29");
				
			} else if (currentDateCount > targetDateCount) {
				
				for (int i = currentDateCount - 1; i >= targetDateCount; 
						i--) {
					
					choiceDate.removeItemAt(i);
				}
			}
		}
		
		if (lastDateSelected) {
			choiceDate.setSelectedIndex(choiceDate.getItemCount() - 1);
		}
	}
	
	
	private boolean isBigMonth(int month) {
		
		return  (month == 1) || (month == 3) || (month == 5) 
				|| (month == 7) || (month == 8) || (month == 10) 
				|| (month == 12);
	}
	
	
	private boolean isSmallMonth(int month) {
		
		return  (month == 4) || (month == 6) || (month == 9) 
				|| (month == 11);
	}
	
	
	// Access methods
	public int getSelectedYear() {
		return  choiceYear.getSelectedIndex() + startYear;
	}
	
	public int getSelectedMonth() {
		return  choiceMonth.getSelectedIndex() + 1;
	}
	
	public int getSelectedDate() {
		return  choiceDate.getSelectedIndex() + 1;
	}
	
	
        public GregorianCalendar getSelectedCalendar() {
            GregorianCalendar gc = new GregorianCalendar(tz);
            gc.set(getSelectedYear(), getSelectedMonth() - 1, getSelectedDate());
            return gc;
        }
	
	
	public JComboBox getYearComboBox() {
		return  choiceYear;
	}
	
	
	public JComboBox getMonthComboBox() {
		return  choiceMonth;
	}
	
	
	public JComboBox getDateComboBox() {
		return  choiceDate;
	}
	
        public String toString() {
            int selectedMonth = getSelectedMonth();
            int selectedDate = getSelectedDate();
            StringBuffer boundBuffer = new StringBuffer();
            boundBuffer.append(getSelectedYear());
            boundBuffer.append("-");
            if (selectedMonth < 10) boundBuffer.append("0");
            boundBuffer.append(selectedMonth);
            boundBuffer.append("-");
            if (selectedDate < 10) boundBuffer.append("0");
            boundBuffer.append(selectedDate);
            return boundBuffer.toString();
        }
        
	// Inner class
	/**
	 *	This class insures the days shown are valid for the month and year 
	 *	selected.  It captures the day currently selected and adjusts it 
	 *	down to the last day of the month if needed.
	 *
	 */
	 
	class DateAdjustmentListener implements ItemListener {
		public void itemStateChanged(ItemEvent e){
			adjustDate();
		}
	}
}
