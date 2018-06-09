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

import net.bobs.own.db.h2.pool.H2Database;
import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;
import net.bobs.own.ezmenu.resources.ui.Messages;

  public class SelectProfilesJob extends Job {

	private 	final String ID = "net.bobs.own.ezmenu.ui"; //$NON-NLS-1$
	private 	Logger 		logger = LogManager.getLogger(SelectProfilesJob.class.getName());
	private 	TreeItem	profsRoot = null;
	private		List<ITable> profsList = null;
	private 	EzMenuProfileMapper profMapper = null;
	SelectJobStatus status = null;
	
	public SelectProfilesJob(TreeItem profsRoot,EzMenuProfileMapper profMapper) {
		super("Select Profiles Job");
		this.profsRoot = profsRoot;
		this.profMapper= profMapper;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		//TODO: Display progress bar when this job is running to indicate updates are in progress?
		logger.debug("Starting SelectProfilesJob");
		try {			
			profsList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (profsRoot.getData() != null) {
						profsRoot.removeAll();
					}
				}
			});	
			
			status = new SelectJobStatus(Status.OK,ID,"Profiles selected successfully",profsList);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					profsRoot.setData(profsList);
					profsRoot.setItemCount(profsList.size());
				}
			});
			
		} catch (RunDMLException rdex) {
			MessageDialog errMsg = new MessageDialog(new Shell(), Messages.EzMenuExplorer_DBErrror_Title, null,
					Messages.bind(Messages.EzMenuExplorer_DBErrror, rdex.getCause().getMessage()), MessageDialog.ERROR,
					new String[] { "Ok" }, 0);
			errMsg.open();
			logger.debug(rdex.getMessage(),rdex);
			status = new SelectJobStatus(Status.ERROR,ID,rdex.getMessage(),rdex);
		}
		
		logger.debug("End SelectProfilesJob status is " + status.getCode() + " " + profsList.size() + " rows fetched.");
		return status;
	}

}
