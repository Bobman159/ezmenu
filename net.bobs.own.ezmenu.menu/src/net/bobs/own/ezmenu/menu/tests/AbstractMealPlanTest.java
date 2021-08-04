package net.bobs.own.ezmenu.menu.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.menu.model.MenuPlan;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.WeekDay;

public abstract class AbstractMealPlanTest {

   private Logger logger = LogManager.getLogger(AbstractMealPlanTest.class.getName());
   private int planWeek = 0;
   private int planDay = 0;
   
   /**
    * Inner class for information on a duplicate <code>EzMenuMeal</code>
    * that was found in the Menu Plan.
    * 
    */
   private class DuplicateMeal {
      private int week;
      private int day;
      private EzMenuMeal dupMeal;

      /**
       * Construct an object identifying a duplicate meal found in the plan.
       * @param week - the week where the duplicate meal was found.
       * @param day - the day where the duplicate meal was found.
       * @param meal - the meal information for the duplicate meal.
       */
      public DuplicateMeal(int week,int day,EzMenuMeal meal) {
         this.week = week;
         this.day = day;
         this.dupMeal = meal;
      }
      
      /**
       * Returns the week in the plan where the duplicate meal was found.
       * @return - the week number
       */
      public int getWeek() {
         return this.week;
      }
      
      /**
       * Returns the day in the week where the duplicate meal was found.
       * @return - the day of the week (0=SUNDAY,1=MONDAY,...6=SATURDAY)
       */
      public int getDay() {
         return this.day;
      }
      
      /**
       * Return the duplicate meal entry from the menu plan
       * @return - the duplicate meal
       */
      public EzMenuMeal getDuplicateMeal() {
         return this.dupMeal;
      }
      
   }
   /**
    * Checks the menu plan meals to see if there are 
    * @param plan
    * @return
    */
   public boolean hasDuplicatesInPlan(MenuPlan plan) {
      
      boolean hasDuplicate = false;
      
      /* Outer loop goes through the number of week in the plan */      
      for (int week = 0;week < plan.planSize();week++) {
         for(int day = 0;day < plan.sizePlanWeek(week); day++) {
            EzMenuMeal planMeal = plan.getMeal(week, day);
            planWeek = week;
            planDay = day;
            DuplicateMeal dupMeal = hasDuplicateMeal(plan,planMeal);
            if (dupMeal != null) {
               logger.error("Meal plan for week " + week + " on day " + day + "has a duplicate meal");
               logger.error("     At week " + dupMeal.getWeek() + " and day " + dupMeal.getDay() );
               logger.error("mealID: " + dupMeal.getDuplicateMeal().getMealId() +
                            "mealName: " + dupMeal.getDuplicateMeal().getMealName());
            }
         }
      }
      
      return hasDuplicate;
   }

   /*
    * Search the MenuPlan to see if any of the entries contain an EzMenuMeal
    * entry that matches the search meal.
    * 
    * @param meal
    * @return
    */
   //TODO: How do I avoid flagging the same meal (day & week) as a duplicate?
   private DuplicateMeal hasDuplicateMeal(MenuPlan plan,EzMenuMeal searchMeal) {
      DuplicateMeal dupMeal = null;
//      int searchWeek = planWeek;
//      int searchDay = planDay;
      int searchWeek = 0;
      int searchDay = 0;
      
      for (searchWeek = 0;searchWeek < plan.planSize();searchWeek++) {
         for(searchDay = 0;searchDay < plan.sizePlanWeek(searchWeek); searchDay++) {
            EzMenuMeal meal = plan.getMeal(searchWeek, searchDay);
            if (searchMeal.equals(meal)) {
               if (searchWeek == planWeek && searchDay == planDay) {
                  dupMeal = null;
                  continue;
               } else {
                  dupMeal = new DuplicateMeal(searchWeek,searchDay,meal);
                  break;
               }
            }
         }
      }
      
      return dupMeal;
   }
   
   
}
