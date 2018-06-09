 
package net.bobs.own.ezmenu.handlers.ui;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import net.bobs.own.ezmenu.constants.ui.EzMenuConstants;


public class FileSaveHandler {

	@Execute
	public void execute(EPartService service) {
		
		MPart part = service.getActivePart();
		if (part != null && part.getElementId().equals(EzMenuConstants.MEAL_EDITOR_PART_ID)) {
			if (part.isDirty()) {
				service.savePart(part, false);
			}
		}
	}
	
	@CanExecute 
	/**
	 * Enables or Disables the Save menu option on the File menu based on whether
	 * an active editor is available. 
	 * @param service - PartService 
	 * @return - true if menu should be enabled, false otherwise
	 */
	public boolean isSaveEnabled(EPartService service) {

		MPart part = service.getActivePart();
		if (part != null && part.getElementId().equals(EzMenuConstants.MEAL_EDITOR_PART_ID)) {
			if (part.isDirty()) {
				return true;
			}
		}

		return false;
	}
	
}