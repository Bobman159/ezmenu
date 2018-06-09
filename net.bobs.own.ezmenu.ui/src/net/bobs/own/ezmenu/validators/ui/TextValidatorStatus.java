package net.bobs.own.ezmenu.validators.ui;

import org.eclipse.core.runtime.IStatus;

public class TextValidatorStatus implements IStatus {

	int severity = IStatus.OK;
	String message = null;
	
	@Override
	public IStatus[] getChildren() {
		return null;
	}
	
	@Override
	public int getCode() {

		return 0;
	}

	@Override
	public Throwable getException() {
		return null;
	}
	
	public void setMessage(String msg) {
		this.message = msg;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getPlugin() {
		return null;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}
	
	@Override
	public int getSeverity() {
		return severity;
	}

	@Override
	public boolean isMultiStatus() {
		return false;
	}

	@Override
	public boolean isOK() {
		if (severity == IStatus.OK) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean matches(int severityMask) {
		return false;
	}

}
