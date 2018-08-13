package net.bobs.own.ezmenu.profile.editing.ui;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;

import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.PrepTimes;

public class PrepTimeEditingSupport extends EditingSupport {

	private	TableViewer viewer;
	private ComboBoxCellEditor editor;
	
	public PrepTimeEditingSupport(TableViewer viewer) {
		
		super(viewer);
		this.viewer = viewer;
		String[] prepTimes = EzMenuProfileDay.getPrepTimeConstants();
		editor = new ComboBoxCellEditor(viewer.getTable(),prepTimes, 
										ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
		
		ICellEditorListener listener = new ICellEditorListener() {

					@Override
					//TODO: Verify this works correctly in the UI (08/12/18)
					public void applyEditorValue() {
						int index = viewer.getTable().getSelectionIndex();
						List<EzMenuProfileDay> dayModels = (List<EzMenuProfileDay>) viewer.getInput();
						EzMenuProfileDay dayModel = dayModels.get(index);
//						dayModel.setPrepTime(Integer.valueOf(editor.getValue().toString()));
                  dayModel.setPrepTime(PrepTimes.toPrepTime(editor.getValue().toString()));
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

	//TODO: Verify this works successfully in UI
	@Override
	protected Object getValue(Object element) {
		EzMenuProfileDay rowModel = (EzMenuProfileDay) element;
//		return rowModel.indexOfPrepTime(rowModel.getprepTime());
		return rowModel.getprepTime().getPrepTime();
	}

	//TODO: Verify this works successfully in UI
	@Override
	protected void setValue(Object element, Object value) {

		EzMenuProfileDay rowModel = (EzMenuProfileDay) element;
//		Integer index = (Integer) value;
//		rowModel.setPrepTime(index);
		String prepTime = (String) value;
		rowModel.setPrepTime(PrepTimes.toPrepTime(prepTime));
		viewer.refresh();

	}

}
