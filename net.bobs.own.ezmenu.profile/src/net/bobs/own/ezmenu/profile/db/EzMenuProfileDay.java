package net.bobs.own.ezmenu.profile.db;

import java.sql.SQLException;

import net.bobs.own.db.h2.db.H2AbstractDatabaseService;

public class EzMenuProfileDay {

	public enum WeekDay {
	      SUNDAY(0), 
	      MONDAY(1), 
	      TUESDAY(2), 
	      WEDNESDAY(3), 
	      THURSDAY(4), 
	      FRIDAY(5), 
	      SATURDAY(6);
	   
	      private int day;
	      
	   private WeekDay(int day) {
	      this.day = day;
	   }
	   
	   /**
	    * Return the current day for this profile day.
	    * @return
	    */
	   public int getDay() {
	      return this.day;
	   }
	   
	    /**
	      * Returns the day matching the integer value. 
	      * @param number - integer value of the day
	      * @return - the day enumeration or Sunday if not found
	      */
	 static public WeekDay toDay(int number) {
	    
	    WeekDay profDay = WeekDay.SUNDAY;
	    
	    if (number == WeekDay.SUNDAY.day) {
	       profDay = WeekDay.SUNDAY;
	    } else if (number == WeekDay.MONDAY.day) {
	       profDay = WeekDay.MONDAY;
	    } else if (number == WeekDay.TUESDAY.day) {
	       profDay = WeekDay.TUESDAY;
	    } else if (number == WeekDay.WEDNESDAY.day) {
	       profDay = WeekDay.WEDNESDAY;
	    } else if (number == WeekDay.THURSDAY.day) {
	       profDay = WeekDay.THURSDAY;
	    } else if (number == WeekDay.FRIDAY.day) {
	       profDay = WeekDay.FRIDAY;
	    } else if (number == WeekDay.SATURDAY.day) {
	       profDay = WeekDay.SATURDAY;
	    }
	    
	    return profDay;
	 }
	 
	 /**
	  * Returns the current week day as a string "Sunday", "Monday", etc
	  * 
	  */
	 @Override
	 public String toString() {
	    String weekDay = "";
	    
	    switch (day) {
	       case 0:
	          weekDay = "Sunday";
	          break;
	       case 1:
	          weekDay = "Monday";
	          break;
	       case 2:
	          weekDay = "Tuesday";
	          break;
	       case 3:
	          weekDay = "Wednesday";
	          break;
	       case 4:
	          weekDay = "Thursday";
	          break;
	       case 5:
	          weekDay = "Friday";
	          break;
	       case 6:
	          weekDay = "Saturday";
	          break;
	    }
	    return weekDay;
	 }
	 
	};
	
	private WeekDay  profDay;
	
	/* The category & prepTimes are used to define the allowed values for 
	 * Category & PrepTime Combo Box editors in the Profile Editor.
	 * 
	 * Updates to these fields may also require the ProfileDataGenerator class to be updated.
	 */
	public enum MealCategory {
	   BEEF("Beef"),
	   CHICKEN("Chicken"), 
	   FISH("Fish"), 
	   PASTA("Pasta"),
	   PORK("Pork"), 
	   TURKEY("Turkey"),
	   VEGGIE("Veggie");
	   	   
	   private String category;
	   
	   private MealCategory(String category) {
	      this.category = category;
	   }
	   
//	   /**
//	    * Returns the current category as a String value;
//	    * @return
//	    */
//	   public String getCategory() {
//	      return this.category;
//	   }
	   
	   /**
	    * Returns the current category as a String value
	    * 
	    */
	   @Override 
	   public String toString() {
	      
	      return this.category;
	   }
	   
	     /**
       * Returns the category matching the string value. 
       * @param category - String value of the category
       * @return - the category enumeration or null if not found
       */
      static public MealCategory toCategory(String category) {
         
         MealCategory rtnCategory = null;

         if (category.equals(EzMenuProfileDay.MealCategory.BEEF.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.BEEF;
         } else if (category.equals(EzMenuProfileDay.MealCategory.CHICKEN.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.CHICKEN;
         } else if (category.equals(EzMenuProfileDay.MealCategory.FISH.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.FISH;  
         } else if (category.equals(EzMenuProfileDay.MealCategory.PASTA.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.PASTA; 
         } else if (category.equals(EzMenuProfileDay.MealCategory.PORK.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.PORK;
         } else if (category.equals(EzMenuProfileDay.MealCategory.TURKEY.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.TURKEY;
         } else if (category.equals(EzMenuProfileDay.MealCategory.VEGGIE.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.VEGGIE;   
         }

         return rtnCategory;
      }
	
	};
	
   private MealCategory profCategory;
   
	public enum PrepTimes {
	   TO15("0-15"),
	   TO30("16-30"),
	   TO45("31-45"),
	   TO60("46-60"),
	   PLUS60("61+");
	   
	   private String prepTime;
	   private PrepTimes(String prepTime) {
	      this.prepTime = prepTime;
	   }
	   
	   
	   
	   /**
	    * Return the preparation time for the current profile day
	    * @return - the preparation time
	    */
	   @Override
	   public String toString() {
	      return this.prepTime;
	   }
	   
	    /**
	    * Returns the Preparation Time matching the string value. 
	    * @param category - String value of the preparation time
	    * @return - the category enumeration or null if not found
	    */
	   static public PrepTimes toPrepTime(String prepTime) {
	      
	      PrepTimes rtnPrepTimes = null;

	      if (prepTime.equals(PrepTimes.TO15.toString())) {
	         rtnPrepTimes = PrepTimes.TO15;
	      } else if (prepTime.equals(PrepTimes.TO30.toString())) {
	         rtnPrepTimes = PrepTimes.TO30;
	      } else if (prepTime.equals(PrepTimes.TO45.toString())) {
	         rtnPrepTimes = PrepTimes.TO45;  
	      } else if (prepTime.equals(PrepTimes.TO60.toString())) {
	         rtnPrepTimes = PrepTimes.TO60; 
	      } else if (prepTime.equals(PrepTimes.PLUS60.toString())) {
	         rtnPrepTimes = PrepTimes.PLUS60;
	      }

	      return rtnPrepTimes;
	   }
	}
	private PrepTimes   profPrepTime;
	
	//Add method to set the days (see the insert or update methods in EzMenuProfile)

	/**
	 * Add a new profile row for an EzMenu.
	 * 
	 * @param day - the day of the week for the new row.
	 * @param category - the meal category for the new row.
	 * @param prepTime - the preparation time range for the new row.
	 */
	public EzMenuProfileDay(EzMenuProfileDay.WeekDay day,
							      EzMenuProfileDay.MealCategory category, PrepTimes prepTime) {
		
		profDay = day;
		profCategory = category;
		profPrepTime = prepTime;
	}
	
	/**
	 * Obtain the day of the week for the current profile row.
	 * @return - a <code>day</code> enumeration value for the day of the week.
	 */
	public WeekDay getDay() {
		return profDay;
	}
	
	
	/**
	 * Obtain the category for the current profile row.
	 * @return - a <code>category</code> enumeration value for the meal category.
	 */
	public MealCategory getCategory() {
		return profCategory;
	}
	

	/**
	 * Set the category for the current profile row.
	 * @param MealCategory - a string value for the enumeration value.
	 */
	public void setCategory(int ordinal) {
		
		String[] categories = getCategoryConstants();
		String category = categories[ordinal];
		
		profCategory = EzMenuProfileDay.MealCategory.valueOf(category);
	}
	
	public void setPrepTime(PrepTimes prepTime) {
		profPrepTime = prepTime;
	}
	
	/**
	 * Obtain the preparation time for the current profile row.
	 * @return - the preparation time for the profile.
	 */
	public PrepTimes getprepTime()  {
		return profPrepTime;
	}
	
	/**
	 * Returns the index for the preparation time given.
	 * @param preptime - the preparation time as a string
	 * @return - index of the preparation time, -1 if no matching entry.
	 */
//	public int indexOfPrepTime(String preptime) {
//		int index = -1;
//		for (int ix = 0;ix < prepTimes.length;ix ++) {
//			if (preptime.matches(prepTimes[ix])) {
//				index = ix;
//			}
//		}
//		return index;
//	}
	
	public void bindCategory(H2AbstractDatabaseService service, int index) throws SQLException {
		service.setString(index, profCategory.toString());
	}
	
	public void bindPrepTime(H2AbstractDatabaseService service, int index) throws SQLException {
		service.setString(index, profPrepTime.toString());
	}
	
	/**
	 * Obtain a list of the currently allowed preparation times for the 
	 * profile definition.
	 * @return - A list of the currrently allowed preparation times.
	 */
	static public String[] getPrepTimeConstants() {
	   PrepTimes[] prepTimes = PrepTimes.values();
	   String[] prepareTimes = new String[prepTimes.length];
	   for (int ix = 0; ix < prepareTimes.length;ix++) {
	      prepareTimes[ix] = prepTimes[ix].prepTime;
	   }
		return prepareTimes;
	}
	
	/**
	 * Obtain a list of the currently allowed Categories for the profile 
	 * definition.
	 * @return - A list of the current profile Category enumerations.
	 */
	static public String[] getCategoryConstants() {
		MealCategory[] categories = MealCategory.values();
		String[] names = new String[categories.length];
		
		for (int ix = 0;ix < names.length; ix++) {
			names[ix] = categories[ix].name();
		}
		
		return names;
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
