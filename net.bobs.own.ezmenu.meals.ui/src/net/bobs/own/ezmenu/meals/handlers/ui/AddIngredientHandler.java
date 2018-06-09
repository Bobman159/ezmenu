package net.bobs.own.ezmenu.meals.handlers.ui;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import net.bobs.own.ezmenu.meals.editors.ui.MealEditor;

public class AddIngredientHandler {

	@Execute
	public void execute(EPartService partService) {

		MPart part = partService.getActivePart();
		if (part != null) {
			MealEditor editor = (MealEditor) part.getObject();
			if (editor != null && editor instanceof MealEditor) {
				editor.addIngredient();
			}
		}

	}
	
}
