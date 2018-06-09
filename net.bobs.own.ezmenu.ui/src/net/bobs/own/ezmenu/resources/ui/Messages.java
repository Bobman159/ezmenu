package net.bobs.own.ezmenu.resources.ui;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "net.bobs.own.ezmenu.resources.ui.messages";
	
	public static String	NameDialog_InitValue;
	public static String	NameDialog_Description;
	public static String	NameDialog_InitMessage;
	public static String    TextValidator_InvalidChar;
	public static String    TextValidator_IsInvalid;
	public static String 	TexValidator_Title;
	public static String	EzMenuExplorer_Title;
	public static String	EzMenuExplorer_Root;
	public static String	EzMenuExplorer_MealsRoot;
	public static String	EzMenuExplorer_ProfilesRoot;
	public static String	EzMenuExplorer_DBErrror_Title;
	public static String	EzMenuExplorer_DBErrror;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	public static ResourceBundle loadBundle() {
		return ResourceBundle.getBundle(BUNDLE_NAME);
	}

}
