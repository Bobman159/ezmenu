package net.bobs.own.ezmenu.menu.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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

class NotEnoughMealsAllCategoriesTest {

   private static H2Database ezMenuDbTest = null;
   private static EzMenuProfile profile = null;
   private static EzMenuProfileMapper profMapper = null;
   private MealDataGenerator mealGen = new MealDataGenerator(ezMenuDbTest);
   private static int[][] profCategories = {{1,1,1,1,1,1,1}};
   private static int[][] profPrepTimes = {{1,0,0,0,0},
                                           {1,0,0,0,0},
                                           {1,0,0,0,0},
                                           {1,0,0,0,0},
                                           {1,0,0,0,0},
                                           {1,0,0,0,0},
                                           {1,0,0,0,0},
                                          };
   
   private static String prepTime = "0-15";
   private static Object[][] mealGenerate1Week = {{0,"Beef",prepTime},
                                                  {0,"Chicken",prepTime},
                                                  {0,"Fish",prepTime},
                                                  {0,"Pasta",prepTime},
                                                  {0,"Pork",prepTime},
                                                  {0,"Turkey",prepTime},
                                                  {0,"Veggie",prepTime}
                                                 };
   
   private static Object[][] mealGenerate2Weeks = {{4,"Beef",prepTime},
                                                  {3,"Chicken",prepTime},
                                                  {3,"Fish",prepTime},
                                                  {3,"Pasta",prepTime},
                                                  {6,"Pork",prepTime},
                                                  {3,"Turkey",prepTime},
                                                  {5,"Veggie",prepTime}
                                                 };
   
   private static Object[][] mealGenerate3Weeks = {{4,"Beef",prepTime},
                                                   {4,"Chicken",prepTime},
                                                   {4,"Fish",prepTime},
                                                   {4,"Pasta",prepTime},
                                                   {4,"Pork",prepTime},
                                                   {4,"Turkey",prepTime},
                                                   {4,"Veggie",prepTime}
                                                  };
   
    private static Object[][] mealGenerate4Weeks = {{5,"Beef",prepTime},
                                                    {5,"Chicken",prepTime},
                                                    {5,"Fish",prepTime},
                                                    {5,"Pasta",prepTime},
                                                    {5,"Pork",prepTime},
                                                    {6,"Turkey",prepTime},
                                                    {5,"Veggie",prepTime}
                                                   };
   @BeforeAll
   static void setUpBeforeClass() throws Exception {
      
      IH2ConnectionPool pool = H2ConnectionPoolFactory.getInstance().makePool(H2ConnectionPoolFactory.PoolTypes.MYOWN, 
            "D:\\\\Java\\\\EzMenu_Workspace\\\\net.bobs.own.ezmenu\\\\db\\\\ezmenu_test", 
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
      
      EzMenuMeal meal = plan.getMeal(0, EzMenuProfileDay.SUNDAY);
      assertEquals(plan.numberWeeks(),1);
      assertEquals(meal.getMealCatgy(),"Beef");
      assertEquals(meal.getMealPrepTime(),prepTime);
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.SUNDAY));
     
      meal = plan.getMeal(0, EzMenuProfileDay.MONDAY);
      assertEquals(meal.getMealCatgy(),"Chicken");
      assertEquals(meal.getMealPrepTime(),prepTime);
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.MONDAY));
      
      meal = plan.getMeal(0, EzMenuProfileDay.TUESDAY);
      assertEquals(meal.getMealCatgy(),"Fish");
      assertEquals(meal.getMealPrepTime(),prepTime);
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.TUESDAY));
      
      meal = plan.getMeal(0, EzMenuProfileDay.WEDNESDAY);
      assertEquals(meal.getMealCatgy(),"Pasta");
      assertEquals(meal.getMealPrepTime(),prepTime);
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.WEDNESDAY));

      meal = plan.getMeal(0, EzMenuProfileDay.THURSDAY);
      assertEquals(meal.getMealCatgy(),"Pork");
      assertEquals(meal.getMealPrepTime(),prepTime);
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.THURSDAY));
      
      meal = plan.getMeal(0, EzMenuProfileDay.FRIDAY);
      assertEquals(meal.getMealCatgy(),"Turkey");
      assertEquals(meal.getMealPrepTime(),prepTime);
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.FRIDAY));
      
      meal = plan.getMeal(0, EzMenuProfileDay.SATURDAY);
      assertEquals(meal.getMealCatgy(),"Veggie");
      assertEquals(meal.getMealPrepTime(),prepTime);
      assertEquals(false,hasDuplicate(plan,meal,0,EzMenuProfileDay.SATURDAY));

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
