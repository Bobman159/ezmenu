package net.bobs.own.ezmenu.profile.db;

import java.sql.SQLException;

import net.bobs.own.db.h2.db.H2AbstractDatabaseService;

public class EzMenuProfileDay {

	public enum WeekDay {
	      Sunday(0), 
	      Monday(1), 
	      Tuesday(2), 
	      Wednesday(3), 
	      Thursday(4), 
	      Friday(5), 
	      Saturday(6);
	   
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
	    
	    WeekDay profDay = WeekDay.Sunday;
	    
	    if (number == WeekDay.Sunday.day) {
	       profDay = WeekDay.Sunday;
	    } else if (number == WeekDay.Monday.day) {
	       profDay = WeekDay.Monday;
	    } else if (number == WeekDay.Tuesday.day) {
	       profDay = WeekDay.Tuesday;
	    } else if (number == WeekDay.Wednesday.day) {
	       profDay = WeekDay.Wednesday;
	    } else if (number == WeekDay.Thursday.day) {
	       profDay = WeekDay.Thursday;
	    } else if (number == WeekDay.Friday.day) {
	       profDay = WeekDay.Friday;
	    } else if (number == WeekDay.Saturday.day) {
	       profDay = WeekDay.Saturday;
	    }
	    
	    return profDay;
	 }
	 
	};
	
	private WeekDay  profDay;
	
	/* The category & prepTimes are used to define the allowed values for 
	 * Category & PrepTime Combo Box editors in the Profile Editor.
	 * 
	 * Updates to these fields may also require the ProfileDataGenerator class to be updated.
	 */
	public enum MealCategory {
	   Beef,Chicken, Fish, Pasta,Pork, Turkey,Veggie;
	   
	     /**
       * Returns the category matching the string value. 
       * @param category - String value of the category
       * @return - the category enumeration or null if not found
       */
      static public MealCategory toCategory(String category) {
         
         MealCategory rtnCategory = null;

         if (category.equals(EzMenuProfileDay.MealCategory.Beef.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.Beef;
         } else if (category.equals(EzMenuProfileDay.MealCategory.Chicken.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.Chicken;
         } else if (category.equals(EzMenuProfileDay.MealCategory.Fish.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.Fish;  
         } else if (category.equals(EzMenuProfileDay.MealCategory.Pasta.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.Pasta; 
         } else if (category.equals(EzMenuProfileDay.MealCategory.Pork.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.Pork;
         } else if (category.equals(EzMenuProfileDay.MealCategory.Turkey.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.Turkey;
         } else if (category.equals(EzMenuProfileDay.MealCategory.Veggie.toString())) {
            rtnCategory = EzMenuProfileDay.MealCategory.Veggie;   
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
	   public String getPrepTime() {
	      return this.prepTime;
	   }
	   
	    /**
	    * Returns the Preparation Time matching the string value. 
	    * @param category - String value of the preparation time
	    * @return - the category enumeration or null if not found
	    */
	   static public PrepTimes toPrepTime(String prepTime) {
	      
	      PrepTimes rtnPrepTimes = null;

	      if (prepTime.equals(PrepTimes.TO15.getPrepTime())) {
	         rtnPrepTimes = PrepTimes.TO15;
	      } else if (prepTime.equals(PrepTimes.TO30.getPrepTime())) {
	         rtnPrepTimes = PrepTimes.TO30;
	      } else if (prepTime.equals(PrepTimes.TO45.getPrepTime())) {
	         rtnPrepTimes = PrepTimes.TO45;  
	      } else if (prepTime.equals(PrepTimes.TO60.getPrepTime())) {
	         rtnPrepTimes = PrepTimes.TO60; 
	      } else if (prepTime.equals(PrepTimes.PLUS60.getPrepTime())) {
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
		service.setString(index, profPrepTime.getPrepTime());
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
