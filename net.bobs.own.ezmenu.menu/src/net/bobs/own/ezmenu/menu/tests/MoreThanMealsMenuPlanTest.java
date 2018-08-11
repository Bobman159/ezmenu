package net.bobs.own.ezmenu.menu.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

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
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;

class MoreThanMealsMenuPlanTest {

   private static H2Database ezMenuDbTest = null;
   private static EzMenuProfile profile = null;
   private static EzMenuProfileMapper profMapper = null;
   private MealDataGenerator mealGen = new MealDataGenerator(ezMenuDbTest);
   private static int[][] profCategories = {{1,1,1,1,1,1,1}};
   private static int[][] profPrepTimes = {{0,0,1,0,0},
                                           {0,0,1,0,0},
                                           {0,0,1,0,0},
                                           {0,0,1,0,0},
                                           {0,0,1,0,0},
                                           {0,0,1,0,0},
                                           {0,0,1,0,0},
                                          };
   
   private static Object[][] mealGenerate1Week = {{2,"Beef","31-45"},
                                                  {2,"Chicken","31-45"},
                                                  {2,"Fish","31-45"},
                                                  {3,"Pasta","31-45"},
                                                  {2,"Pork","31-45"},
                                                  {4,"Turkey","31-45"},
                                                  {2,"Veggie","31-45"}
                                                 };
   
   private static Object[][] mealGenerate2Weeks = {{4,"Beef","31-45"},
                                                  {3,"Chicken","31-45"},
                                                  {3,"Fish","31-45"},
                                                  {3,"Pasta","31-45"},
                                                  {6,"Pork","31-45"},
                                                  {3,"Turkey","31-45"},
                                                  {5,"Veggie","31-45"}
                                                 };
   
   private static Object[][] mealGenerate3Weeks = {{4,"Beef","31-45"},
                                                   {4,"Chicken","31-45"},
                                                   {4,"Fish","31-45"},
                                                   {4,"Pasta","31-45"},
                                                   {4,"Pork","31-45"},
                                                   {4,"Turkey","31-45"},
                                                   {4,"Veggie","31-45"}
                                                  };
   
    private static Object[][] mealGenerate4Weeks = {{5,"Beef","31-45"},
                                                    {5,"Chicken","31-45"},
                                                    {5,"Fish","31-45"},
                                                    {5,"Pasta","31-45"},
                                                    {5,"Pork","31-45"},
                                                    {6,"Turkey","31-45"},
                                                    {5,"Veggie","31-45"}
                                                   };
    
   @BeforeAll
   static void setUpBeforeClass() throws Exception {
      IH2ConnectionPool pool = H2ConnectionPoolFactory.getInstance().makePool(H2ConnectionPoolFactory.PoolTypes.MYOWN, 
            "C:\\Users\\Robert Anderson\\git\\ezmenu\\net.bobs.own.ezmenu\\db\\ezmenu_test",
            "EzMenuUser", "Aqpk3728", "10", "ezmenuTest.pool");      
      ezMenuDbTest = new H2Database(pool);
      ProfileDataGenerator profGen = new ProfileDataGenerator(ezMenuDbTest);
      profGen.deleteProfiles();
      profGen.generateProfiles(profCategories,profPrepTimes);
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
      
      EzMenuMeal meal = plan.getMeal(0, EzMenuProfileDay.SUNDAY);
      assertEquals(plan.numberWeeks(),1);
      assertEquals(meal.getMealCatgy(),"Beef");
      assertEquals(meal.getMealPrepTime(),"31-45");
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.SUNDAY));
     
      meal = plan.getMeal(0, EzMenuProfileDay.MONDAY);
      assertEquals(meal.getMealCatgy(),"Chicken");
      assertEquals(meal.getMealPrepTime(),"31-45");
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.MONDAY));
      
      meal = plan.getMeal(0, EzMenuProfileDay.TUESDAY);
      assertEquals(meal.getMealCatgy(),"Fish");
      assertEquals(meal.getMealPrepTime(),"31-45");
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.TUESDAY));
      
      meal = plan.getMeal(0, EzMenuProfileDay.WEDNESDAY);
      assertEquals(meal.getMealCatgy(),"Pasta");
      assertEquals(meal.getMealPrepTime(),"31-45");
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.WEDNESDAY));

      meal = plan.getMeal(0, EzMenuProfileDay.THURSDAY);
      assertEquals(meal.getMealCatgy(),"Pork");
      assertEquals(meal.getMealPrepTime(),"31-45");
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.THURSDAY));
      
      meal = plan.getMeal(0, EzMenuProfileDay.FRIDAY);
      assertEquals(meal.getMealCatgy(),"Turkey");
      assertEquals(meal.getMealPrepTime(),"31-45");
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.FRIDAY));
      
      meal = plan.getMeal(0, EzMenuProfileDay.SATURDAY);
      assertEquals(meal.getMealCatgy(),"Veggie");
      assertEquals(meal.getMealPrepTime(),"31-45");
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.SATURDAY));

   }
   
   @Test
   void testGenerate2Weeks() {
      
      mealGen.deleteMeals();     
      mealGen.generateMeals(mealGenerate2Weeks);
      MenuPlan plan = new MenuPlan(profile,2);
      plan.generate();
      assertEquals(plan.isOk(),true);
      
      assertEquals(plan.numberWeeks(),2);
      for (int weekIx = 0;weekIx < plan.numberWeeks();weekIx++) {
         EzMenuMeal meal = plan.getMeal(weekIx, EzMenuProfileDay.SUNDAY);
         assertEquals(meal.getMealCatgy(),"Beef");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.SUNDAY));
         
         meal = plan.getMeal(weekIx, EzMenuProfileDay.MONDAY);
         assertEquals(meal.getMealCatgy(),"Chicken");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.MONDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.TUESDAY);
         assertEquals(meal.getMealCatgy(),"Fish");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.TUESDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.WEDNESDAY);
         assertEquals(meal.getMealCatgy(),"Pasta");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.WEDNESDAY));
   
         meal = plan.getMeal(weekIx, EzMenuProfileDay.THURSDAY);
         assertEquals(meal.getMealCatgy(),"Pork");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.THURSDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.FRIDAY);
         assertEquals(meal.getMealCatgy(),"Turkey");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.FRIDAY));
          
         meal = plan.getMeal(weekIx, EzMenuProfileDay.SATURDAY);
         assertEquals(meal.getMealCatgy(),"Veggie");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.SATURDAY));

      }
   }
   
   @Test
   void testGenerate3Weeks() {
      
      mealGen.deleteMeals();     
      mealGen.generateMeals(mealGenerate3Weeks);
      MenuPlan plan = new MenuPlan(profile,3);
      plan.generate();
      assertEquals(plan.isOk(),true);
      
      assertEquals(plan.numberWeeks(),3);
      for (int weekIx = 0;weekIx < plan.numberWeeks();weekIx++) {   
         EzMenuMeal meal = plan.getMeal(weekIx, EzMenuProfileDay.SUNDAY);
         assertEquals(meal.getMealCatgy(),"Beef");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.SUNDAY));
         
         meal = plan.getMeal(weekIx, EzMenuProfileDay.MONDAY);
         assertEquals(meal.getMealCatgy(),"Chicken");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.MONDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.TUESDAY);
         assertEquals(meal.getMealCatgy(),"Fish");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.TUESDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.WEDNESDAY);
         assertEquals(meal.getMealCatgy(),"Pasta");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.WEDNESDAY));
   
         meal = plan.getMeal(weekIx, EzMenuProfileDay.THURSDAY);
         assertEquals(meal.getMealCatgy(),"Pork");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.THURSDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.FRIDAY);
         assertEquals(meal.getMealCatgy(),"Turkey");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.FRIDAY));
          
         meal = plan.getMeal(weekIx, EzMenuProfileDay.SATURDAY);
         assertEquals(meal.getMealCatgy(),"Veggie");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.SATURDAY));

      }
   }
   
   @Test
   void testGenerate4Weeks() {
      
      mealGen.deleteMeals();     
      mealGen.generateMeals(mealGenerate4Weeks);
      MenuPlan plan = new MenuPlan(profile,4);
      plan.generate();
      assertEquals(plan.isOk(),true);
      
      assertEquals(plan.numberWeeks(),4);
      for (int weekIx = 0;weekIx < plan.numberWeeks();weekIx++) {   
         EzMenuMeal meal = plan.getMeal(weekIx, EzMenuProfileDay.SUNDAY);
         assertEquals(meal.getMealCatgy(),"Beef");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.SUNDAY));
         
         meal = plan.getMeal(weekIx, EzMenuProfileDay.MONDAY);
         assertEquals(meal.getMealCatgy(),"Chicken");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.MONDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.TUESDAY);
         assertEquals(meal.getMealCatgy(),"Fish");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.TUESDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.WEDNESDAY);
         assertEquals(meal.getMealCatgy(),"Pasta");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.WEDNESDAY));
   
         meal = plan.getMeal(weekIx, EzMenuProfileDay.THURSDAY);
         assertEquals(meal.getMealCatgy(),"Pork");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.THURSDAY));
            
         meal = plan.getMeal(weekIx, EzMenuProfileDay.FRIDAY);
         assertEquals(meal.getMealCatgy(),"Turkey");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.FRIDAY));
          
         meal = plan.getMeal(weekIx, EzMenuProfileDay.SATURDAY);
         assertEquals(meal.getMealCatgy(),"Veggie");
         assertEquals(meal.getMealPrepTime(),"31-45");
         assertEquals(false,hasDuplicate(plan,meal,weekIx,EzMenuProfileDay.SATURDAY));

      }
   }
   
   private boolean hasDuplicate(MenuPlan plan, EzMenuMeal meal,int planWeek,int planDay) {
      
      boolean hasDuplicate = false;
      
      for (int week = 0;week < plan.numberWeeks();week++) {
         for(int day = 0;day < EzMenuProfileDay.SATURDAY; day++) {
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
