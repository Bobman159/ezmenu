package net.bobs.own.ezmenu.validators.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import net.bobs.own.ezmenu.resources.ui.Messages;

/**
 * A simple class that implements an SWT <code>VerifyListener</code> to verify
 * text input for a SWT <code>Text</code>. The default behavior displays error
 * messages using the Jface <MessageDialog>. Clients may alternatively specify
 * an SWT <code>Label</code> widget to display message text. Messages and
 * decorators are also used by this class, so sufficient margin should be added
 * for the control used by this class. CLients only need to instantiate
 * <code>TextValidator</code>, the instance of this class is added as a listener
 * automatically.
 * 
 * @author Robert Anderson
 *
 */
public class TextValidator implements VerifyListener {

	private Text txtControl = null;
	private String validationString = "[a-zA-Z0-9_\\u0000]";
	IValidatorCallback callback = null;
	TextValidatorStatus status = new TextValidatorStatus();

	public TextValidator(Text control, IValidatorCallback callback) {
		this.txtControl = control;
		this.callback = callback;
	}

	public TextValidator(Text control, IValidatorCallback callback, String validationString) {
		this.txtControl = control;
		if (validationString == null) {
			throw new IllegalArgumentException("Validation string can not be null");
		}
		this.validationString = validationString;
		this.callback = callback;
	}

	/**
	 * Binds the Text widget in this this control to a <code>VerifyListener</code>
	 * for that control.
	 */
	public void addVerifyListener() {

		txtControl.addVerifyListener(this);

	}

	@Override
	public void verifyText(VerifyEvent event) {
		event.doit = false;

		//	Could make this validation asynchronous - not on the UI thread, but for now I will leave  it 
		//  alone. The performance does not seem to be impacted.
		char verifyChar = event.character;
		String verify = String.valueOf(verifyChar);
		String profName = txtControl.getText();
		status.setSeverity(IStatus.OK);

		if (verify.matches(validationString)) {
			event.doit = true;
			profName = profName + verifyChar;
		} else if ((verifyChar == '\u0008') || (verifyChar == '\u007F') || (verifyChar == '\u0000')) {
			event.doit = true;
			if (profName.length() > 0) {
				profName = profName.substring(0, profName.length() - 1);
			}
		} else {
			event.doit = false;
			String msg = Messages.TextValidator_InvalidChar + "\'" + verifyChar + "\' "
					+ Messages.TextValidator_IsInvalid;
			status.setSeverity(IStatus.ERROR);
			status.setMessage(msg);
		}
		callback.validated(status);
	}

}
