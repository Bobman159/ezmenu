package net.bobs.own.ezmenu.menu.tests;

import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.MealCategory;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.PrepTimes;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.WeekDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;

class NotEnoughMealsAllCategoriesTest {

   private static H2Database ezMenuDbTest = null;
   private static ProfileDataGenerator profGen = null;
   private static EzMenuProfile profile = null;
   private static EzMenuProfileMapper profMapper = null;
   private MealDataGenerator mealGen = new MealDataGenerator(ezMenuDbTest);
   
   private static Object[][] noMealsProfile = {
         {1,1,MealCategory.Beef,PrepTimes.TO15},
         {1,1,MealCategory.Chicken,PrepTimes.TO15},
         {1,1,MealCategory.Fish,PrepTimes.TO15},
         {1,1,MealCategory.Pasta,PrepTimes.TO15},
         {1,1,MealCategory.Pork,PrepTimes.TO15},
         {1,1,MealCategory.Turkey,PrepTimes.TO15},
         {1,1,MealCategory.Veggie,PrepTimes.TO15},
   };
   
   private static String prepTime = "0-15";
   private static Object[][] noMealGenerate = {{0,"Beef",prepTime},
                                                  {0,"Chicken",prepTime},
                                                  {0,"Fish",prepTime},
                                                  {0,"Pasta",prepTime},
                                                  {0,"Pork",prepTime},
                                                  {0,"Turkey",prepTime},
                                                  {0,"Veggie",prepTime}
                                                 };
  
   private static Object[][] incompletePlan1WeekProfile = {
         {1,1,MealCategory.Fish,PrepTimes.TO45},
         {1,1,MealCategory.Fish,PrepTimes.TO60},
         {1,1,MealCategory.Pasta,PrepTimes.TO30}, 
         {1,1,MealCategory.Pasta,PrepTimes.TO60},          
         {1,1,MealCategory.Veggie,PrepTimes.TO15}, 
         {1,1,MealCategory.Veggie,PrepTimes.TO30}, 
         {1,1,MealCategory.Veggie,PrepTimes.TO45} 
   };
   
   private static Object[][] incompletePlan2WeeksProfile = {
         {1,1,MealCategory.Chicken,PrepTimes.TO45},
         {1,1,MealCategory.Fish,PrepTimes.TO60},
         {1,1,MealCategory.Pasta,PrepTimes.TO30}, 
         {1,1,MealCategory.Pork,PrepTimes.TO60},          
         {1,1,MealCategory.Turkey,PrepTimes.TO15}, 
         {1,1,MealCategory.Veggie,PrepTimes.TO30}, 
         {1,1,MealCategory.Veggie,PrepTimes.TO45} 
   };

   private static Object[][] incompletePlan1WeekMeals = {{0,"Beef",prepTime},
                                                         {0,"Chicken",prepTime},
                                                         {1,"Fish","31-45"},
                                                         {0,"Pasta",prepTime},
                                                         {0,"Pork",prepTime},
                                                         {0,"Turkey",prepTime},
                                                         {2,"Veggie",prepTime}
                                                       };
   
   private static Object[][] incompletePlan2WeeksMeals = {{0,"Beef",PrepTimes.TO15.getPrepTime()},
                                                   {1,"Chicken",PrepTimes.TO45.getPrepTime()},
                                                   {1,"Fish",PrepTimes.TO60.getPrepTime()},
                                                   {1,"Pasta",PrepTimes.TO30.getPrepTime()},
                                                   {1,"Pork",PrepTimes.TO60.getPrepTime()},
                                                   {1,"Turkey",PrepTimes.TO15.getPrepTime()},
                                                   {2,"Veggie",PrepTimes.TO45.getPrepTime()}
                                                  };
   
   private static Object[][] incompletePlan3WeeksMeals = {
         {3,"Beef",PrepTimes.TO15.getPrepTime()},
         {3,"Chicken",PrepTimes.TO45.getPrepTime()},
         {3,"Fish",PrepTimes.TO60.getPrepTime()},
         {3,"Pasta",PrepTimes.TO30.getPrepTime()},
         {2,"Pork",PrepTimes.TO60.getPrepTime()},
         {3,"Turkey",PrepTimes.TO15.getPrepTime()},
         {3,"Veggie",PrepTimes.TO45.getPrepTime()}
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
            "C:\\Users\\Robert Anderson\\git\\ezmenu\\net.bobs.own.ezmenu\\db\\ezmenu_test",
            "EzMenuUser", "Aqpk3728", "10", "ezmenuTest.pool");      
      ezMenuDbTest = new H2Database(pool);
      profGen = new ProfileDataGenerator(ezMenuDbTest);
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

   @Disabled
   void testNoMealsInDatabase() {
      
      generateProfiles(noMealsProfile);
      mealGen.deleteMeals();     
      mealGen.generateMeals(noMealGenerate);
      MenuPlan plan = new MenuPlan(profile,1);
      plan.generate();
      assertEquals(plan.isEmpty(),true);
      assertEquals(plan.planSize(),0);
      assertEquals(plan.sizePlanWeek(1),-1);

   }

   /*
    * Test an incomplete Meal Plan is generated for 1 week 
    * with only 1 week generated.
    */
   @Disabled
   void testIncompletePlan1Week() {
      
      generateProfiles(incompletePlan1WeekProfile);
      mealGen.deleteMeals();     
      mealGen.generateMeals(incompletePlan1WeekMeals);
      MenuPlan plan = new MenuPlan(profile,1);
      plan.generate();
      assertEquals(plan.isIncomplete(),true);
      assertEquals(plan.planSize(),1);
      assertEquals(plan.sizePlanWeek(1),1);

   }
   
   @Disabled
   void testIncompletePlan2Weeks() {
      
      generateProfiles(incompletePlan2WeeksProfile);
      mealGen.deleteMeals();     
      mealGen.generateMeals(incompletePlan2WeeksMeals);
      MenuPlan plan = new MenuPlan(profile,2);
      plan.generate();
      assertEquals(plan.isIncomplete(),true);
      assertEquals(plan.planSize(),1);
      assertEquals(plan.sizePlanWeek(1),7);

   }
   
   private boolean hasDuplicate(MenuPlan plan, EzMenuMeal meal,int planWeek,int planDay) {
      
      boolean hasDuplicate = false;
      
      for (int week = 0;week < plan.planSize();week++) {
         for(int day = 0;day < WeekDay.Saturday.getDay(); day++) {
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
   
   private void generateProfiles(Object[][] profileDefinition) {
 
      profGen.deleteProfiles();
      profGen.generateProfiles(profileDefinition);
      try {
         profMapper = EzMenuProfileMapper.makeMapper(ezMenuDbTest);
         List<ITable> profList = RunDMLRequestFactory.makeSelectRequest(profMapper);
         profile = (EzMenuProfile) profList.get(0);
      } catch (RunDMLException e) {
         e.printStackTrace();
      }
   }

   /*
    * Test an incomplete Meal Plan is generated with 7
    * meals in the plan.
    */
   @Test
   void testIncompletePlan3Weeks() {
      
      generateProfiles(incompletePlan2WeeksProfile);
      mealGen.deleteMeals();     
      mealGen.generateMeals(incompletePlan3WeeksMeals);
      MenuPlan plan = new MenuPlan(profile,3);
      plan.generate();
      assertEquals(plan.isIncomplete(),true);
      assertEquals(plan.planSize(),3);
      assertEquals(plan.sizePlanWeek(1),7);
      assertEquals(plan.sizePlanWeek(2),7);
      System.out.println("planWeek3.size= " + plan.sizePlanWeek(3));
      assertEquals(plan.sizePlanWeek(3),6);

   }
}
