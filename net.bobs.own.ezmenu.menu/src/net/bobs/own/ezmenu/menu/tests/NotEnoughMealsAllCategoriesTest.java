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
import net.bobs.own.ezmenu.menu.model.MenuPlan;
import net.bobs.own.ezmenu.menu.model.MenuPlanMissingMeal;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.MealCategory;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.PrepTimes;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;

class NotEnoughMealsAllCategoriesTest extends AbstractMealPlanTest {

   private static H2Database ezMenuDbTest = null;
   private static ProfileDataGenerator profGen = null;
   private static EzMenuProfile profile = null;
   private static EzMenuProfileMapper profMapper = null;
   private MealDataGenerator mealGen = new MealDataGenerator(ezMenuDbTest);
   
   private static Object[][] noMealsProfile = {
         {1,1,MealCategory.BEEF,PrepTimes.TO15},
         {1,1,MealCategory.CHICKEN,PrepTimes.TO15},
         {1,1,MealCategory.FISH,PrepTimes.TO15},
         {1,1,MealCategory.PASTA,PrepTimes.TO15},
         {1,1,MealCategory.PORK,PrepTimes.TO15},
         {1,1,MealCategory.TURKEY,PrepTimes.TO15},
         {1,1,MealCategory.VEGGIE,PrepTimes.TO15},
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
         {1,1,MealCategory.FISH,PrepTimes.TO45},
         {1,1,MealCategory.FISH,PrepTimes.TO60},
         {1,1,MealCategory.PASTA,PrepTimes.TO30}, 
         {1,1,MealCategory.PASTA,PrepTimes.TO60},          
         {1,1,MealCategory.VEGGIE,PrepTimes.TO15}, 
         {1,1,MealCategory.VEGGIE,PrepTimes.TO30}, 
         {1,1,MealCategory.VEGGIE,PrepTimes.TO45} 
   };
   
   private static Object[][] incompletePlan2WeeksProfile = {
         {1,1,MealCategory.CHICKEN,PrepTimes.TO45},
         {1,1,MealCategory.FISH,PrepTimes.TO60},
         {1,1,MealCategory.PASTA,PrepTimes.TO30}, 
         {1,1,MealCategory.PORK,PrepTimes.TO60},          
         {1,1,MealCategory.TURKEY,PrepTimes.TO15}, 
         {1,1,MealCategory.VEGGIE,PrepTimes.TO15}, 
         {1,1,MealCategory.VEGGIE,PrepTimes.TO45} 
   };

   private static Object[][] incompletePlan1WeekMeals = {{0,"Beef",prepTime},
                                                         {0,"Chicken",prepTime},
                                                         {1,"Fish","31-45"},
                                                         {0,"Pasta",prepTime},
                                                         {0,"Pork",prepTime},
                                                         {0,"Turkey",prepTime},
                                                         {2,"Veggie",prepTime}
                                                       };
   
   private static Object[][] incompletePlan2WeeksMeals = {{0,"Beef",PrepTimes.TO15.toString()},
                                                   {1,"Chicken",PrepTimes.TO45.toString()},
                                                   {1,"Fish",PrepTimes.TO60.toString()},
                                                   {1,"Pasta",PrepTimes.TO30.toString()},
                                                   {1,"Pork",PrepTimes.TO60.toString()},
                                                   {1,"Turkey",PrepTimes.TO15.toString()},
                                                   {2,"Veggie",PrepTimes.TO45.toString()}
                                                  };
   
   private static Object[][] incompletePlan3WeeksMeals = {
//         {3,MealCategory.BEEF.getCategory(),PrepTimes.TO15.getPrepTime()},
         {3,MealCategory.CHICKEN.toString(),PrepTimes.TO45.toString()},
         {3,MealCategory.FISH.toString(),PrepTimes.TO60.toString()},
         {3,MealCategory.PASTA.toString(),PrepTimes.TO30.toString()},
         {2,MealCategory.PORK.toString(),PrepTimes.TO60.toString()},
         {3,MealCategory.TURKEY.toString(),PrepTimes.TO15.toString()},
         {3,MealCategory.VEGGIE.toString(),PrepTimes.TO30.toString()},
         {3,MealCategory.VEGGIE.toString(),PrepTimes.TO45.toString()}
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
   @Test
   void testIncompletePlan1Week() {
      
      generateProfiles(incompletePlan1WeekProfile);
      mealGen.deleteMeals();     
      mealGen.generateMeals(incompletePlan1WeekMeals);
      MenuPlan plan = new MenuPlan(profile,1);
      plan.generate();
      assertEquals(true,plan.isIncomplete());
      assertEquals(1,plan.planSize());
      assertEquals(2,plan.sizePlanWeek(0));
      
      assertEquals(false,super.hasDuplicatesInPlan(plan));

   }
   
   @Test
   void testIncompletePlan2Weeks() {
      
      generateProfiles(incompletePlan2WeeksProfile);
      mealGen.deleteMeals();     
      mealGen.generateMeals(incompletePlan2WeeksMeals);
      MenuPlan plan = new MenuPlan(profile,2);
      plan.generate();
      assertEquals(false,super.hasDuplicatesInPlan(plan));
      assertEquals(plan.isIncomplete(),true);
      assertEquals(plan.planSize(),2);

      assertEquals(plan.sizePlanWeek(0),6);
      assertEquals(plan.sizePlanWeek(1),1);
      assertEquals(false,super.hasDuplicatesInPlan(plan));

      assertEquals(true,plan.hasMissingMeals());
      assertEquals(7,plan.sizeMissingMeals());

//    1,"Chicken",PrepTimes.TO45.toString()},
      MenuPlanMissingMeal missMeal = plan.getMissingMeal(0);
      assertEquals(missMeal.getCategory(),MealCategory.VEGGIE);
      assertEquals(missMeal.getPrepTime(),PrepTimes.TO15);
      assertEquals(missMeal.getWeek(),0);
      assertEquals(missMeal.getDay(),5);
      
//    1,"Chicken",PrepTimes.TO45.toString()},
      missMeal = plan.getMissingMeal(1);
      assertEquals(missMeal.getCategory(),MealCategory.CHICKEN);
      assertEquals(missMeal.getPrepTime(),PrepTimes.TO45);
      assertEquals(missMeal.getWeek(),1);
      assertEquals(missMeal.getDay(),0);
    
//    {1,"Fish",PrepTimes.TO60.toString()},      
      missMeal = plan.getMissingMeal(2);
      assertEquals(missMeal.getCategory(),MealCategory.FISH);
      assertEquals(missMeal.getPrepTime(),PrepTimes.TO60);
      assertEquals(missMeal.getWeek(),1);
      assertEquals(missMeal.getDay(),1);

//    {1,"Pasta",PrepTimes.TO30.toString()},
      missMeal = plan.getMissingMeal(3);
      assertEquals(missMeal.getCategory(),MealCategory.PASTA);
      assertEquals(missMeal.getPrepTime(),PrepTimes.TO30);
      assertEquals(missMeal.getWeek(),1);
      assertEquals(missMeal.getDay(),2);

//    {1,"Pork",PrepTimes.TO60.toString()},
      missMeal = plan.getMissingMeal(4);
      assertEquals(missMeal.getCategory(),MealCategory.PORK);
      assertEquals(missMeal.getPrepTime(),PrepTimes.TO60);
      assertEquals(missMeal.getWeek(),1);
      assertEquals(missMeal.getDay(),3);
    
//    {1,"Turkey",PrepTimes.TO15.toString()
      missMeal = plan.getMissingMeal(5);
      assertEquals(missMeal.getCategory(),MealCategory.TURKEY);
      assertEquals(missMeal.getPrepTime(),PrepTimes.TO15);
      assertEquals(missMeal.getWeek(),1);
      assertEquals(missMeal.getDay(),4);      

      missMeal = plan.getMissingMeal(6);
      assertEquals(missMeal.getCategory(),MealCategory.VEGGIE);
      assertEquals(missMeal.getPrepTime(),PrepTimes.TO15);
      assertEquals(missMeal.getWeek(),1);
      assertEquals(missMeal.getDay(),5);      
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
      assertEquals(false,super.hasDuplicatesInPlan(plan));
      assertEquals(plan.isIncomplete(),true);
      assertEquals(plan.planSize(),3);
      assertEquals(6,plan.sizePlanWeek(0));
      assertEquals(plan.sizePlanWeek(1),6);
      System.out.println("planWeek3.size= " + plan.sizePlanWeek(2));
      assertEquals(5,plan.sizePlanWeek(2));
      
      assertEquals(true,plan.hasMissingMeals());
      assertEquals(4,plan.sizeMissingMeals());
      
// {2,MealCategory.PORK.toString(),PrepTimes.TO60.toString()},      
//    MenuPlanMissingMeal missMeal = plan.getMissingMeal(0);
//    assertEquals(missMeal.getCategory(),MealCategory.PORK);
//    assertEquals(missMeal.getPrepTime(),PrepTimes.TO60);
//    assertEquals(missMeal.getWeek(),2);
//    assertEquals(missMeal.getDay(),3);

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
