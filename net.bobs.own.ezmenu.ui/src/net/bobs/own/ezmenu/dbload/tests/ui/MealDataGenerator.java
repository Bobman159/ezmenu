package net.bobs.own.ezmenu.dbload.tests.ui;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bobs.own.db.h2.pool.H2Database;
import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.meals.ListIngredients;
import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.db.EzMenuMealIngredient;
import net.bobs.own.ezmenu.meals.db.EzMenuMealMapper;

/**
 * A class to generate <code>EzMenuMeal</code> objects for testing purposes.  Two mechanisms are provided for 
 * generating the objects:
 * <ul><li>Generate a large number of meals using default values.</li> <li>Generate a specific list of meals 
 * based on specific input</li></ul>
 * 
 * The main purpose of this class is to provide a facility to generate test meals for Menu Plan generation and validation.
 * @author Robert Anderson
 *
 */
public class MealDataGenerator {

	/*
	 *	Generate test data for load testing the EzMenuExplorer & 
	 * 	Database process performance.  
	 * 
	 * 	Existing Data is DELETED before the test data is generated.
	 * 
	 */
	
	private EzMenuMeal  meal = null;
	private Logger logger = LogManager.getLogger(MealDataGenerator.class.getName());
	//false = no logging, true = debug logging 
	private final boolean LOG = false;

	H2Database db = null;
	EzMenuMealMapper  mealMapper = null;
	
	
	public MealDataGenerator(H2Database db) {
	
      this.db = db;
      mealMapper = EzMenuMealMapper.makeMapper(this.db);

	}
	
	/**
	 * Generate 0 to n <code>EzMenuMeal</code> objects for testing.  The objects are generated based on the input of a
	 * two dimensional array in a specific format.  Each row in the genMeals array corresponds to one or more meals 
	 * in the database.
	 * 
	 * Each row in the genMeals array follows the following format {#Meals,meal-category,meal-prep}.  <ul><li>#Meals = 
	 * the number of meals to be generated.</li><li>meal-category = the meal category in String form "Beef","Fish" etc.</li>
	 * <li>meal-prep = meal preparation time in string form "0-15","16-30" etc.</li></ul>
	 * 
	 * <b>Usage Example</b>
	 * <pre>private Object[][] mealGenerate = {{2,"Beef","0-15"},
	 *                                   {1,"Chicken","16-30"},
	 *                                   {1,"Pasta","31-45"}};
	 * </pre>
	 * 
	 * will result in the following Meals being generated:
	 * <pre>
	 *   2  Meals with category Beef and preparation time 0-15
	 *   1 meal with category Chicken and preparation time 16-30
	 *   1 meal with category Pasta and preparation time 31-45
	 * </pre>
	 * @param genMeals
	 * @return
	 */
	public int generateMeals(Object[][] genMeals) {
	   
	   int mealsAdded = 0;
	   EzMenuMeal meal = null;
      ListIngredients ingredientsList = new ListIngredients();	   
      
	   for (int mealRow = 0; mealRow < genMeals.length;mealRow++) {
	      Integer intCount = (Integer) genMeals[mealRow][0];
	      int mealCount = intCount.intValue();
	      String mealCategory = (String) genMeals[mealRow][1];
	      String prepTime = (String) genMeals[mealRow][2];
	      while (mealCount > 0) {
	         ingredientsList.clear();
	         for (int ingredIx = 0; ingredIx < 10; ingredIx++ ) {
	            EzMenuMealIngredient ingredient = new EzMenuMealIngredient(mealsAdded,"Ingredient_" + ingredIx,
	                        "1","cup");
	            ingredientsList.addIngredient(ingredient);
	         }
	         
	         meal = new EzMenuMeal("Meal_Name_" + mealsAdded,mealCategory,prepTime,ingredientsList,"directions text");
	         try {
	            RunDMLRequestFactory.makeInsertRequest(mealMapper, meal);
	            mealsAdded++;
	         } catch (RunDMLException hex) {
	            logger.debug(hex.getMessage(),hex);
	         }
	         mealCount--;
	      }
	   }
	   
	   return mealsAdded;
	}
	
	/**
	 * Generate a number of "default" meals for testing.  A default meal contains 10 ingredients and a category of 
	 * "Beef" with a preparation time of "0-15".
	 * 
	 * @param mealCount - number of meals to be generated
	 */
	public void generateMeals(int mealCount) {
		

		EzMenuMeal meal = null;
		ListIngredients ingredientsList = new ListIngredients();
		
		while (mealCount > 0) {
			ingredientsList.clear();
			for (int ingredIx = 0; ingredIx < 10; ingredIx++ ) {
				EzMenuMealIngredient ingredient = new EzMenuMealIngredient(mealCount,"Ingredient_" + ingredIx,
						"1","cup");
				ingredientsList.addIngredient(ingredient);
			}
			
			meal = new EzMenuMeal(mealCount,"meal_Name_" + mealCount, "Beef",
					"0-15",ingredientsList,"directions text");
			try {
				RunDMLRequestFactory.makeInsertRequest(mealMapper, meal);
			} catch (RunDMLException hex) {
				logger.debug(hex.getMessage(), hex);
			}
			mealCount--;
			writeToLog("mealCount= " + mealCount);
			writeToLog("Meal= " + meal.getMealName() + "added");
		}
	}
	
	/**
	 * Deletes ALL meals from the database.  As each meal is deleted, the ingredients for each meal are also deleted.
	 */
	public void deleteMeals() {
		
		try {
			writeToLog("deleteMeals method ENTER:");
			List<ITable> mealsList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			writeToLog("# of meals in database= " + mealsList.size());
			
			for (int mealsIx = 0; mealsIx < mealsList.size(); mealsIx++) {
				meal = (EzMenuMeal) mealsList.get(mealsIx);
				writeToLog("Calling meal.delete() method for meal= " + meal.getMealName());
				mealMapper.delete(meal);
				RunDMLRequestFactory.makeDeleteRequest(mealMapper, meal);
				writeToLog("Meal=" + meal.getMealName() + " deleted");
			}
		} catch (RunDMLException hex) {
			hex.printStackTrace();
			System.exit(16);
		}
		writeToLog("deleteMeals method EXIT:");
	}
	
	private void writeToLog(String message) {
	   if (LOG) {
	      logger.debug(message);
	   }
	}

}
