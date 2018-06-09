package net.bobs.own.ezmenu.profile.resources.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "net.bobs.own.ezmenu.profile.resources.ui.messages";
	

	public static String NameDialog_Title;
	public static String NameDialog_InitValue;
	public static String NameDialog_LblName;
	public static String NameDialog_Day;
	public static String NameDialog_PrepTime;
	public static String NameDialog_Category;
	public static String ProfileValidator_NameExists;
	public static String ProfEditDlg_DBMsg_Title;
	public static String ProfEditDlg_ProfName_DBAdded;
	public static String ProfEditDlg_ProfName_DBUpdated;
	public static String ProfEditDlg_DBError;
	public static String ProfEditDlg_DBError_Title;
	public static String DeleteProfile_ConfirmDelete_Title;
	public static String DeleteProfile_ConfirmDelete_Msg;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
}
