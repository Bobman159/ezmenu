package net.bobs.own.ezmenu.profile.providers.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ProfileLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {

		System.out.println("ProfileLabelProvider.addListener");

	}

	@Override
	public void dispose() {

		System.out.println("ProfileLabelProvider.dispose");
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {

		System.out.println("ProfileLabelProvider.isLabelProperty");
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

		System.out.println("ProfileLabelProvider.removeListener");
	}

	@Override
	public Image getImage(Object element) {

		System.out.println("ProfileLabelProvider.getImage");
		return null;
	}

	@Override
	public String getText(Object element) {

		System.out.println("ProfileLabelProvider.getText");
		return "test";
	}

}
