 
package net.bobs.own.ezmenu.profile.handlers.ui;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TreeItem;

import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;
import net.bobs.own.ezmenu.profile.resources.ui.Messages;

public class DeleteProfileHandler {
	
	//TODO: Have EzMenuExplorer update the Profiles Tree List after the udpates are processed from the Profile Dialog
	//Prefer updating the individual profile if possible or the entire list of profiles if necessary....
	@Execute
	public void execute(ESelectionService selectionService) {
		Object activeSelection = selectionService.getSelection("net.bobs.own.ezmenu.part.ezmenuexplorer");
		if (activeSelection != null &&
			activeSelection instanceof TreeItem) {
			TreeItem item = (TreeItem) activeSelection;
			TreeItem parent = item.getParentItem();
			//Verify that the active selection is an EzMenuProfile AND that it's under the "Profiles" tree item.
			if (parent != null && 
				parent.getText().equals("Profiles")) {
				EzMenuProfile profile = (EzMenuProfile) item.getData();
				if (profile != null &
					profile instanceof EzMenuProfile) {
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(),
														SWT.ICON_WARNING | SWT.YES | SWT.NO);
					messageBox.setText(Messages.DeleteProfile_ConfirmDelete_Title);
					messageBox.setMessage(Messages.bind(Messages.DeleteProfile_ConfirmDelete_Msg, profile.getName()));
					int rc = messageBox.open();
					if (rc == SWT.YES) {
						try {
							EzMenuProfileMapper profMapper = EzMenuProfileMapper.getMapper();
							RunDMLRequestFactory.makeDeleteRequest(profMapper, profile);
							profMapper.fireTableUpdated();
						} catch (RunDMLException ex) {
							MessageDialog errMsg = new MessageDialog(Display.getCurrent().getActiveShell(),
									Messages.ProfEditDlg_DBError_Title,null,
									Messages.bind(Messages.ProfEditDlg_DBError, ex.getCause().getMessage()),
									MessageDialog.ERROR, new String[] {"OK"},0);
							errMsg.open();
						}
					}
				}
			}
		}
	}
		
}