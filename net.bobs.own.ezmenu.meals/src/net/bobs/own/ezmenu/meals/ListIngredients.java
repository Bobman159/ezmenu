package net.bobs.own.ezmenu.meals;

import java.util.ArrayList;

import net.bobs.own.ezmenu.meals.db.EzMenuMealIngredient;

public class ListIngredients {

	private ArrayList<EzMenuMealIngredient> listIngredients;
	
	public ListIngredients() {
		listIngredients = new ArrayList<EzMenuMealIngredient>();
	}
	
	public ListIngredients(ArrayList<EzMenuMealIngredient> listIngredients) {
		this.listIngredients = listIngredients;
	}
	
	public ArrayList<EzMenuMealIngredient> getIngredients() {
		return listIngredients;
	}
	
	public EzMenuMealIngredient getIngredient(int index) {
		return listIngredients.get(index);
	}
	
	public void addIngredient(EzMenuMealIngredient ingredient) {
		listIngredients.add(ingredient);
	}
	
	public void replaceIngredient(int index,EzMenuMealIngredient ingredient) {
		EzMenuMealIngredient oldIngred = listIngredients.set(index, ingredient);
		oldIngred = null;
	}
	
	public void deleteIngredient(int index) {
		listIngredients.remove(index);
	}
	
	public void clear() {
		listIngredients.clear();
	}
	
	public int size() {
		return listIngredients.size();
	}
}
