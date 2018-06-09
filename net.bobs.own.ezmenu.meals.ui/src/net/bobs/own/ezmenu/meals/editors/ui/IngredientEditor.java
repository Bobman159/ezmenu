package net.bobs.own.ezmenu.meals.editors.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

public class IngredientEditor extends EditingSupport {

	TableViewer viewer = null;
	
	public IngredientEditor(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;

	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		
	}




}
