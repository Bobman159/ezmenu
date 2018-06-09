package net.bobs.own.ezmenu.validators.ui;

import org.eclipse.core.runtime.IStatus;

public interface IValidatorCallback {

	/**
	 * Callback method for TextValidator.  This method is called if specified
	 * when the text being verified has been validated ok.
	 */
	public void validated(IStatus status);
	
}