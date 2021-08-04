package net.bobs.own.ezmenu.menu.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.bobs.own.db.h2.pool.H2ConnectionPoolFactory;
import net.bobs.own.db.h2.pool.H2Database;
import net.bobs.own.db.h2.pool.IH2ConnectionPool;
import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.dbload.tests.ui.MealDataGenerator;
import net.bobs.own.ezmenu.dbload.tests.ui.ProfileDataGenerator;
import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.menu.model.MenuPlan;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.MealCategory;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.PrepTimes;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.WeekDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;

class ExactMoreThanMealsMenuPlanTest {
   
   private static H2Database ezMenuDbTest = null;
   private static EzMenuProfile profile = null;
   private static EzMenuProfileMapper profMapper = null;
   private static Logger logger = LogManager.getLogger(ExactMoreThanMealsMenuPlanTest.class.getName());
   private MealDataGenerator mealGen = new MealDataGenerator(ezMenuDbTest);
   
   private static Object[][] profilePlus60 = {
         {1,1,MealCategory.BEEF,PrepTimes.PLUS60},
         {1,1,MealCategory.CHICKEN,PrepTimes.PLUS60},
         {1,1,MealCategory.FISH,PrepTimes.PLUS60},
         {1,1,MealCategory.PASTA,PrepTimes.PLUS60},
         {1,1,MealCategory.PORK,PrepTimes.PLUS60},
         {1,1,MealCategory.TURKEY,PrepTimes.PLUS60},
         {1,1,MealCategory.VEGGIE,PrepTimes.PLUS60},
   };
   
   private static Object[][] mealGenerate1Week = {{2,"Beef","61+"},
                                                  {2,"Chicken","61+"},
                                                  {1,"Fish","61+"},
                                                  {3,"Pasta","61+"},
                                                  {1,"Pork","61+"},
                                                  {4,"Turkey","61+"},
                                                  {1,"Veggie","61+"}
                                                 };
   
   private static Object[][] mealGenerate2Weeks = {{4,"Beef","61+"},
                                                  {2,"Chicken","61+"},
                                                  {3,"Fish","61+"},
                                                  {2,"Pasta","61+"},
                                                  {6,"Pork","61+"},
                                                  {2,"Turkey","61+"},
                                                  {5,"Veggie","61+"}
                                                 };
   
   private static Object[][] mealGenerate3Weeks = {{3,"Beef","61+"},
                                                   {3,"Chicken","61+"},
                                                   {3,"Fish","61+"},
                                                   {3,"Pasta","61+"},
                                                   {3,"Pork","61+"},
                                                   {3,"Turkey","61+"},
                                                   {4,"Veggie","61+"}
                                                  };
   
    private static Object[][] mealGenerate4Weeks = {{4,"Beef","61+"},
                                                    {5,"Chicken","61+"},
                                                    {5,"Fish","61+"},
                                                    {5,"Pasta","61+"},
                                                    {4,"Pork","61+"},
                                                    {5,"Turkey","61+"},
                                                    {4,"Veggie","61+"}
                                                   };

   @BeforeAll
   static void setUpBeforeClass() throws Exception {
      IH2ConnectionPool pool = H2ConnectionPoolFactory.getInstance().makePool(H2ConnectionPoolFactory.PoolTypes.MYOWN, 
            "C:\\Users\\Robert Anderson\\git\\ezmenu\\net.bobs.own.ezmenu\\db\\ezmenu_test",
            "EzMenuUser", "Aqpk3728", "10", "ezmenuTest.pool");      
      ezMenuDbTest = new H2Database(pool);
      ProfileDataGenerator profGen = new ProfileDataGenerator(ezMenuDbTest);
      profGen.deleteProfiles();
      profGen.generateProfiles(profilePlus60);
      try {
         profMapper = EzMenuProfileMapper.makeMapper(ezMenuDbTest);
         List<ITable> profList = RunDMLRequestFactory.makeSelectRequest(profMapper);
         profile = (EzMenuProfile) profList.get(0);
      } catch (RunDMLException e) {
         e.printStackTrace();
      }
   
   }

   @AfterAll
   static void tearDownAfterClass() throws Exception {
   }

   @BeforeEach
   void setUp() throws Exception {
   }

   @AfterEach
   void tearDown() throws Exception {
   }

   @Test
   void testGenerate1Week() {

      mealGen.deleteMeals();     
      mealGen.generateMeals(mealGenerate1Week);
      MenuPlan plan = new MenuPlan(profile,1);
      plan.generate();
      assertEquals(plan.isOk(),true);
      
      EzMenuMeal meal = plan.getMeal(0, WeekDay.SUNDAY.getDay());
      assertEquals(plan.planSize(),1);
      assertEquals(meal.getMealCatgy(),"Beef");
      assertEquals(meal.getMealPrepTime(),"61+");
      assertEquals(false,hasDuplicate(plan,meal,0,WeekDay.SUNDAY.getDay()));
     
      meal = plan.getMeal(0, WeekDay.MONDAY.getDay());
      assertEquals(meal.getMealCatgy(),"Chicken");
      assertEquals(meal.getMealPrepTime(),"61+");
      assertEquals(false,hasDuplicate(plan,meal,0,WeekDay.MONDAY.getDay()));
      
      meal = plan.getMeal(0, WeekDay.TUESDAY.getDay());
      assertEquals(meal.getMealCatgy(),"Fish");
      assertEquals(meal.getMealPrepTime(),"61+");
      assertEquals(false,hasDuplicate(plan,meal,0,WeekDay.TUESDAY.getDay()));
      
      meal = plan.getMeal(0, WeekDay.WEDNESDAY.getDay());
      assertEquals(meal.getMealCatgy(),"Pasta");
      assertEquals(meal.getMealPrepTime(),"61+");
      assertEquals(false,hasDuplicate(plan,meal,0,WeekDay.WEDNESDAY.getDay()));

      meal = plan.getMeal(0, WeekDay.THURSDAY.getDay());
      assertEquals(meal.getMealCatgy(),"Pork");
      assertEquals(meal.getMealPrepTime(),"61+");
      assertEquals(false,hasDuplicate(plan,meal,0,WeekDay.THURSDAY.getDay()));
      
      meal = plan.getMeal(0, WeekDay.FRIDAY.getDay());
      assertEquals(meal.getMealCatgy(),"Turkey");
      assertEquals(meal.getMealPrepTime(),"61+");
      assertEquals(false,hasDuplicate(plan,meal,0,WeekDay.FRIDAY.getDay()));
      
      meal = plan.getMeal(0, WeekDay.SATURDAY.getDay());
      assertEquals(meal.getMealCatgy(),"Veggie");
      assertEquals(meal.getMealPrepTime(),"61+");
      assertEquals(false,hasDuplicate(plan,meal,0,WeekDay.SATURDAY.getDay()));
   }
   
   @Test
   void testGenerate2Weeks() {
      
      mealGen.deleteMeals();     
      mealGen.generateMeals(mealGenerate2Weeks);
      MenuPlan plan = new MenuPlan(profile,2);
      plan.generate();
      assertEquals(plan.isOk(),true);
      
      assertEquals(plan.planSize(),2);
      for (int weekIx = 0;weekIx < plan.planSize();weekIx++) {
         EzMenuMeal meal = plan.getMeal(weekIx, WeekDay.SUNDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Beef");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.SUNDAY.getDay()));
         
         meal = plan.getMeal(weekIx, WeekDay.MONDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Chicken");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.MONDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.TUESDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Fish");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.TUESDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.WEDNESDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Pasta");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.WEDNESDAY.getDay()));
   
         meal = plan.getMeal(weekIx, WeekDay.THURSDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Pork");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.THURSDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.FRIDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Turkey");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.FRIDAY.getDay()));
          
         meal = plan.getMeal(weekIx, WeekDay.SATURDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Veggie");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.SATURDAY.getDay()));

      }
   }
   
   @Test
   void testGenerate3Weeks() {
      
      //Only Veggie category has more than enough meals
      mealGen.deleteMeals();     
      mealGen.generateMeals(mealGenerate3Weeks);
      MenuPlan plan = new MenuPlan(profile,3);
      plan.generate();
      assertEquals(plan.isOk(),true);
      
      assertEquals(plan.planSize(),3);
      for (int weekIx = 0;weekIx < plan.planSize();weekIx++) {   
         EzMenuMeal meal = plan.getMeal(weekIx, WeekDay.SUNDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Beef");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.SUNDAY.getDay()));
         
         meal = plan.getMeal(weekIx, WeekDay.MONDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Chicken");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.MONDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.TUESDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Fish");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.TUESDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.WEDNESDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Pasta");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.WEDNESDAY.getDay()));
   
         meal = plan.getMeal(weekIx, WeekDay.THURSDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Pork");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.THURSDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.FRIDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Turkey");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.FRIDAY.getDay()));
          
         meal = plan.getMeal(weekIx, WeekDay.SATURDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Veggie");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.SATURDAY.getDay()));

      }
   }
   
   @Test
   void testGenerate4Weeks() {
      
      mealGen.deleteMeals();     
      mealGen.generateMeals(mealGenerate4Weeks);
      MenuPlan plan = new MenuPlan(profile,4);
      plan.generate();
      assertEquals(plan.isOk(),true);
      
      assertEquals(plan.planSize(),4);
      for (int weekIx = 0;weekIx < plan.planSize();weekIx++) {   
         EzMenuMeal meal = plan.getMeal(weekIx, WeekDay.SUNDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Beef");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.SUNDAY.getDay()));
         
         meal = plan.getMeal(weekIx, WeekDay.MONDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Chicken");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.MONDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.TUESDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Fish");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.TUESDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.WEDNESDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Pasta");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.WEDNESDAY.getDay()));
   
         meal = plan.getMeal(weekIx, WeekDay.THURSDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Pork");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.THURSDAY.getDay()));
            
         meal = plan.getMeal(weekIx, WeekDay.FRIDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Turkey");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.FRIDAY.getDay()));
          
         meal = plan.getMeal(weekIx, WeekDay.SATURDAY.getDay());
         assertEquals(meal.getMealCatgy(),"Veggie");
         assertEquals(meal.getMealPrepTime(),"61+");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,WeekDay.SATURDAY.getDay()));

      }
   }

   private boolean hasDuplicate(MenuPlan plan, EzMenuMeal meal,int planWeek,int planDay) {
      
      boolean hasDuplicate = false;
      
      for (int week = 0;week < plan.planSize();week++) {
         for(int day = 0;day < WeekDay.SATURDAY.getDay(); day++) {
            EzMenuMeal planMeal = plan.getMeal(week, day);
            if (planMeal.equals(meal)) {
               if (week == planWeek && day == planDay) {
                  hasDuplicate = false;
                  continue;
               } else {
                  hasDuplicate = true;
                  break;
               }
            }
         }
      }
      
      return hasDuplicate;
   }

}
