package net.bobs.own.ezmenu.menu.tests;

import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.*;

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
   
   private static Object[][] noDaysProfile = {
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
  
   private static Object[][] insufficientDaysProfile = {
         {1,1,MealCategory.Fish,PrepTimes.TO45},
         {1,1,MealCategory.Fish,PrepTimes.TO60},
         {1,1,MealCategory.Pasta,PrepTimes.TO30}, 
         {1,1,MealCategory.Pasta,PrepTimes.TO60},          
         {1,1,MealCategory.Veggie,PrepTimes.TO15}, 
         {1,1,MealCategory.Veggie,PrepTimes.TO30}, 
         {1,1,MealCategory.Veggie,PrepTimes.TO45} 
   };

   private static Object[][] insufficientMealGenerate = {{0,"Beef",prepTime},
                                                         {0,"Chicken",prepTime},
                                                         {1,"Fish","31-45"},
                                                         {0,"Pasta",prepTime},
                                                         {0,"Pork",prepTime},
                                                         {0,"Turkey",prepTime},
                                                         {2,"Veggie",prepTime}
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

   @Test
   void testNoMealsInDatabase() {
      
      generateProfiles(noDaysProfile);
      mealGen.deleteMeals();     
      mealGen.generateMeals(noMealGenerate);
      MenuPlan plan = new MenuPlan(profile,1);
      plan.generate();
      assertEquals(plan.isEmpty(),true);

   }

   @Test
   void testInsufficientMealsInDatabase() {
      
      generateProfiles(insufficientDaysProfile);
      mealGen.deleteMeals();     
      mealGen.generateMeals(insufficientMealGenerate);
      MenuPlan plan = new MenuPlan(profile,1);
      plan.generate();

   }
   
   private boolean hasDuplicate(MenuPlan plan, EzMenuMeal meal,int planWeek,int planDay) {
      
      boolean hasDuplicate = false;
      
      for (int week = 0;week < plan.numberWeeks();week++) {
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


}
