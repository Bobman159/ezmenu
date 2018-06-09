 
package net.bobs.own.ezmenu.meals.handlers.ui;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import net.bobs.own.ezmenu.meals.ui.EzMenuUIServices;

public class NewMealHandler {
	
	private Logger logger = LogManager.getLogger(NewMealHandler.class.getName());
	
	@Inject
	EModelService modelService;

	
	@Execute
	public void execute(MApplication app,EPartService partService, EModelService modelService) {
		logger.debug("New Meal editor created");		
		
		/*	Find the editors part stack in the model.  It's defined in the 
		 *  application.e4xmi file so it should always be present, thus I don't
		 *  check for null values.
		 */			
		
		MPart mealPart = EzMenuUIServices.newPartDescriptor(app,modelService,partService);
		partService.showPart(mealPart, PartState.ACTIVATE);

	}
		
}