 
package net.bobs.own.ezmenu.meals.handlers.ui;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.TreeItem;

import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.editors.ui.MealEditor;
import net.bobs.own.ezmenu.meals.ui.EzMenuUIServices;

public class OpenMealHandler {	

	@Execute
	public void execute(MApplication app,EPartService partService, EModelService modelService,
						ESelectionService selectionService) {

		EzMenuMeal meal = null; 
		
		//Get the active selection from EzMenuExplorer part
		Object activeSelection = selectionService.getSelection("net.bobs.own.ezmenu.part.ezmenuexplorer");
		if (activeSelection != null && 
			activeSelection instanceof TreeItem) {
			TreeItem item = (TreeItem) activeSelection;
			TreeItem parent = item.getParentItem();
			//Verify that the active selection is an EzMenuMeal AND that it's under the "Meals" tree item.
			if (parent != null &&
				parent.getText().equals("Meals")) {
				/*
				 * 	I didn't remember/know that each TreeItem.data field under the Meals TreeItem points to 
				 * 	an EzMenuMeal object.  IF for some reason this went away, then another approach would be
				 * 		int index = parent.indexOf(item);				//Gets index of selected item in tree
				 * 		ArrayList<ITable> mealsList = parent.getData();	//Gets ALL items in the meals tree item (mealsRoot)
				 * 		EzMenuMeal meal = (EzMenuMeal) mealsList.get(index);
				 * 
				 * 	To get the EzMenuMeal Object
				 * 
				 */
				meal = (EzMenuMeal) item.getData();
				if (meal != null &&
					meal instanceof EzMenuMeal) {
					MPart mealPart = EzMenuUIServices.newPartDescriptor(app,modelService,partService);
					MealEditor editor = (MealEditor) mealPart.getObject();
					editor.setInput(meal);
					partService.showPart(mealPart, PartState.ACTIVATE);
				}
			}
		}

	} 
		
}