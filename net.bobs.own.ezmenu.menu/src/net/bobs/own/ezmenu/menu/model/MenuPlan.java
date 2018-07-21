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

public class MenuPlan {

   private EzMenuProfile profile = null;
   private int numWeeks = 1;
   private ArrayList<Object> menuPlan = null;
   private String[] categories = EzMenuProfileDay.getCategoryConstants();
   private HashMap<String, List<ITable>> mealMap = null;
   private String switchCategory = " ";
   static private Logger logger = LogManager.getLogger(MenuPlan.class.getName());

   public MenuPlan(EzMenuProfile profile, int numWeeks) {
      this.profile = profile;
      this.numWeeks = numWeeks;
      this.mealMap = new HashMap<String, List<ITable>>();
      this.menuPlan = new ArrayList<Object>();
   }

   public void generate() {

      ArrayList<EzMenuMeal> planWeek = new ArrayList<EzMenuMeal>();
      List<ITable> mealsList = null;

      menuPlan.clear();
      mealMap.clear();
      logger.debug("Generate meal plan for " + numWeeks + " weeks");

      for (int weekIx = 0; weekIx < numWeeks; weekIx++) {
         for (int day = 0; day <= EzMenuProfileDay.SATURDAY; day++) {

            if (mealsList != null && mealsList.size() > 0) {
               mealsList.clear();
            }

            EzMenuProfileDay profDay = profile.getProfileDay(day);
            /* *  Get the meals information up front instead of as each day is processed
             * *  Call selectByCategoryPrep 1x to query the database.  The results of the query are
             *    saved to mealMap.  The profile  category.prepTime is used as the key for the list of meals
             */
            String mealKey = profDay.getCategory().toString() + "." + profDay.getprepTime();
            mealsList = getMeals(mealKey);
            if (mealsList != null && mealsList.size() == 0) {
               //TODO: Skip to next category since no meals? (may need to handle mealsList == null) also
            }


            // NOW pick the meal(s) to use in the menu plan...
            int lowerSeed = 0;
            int upperSeed = mealsList.size();
//            logger.debug("lowerSeed =" + lowerSeed + " upperSeed= " + upperSeed);

//            boolean planned = false;
//            while (planned == false) {
              while (mealsList.size() > 0) { 
               int randomNumber = ThreadLocalRandom.current().nextInt(lowerSeed, upperSeed);
               EzMenuMeal meal = (EzMenuMeal) mealsList.get(randomNumber);
               //Add the meal to the menu Plan AND remove it from the list of Results from the database
               //this should eliminate the need to check for duplicate meals being used since only 
               //meals not currently in the menu plan will be in the list of database results.
               planWeek.add(meal);
               logger.debug("Meal " + meal.getMealName() + " with category " + meal.getMealCatgy()
               + " and preparation time " + meal.getMealPrepTime() + " added to plan");
               removeMeal(meal,mealKey);
               //TODO: Don't quit the loop till the plan is complete?
            }
         } // END for(int day = 0;day < EzMenuProfileDay.SATURDAY...)

         logger.debug("Added menu for week " + (weekIx + 1) + " to plan");
         menuPlan.add(planWeek);
         planWeek = new ArrayList<EzMenuMeal>(7);

      } // END for(int wekIx = 0;weekIx < numWeeks...)
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
                  int index = mealKey.indexOf('.');
                  String weekCategory  = mealKey.substring(0, index);
                  String weekPrepTime = mealKey.substring(index+1);
                  mealsList = mealMapper.selectByCategoryPrep(weekCategory, weekPrepTime);
                  logger.debug("Retrieved " + mealsList.size() + " meals from database for category " +
                               weekCategory + " and prep time " + weekPrepTime);
                  mealMap.put(mealKey, mealsList);
//               } else {
                  // TODO: May need to switch/change prepTime as well so meals can be found?
//                  mealsList = mealMapper.selectByCategoryPrep(switchCategory, weekPrepTime);
//               }
            } catch (RunDMLException rdex) {
               rdex.printStackTrace();
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
   
   private void removeMeal(EzMenuMeal meal,String mealKey) {
      
   }
}
