package net.bobs.own.ezmenu.menu.model;

import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * Generate a menu plan for a specified number of weeks using a <code>EzMenuProfile</code>
 * to determine on what days what meal categories should be used.
 *
 */
public class MenuPlan {

   private enum PlanStatus {OK,INCOMPLETE,EMPTY}; 
   private EzMenuProfile profile = null;
   private int numWeeks = 1;
   private ArrayList<Object> menuPlan = null;
 
   private HashMap<String, List<ITable>> mealMap = null;
   private List<ITable> mealsList = null;
   private PlanStatus planStatus = PlanStatus.EMPTY;
   private ArrayList<String> availableCategories = null;
   private ArrayList<MenuPlanMissingMeal> missingMeals = null;
   static private Logger logger = LogManager.getLogger(MenuPlan.class.getName());

   /**
    * Create a menu plan instance which will use the specified profile and for the
    * number of weeks.
    * @param profile - the profile to use
    * @param numWeeks - number of weeks, 1 to 4 values <= 0 default to 1 and
    *                   values > 4 are set to 4.
    */
   public MenuPlan(EzMenuProfile profile, int numWeeks) {
      if (profile == null) {
         throw new IllegalArgumentException("Profile is required and must not equal null");
      }
      this.profile = profile;
      if (numWeeks <= 0) {
         numWeeks = 1;
      } else if (numWeeks > 4) {
         numWeeks = 4;
      }
      this.numWeeks = numWeeks;
      this.mealMap = new HashMap<String, List<ITable>>();
      this.menuPlan = new ArrayList<Object>();
      this.mealsList = new ArrayList<ITable>();
      this.missingMeals = new ArrayList<MenuPlanMissingMeal>();
   }
   
   public void generate (int numWeeks) {
      menuPlan.clear();
      mealMap.clear();
      mealsList.clear();
      availableCategories = new ArrayList<String>(Arrays.asList(EzMenuProfileDay.getCategoryConstants()));
      
   }

   /**
    * Generates a meal plan using the profile and number of weeks for this 
    * menu plan instance.
    */
   //TODO: Remove numWeeks? - Only used so I can override the method
   public void generate() {

      ArrayList<EzMenuMeal> planWeek = new ArrayList<EzMenuMeal>();
      int requiredMeals = numWeeks * 7;

      menuPlan.clear();
      mealMap.clear();
      mealsList.clear();
      availableCategories = new ArrayList<String>(Arrays.asList(EzMenuProfileDay.getCategoryConstants()));

      logger.debug("Generate meal plan for " + numWeeks + " weeks");

      for (int weekIx = 0; weekIx < numWeeks; weekIx++) {
         for (int day = 0; day <= WeekDay.SATURDAY.getDay(); day++) {

            EzMenuProfileDay profDay = profile.getProfileDay(day);
            /* *  Get the meals information up front instead of as each day is processed
             * *  Call selectByCategoryPrep 1x to query the database.  The results of the query are
             *    saved to mealMap.  The profile  category.prepTime is used as the key for the 
             *    list of meals
             */
            String mealKey = profDay.getCategory().toString() + "." + 
                             profDay.getprepTime().toString();
            /* Get a list of Meals to process
               *  getMeals() should return a list of meals (and will handle category switching?)
               *  OR do it here? 
            */
            getMeals(mealKey);
            if (mealsList == null || mealsList.size() == 0) {
               /* There are no meals for current category & preparation time, 
                * log a message and continue processing.
                * 
                *  NOTE: I could add break command but that stops processing entirely.
                */
               logger.debug("Meals list null or empty, exiting menu generation");
               MenuPlanMissingMeal missingMeal = new MenuPlanMissingMeal(weekIx,day,profDay);
               missingMeals.add(missingMeal);
//               break;
            } else if (mealsList != null & mealsList.size() > 0) {   

               // NOW pick the meal(s) to use in the menu plan...
               int lowerSeed = 0;
               int upperSeed = mealsList.size();  
               
               //Only adding up one meal at a time, SO no need for a loop.   
               int randomNumber = ThreadLocalRandom.current().nextInt(lowerSeed, upperSeed);
               EzMenuMeal meal = (EzMenuMeal) mealsList.get(randomNumber);
               //Add the meal to the menu Plan AND remove it from the list of Results from the database
               //this should eliminate the need to check for duplicate meals being used since only 
               //meals not currently in the menu plan will be in the list of database results.
               if (!isInPlan(meal)) {
                  planWeek.add(meal);
                  requiredMeals--;
                  logger.debug("Meal " + meal.getMealName() + " with category " + meal.getMealCatgy()
                            + " and preparation time " + meal.getMealPrepTime() + " added to plan");
   //               removeMeal(meal,mealKey);
                  //ASSUME: There are no duplicate meals in the meal list - remove() 
                  //        only removes the first occurrence
                  mealsList.remove(meal);
               }
            }
         } // END for(int day = 0;day < EzMenuProfileDay.SATURDAY...)

         logger.debug("Added menu for week " + (weekIx + 1) + " to plan");
         if (planWeek.size() > 0) {
            menuPlan.add(planWeek);
         }
         planWeek = new ArrayList<EzMenuMeal>(7);         

      } // END for(int wekIx = 0;weekIx < numWeeks...)
      
      //Set the Plan Status based on the number of meals added to the plan
      if (requiredMeals == 0) {
         planStatus = PlanStatus.OK;
      } else if (requiredMeals == numWeeks * 7) {
         planStatus = PlanStatus.EMPTY;
      } else if (requiredMeals < numWeeks * 7) {
         planStatus = PlanStatus.INCOMPLETE;
      }
   }

   /**
    * Return the current number of generated weeks in the menu plan.
    * <b>Not all the plan weeks may be completed (have 7 meals)</b>
    * @return
    */
   public int planSize() {
      return menuPlan.size();
   }

   /**
    * Return the meal from the menu plan for a specified week and day.
    * 
    * @param week
    *           - 0 based week (0 = week 1, 1=week 2, etc)
    * @param day
    *           - 0 based day (0 = Sunday, 1 = Monday, 2 = Tuesday, etc)
    * @return
    */
   public EzMenuMeal getMeal(int week, int day) {

      // week--;
      ArrayList<Object> arrayList = (ArrayList<Object>) menuPlan.get(week);
      ArrayList<Object> weekPlan = arrayList;
      return (EzMenuMeal) weekPlan.get(day);
   }
   
   /**
    * Return the size (# of meals) for a week in the plan
    * 
    * @param week - the number of the week, first week is 0, second week is 1, etc
    * @return - the # of meals for the week, -1 if the week is not found
    */
   public int sizePlanWeek(int week) {
      int planSize = -1;

//      if (week > 0) {
//         week  = week - 1;         
//      }
      
      if ((week <= menuPlan.size()) && menuPlan.size() != 0) {
         @SuppressWarnings("unchecked")
         ArrayList<Object> weekPlan = (ArrayList<Object>) menuPlan.get(week);
         planSize = weekPlan.size();
      }
      
      return planSize;
   }
   
   /**
    * Check if the Menu Plan was generated with no meals (EMPTY)
    * @return - true if menu plan is empty (has no entries), false otherwise
    */
   public boolean isEmpty() {
      boolean empty = false;
      if (planStatus.equals(PlanStatus.EMPTY)) {
         empty = true;
      }
      return empty;
   }
   
   /**
    * Check if the Menu Plan was generated with a a partial list 
    * of meals (INCOMPLETE)
    * 
    * @return - true if the meal plan is incomplete, false otherwise
    */
   public boolean isIncomplete() {
      boolean incomplete = false;
      if (planStatus.equals(PlanStatus.INCOMPLETE)) {
         incomplete = true;
      }
      return incomplete;
   }
   
   /**
    * Check if the menu plan status was generated and was able to be built with 
    * the requested number of meals.
    * 
    * @return - true if the plan built the requested number of meals, false otherwise.
    */
   public boolean isOk() {
      boolean ok = false;
      if (planStatus.equals(PlanStatus.OK)) {
         ok = true;
      }
      return ok;
   }
   
   /**
    * Indicates if the menu plan has any missing meals
    * @return true if there are missing meals, false otherwise
    */
   public boolean hasMissingMeals() {
      boolean missMeals = false;
      if (missingMeals.size() > 0) {
         missMeals = true;
      }
      return missMeals;
   }
   
   /**
    * Returns the number of missing meals in the current menu plan
    * @return - the number of missing meals
    */
   public int sizeMissingMeals() {
      return missingMeals.size();
   }
   
   public MenuPlanMissingMeal getMissingMeal(int index) {
      if (index < 0) {
         throw new IllegalArgumentException("Index is negative number");
      } else if (index > missingMeals.size()) {
         throw new IllegalArgumentException("Index is > size");
      }
      
      return missingMeals.get(index);
   }


   private boolean isInPlan(EzMenuMeal meal) {
      boolean inPlan = false;
      ArrayList<EzMenuMeal> planWeek = null;

      Iterator<Object> planIterator = menuPlan.iterator();
      while (planIterator.hasNext()) {
         ArrayList<EzMenuMeal> next = (ArrayList<EzMenuMeal>) planIterator.next();
         planWeek = next;
         inPlan = planWeek.contains(meal);
         // Found an entry, stop looking for more.
         if (inPlan) {
//            logger.debug("Duplicate meal " + meal.getMealName() + " with category " + 
//                         meal.getMealCatgy() + " and " + "preparation time " + 
//                         meal.getMealPrepTime() + " found in plan");
            break;
         }
      }

      return inPlan;
   }

   /*
    * Returns the number of entries from the mealsList (database Query) that are
    * currently used in the menu plan. 0 is returned if no entries are found.
    */
   // private int countUsedMeals(List<ITable> mealsList) {
   //
   // int usedCount = 0;
   //
   // if (mealsList.size() == 0) {
   // return 0;
   // }
   //
   // for (int index = 0; index <= mealsList.size(); index++) {
   // EzMenuMeal meal = (EzMenuMeal) mealsList.get(index);
   // if (isInPlan(meal)) {
   // usedCount++;
   // }
   // }
   //
   // return usedCount;
   // }

   // private void switchCategory(String weekCategory) {

   //// String newSwitchCategory = " ";
   // if (switchCategory.equals(" ")) {
   // switchCategory = weekCategory;
   // } else if (switchCategory.equals(weekCategory)) {
   // switchCategory = null;
   // }
   //
   // for (int index = 0; index < categories.length; index++) {
   // if (categories[index].equals(switchCategory)) {
   // switchCategory = categories[index+1];
   // break;
   // }
   // }

   //// return newSwitchCategory;
   // }

   /*
    * Returns a list meals for a given meal key ("category.prep_time").  
    *    *  Check mealMap to see if the database has already been queried for the mealKey.  
    *       *  IF an entry is found THEN 
    *             the list of meals in the list will be returned.  
    *       *  IF an entry is found in mealMap AND no meals are in the list THEN
    *             category switching (checking the database for a different category) is done UNTIL
    *                *  a category with a matching prep time is found OR
    *                *  all categories have been checked and NO other categories for the prep time are found.
    *                   *  a NULL list is returned.
    *                   
    * @param mealKey - concatenation of meal category + meal preparation time.
    * @return - list of meals for the mealKey from database or mealMap, null if no list found.
    * 
    * calls - findMealsByCategory - to perform category switching logic                  
    */
   
   /*
    * 
    * @param mealKey
    * @return
    */
     private void getMeals(String mealKey) {

      EzMenuMealMapper mealMapper = EzMenuMealMapper.getMapper();
      boolean done = false;
      
      mealsList = mealMap.get(mealKey);      
      if (mealsList != null && mealsList.size() == 0) {
         //TODO: No meals for this category - switch to a different category 
         /* *  Do this until a category that has meals is found OR 
          *    all the current categories have been 
          *   checked AND not enough meals have been found - 
          *   *  IF this is true issue a message and return the partially completed meal plan
          *   *  May need to queryDatabase....
          */
//      } else if (mealsList.size() > 0) {
//         //TODO: Found meals - ret
//         String test = null;
      } else if (mealsList == null ) {
         //No meals found in map, query database....
         while (!done) {
            try {
                  //mealKey = "category.prepTime" so extract those values for querying the database
                  String keys[] = getMealKeys(mealKey);
                  mealsList = mealMapper.selectByCategoryPrep(keys[0], keys[1]);
                  if (mealsList.size() > 0) {
                     logger.debug("Retrieved " + mealsList.size() + " meals from database for category " +
                                  keys[0] + " and prep time " + keys[1]);
                     mealMap.put(mealKey, mealsList);
                  } else if (mealsList.size() == 0) {
                     /* No meals found for the category & prep time... Try a different category
                      * IF NO other categories are found, THEN null will be returned 
                      */
                       findMealsByCategory(mealKey);
                  }
            } catch (RunDMLException rdex) {
               rdex.printStackTrace();
               planStatus = PlanStatus.EMPTY;
            }

//            requiredMeals = profile.numberOfCategories(weekCategory) * numWeeks;
//            // alreadyUsedMeals = countUsedMeals(mealsList);
//            for (int index = 0; index < mealsList.size(); index++) {
//               EzMenuMeal meal = (EzMenuMeal) mealsList.get(index);
//               if (isInPlan(meal)) {
//                  // usedCount++;
//                  alreadyUsedMeals++;
//               }
//            }

//            // IF no meals matching category & preparation time switch to next category....
//            mealsNeeded = requiredMeals - alreadyUsedMeals;
//            mealsNeeded = mealsList.size() - mealsNeeded;
//            if (mealsNeeded <= 0) {
//               if (switchCategory.equals(" ")) {
//                  switchCategory = weekCategory;
//               } else {
//                  int lastIx = categories.length;
//                  lastIx--;
//                  if (categories[lastIx].equals(switchCategory)) {
//                     switchCategory = null;
//                     done = true;
//                  }
//               }

//               for (int index = 0; index < categories.length && switchCategory != null; index++) {
//                  if (categories[index].equals(switchCategory)) {
//                     switchCategory = categories[index + 1];
//                     break;
//                  }
//               }
//
//            } else {
               done = true;
//              }  
         } // END while(!done)         
      }

//      return mealsList;

   }
   
     private void findMealsByCategory(String mealKey) {

      boolean done = false;
      String keys[] = getMealKeys(mealKey);
      EzMenuMealMapper mealMapper = EzMenuMealMapper.getMapper();
      String queryCategory = keys[0];     //The category of the current mealKey

      while (done == false) {
         // NOTE: For now, this code only uses 1 preparation time value for each category.
         //       Logic could be added to switch/change prepTime as well so meals can be found
         //
         
         /* IF currentCategory already been used THEN  
          *    *  it's not in the availableCategories list (ix = -1)
          * ELSE
          *    *  use the category & remove it from the available categories list
          */
         int ix = availableCategories.indexOf(queryCategory);
         if (ix != -1) {
            availableCategories.remove(queryCategory);
            //Make sure to not go past the end of the list
            if ((ix + 1) > availableCategories.size()) {
               ix = 0;
            }
         } else if (ix == -1) {
            ix = 0;
         }         

         /*
          * IF there are still available categories THEN
          *    *  query database 
          *    *  check if meals are returned for the category 
          *    *  remove any meals from the list returned from the database IF
          *       the meal is already in the plan.
          * ELSE (have checked all categories) 
          *    *  set done to true so no more checks are done
          *    *  set mealsList = null to indicate no more meals could be found.
          */
         if (availableCategories.size() > 0) {
            queryCategory = availableCategories.get(ix);            
            try {
               mealsList = mealMapper.selectByCategoryPrep(queryCategory, keys[1]);
               //For now, don't worry if the # of meals returned is sufficient to complete the 
               //meal plan
               if (mealsList != null && mealsList.size() > 0) {
                  done = true;
                  removePlanMeals();
//             } else if (availableCategories.size() == 0) {
//                  //This SHOULD indicate that all categories have been checked.
//                  done = true;
               }          
            } catch (RunDMLException rdex) {
               logger.error(rdex.getMessage(), rdex);
            }
            
         } else {
            done = true;
            mealsList = null;
         }
      }
      
//      return mealsList;
   }
     
   private void removePlanMeals() {
      
      Iterator<ITable> iterator = mealsList.iterator();
      while (iterator.hasNext()) {
         ITable table = iterator.next();
         if (table instanceof EzMenuMeal) {
            EzMenuMeal meal = (EzMenuMeal) table;
            if (isInPlan(meal)) {
               iterator.remove();
            }
         }
      }
//      for (ITable meal : mealsList) {
//         if (isInPlan((EzMenuMeal) meal)) {
//            mealsList.remove((EzMenuMeal) meal);
//         }
//      }
   }
   
   /*
    * Returns the meal category and meal preparation time values from a mealKey
    * 
    * @param mealKey - string key used to locate meals in mealMap
    * @return - a string array [0]contains the category,
    *           [1] contains the preparation time 
    */
   private String[] getMealKeys(String mealKey) {
      
      String keys[] = new String[2];
      
      int index = mealKey.indexOf('.');
      keys[0] =  mealKey.substring(0, index);
      keys[1] = mealKey.substring(index+1);
      
      return keys;
   }
   
//   private void removeMeal(EzMenuMeal meal,String mealKey) {
//      
//   }
   
}
