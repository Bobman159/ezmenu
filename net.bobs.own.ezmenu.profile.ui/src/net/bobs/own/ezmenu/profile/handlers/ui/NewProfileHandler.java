package net.bobs.own.ezmenu.profile.handlers.ui;

import org.eclipse.e4.core.di.annotations.Execute;
//import org.eclipse.e4.ui.model.application.MApplication;
//import org.eclipse.e4.ui.workbench.modeling.EModelService;
//import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.bobs.own.ezmenu.profile.dialogs.ui.ProfileEditDialog;

/**
 * Command handler for New Profile Command.
 * 
 * @author Robert Anderson
 *
 */
public class NewProfileHandler  {
	
	/**
	 * Create a dialog to allow for the creation of an EzMenu Profile in the
	 * database.  Validation of the name, length characters entered and 
	 * existence of the profile name name is done in the dialog.
	 * 
	 * @param app
	 * @param partService
	 * @param modelService
	 */
	@Execute
	public void execute(Shell shell) {

		/*	Create a dialog to prompt the user for a name.  Validation of the 
		 * 	name length, characters entered and existence of the name is done
		 * 	in the dialog.
		 */		
		ProfileEditDialog dialog = new ProfileEditDialog(Display.getCurrent().getActiveShell());		
		if (dialog.open() == Window.OK) {
 		
		}
	}
	
}