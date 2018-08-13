package net.bobs.own.ezmenu.menu.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public class MenuPlan {

   private enum MENUPLAN_STATUS {OK,PARTIAL,EMPTY}; 
   private EzMenuProfile profile = null;
   private int numWeeks = 1;
   private ArrayList<Object> menuPlan = null;
 
   private HashMap<String, List<ITable>> mealMap = null;
//   private String switchCategory = " ";
   private MENUPLAN_STATUS planStatus = MENUPLAN_STATUS.OK;
   private ArrayList<String> availableCategories = null;
   static private Logger logger = LogManager.getLogger(MenuPlan.class.getName());
   

   

   public MenuPlan(EzMenuProfile profile, int numWeeks) {
      this.profile = profile;
      this.numWeeks = numWeeks;
      this.mealMap = new HashMap<String, List<ITable>>();
      this.menuPlan = new ArrayList<Object>();
   }

   /**
    * Generates a meal plan
    */
   public void generate() {

      ArrayList<EzMenuMeal> planWeek = new ArrayList<EzMenuMeal>();
      List<ITable> mealsList = null;

      menuPlan.clear();
      mealMap.clear();
      availableCategories = new ArrayList<String>(Arrays.asList(EzMenuProfileDay.getCategoryConstants()));

      logger.debug("Generate meal plan for " + numWeeks + " weeks");

      for (int weekIx = 0; weekIx < numWeeks; weekIx++) {
         for (int day = 0; day <= WeekDay.Saturday.getDay(); day++) {

            EzMenuProfileDay profDay = profile.getProfileDay(day);
            /* *  Get the meals information up front instead of as each day is processed
             * *  Call selectByCategoryPrep 1x to query the database.  The results of the query are
             *    saved to mealMap.  The profile  category.prepTime is used as the key for the list of meals
             */
            String mealKey = profDay.getCategory().toString() + "." + 
                             profDay.getprepTime().getPrepTime();
            /* Get a list of Meals to process
               *  getMeals() should return a list of meals (and will handle category switching?)
               *  OR do it here? 
            */
            mealsList = getMeals(mealKey);
            if (mealsList == null || mealsList.size() == 0) {
               //TODO: getMeals() could not find a list of meals, display message to user and return a partial plan
               logger.debug("Meals list null or empty, exiting menu generation");
               break;
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
               planWeek.add(meal);
               logger.debug("Meal " + meal.getMealName() + " with category " + meal.getMealCatgy()
                            + " and preparation time " + meal.getMealPrepTime() + " added to plan");
   //               removeMeal(meal,mealKey);
               //ASSUME: There are no duplicate meals in the meal list - remove() only removes the first occurrence
               mealsList.remove(meal);
            }
         } // END for(int day = 0;day < EzMenuProfileDay.SATURDAY...)

         logger.debug("Added menu for week " + (weekIx + 1) + " to plan");
         menuPlan.add(planWeek);
         planWeek = new ArrayList<EzMenuMeal>(7);

      } // END for(int wekIx = 0;weekIx < numWeeks...)
      
//      return planStatus;
      
   }

   public int numberWeeks() {
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
      ArrayList<Object> weekPlan = (ArrayList<Object>) menuPlan.get(week);
      return (EzMenuMeal) weekPlan.get(day);
   }
   
   /**
    * Check if the Menu Plan was generated with no meals (EMPTY)
    * @return - true if menu plan is empty (has no entries), false otherwise
    */
   public boolean isEmpty() {
      boolean empty = false;
      if (planStatus.equals(MENUPLAN_STATUS.EMPTY)) {
         empty = true;
      }
      return empty;
   }
   
   /**
    * Check if the menu plan status was generated and was able to be built with 
    * the requested number of meals.
    * 
    * @return - true if the plan built the requested number of meals, false otherwise.
    */
   public boolean isOk() {
      boolean ok = false;
      if (planStatus.equals(MENUPLAN_STATUS.OK)) {
         ok = true;
      }
      return ok;
   }


//   private boolean isInPlan(EzMenuMeal meal) {
//      boolean inPlan = false;
//      ArrayList<EzMenuMeal> planWeek = null;
//
//      Iterator planIterator = menuPlan.iterator();
//      while (planIterator.hasNext()) {
//         planWeek = (ArrayList<EzMenuMeal>) planIterator.next();
//         inPlan = planWeek.contains(meal);
//         // Found an entry, stop looking for more.
//         if (inPlan) {
//            logger.debug("Duplicate meal " + meal.getMealName() + " with category " + meal.getMealCatgy() + " and "
//                  + "preparation time " + meal.getMealPrepTime() + " found in plan");
//            break;
//         }
//      }
//
//      return inPlan;
//   }

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

//   private List<ITable> getMeals(String weekCategory, String weekPrepTime) {
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
   /**
    * 
    * @param mealKey
    * @return
    */
   private List<ITable> getMeals(String mealKey) {

      EzMenuMealMapper mealMapper = EzMenuMealMapper.getMapper();
      List<ITable> mealsList = null;
      boolean done = false;

      //Has the meal(s) already been queried from the database? - YES use that
      //NO - get the meal(s) from the database
       mealsList = mealMap.get(mealKey);
      
      if (mealsList != null && mealsList.size() == 0) {
         //TODO: No meals for this category - switch to a different category 
         // *  Do this until a category that has meals is found OR all the current categories have been 
         //    checked AND not enough meals have been found - 
         //    *  IF this is true issue a message and return the partially completed meal plan
         //    *  May need to queryDatabase....
         
//      } else if (mealsList.size() > 0) {
//         //TODO: Found meals - ret
//         String test = null;
      } else if (mealsList == null ) {
         //No meals found in map, query database....
         while (!done) {
            try {
//               if (switchCategory.equals(" ")) {
               
                 //mealKey = "category.prepTime" so extract those values for querying the database
                  String keys[] = substringMealKey(mealKey);
                  mealsList = mealMapper.selectByCategoryPrep(keys[0], keys[1]);
                  if (mealsList.size() > 0) {
                     logger.debug("Retrieved " + mealsList.size() + " meals from database for category " +
                                  keys[0] + " and prep time " + keys[1]);
                     mealMap.put(mealKey, mealsList);
                  } else if (mealsList.size() == 0) {
                     /* No meals found for the category & prep time... Try a different category
                      * IF NO other categories are found, THEN null will be returned 
                      */
                     mealsList = findMealsByCategory(mealKey);
                     if (mealsList == null) {
                        planStatus = MENUPLAN_STATUS.EMPTY;
                     }  
                  }
            } catch (RunDMLException rdex) {
               rdex.printStackTrace();
               planStatus = MENUPLAN_STATUS.EMPTY;
               // TODO Display Message dialog to user?
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

      return mealsList;

   }
   
   /*
    * 
    */
   private List<ITable> findMealsByCategory(String mealKey) {

      List<ITable> mealsList = null;
      boolean done = false;

//    int categoriesIndex = 0;
      String keys[] = substringMealKey(mealKey);
      EzMenuMealMapper mealMapper = EzMenuMealMapper.getMapper();
      String queryCategory = keys[0];


      while (done == false) {
         // NOTE: For now, this code only uses 1 preparation time value for each category.
         //       Logic could be added to switch/change prepTime as well so meals can be found
//         
         int ix = availableCategories.indexOf(queryCategory);
         if (ix != -1) {
            availableCategories.remove(queryCategory);
            //Make sure to not go past the end of the list
            if ((ix + 1) > availableCategories.size()) {
               ix = 0;
            }
         }

         if (availableCategories.size() > 0) {
            queryCategory = availableCategories.get(ix);
            try {
               mealsList = mealMapper.selectByCategoryPrep(queryCategory, keys[1]);
               //For now, don't worry if the # of meals returned is sufficient to complete the meal plan
               if (mealsList != null && mealsList.size() > 0) {
                  done = true;
//               } else if (availableCategories.size() == 0) {
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
      
      return mealsList;
   }
   
   private String[] substringMealKey(String mealKey) {
      
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
