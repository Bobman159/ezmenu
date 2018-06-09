package net.bobs.own.ezmenu.meals.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.mapper.ITable;

public class EzMenuMealIngredient implements ITable {

	private		int 	mealId;
	private 	long	ingredientId;
	private		String	ingredient;
	private		String	quantity;
	private		String	unitOfMeasure;

	//Indication of what needs to happen with this ingredient in the database.
	/*
	 *	INSERT =	Insert ingredient into the database ezmenu.ingredients table
	 *	UPDATE =	Update ingredient into the database ezmenu.ingredients table
	 *	DELETE =	Delete ingredient from the database ezmenu.ingredients table
	 *
	 */
	private enum	dbStatus {INSERT,UPDATE,DELETE};
	private	dbStatus	status;
	static private Logger 		logger = LogManager.getLogger(EzMenuMealIngredient.class.getName());
	
	/**
	 * Create a meal ingredient.
	 * @param ingredient - ingredient description
	 * @param quantity - quantity for the ingredient (1/4,1/2 etc)
	 * @param unitOfMeasure - unit of measure for ingredient (cup, teaspoon etc)
	 */
	public EzMenuMealIngredient(String ingredient, String quantity, 
								String unitOfMeasure) {
		this.mealId = -1;
		this.ingredientId = -1;
		this.ingredient = ingredient;
		this.quantity = quantity;
		this.unitOfMeasure = unitOfMeasure;
	}

	/**
	 * 
	 * Create a meal ingredient.
	 * @param mealId - identifier of the meal for this ingredient
	 * @param ingredient - ingredient description
	 * @param quantity - quantity for the ingredient (1/4,1/2 etc)
	 * @param unitOfMeasure - unit of measure for ingredient (cup, teaspoon etc)
	 */
	public EzMenuMealIngredient(int mealId,String ingredient, String quantity, 
								String unitOfMeasure) {
		this.mealId = mealId;
		this.ingredientId = -1;
		this.ingredient = ingredient;
		this.quantity = quantity;
		this.unitOfMeasure = unitOfMeasure;
	}	
	
	/**
	 * 
	 * Create a meal ingredient.
	 * @param mealId - identifier of the meal for this ingredient
	 * @param ingredId - identifier for the ingredient
	 * @param ingredient - ingredient description
	 * @param quantity - quantity for the ingredient (1/4,1/2 etc)
	 * @param unitOfMeasure - unit of measure for ingredient (cup, teaspoon etc)
	 */
	public EzMenuMealIngredient(int mealId,long ingredientId, String ingredient, String quantity, 
								String unitOfMeasure) {
		this.mealId = mealId;
		this.ingredientId = ingredientId;
		this.ingredient = ingredient;
		this.quantity = quantity;
		this.unitOfMeasure = unitOfMeasure;
	}	

	public int getMealId() {
		return mealId;
	}
	
	public long getIngredientId() {
		return ingredientId;
	}

	public String getIngredient() {
		return ingredient;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}
	
	/**
	 * Check if the ingredient is an ingredient that is defined in the database or not
	 * 
	 * @return true - if the ingredient is defined in the database, false otherwise
	 */
	public boolean isExistingIngredient() {

		boolean existingIngredient = false;

		if (ingredientId != -1) {
			existingIngredient = true;
		}
		
		return existingIngredient;

	}
	

	
	/**
	 * Indicate the ingredient should be inserted into the database.
	 */
	public void markForInsert() {
		status = dbStatus.INSERT;
	}
	
	public boolean isMarkedInsert() {
		boolean isMarkedInsert = false;
		if (status == dbStatus.INSERT) {
			isMarkedInsert = true;
		}
		return isMarkedInsert;
	}
	
	/**
	 * Indicate the ingredient should be updated in the database.
	 */
	public void markForUpdate() {
		status = dbStatus.UPDATE;
	}
	
	public boolean isMarkedUpdate() {
		boolean isMarkedUpdate = false;
		if (status == dbStatus.UPDATE) {
			isMarkedUpdate = true;
		}
		return isMarkedUpdate;
	}
	
	/***
	 * Indicate the ingredient should be deleted in the database
	 */
	public void markForDelete() {
		status = dbStatus.DELETE;
	}
	
	public boolean isMarkedDelete() {
		boolean isMarkedDelete = false;
		if (status == dbStatus.DELETE) {
			isMarkedDelete = true;
		}
		return isMarkedDelete;
	}

}
