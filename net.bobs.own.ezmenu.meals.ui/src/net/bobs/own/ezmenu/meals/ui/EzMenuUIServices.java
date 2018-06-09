package net.bobs.own.ezmenu.meals.ui;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import net.bobs.own.ezmenu.constants.ui.EzMenuConstants;

public class EzMenuUIServices {

	private final static String ID_PART_EDITOR  = "net.bobs.own.ezmenu.partstack.editors";	
	
	public static MPartStack getEditorStack(EModelService modelService, 
											MApplication app) 
	{
		
		MPartStack partStack = (MPartStack) modelService.find(ID_PART_EDITOR, app);
		return partStack;
	
	}
	
	public static MPart newPartDescriptor(MApplication app, EModelService modelService,
										  EPartService partService) {
		
		MPartStack partStack = EzMenuUIServices.getEditorStack(modelService, app);
		MPart mealPart = partService.createPart("net.bobs.own.ezmenu.meals.editors.ui.MealEditor");				
		partStack.getChildren().add(mealPart);
		partStack.setSelectedElement(mealPart);

		return mealPart;
	}
	
	
	public static MPart newPartEditor(EModelService modelService, String label,
									  String elementId,String URIID) {

		MPart part = modelService.createModelElement(MPart.class);
		part.setLabel(label);
		part.setElementId(elementId);
		//The ContributionURI is what links the MPart created to the MealEditor
		part.setContributionURI(URIID);
		part.setVisible(true);
		part.setCloseable(true);

		return part;

	}

}
