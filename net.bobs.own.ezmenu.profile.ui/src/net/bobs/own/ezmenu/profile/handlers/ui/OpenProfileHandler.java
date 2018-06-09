 
package net.bobs.own.ezmenu.profile.handlers.ui;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.dialogs.ui.ProfileEditDialog;

public class OpenProfileHandler {
	
	@Execute
	public void execute(ESelectionService selectionService) {
		
		EzMenuProfile profile = null; 
		
		//Get the active selection from EzMenuExplorer part
		Object activeSelection = selectionService.getSelection("net.bobs.own.ezmenu.part.ezmenuexplorer");
		if (activeSelection != null && 
			activeSelection instanceof TreeItem) {
			TreeItem item = (TreeItem) activeSelection;
			TreeItem parent = item.getParentItem();
			//Verify that the active selection is an EzMenuProfile AND that it's under the "Meals" tree item.
			if (parent != null &&
				parent.getText().equals("Profiles")) {
				/*
				 * 	I didn't remember/know that each TreeItem.data field under the Profiles TreeItem points to 
				 * 	an EzMenuProfile object.  IF for some reason this went away, then another approach would be
				 * 		int index = parent.indexOf(item);				//Gets index of selected item in tree
				 * 		ArrayList<ITable> profsList = parent.getData();	//Gets ALL items in the meals tree item (mealsRoot)
				 * 		EzMenuProfile prof = (EzMenuProfile) profssList.get(index);
				 * 
				 * 	To get the EzMenuProfile Object
				 * 
				 */
				profile = (EzMenuProfile) item.getData();
				if (profile != null &&
					profile instanceof EzMenuProfile) {
					ProfileEditDialog dialog = new ProfileEditDialog(Display.getCurrent().getActiveShell(),profile);
					dialog.open();
				}
			}
		}
	}
		
}