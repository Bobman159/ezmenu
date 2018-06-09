package net.bobs.own.ezmenu.menu.tests;

import net.bobs.own.db.h2.pool.H2ConnectionPoolFactory;
import net.bobs.own.db.h2.pool.H2Database;
import net.bobs.own.db.h2.pool.IH2ConnectionPool;
import net.bobs.own.ezmenu.dbload.tests.ui.MealDataGenerator;
import net.bobs.own.ezmenu.dbload.tests.ui.ProfileDataGenerator;

public class ProfileDataGenTest {

   private static H2Database ezMenuDbTest = null;
   
   /*
    * 1st Profile 
    *    Day         Category       Prep Time
    *    Sunday      Beef           0-15
    *    Monday      Beef           46-60
    *    Tuesday     Fish           31-45
    *    Wednesday   Pasta          16-30
    *    Thursday    Pasta          16-30
    *    Friday      Pasta          61+
    *    Saturday    Turkey         31-45
    */

   /*
    * 2nd Profile 
    *    Day         Category       Prep Time
    *    Sunday      Chicken        0-15
    *    Monday      Chicken        46-60
    *    Tuesday     Pork           31-45
    *    Wednesday   Turkey         0-15 
    *    Thursday    Veggie         0-15 
    *    Friday      Veggie         31-45
    *    Saturday    Veggie         61+  
    */
   
   /*
    * 3rd Profile 
    *    Day         Category       Prep Time
    *    Sunday      Beef           0-15
    *    Monday      Beef           16-30
    *    Tuesday     Beef           16-30d
    *    Wednesday   Beef           31-45
    *    Thursday    Beef           46-60
    *    Friday      Beef           46-60
    *    Saturday    Beef           61+  
    */
   private static int[][] profCategories = {{2,0,1,3,0,1,0},
                                            {0,2,0,0,1,1,3},
                                            {7,0,0,0,0,0,0}                      
                                           };
   
   private static int[][] profPrepTimes = {{1,0,0,1,0},   //Beef Prep Times (1st categoriesMatrix)
                                           {0,0,1,0,0},   //Fish Prep Times (1st categoriesMatrix) 
                                           {0,2,0,0,1},   //Pasta Prep Times (1st categoriesMatrix)
                                           {0,0,1,0,0},   //Tukey Prep Times (1st categoriesMatrix)           
                                           {1,0,0,1,0},   //Chicken Prep Times (2nd categoriesMatrix)
                                           {0,0,1,0,0},   //Pork Prep Times (2nd categoriesMatrix)
                                           {1,0,0,0,0},   //Tukey Prep Times (2nd categoriesMatrix)
                                           {1,0,1,0,1},   //Veggie Prep Times (2nd categoriesMatrix)
                                           {1,2,1,2,1}    //Beef Prep Times (3rd categoriesMatrix)
                                           };   
   
//   private static Object[][] mealGenerate = {{2,"Beef","0-15"},
//                                             {1,"Chicken","16-30"},
//                                             {1,"Pasta","31-45"}};
   private static Object[][] mealGenerate2Weeks = {{2,"Beef","0-15"},
         {2,"Chicken","16-30"},
         {2,"Fish","31-45"},
         {2,"Pasta","46-60"},
         {2,"Pork","61+"},
         {2,"Turkey","16-30"},
         {2,"Veggie","31-45"}
        };

                                            
   public static void main(String[] args) {
      IH2ConnectionPool pool = H2ConnectionPoolFactory.getInstance().makePool(H2ConnectionPoolFactory.PoolTypes.MYOWN, 
            "C:\\Users\\Robert Anderson\\git\\ezmenu\\net.bobs.own.ezmenu\\db\\ezmenu_test",
            "EzMenuUser", "Aqpk3728", "10", "ezmenuTest.pool");      
      ezMenuDbTest = new H2Database(pool);
      
      //Profiles Tests
//      ProfileDataGenerator profGen = new ProfileDataGenerator(ezMenuDbTest);
//      profGen.deleteProfiles();
//      profGen.generateProfiles(profCategories,profPrepTimes);
//      profGen.generateProfiles(1000);

      //Meals Tests
      MealDataGenerator mealGen = new MealDataGenerator(ezMenuDbTest);
      mealGen.deleteMeals();
//      mealGen.generateMeals(1000);
//      mealGen.generateMeals(mealGenerate);
      mealGen.generateMeals(mealGenerate2Weeks);
   }

}
