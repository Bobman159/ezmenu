package net.bobs.own.ezmenu.profile.editing.ui;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;

public class CategoryEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private ComboBoxCellEditor editor = null;
	
	public CategoryEditingSupport(TableViewer viewer) {

		super(viewer);
		this.viewer = viewer;
		String[] categories = (String []) EzMenuProfileDay.getCategoryConstants();
		editor = new ComboBoxCellEditor(viewer.getTable(),categories,
										SWT.READ_ONLY | SWT.DROP_DOWN);
		
		ICellEditorListener listener =
				   new ICellEditorListener() {

					@Override
					public void applyEditorValue() {
						int index = viewer.getTable().getSelectionIndex();
						@SuppressWarnings("unchecked")
						List<EzMenuProfileDay> dayModels = (List<EzMenuProfileDay>) viewer.getInput();
						EzMenuProfileDay dayModel = dayModels.get(index);
						dayModel.setCategory(Integer.valueOf(editor.getValue().toString()));
					}

					@Override
					public void cancelEditor() {
						System.out.println("*** Cancel editor notification ***");
						
					}

					@Override
					public void editorValueChanged(boolean oldValidState, boolean newValidState) {
						System.out.println("*** Editor value changed notification ***");
						
					}
				   };
				 editor.addListener(listener);
		
	}
	
	@Override
	protected CellEditor getCellEditor(Object element) {

		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {

		return true;
	}

	@Override
	protected Object getValue(Object element) {

		EzMenuProfileDay rowModel = (EzMenuProfileDay) element;
		return rowModel.getCategory().ordinal();
		
	}

	@Override
	protected void setValue(Object element, Object value) {
		EzMenuProfileDay rowModel = (EzMenuProfileDay) element;
		Integer index = (Integer) value;
		rowModel.setCategory(index.intValue());
		viewer.refresh();
		

	}

}
