 
package net.bobs.own.ezmenu.handlers.ui;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

public class ExitCommandHandler {
	@Execute
	public void execute() {

		Display.getCurrent().close();
		
	}
		
}