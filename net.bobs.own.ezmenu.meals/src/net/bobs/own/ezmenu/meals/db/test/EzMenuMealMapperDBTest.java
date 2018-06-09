package net.bobs.own.ezmenu.meals.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
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
import net.bobs.own.ezmenu.meals.ListIngredients;
import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.db.EzMenuMealIngredient;
import net.bobs.own.ezmenu.meals.db.EzMenuMealIngredientMapper;
import net.bobs.own.ezmenu.meals.db.EzMenuMealMapper;

class EzMenuMealMapperDBTest {

	//TODO: Add additional tests to improve code coverage
	//Especially failure tests, right now this tests mostly the "happy path"
	private static H2Database ezMenuDbTest;
	private static EzMenuMealMapper mealMapper = null;
	private static EzMenuMealIngredientMapper ingredientMapper = null;
	private static Logger logger = LogManager.getLogger(EzMenuMealMapperDBTest.class.getName());
	private static ArrayList<EzMenuMealIngredient> ingredientsList = new ArrayList<EzMenuMealIngredient>();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		IH2ConnectionPool pool = H2ConnectionPoolFactory.getInstance()
		                                                .makePool(H2ConnectionPoolFactory.PoolTypes.MYOWN, 
		      "D:\\Java\\EzMenu_Workspace\\net.bobs.own.ezmenu\\db\\ezmenu_test", 
				"EzMenuUser", "Aqpk3728", "10", "ezmenuTest.pool");		
		      
		ezMenuDbTest = new H2Database(pool);
		mealMapper = new EzMenuMealMapper(ezMenuDbTest);
		ingredientMapper = new EzMenuMealIngredientMapper(ezMenuDbTest);
		deleteAllRows();
		
		ingredientsList.add(new EzMenuMealIngredient("Ingredient_Teaspoon","1","teaspoon"));
		ingredientsList.add(new EzMenuMealIngredient("Ingredient_Tablespoon","1 1/4","tablespoon"));
		ingredientsList.add(new EzMenuMealIngredient("Ingredient_Pound","2 1/2","pound"));
		ingredientsList.add(new EzMenuMealIngredient("Ingredient_Ounces","16 3/4","ounces"));
		ingredientsList.add(new EzMenuMealIngredient("Ingredient_Cup","2","cup"));
		ingredientsList.add(new EzMenuMealIngredient("Ingredient_Can","2","can"));
		ingredientsList.add(new EzMenuMealIngredient("Ingredient_Slices","8","slices"));
		ingredientsList.add(new EzMenuMealIngredient("Ingredient_Package","8 1/2","package"));

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ezMenuDbTest.reset();
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testInsert() {
		//Test Default Insert of Meal
		ListIngredients listIngred = new ListIngredients(ingredientsList);
		
		EzMenuMeal meal = new EzMenuMeal("Insert_MealName", "Fish", "61+",
						listIngred, "Insert directions text");

		try {
			RunDMLRequestFactory.makeInsertRequest(mealMapper, meal);
			List<ITable> mealList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			assertEquals(mealList.size(),1);
			assertEquals("Insert_MealName",((EzMenuMeal) mealList.get(0)).getMealName());
			assertEquals("Fish",((EzMenuMeal) mealList.get(0)).getMealCatgy());
			assertEquals("61+",((EzMenuMeal) mealList.get(0)).getMealPrepTime());
			assertEquals("Insert directions text",meal.getMealDirections());
		
			
			//Validate Ingredients List
			EzMenuMeal insertedMeal = (EzMenuMeal) mealList.get(0);
			for(int ingredIx = 0; ingredIx < insertedMeal.sizeIngredients(); ingredIx++) {
				EzMenuMealIngredient insertedIngredient = insertedMeal.getIngredient(ingredIx);
				assertEquals(insertedIngredient.getIngredient(),ingredientsList.get(ingredIx).getIngredient());
				assertEquals(insertedIngredient.getMealId(),ingredientsList.get(ingredIx).getMealId());
				assertEquals(insertedIngredient.getQuantity(),ingredientsList.get(ingredIx).getQuantity());
				assertEquals(insertedIngredient.getUnitOfMeasure(),ingredientsList.get(ingredIx).getUnitOfMeasure());
			}

		} catch (RunDMLException rdex) {
			failException(rdex);
		}
	}
	
	@Test
	void testUpdate() {
		//Test Update of Profile
		ArrayList<EzMenuMealIngredient> updateIngredients = new ArrayList<EzMenuMealIngredient>();
		
		try {
			List<ITable> mealsList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			assertEquals(mealsList.size(),1);
			EzMenuMeal meal = (EzMenuMeal) mealsList.get(0);
			String mealName = "Updated_MealName";
			
			updateIngredients.add(new EzMenuMealIngredient(meal.getMealId(),meal.getIngredient(0).getIngredientId(),"UpdIngredient_Package","9 1/2","package"));
			updateIngredients.add(new EzMenuMealIngredient(meal.getMealId(),meal.getIngredient(1).getIngredientId(),"UpdIngredient_Slices","9","slices"));
			updateIngredients.add(new EzMenuMealIngredient(meal.getMealId(),meal.getIngredient(2).getIngredientId(),"Ingredient_Can","3","can"));
			updateIngredients.add(new EzMenuMealIngredient(meal.getMealId(),meal.getIngredient(3).getIngredientId(),"UpdIngredient_Cup","1","cup"));			
			updateIngredients.add(new EzMenuMealIngredient(meal.getMealId(),meal.getIngredient(4).getIngredientId(),"UpdIngredient_Ounces","15 3/4","ounces"));			
			updateIngredients.add(new EzMenuMealIngredient(meal.getMealId(),meal.getIngredient(5).getIngredientId(),"UpdIngredient_Pound","2 1/4","pound"));
			updateIngredients.add(new EzMenuMealIngredient(meal.getMealId(),meal.getIngredient(6).getIngredientId(),"UpdIngredient_Tablespoon","3 3/4","tablespoon"));
			updateIngredients.add(new EzMenuMealIngredient(meal.getMealId(),meal.getIngredient(7).getIngredientId(),"UpdIngredient_Teaspoon","2 1/2","teaspoon"));
			
			EzMenuMeal updatedMeal = new EzMenuMeal(meal.getMealId(),mealName,"Pasta","0-15",new ListIngredients(),"Updated directions text");
			updatedMeal.addIngredient(updateIngredients.get(0));
			updateIngredients.get(0).markForUpdate();
			updatedMeal.addIngredient(updateIngredients.get(1));
			updateIngredients.get(1).markForUpdate();
			updatedMeal.addIngredient(updateIngredients.get(2));
			updateIngredients.get(2).markForUpdate();
			updatedMeal.addIngredient(updateIngredients.get(3));
			updateIngredients.get(3).markForUpdate();
			updatedMeal.addIngredient(updateIngredients.get(4));
			updateIngredients.get(4).markForUpdate();
			updatedMeal.addIngredient(updateIngredients.get(5));
			updateIngredients.get(5).markForUpdate();
			updatedMeal.addIngredient(updateIngredients.get(6));			
			updateIngredients.get(6).markForUpdate();
			updatedMeal.addIngredient(updateIngredients.get(7));			
			updateIngredients.get(7).markForUpdate();
			RunDMLRequestFactory.makeUpdateRequest(mealMapper, updatedMeal);
			
			List<ITable> updatedMealList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			assertEquals(updatedMealList.size(),1);
			
			for (ITable table : updatedMealList) {
				EzMenuMeal dbupdatedMeal = (EzMenuMeal) table;
				assertEquals(dbupdatedMeal.getMealId(),meal.getMealId());				
				assertEquals(dbupdatedMeal.getMealName(),mealName);
				assertEquals(dbupdatedMeal.getMealCatgy(),"Pasta");
				assertEquals(dbupdatedMeal.getMealPrepTime(),"0-15");
				assertEquals(dbupdatedMeal.getMealDirections(),"Updated directions text");
				assertEquals(dbupdatedMeal.sizeIngredients(),updatedMeal.sizeIngredients());
				
				for (int ingredIx = 0; ingredIx < dbupdatedMeal.sizeIngredients();ingredIx++) {
					
					assertEquals(dbupdatedMeal.getIngredient(ingredIx).getIngredient(),updatedMeal.getIngredient(ingredIx).getIngredient());
					assertEquals(dbupdatedMeal.getIngredient(ingredIx).getMealId(),updatedMeal.getIngredient(ingredIx).getMealId());	
					assertEquals(dbupdatedMeal.getIngredient(ingredIx).getIngredientId(),updatedMeal.getIngredient(ingredIx).getIngredientId());
					assertEquals(dbupdatedMeal.getIngredient(ingredIx).getQuantity(),updatedMeal.getIngredient(ingredIx).getQuantity());
					assertEquals(dbupdatedMeal.getIngredient(ingredIx).getUnitOfMeasure(),updatedMeal.getIngredient(ingredIx).getUnitOfMeasure());
				}
			}
			
			
		} catch (RunDMLException rdex) {
			failException(rdex);
		}

	}
	private static void deleteAllRows() {
		try {
			List<ITable> mealList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			for(ITable table :  mealList) {
				EzMenuMeal meal = (EzMenuMeal) table;
				RunDMLRequestFactory.makeDeleteRequest(mealMapper, meal);
			}
		} catch (RunDMLException rdex) {
			failException(rdex);
		}
	}
	
	@Test
	void testDelete() {
		
		try {
			//Insert of Meal
			ListIngredients listIngred = new ListIngredients(ingredientsList);
			
			EzMenuMeal meal = new EzMenuMeal("Insert_MealName", "Fish", "61+",
							listIngred, "Insert directions text");
			
			RunDMLRequestFactory.makeInsertRequest(mealMapper, meal);
			List<ITable> mealList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			assertEquals(mealList.size(),1);
			logger.debug("# of meals= " + mealList.size());
			logger.debug("# of ingredients= " + ((EzMenuMeal)mealList.get(0)).sizeIngredients());
			
			//Test Delete of Meal
			for ( ITable table : mealList) {
				EzMenuMeal mealDelete = (EzMenuMeal) table;
				RunDMLRequestFactory.makeDeleteRequest(mealMapper, mealDelete);
			}
			
			//Should have an empty Meals table.
			List<ITable> deleteMealsList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			assertEquals(deleteMealsList.size(),0);
			
			//AND an empty Ingredients Table
			List<ITable> deleteIngredientList = RunDMLRequestFactory.makeSelectRequest(ingredientMapper);
			assertEquals(deleteIngredientList.size(),0);

		} catch (RunDMLException rdex) {
			failException(rdex);
		}

	}
	private static void failException(RunDMLException rdex) {

		logger.error(rdex.getMessage(), rdex);
		fail("RunDMLException");

	}
}
