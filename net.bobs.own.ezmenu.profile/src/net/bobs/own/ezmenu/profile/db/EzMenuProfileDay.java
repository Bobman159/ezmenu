package net.bobs.own.ezmenu.profile.db;

import java.sql.SQLException;

import net.bobs.own.db.h2.db.H2AbstractDatabaseService;

public class EzMenuProfileDay {

	public static final	int		SUNDAY = 0;
	public static final int		MONDAY = 1;
	public static final int 	TUESDAY = 2;
	public static final int		WEDNESDAY = 3;
	public static final int		THURSDAY = 4;
	public static final int		FRIDAY = 5;
	public static final int		SATURDAY = 6;

	public enum day {Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday};
	/* The category & prepTimes are used to define the allowed values for 
	 * Category & PrepTime Combo Box editors in the Profile Editor.
	 * 
	 * Updates to these fields may also require the ProfileDataGenerator class to be updated.
	 */
	public enum category {Beef,Chicken, Fish, Pasta,Pork, Turkey,Veggie};
	final static String[] prepTimes = new String[] {"0-15","16-30","31-45","46-60","61+"};
	
	private day  profDay;
	private category profCategory;
	private String  profPrepTime;
	
	//Add method to set the days (see the insert or update methods in EzMenuProfile)

	/**
	 * Add a new profile row for an EzMenu.
	 * 
	 * @param day - the day of the week for the new row.
	 * @param category - the meal category for the new row.
	 * @param prepTime - the preparation time range for the new row.
	 */
	public EzMenuProfileDay(EzMenuProfileDay.day day,
							EzMenuProfileDay.category category, String prepTime) {
		
		profDay = day;
		profCategory = category;
		profPrepTime = prepTime;
	}
	
	/**
	 * Obtain the day of the week for the current profile row.
	 * @return - a <code>day</code> enumeration value for the day of the week.
	 */
	public day getDay() {
		return profDay;
	}
	
	
	/**
	 * Obtain the category for the current profile row.
	 * @return - a <code>category</code> enumeration value for the meal category.
	 */
	public category getCategory() {
		return profCategory;
	}
	

	/**
	 * Set the category for the current profile row.
	 * @param category - a string value for the enumeration value.
	 */
	public void setCategory(int ordinal) {
		
		String[] categories = getCategoryConstants();
		String category = categories[ordinal];
		
		profCategory = EzMenuProfileDay.category.valueOf(category);
	}
	
	/**
	 * Set the preparation time for the current profile row.
	 * @param time - The preparation time string.
	 */
	public void setPrepTime(String time) {
		profPrepTime = time;
	}
	
	public void setPrepTime(int index) {
		profPrepTime = prepTimes[index];
	}
	
	/**
	 * Obtain the preparation time for the current profile row.
	 * @return - the preparation time for the profile.
	 */
	public String getprepTime()  {
		return profPrepTime;
	}
	
	/**
	 * Returns the index for the preparation time given.
	 * @param preptime - the preparation time as a string
	 * @return - index of the preparation time, -1 if no matching entry.
	 */
	public int indexOfPrepTime(String preptime) {
		int index = -1;
		for (int ix = 0;ix < prepTimes.length;ix ++) {
			if (preptime.matches(prepTimes[ix])) {
				index = ix;
			}
		}
		return index;
	}
	
	public void bindCategory(H2AbstractDatabaseService service, int index) throws SQLException {
		service.setString(index, profCategory.toString());
	}
	
	public void bindPrepTime(H2AbstractDatabaseService service, int index) throws SQLException {
		service.setString(index, profPrepTime);
	}
	
	/**
	 * Obtain a list of the currently allowed preparation times for the 
	 * profile definition.
	 * @return - A list of the currrently allowed preparation times.
	 */
	static public String[] getPrepTimeConstants() {
		return prepTimes;
	}
	
	/**
	 * Obtain a list of the currently allowed Categories for the profile 
	 * definition.
	 * @return - A list of the current profile Category enumerations.
	 */
	static public String[] getCategoryConstants() {
		category[] categories = category.values();
		String[] names = new String[categories.length];
		
		for (int ix = 0;ix < names.length; ix++) {
			names[ix] = categories[ix].name();
		}
		
		return names;
	}
	
	  /**
	   * Returns the day matching the integer value. 
	   * @param number - integer value of the day
	   * @return - the day enumeration or Sunday if not found
	   */
	static public day toDay(int number) {
	   
	   day profDay = day.Sunday;
	   
	   if (number == EzMenuProfileDay.SUNDAY) {
	      profDay = day.Sunday;
	   } else if (number == EzMenuProfileDay.MONDAY) {
	      profDay = day.Monday;
	   } else if (number == EzMenuProfileDay.TUESDAY) {
	      profDay = day.Tuesday;
	   } else if (number == EzMenuProfileDay.WEDNESDAY) {
	      profDay = day.Wednesday;
	   } else if (number == EzMenuProfileDay.THURSDAY) {
	      profDay = day.Thursday;
	   } else if (number == EzMenuProfileDay.FRIDAY) {
	      profDay = day.Friday;
	   } else if (number == EzMenuProfileDay.SATURDAY) {
	      profDay = day.Saturday;
	   }
	   
	   return profDay;
	}
	
	/**
	 * Returns the category matching the string value. 
	 * @param category - String value of the category
	 * @return - the category enumeration or null if not found
	 */
	static public category toCategory(String category) {
		
		category rtnCategory = null;

		if (category.equals(EzMenuProfileDay.category.Beef.toString())) {
			rtnCategory = EzMenuProfileDay.category.Beef;
		} else if (category.equals(EzMenuProfileDay.category.Chicken.toString())) {
			rtnCategory = EzMenuProfileDay.category.Chicken;
		} else if (category.equals(EzMenuProfileDay.category.Fish.toString())) {
			rtnCategory = EzMenuProfileDay.category.Fish;	
		} else if (category.equals(EzMenuProfileDay.category.Pasta.toString())) {
			rtnCategory = EzMenuProfileDay.category.Pasta;	
		} else if (category.equals(EzMenuProfileDay.category.Pork.toString())) {
			rtnCategory = EzMenuProfileDay.category.Pork;
		} else if (category.equals(EzMenuProfileDay.category.Turkey.toString())) {
			rtnCategory = EzMenuProfileDay.category.Turkey;
		} else if (category.equals(EzMenuProfileDay.category.Veggie.toString())) {
			rtnCategory = EzMenuProfileDay.category.Veggie;	
		}

		return rtnCategory;
	}
	
	@Override 
	public String toString() {
		String dump = null;
		
		dump = "day= " + profDay.toString() +
			   " category= "  + profCategory.toString() + 
			   " prepTime= " + profPrepTime;
		
		return dump;
	}

}
