 
package net.bobs.own.ezmenu.meals.handlers.ui;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.db.EzMenuMealMapper;
import net.bobs.own.ezmenu.meals.resources.ui.Messages;

public class DeleteMealHandler {

	@Execute
	public void execute(ESelectionService selectionService) {

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
					EzMenuMeal meal = (EzMenuMeal) item.getData();
					if (meal != null && meal instanceof EzMenuMeal) { 
						MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING| SWT.YES | SWT.NO);
						messageBox.setText(Messages.DeleteMeal_ConfirmDelete_Title);	
						messageBox.setMessage(Messages.bind(Messages.DeleteMeal_ConfirmDelete_Msg,
											  meal.getMealName(),meal.getIngredients().size()));
						int rc = messageBox.open();
						if (rc == SWT.YES) {
							try {
								EzMenuMealMapper mealMap = EzMenuMealMapper.getMapper();
								RunDMLRequestFactory.makeDeleteRequest(mealMap,meal);
								mealMap.fireTableUpdated();
							} catch (RunDMLException ex) {
								MessageDialog errMsg = new MessageDialog(new Shell(), Messages.MealEditor_DBError_Title, null,
										Messages.bind(Messages.MealEditor_DBError, ex.getCause().getMessage()), MessageDialog.ERROR,
										new String[] { "Ok" }, 0);
								errMsg.open();
							}
						}
					}
				}
			}
		
	}
		
}