package net.bobs.own.ezmenu.menu.model;

import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.MealCategory;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.PrepTimes;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.WeekDay;

/**
 * Identifies information for a meal that is missing 
 * (could not be added) to a Menu Plan.
 * 
 *
 */
public class MenuPlanMissingMeal {
   
   private int week;
   private int day;
//   private MealCategory mealCategory;
//   private PrepTimes    prepTime;
//   WeekDay      weekDay;
   private EzMenuProfileDay   profDay;
   
   /**
    * Create an entry for a meal that could not be added to the menu plan.
    * 
    * @param week - the week in the plan for the meal that could not be added (zero based)
    * @param day - the day in the week for the meal that could not be added (zero based)
    * @param profDay - <code>EzMenuProfileDay</code> information 
    */
   public MenuPlanMissingMeal(int week, int day, EzMenuProfileDay profDay) {
      
      this.week = week;
      this.day = day;
      this.profDay = profDay;
      
   }
   
   /**
    * The week of the meal plan.
    * @return - the week in the meal plan (zero based)
    */
   public int getWeek() {
      return this.week;
   }
   
   /**
    * The day of the week.
    * @return - the day of the week in the meal plan (zero based)
    */
   public int getDay() {
      return this.day;
   }
   
   /**
    * The profile category for the meal that could not be added.
    * @return - the profile category.
    */
   public MealCategory getCategory() {
      return this.profDay.getCategory();
   }
   
   /**
    * The profile preparation time for the meal that could not be added.
    * @return - the profile preparation time.
    */
   public PrepTimes getPrepTime() {
      return this.profDay.getprepTime();
   }
   
   /**
    * The profile week day for the meal that could not be added.
    * @return - the profile week day.
    */
   public WeekDay getWeekDay() {
      return this.profDay.getDay();
   }
   
}
