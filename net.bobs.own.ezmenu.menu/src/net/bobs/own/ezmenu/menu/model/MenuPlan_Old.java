package net.bobs.own.ezmenu.menu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.db.EzMenuMealMapper;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.WeekDay;

public class MenuPlan_Old {

	private EzMenuProfile	profile = null;
	private int numWeeks = 1;
	private ArrayList<Object>	menuPlan = null;
	private String[] categories = EzMenuProfileDay.getCategoryConstants();
	private HashMap<String,ArrayList<EzMenuMeal>> mealMap = null;
	private String switchCategory = " ";
	static private Logger logger = LogManager.getLogger(MenuPlan_Old.class.getName());

	
	public MenuPlan_Old(EzMenuProfile profile,int numWeeks) {
		this.profile = profile;
		this.numWeeks = numWeeks;
		this.mealMap = new HashMap<String,ArrayList<EzMenuMeal>>();
		this.menuPlan = new ArrayList<Object>();
	}
	
	public void generate() {
		
	   ArrayList<EzMenuMeal> planWeek = new ArrayList<EzMenuMeal>();
	   List<ITable> mealsList = null;

	   
	   menuPlan.clear();
	   logger.debug("Generate meal plan for " + numWeeks + " weeks");
	   
	   for (int weekIx = 0; weekIx < numWeeks; weekIx++) {

	      for (int day = 0;day <= WeekDay.Saturday.getDay();day++) {
	         
	         if (mealsList != null && mealsList.size() > 0) {
	            mealsList.clear();
	         }

	         EzMenuProfileDay profDay = profile.getProfileDay(day);
	         String weekCatgy = profDay.getCategory().toString();
	         String weekPrepTime = profDay.getprepTime().getPrepTime();
	         
//	         try {
	            //TODO: Consider getting the meals information up front instead of as each day is processed
	            // IE right now if a category & prep-time ("beef","0-15") is in the profile 2x then
	            //selectByCategoryPrep is called 2x (1x for each day).  maybe it would be better to 
	            //make the database query 1x and save it.  This logic would then use the list from where it was saved
	            mealsList = getMeals(weekCatgy,weekPrepTime);
	            
	            //TODO: If there are 0 meals (mealsList = null?) THEN exit?
	            //NO, think I want to skip to next category...
//	            mealsList = mealMapper.selectByCategoryPrep(weekCatgy,weekPrepTime);
	            logger.debug("Retrieved " + mealsList.size() + " meals from database");


//	         } catch (RunDMLException e) {
//	            // TODO Display Message dialog to user?
//	            e.printStackTrace();
//	         }
	      
   	      //NOW pick the meal(s) to use in the menu plan...
   	      int lowerSeed = 0;
   	      int upperSeed = mealsList.size();
   	      
   	      boolean planned = false;
   	      while (planned == false) {
      	      int randomNumber = ThreadLocalRandom.current().nextInt(lowerSeed, upperSeed);
      	      EzMenuMeal meal = (EzMenuMeal) mealsList.get(randomNumber);
      	      //Check for duplicate entry and if it's not in the plan add it, otherwise generate a new number to use
      	      //another meal
      	      boolean used = false;
      	      used = planWeek.contains(meal);
      	      if (used == false) {
      	         //Make sure the meal wasn't used elsewhere in the Menu plan before adding it....
      	         used = isInPlan(meal);
      	         if (used == false) {
         	         planWeek.add(meal);
         	         logger.debug("Meal " + meal.getMealName() + "with category " + meal.getMealCatgy() + 
         	                      " and preparation time " + meal.getMealPrepTime() + " added to plan");
         	         planned = true;
      	         }
      	      }
	         }
         } //END for(int day = 0;day < WeekDay.Saturday.getDay()...)
	      
         logger.debug("Added menu for week " + numWeeks + " to plan");
	      menuPlan.add(planWeek);
	      planWeek = new ArrayList<EzMenuMeal>(7);
	      
	   } //END for(int wekIx = 0;weekIx < numWeeks...)
	}
	
	public int numberWeeks() {
	   return menuPlan.size();
	}
	
	/**
	 * Return the meal from the menu plan for a specified week and day.  
	 * @param week - 0 based week (0 = week 1, 1=week 2, etc)
	 * @param day - 0 based day (0 = Sunday, 1 = Monday, 2 = Tuesday, etc)
	 * @return
	 */
	public EzMenuMeal getMeal(int week,int day) {

//	   week--;
	   ArrayList<Object> weekPlan = (ArrayList<Object>) menuPlan.get(week);
	   return (EzMenuMeal) weekPlan.get(day);
	}
	
	private boolean isInPlan(EzMenuMeal meal) {
	   boolean inPlan = false;
	   ArrayList<EzMenuMeal> planWeek = null;
	   
	   Iterator planIterator = menuPlan.iterator();
	   while(planIterator.hasNext()) {
	      planWeek = (ArrayList<EzMenuMeal>) planIterator.next();
	      inPlan = planWeek.contains(meal);
	      //Found an entry, stop looking for more.
	      if (inPlan) {
	         logger.debug("Duplicate meal " + meal.getMealName() + " with category " + meal.getMealCatgy() + 
	                      " and " + "preparation time " + meal.getMealPrepTime() + " found in plan");
	         break;
	      }
	   }
	   
	   return inPlan;
	}
	
	/*
	 * Returns the number of entries from the mealsList (database Query) that are currently 
	 * used in the menu plan.  0 is returned if no entries are found.
	 */
//	private int countUsedMeals(List<ITable> mealsList) {
//	   
//	   int usedCount = 0;
//	   
//	   if (mealsList.size() == 0) {
//	      return 0;
//	   }
//	   
//	   for (int index = 0; index <= mealsList.size(); index++) {
//	      EzMenuMeal meal = (EzMenuMeal) mealsList.get(index);
//	      if (isInPlan(meal)) {
//	         usedCount++;
//	      }
//	   }
//	   
//	   return usedCount;
//	}
	
//	private void switchCategory(String weekCategory) {
	   
////	   String newSwitchCategory = " ";
//	   if (switchCategory.equals(" ")) {
//	      switchCategory = weekCategory;
//	   } else if (switchCategory.equals(weekCategory)) {
//	      switchCategory = null;
//	   }
//	   
//	   for (int index = 0; index < categories.length; index++) {
//	      if (categories[index].equals(switchCategory)) {
//	         switchCategory = categories[index+1];
//	         break;
//	      }
//	   }
	   
////	   return newSwitchCategory;
//	}
	
	private List<ITable> getMeals(String weekCategory,String weekPrepTime) {
	   
	   EzMenuMealMapper mealMapper = EzMenuMealMapper.getMapper();
	   List<ITable>  mealsList = null;
	   int requiredMeals = 0;
	   int alreadyUsedMeals = 0;
	   int mealsNeeded = 0;
	   boolean done = false;
	   
	   while (!done) {
	      
//	      String switchCatgy = weekCategory;
   	   try {
   	      if (switchCategory.equals(" ")) {
   	         mealsList = mealMapper.selectByCategoryPrep(weekCategory,weekPrepTime);
   	      } else {
   	         //TODO: May need to switch/change prepTime as well so meals can be found?
               mealsList = mealMapper.selectByCategoryPrep(switchCategory,weekPrepTime);
   	      }
   	   } catch (RunDMLException rdex) {
   	      rdex.printStackTrace();
   	      // TODO Display Message dialog to user?
   	   }
   	   
         requiredMeals = profile.numberOfCategories(weekCategory) * numWeeks;
   //    alreadyUsedMeals = countUsedMeals(mealsList);
         for (int index = 0; index < mealsList.size(); index++) {
            EzMenuMeal meal = (EzMenuMeal) mealsList.get(index);
            if (isInPlan(meal)) {
   //            usedCount++;
               alreadyUsedMeals++;
            }
         }
         
         //IF no meals matching category & preparation time switch to next category....
         mealsNeeded = requiredMeals - alreadyUsedMeals;
         mealsNeeded = mealsList.size() - mealsNeeded;
         if(mealsNeeded <= 0) {
            if (switchCategory.equals(" ")) {
               switchCategory = weekCategory;
            } else {
               int lastIx = categories.length;
               lastIx--;
               if (categories[lastIx].equals(switchCategory)) {
                  switchCategory = null;
                  done = true;
               }
            }
       
            for (int index = 0; index < categories.length && switchCategory != null; index++) {
               if (categories[index].equals(switchCategory)) {
                  switchCategory = categories[index+1];
                  break;
               }
            }
            
         } else {
            done = true;
         }
   	}    //END while(!done)
	   return mealsList;
	   
	}
}
