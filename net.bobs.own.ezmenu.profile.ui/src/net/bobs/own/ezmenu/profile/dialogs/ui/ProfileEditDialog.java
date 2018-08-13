package net.bobs.own.ezmenu.profile.dialogs.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.WeekDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;
import net.bobs.own.ezmenu.profile.editing.ui.CategoryEditingSupport;
import net.bobs.own.ezmenu.profile.editing.ui.PrepTimeEditingSupport;
import net.bobs.own.ezmenu.profile.resources.ui.Messages;
import net.bobs.own.ezmenu.validators.ui.IValidatorCallback;
import net.bobs.own.ezmenu.validators.ui.TextValidator;
import net.bobs.own.ezmenu.validators.ui.TextValidatorStatus;

/**
 * A simple dialog to edit or create a EzMenu profile.  
 * <ul><li>If a new profile is being created, prompt the user to enter a name 
 * and specify  settings for an EzMenu profile.</li>
 * <li>If an existing profile is being edited, then the profile name will be 
 * display only, but the user will be able change the settings for the EzMenu
 * profile</li><ul>
 * 
 * By default the name length is unlimited, 
 * but can be set to a limit.
 * Validation of the name length and characters is also allowed when requested.
 * 
 * @author Robert Anderson
 *
 */

public class ProfileEditDialog extends TitleAreaDialog implements IValidatorCallback {
	
	private Composite area = null;
	private Composite container = null;
	private Composite tblComposite = null;
	private TableViewer	profViewer = null;
	private EzMenuProfile profile = null;
	Text txtName = null;
	private boolean newProf = false;
	TextValidator validator = null;
	
	private Logger logger = LogManager.getLogger(ProfileEditDialog.class.getName());
	private TableViewerColumn dayViewer;
	
	/**
	 * Create an instance of the ProfileEditDialog for the creation of a New 
	 * EzMenu Profile.
	 * @param parentShell - parent shell of dialog
	 * 
	 */
	/**
	 * @wbp.parser.constructor
	 */
	public ProfileEditDialog(Shell parentShell) {

		super(parentShell);
		profile = new EzMenuProfile(Messages.NameDialog_InitValue);
		newProf = true;
		
	}
	
	/**
	 * Create an instance of the ProfileEditDialog.  This constructor should be
	 * used when editing an existing a New EzMenu Profile.
	 * @param parentShell - parent shell of dialog
	 * @param name - the name of the Profile
	 * @param profRows - An array list of <Code>ProfileDayModel</code> entries.
	 * 
	 */
	public ProfileEditDialog(Shell parentShell,EzMenuProfile profile) {

		super(parentShell);
		this.profile = profile;
		newProf = false;
	}

	@Override
	public void okPressed() {

		EzMenuProfileMapper profMapper = EzMenuProfileMapper.getMapper();
		
		//Check if Profile already exists... & display message....
		setErrorMessage(null);
		String profName = txtName.getText();
	
		boolean profExists = profMapper.checkForExistence(profName);
		//Since Ok button is disabled for errors, this should be a valid 
		//profile name....
		try {			
			//Don't allow for create OR rename of profile to an existing profile name....
			EzMenuProfile newProfile = new EzMenuProfile(profile.getId(),txtName.getText());
			newProfile.addProfileDay(profile.getProfileDay(WeekDay.Sunday.getDay()));
			newProfile.addProfileDay(profile.getProfileDay(WeekDay.Monday.getDay()));
			newProfile.addProfileDay(profile.getProfileDay(WeekDay.Tuesday.getDay()));
			newProfile.addProfileDay(profile.getProfileDay(WeekDay.Wednesday.getDay()));					
			newProfile.addProfileDay(profile.getProfileDay(WeekDay.Thursday.getDay()));
			newProfile.addProfileDay(profile.getProfileDay(WeekDay.Friday.getDay()));
			newProfile.addProfileDay(profile.getProfileDay(WeekDay.Saturday.getDay()));

			if ((profExists == true) &&
				(txtName.getText().equals(profile.getName()) == false)) {
				setErrorMessage(Messages.bind(Messages.ProfileValidator_NameExists, profName));
			} else {
				if (newProf) {
					logger.debug("Adding profile name= " + txtName.getText() + " to database.");
					RunDMLRequestFactory.makeInsertRequest(profMapper,newProfile);
					MessageDialog msgdlg= new MessageDialog(new Shell(),
						Messages.ProfEditDlg_DBMsg_Title, null,
						Messages.bind(Messages.ProfEditDlg_ProfName_DBAdded, 
												txtName.getText()),
						MessageDialog.INFORMATION,new String[] { "Ok" },0);
						msgdlg.open();
				} else {
					profile = null;
					profile = newProfile;
					logger.debug("Update profile name= " + txtName.getText() + " to database.");
					RunDMLRequestFactory.makeUpdateRequest(profMapper, profile);
				}
				profMapper.fireTableUpdated();
				super.okPressed();
			}
		} catch (RunDMLException ex) {
			MessageDialog errMsg = new MessageDialog(new Shell(),
					Messages.ProfEditDlg_DBError_Title, null,
					Messages.bind(Messages.ProfEditDlg_DBError, ex.getCause().getMessage()),
					MessageDialog.ERROR,new String[] { "Ok" },0);
			errMsg.open();
			ex.printStackTrace();
		}
		
	}
	
	
	@Override
	public Control createDialogArea(Composite parent) {
		
		area = (Composite) super.createDialogArea(parent);
		
		container = new Composite(area,SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		layout.marginBottom = 10;
		container.setLayout(layout);
		GridData data = new GridData(SWT.FILL,SWT.FILL,false,false);
		container.setLayoutData(data);
	
		Label lblProfile = new Label(container,SWT.NONE);		
		lblProfile.setText(Messages.NameDialog_LblName);
		
		txtName = new Text(container,SWT.SINGLE);
		GridData txtData = new GridData(SWT.FILL,SWT.FILL,true,false);
		txtName.setLayoutData(txtData);
		txtName.setTextLimit(25);
		txtName.setText(profile.getName());
		
		/* Build the Table using ProfileModel */
		tblComposite = new Composite(area,SWT.NONE);
		GridData viewerData = new GridData(SWT.FILL,SWT.FILL,true,true);
		tblComposite.setLayoutData(viewerData);
		TableColumnLayout tbLayout = new TableColumnLayout();
		

		profViewer = new TableViewer(tblComposite,SWT.SINGLE |SWT.H_SCROLL | 
													SWT.V_SCROLL | SWT.FULL_SELECTION);
		profViewer.getTable().setHeaderVisible(true);
		profViewer.getTable().setLinesVisible(true);
		    
		dayViewer = new TableViewerColumn(profViewer,SWT.CENTER,0);
		tbLayout.setColumnData(dayViewer.getColumn(), new ColumnWeightData(33));
		dayViewer.getColumn().setText(Messages.NameDialog_Day);//$NON-NLS-1$
						
		TableViewerColumn categoryViewer = new TableViewerColumn(profViewer,SWT.CENTER);
		tbLayout.setColumnData(categoryViewer.getColumn(), new ColumnWeightData(33));
		categoryViewer.getColumn().setText(Messages.NameDialog_Category);
			
		TableViewerColumn prepViewer = new TableViewerColumn(profViewer,SWT.CENTER);
		tbLayout.setColumnData(prepViewer.getColumn(), new ColumnWeightData(33));
		prepViewer.getColumn().setText(Messages.NameDialog_PrepTime);

		tblComposite.setLayout(tbLayout);

		dayViewer.setLabelProvider(new ColumnLabelProvider() {
		@Override
			public String getText(Object element) {
				   
				EzMenuProfileDay model = (EzMenuProfileDay) element;
				String day = model.getDay().toString();
				return day;
			}
		});
		
		categoryViewer.setLabelProvider(new ColumnLabelProvider() {
		@Override
			public String getText(Object element) {
				   
				EzMenuProfileDay model = (EzMenuProfileDay) element;
				String category = model.getCategory().toString();
				return category;
			}
		});

		categoryViewer.setEditingSupport(new CategoryEditingSupport(profViewer));
		prepViewer.setLabelProvider(new ColumnLabelProvider() {
		@Override
			public String getText(Object element) {
				//TODO: Verify this works correctly in the UI (08/12/18)   
				EzMenuProfileDay model = (EzMenuProfileDay) element;
				String prepTime = model.getprepTime().getPrepTime();
				return prepTime;
			}
		});
		prepViewer.setEditingSupport(new PrepTimeEditingSupport(profViewer));
		
		profViewer.setContentProvider(new ArrayContentProvider());
		profile.setTableInput(profViewer);

		/* Validation Logic here, the validation is done for each character
		 * entered by user.
		 */

		validator = new TextValidator(txtName,this);
		validator.addVerifyListener();
		if (newProf == false) {
			loadProfile();
		}
		
		return area;
	}

	@Override 
	public void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		if (newProf) {
			newShell.setText(Messages.bind(Messages.NameDialog_Title,"New"));	
		} else {
			newShell.setText(Messages.bind(Messages.NameDialog_Title, "Edit"));
		}
	}
		
	@Override
	public boolean close() {
		tblComposite.dispose();
		area.dispose();
		container.dispose();
		super.close();
		return true;
	}
	
	@Override 
	public void validated(IStatus status) {
		setErrorMessage(null);
		enableOkButton();
		if (status instanceof TextValidatorStatus) {
			TextValidatorStatus txtValidStatus = (TextValidatorStatus) status;
			if (txtValidStatus.getSeverity() != IStatus.OK) {
				setErrorMessage(txtValidStatus.getMessage());
				disableOkButton();
			}
		}
		
	}	
	private void disableOkButton() {
		Button btnOk = getButton(IDialogConstants.OK_ID);
		if (btnOk != null) {
			btnOk.setEnabled(false);
		}
	}
	
	private void enableOkButton() {
		Button btnOk = getButton(IDialogConstants.OK_ID);
		if (btnOk != null) {
			btnOk.setEnabled(true);
		}
	}
	
	private void loadProfile() {
		txtName.setText(profile.getName());
		profile.setTableInput(profViewer);
	}
	
}
