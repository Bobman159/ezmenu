package net.bobs.own.ezmenu.meals.resources.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "net.bobs.own.ezmenu.meals.resources.ui.messages";
	public static String		mealeditor_title;
	public static String		MealEditor_Mealhdg;
	public static String		MealEditor_LblName;
	public static String		MealEditor_LblCategory;
	public static String		MealEditor_LblPrepTime;
	public static String		MealEditor_InvalidChar_Title;
	public static String		MealEditor_ConfirmDelete_Ingredient;
	public static String		MealEditor_DeleteIngredient_Title;
	public static String		MealEditor_NoSelection_Title;
	public static String		MealEditor_NoSelection_Msg;
	public static String		IngredientsHdg;
	public static String 		QuantityHdg;
	public static String		UnitMeasureHdg;
	public static String		MealEditor_DirectionsHdg;
	public static String		IngredientDialog_Title;
	public static String		IngredientDialog_CreateAnother;
	public static String		IngredientsDialog_EmptyIngredient;
	public static String        MealEditor_DBError;
	public static String		MealEditor_DBError_Title;
	public static String		MealEditor_EmptyMealName;
	public static String		MealEditor_Warn_Title;
	public static String		DeleteMeal_ConfirmDelete_Title;
	public static String		DeleteMeal_ConfirmDelete_Msg;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
