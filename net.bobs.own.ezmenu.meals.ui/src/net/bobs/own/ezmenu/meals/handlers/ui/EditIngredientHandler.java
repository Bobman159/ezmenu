package net.bobs.own.ezmenu.meals.handlers.ui;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import net.bobs.own.ezmenu.meals.editors.ui.MealEditor;

public class EditIngredientHandler {

	@Inject
	EPartService partService;
	
	@Execute
	public void execute() {
		MPart part = partService.getActivePart();
		if (part != null) {
			MealEditor editor = (MealEditor) part.getObject();
			if (editor != null && editor instanceof MealEditor) {
				editor.editIngredient();
			}
		}
	}

}
