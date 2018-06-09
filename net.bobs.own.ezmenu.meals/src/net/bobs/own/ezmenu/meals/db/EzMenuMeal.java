
package net.bobs.own.ezmenu.meals.db;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.meals.ListIngredients;

public class EzMenuMeal implements ITable  {//IH2ActiveRecord {

	private int mealId;
	private String mealName;
	private String mealCategory;
	private String mealPrepTime;
	private ListIngredients listIngredients = null;
	private String directions;
	static private Logger logger = LogManager.getLogger(EzMenuMeal.class.getName());
	
	/**
	 * Create a meal.
	 * 
	 * @param mealName
	 *            - name of the meal
	 * @param mealCategory
	 *            - the category for the meal (Beef,Chicken, etc)
	 * @param mealPrepTime
	 *            - the preparation time for the meal (0-15,16-30, etc)
	 */
	public EzMenuMeal(String mealName, String mealCategory, String mealPrepTime) {
		this.mealId = -1;
		this.mealName = mealName;
		this.mealCategory = mealCategory;
		this.mealPrepTime = mealPrepTime;
		listIngredients = new ListIngredients();
		this.directions = null;
	}
	
	/**
	 * Create a meal.
	 * 
	 * @param mealName
	 *            - name of the meal
	 * @param mealCategory
	 *            - the category for the meal (Beef,Chicken, etc)
	 * @param mealPrepTime
	 *            - the preparation time for the meal (0-15,16-30, etc)
	 * @param listIngredients
	 *            - list of ingredients for the meal
	 */
	public EzMenuMeal(String mealName, String mealCategory, String mealPrepTime,
			ListIngredients listIngredients, String directions) {
		this.mealId = -1;
		this.mealName = mealName;
		this.mealCategory = mealCategory;
		this.mealPrepTime = mealPrepTime;
		this.listIngredients = listIngredients;
		this.directions = directions;
	}

	/**
	 * Create a meal.
	 * 
	 * @param mealId
	 *            - identifier of the meal.
	 * @param mealName
	 *            - name of the meal
	 * @param mealCategory
	 *            - the category for the meal (Beef,Chicken, etc)
	 * @param mealPrepTime
	 *            - the preparation time for the meal (0-15,16-30, etc)
	 * @param listIngredients
	 *            - list of ingredients for the meal
	 */
	public EzMenuMeal(int mealId, String mealName, String mealCategory, String mealPrepTime,
			ListIngredients listIngredients, String directions) {
		this.mealId = mealId;
		this.mealName = mealName;
		this.mealCategory = mealCategory;
		this.mealPrepTime = mealPrepTime;
		this.listIngredients = listIngredients;
		this.directions = directions;
	}

	/**
	 * Check if the meal is a meal that is defined in the database or not
	 * 
	 * @return true - if the meal is defined in the database, false otherwise
	 */
	public boolean isExistingMeal() {

		boolean existingMeal = false;
		if (mealId != -1) {
			existingMeal = true;
		}
		return existingMeal;

	}

	/**
	 * Adds an ingredient to the ingredients list for the meal.
	 * 
	 * @param ingredient - the meal to be added
	 */
	public void addIngredient(EzMenuMealIngredient ingredient) {
		ingredient.markForInsert();
		listIngredients.addIngredient(ingredient);
	}

	public void replaceIngredient(int index, EzMenuMealIngredient ingredient) {
		listIngredients.replaceIngredient(index, ingredient);
	}

	public void deleteIngredient(int index) {
		
		listIngredients.deleteIngredient(index);
	}

	public int getMealId() {
		return mealId;
	}
	
	public String getMealName() {
		return mealName;
	}
	
	public String getMealCatgy() {
		return mealCategory;
	}
	
	public String getMealPrepTime() {
		return mealPrepTime;
	}
	
	public String getMealDirections() {
		return directions;
	}
	
	public ArrayList<EzMenuMealIngredient> getIngredients() {
		return listIngredients.getIngredients();
	}

	public EzMenuMealIngredient getIngredient(int index) {
		return listIngredients.getIngredient(index);
	}
	
	public int sizeIngredients() {
		return listIngredients.size();
	}
	
	@Override
	public boolean equals(Object obj) {
	   
	   boolean equal = false;
	   if (obj instanceof EzMenuMeal) {
	      EzMenuMeal meal = (EzMenuMeal) obj;
	      if (meal.getMealId() == mealId &&
	          meal.getMealName().equals(mealName) && 
	          meal.getMealCatgy().equals(mealCategory) &&
	          meal.getMealPrepTime().equals(mealPrepTime) &&
	          meal.getMealDirections().equals(directions)) {
	         equal = true;
	      }
	   }
	   
	   return equal;
	}

}
