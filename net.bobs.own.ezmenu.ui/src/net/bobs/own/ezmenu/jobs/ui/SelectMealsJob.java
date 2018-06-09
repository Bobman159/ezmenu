package net.bobs.own.ezmenu.jobs.ui;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.meals.db.EzMenuMealMapper;
import net.bobs.own.ezmenu.resources.ui.Messages;

  public class SelectMealsJob extends Job {

	  //An alternative approach for synchronization would be found at http://www.vogella.com/tutorials/EclipseJobs/article.html
	  //I chose to use Display.async because it avoids having to make this job injectable by using @Singleton & @Creatable annotations.
	  
	private 	final String ID = "net.bobs.own.ezmenu.ui"; //$NON-NLS-1$
	private 	Logger 		logger = LogManager.getLogger(SelectMealsJob.class.getName());

	private		TreeItem	mealsRoot = null;
	List<ITable> mealsList = null;
	SelectJobStatus status = null; 
	boolean done = false;
	int offset = 0;
	int resultCount = 0;
	EzMenuMealMapper mealMapper;

	public SelectMealsJob(TreeItem mealsRoot,EzMenuMealMapper mealMapper) {		
		super("Select meals job");
		this.mealsRoot = mealsRoot;
		this.mealMapper = mealMapper;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		//TODO: Display progress bar when this job is running to indicate updates are in progress
		logger.debug("Starting SelectMealsJob");
		try {
			Display.getDefault().asyncExec(new Runnable() {
				@Override 
				public void run() {
					if (mealsRoot.getData() != null) {
						mealsRoot.removeAll();
					}
				}
			});
			
			mealsList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			status = new SelectJobStatus(Status.OK,ID,"Meals selected successfully",mealsList);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
						mealsRoot.setData(mealsList);
						mealsRoot.setItemCount(mealsList.size());
				}
			});	
		} catch (RunDMLException rdex) {
			MessageDialog errMsg = new MessageDialog(new Shell(), Messages.EzMenuExplorer_DBErrror_Title, null,
					Messages.bind(Messages.EzMenuExplorer_DBErrror, rdex.getCause().getMessage()), MessageDialog.ERROR,
					new String[] { "Ok" }, 0);
			errMsg.open();
			status = new SelectJobStatus(Status.ERROR,ID,rdex.getMessage(),rdex);
		}

		logger.debug("End SelectMealsJob status is " + status.getCode() + " " + mealsList.size() + " rows fetched.");
		return status;
	}

}
