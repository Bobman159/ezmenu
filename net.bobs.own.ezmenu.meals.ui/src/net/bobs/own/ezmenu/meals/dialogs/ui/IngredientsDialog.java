package net.bobs.own.ezmenu.meals.dialogs.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.db.EzMenuMealIngredient;
import net.bobs.own.ezmenu.meals.resources.ui.Messages;
import net.bobs.own.ezmenu.validators.ui.IValidatorCallback;
import net.bobs.own.ezmenu.validators.ui.TextValidator;
import net.bobs.own.ezmenu.validators.ui.TextValidatorStatus;

public class IngredientsDialog extends TitleAreaDialog implements IValidatorCallback {

	private Composite area = null;
	private Composite container = null;
	private String validationString = "[a-zA-Z1-9_ ]*";
	private Text txtIngredient;
	private Combo cmbQuantity;
	private Combo cmbUnitMeasure;
	private Button btnAddAnother;

	private boolean newIngredient = false;
	private EzMenuMeal meal = null;
	private int ingredientIndex = -1;

	/**
	 * @wbp.parser.constructor
	 */
	/**
	 * Constructs an Ingredient Dialog to add new Ingredients for an
	 * <code>EzMenuMeal</code> The user is able to enter 1 or more ingredients at a
	 * time.
	 * 
	 * @param parentShell
	 *            - the parent shell for dialog
	 * @param meal
	 *            - meal being created.
	 */
	public IngredientsDialog(Shell parentShell, EzMenuMeal meal) {
		super(parentShell);
		this.meal = meal;
		this.newIngredient = true;
	}

	/**
	 * Constructs an Ingredient Dialog to edit a single ingredient for an existing
	 * meal.
	 * 
	 * @param parentShell
	 *            - the parent shell for dialog
	 * @param meal
	 *            - meal being edited.
	 * @param int
	 *            - index of Ingredient to be edited
	 */
	public IngredientsDialog(Shell parentShell, EzMenuMeal meal, int index) {
		super(parentShell);
		this.newIngredient = false;
		this.meal = meal;
		ingredientIndex = index;
	}

	@Override
	public void configureShell(Shell newShell) {
		super.configureShell(newShell);

		if (newIngredient) {
			newShell.setText(Messages.bind(Messages.IngredientDialog_Title, "New"));
		} else {
			newShell.setText(Messages.bind(Messages.IngredientDialog_Title, "Edit"));
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);

		if (newIngredient) {
			btnAddAnother = new Button(parent, SWT.CHECK);
			GridData dataAdd = new GridData(SWT.FILL, SWT.FILL, false, false);
			btnAddAnother.setLayoutData(dataAdd);
			btnAddAnother.setText(Messages.IngredientDialog_CreateAnother);
		}

		Button btnOk = super.createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		Button btnCancel = super.createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		if (newIngredient == false) {
			loadIngredient();
		}

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		area = (Composite) super.createDialogArea(parent);
		container = new Composite(area, SWT.NONE);
		GridLayout dialogLayout = new GridLayout(2, false);
		dialogLayout.marginTop = 10;
		dialogLayout.verticalSpacing = 10;
		container.setLayout(dialogLayout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label lblIngredient = new Label(container, SWT.NONE);
		lblIngredient.setText(Messages.IngredientsHdg);

		txtIngredient = new Text(container, SWT.NONE);
		txtIngredient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		TextValidator validator = new TextValidator(txtIngredient, this, validationString);
		validator.addVerifyListener();

		Label lblQuantity = new Label(container, SWT.NONE);
		lblQuantity.setText(Messages.QuantityHdg);
		cmbQuantity = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN);
		cmbQuantity.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cmbQuantity.setItems(loadQuantities());
		cmbQuantity.select(0);

		Label unitMeasure = new Label(container, SWT.NONE);
		unitMeasure.setText(Messages.UnitMeasureHdg);
		cmbUnitMeasure = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN);
		cmbUnitMeasure.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cmbUnitMeasure.setItems(loadUnitMeasures());
		cmbUnitMeasure.select(0);

		return area;
	}

	@Override
	public void okPressed() {
		// Make sure that some text was entered....
		String txtIngred = txtIngredient.getText();
		if (txtIngred.length() == 0) {
			setErrorMessage(Messages.IngredientsDialog_EmptyIngredient);
			return;
		}

		if (newIngredient) {
			// Save values entered by user
			meal.addIngredient(createIngredientFromUI());
			if (btnAddAnother != null && btnAddAnother.getSelection()) {
				txtIngredient.setText("");
				cmbQuantity.select(0);
				cmbUnitMeasure.select(0);
			}
		} else  {
			// Update the edited Ingredient with values from the user
			meal.replaceIngredient(ingredientIndex, createIngredientFromUI());
		}
		
		if ((btnAddAnother == null) || 
			(btnAddAnother != null && btnAddAnother.getSelection()) == false) {
			super.okPressed();
		}
	}

	@Override
	public boolean close() {
		area.dispose();
		
		container.dispose();
		super.close();
		return true;
	}

	@Override
	public void validated(IStatus status) {
		setErrorMessage(null);
		enableOkButton();
		if (status instanceof TextValidatorStatus) {
			TextValidatorStatus txtValidStatus = (TextValidatorStatus) status;
			if (txtValidStatus.getSeverity() != IStatus.OK) {
				setErrorMessage(txtValidStatus.getMessage());
				disableOkButton();
			}
		}
	}

	private void disableOkButton() {
		Button btnOk = getButton(IDialogConstants.OK_ID);
		btnOk.setEnabled(false);
	}

	private void enableOkButton() {
		Button btnOk = getButton(IDialogConstants.OK_ID);
		btnOk.setEnabled(true);
	}

	private String[] loadQuantities() {

		return new String[] { "1/8", "1/4", "1/3", "1/2", "2/3", "3/4", 
				"1", "1 1/4", "1 1/3", "1 1/2", "1 2/3","1 3/4", 
				"2", "2 1/4", "2 1/3", "2 1/2", "2 2/3", "2 3/4", 
				"3", "3 1/4", "3 1/3", "3 1/2", "3 2/3","3 3/4", 
				"4", "4 1/4", "4 1/3", "4 1/2", "4 2/3", "4 3/4", 
				"5", "5 1/4", "5 1/3", "5 1/2", "5 2/3","5 3/4", 
				"6", "6 1/4", "6 1/3", "6 1/2", "6 2/3", "6 3/4", 
				"7", "7 1/4", "7 1/3", "7 1/2", "7 2/3","7 3/4", 
				"8", "8 1/4", "8 1/3", "8 1/2", "8 2/3","8 3/4", 
				"9", "9 1/4", "9 1/3", "9 1/2", "9 2/3","9 3/4",  
				"10", "10 1/4", "10 1/3", "10 1/2", "10 2/3","10 3/4",
				"11", "11 1/4", "11 1/3", "11 1/2", "11 2/3","11 3/4",
				"12", "12 1/4", "12 1/3", "12 1/2", "12 2/3","12 3/4",
				"13", "13 1/4", "13 1/3", "13 1/2", "13 2/3","13 3/4",
				"14", "14 1/4", "14 1/3", "14 1/2", "14 2/3","14 3/4",
				"15", "15 1/4", "15 1/3", "15 1/2", "15 2/3","15 3/4",
				"16", "16 1/4", "16 1/3", "16 1/2", "16 2/3","16 3/4",
				};

	}

	private String[] loadUnitMeasures() {
		return new String[] { "teaspoon", "tablespoon", "pound", "ounces", "cup", "can", "slices", "package", " " };
	}

	private void loadIngredient() {
		EzMenuMealIngredient ingredient = meal.getIngredient(ingredientIndex);
		txtIngredient.setText(ingredient.getIngredient());
		int qtyIx = cmbQuantity.indexOf(ingredient.getQuantity());
		cmbQuantity.select(qtyIx);
		int unitMeasureIx = cmbUnitMeasure.indexOf(ingredient.getUnitOfMeasure());
		cmbUnitMeasure.select(unitMeasureIx);
	}
	
	/*
	 * 	Create a Meal ingredient from the UI values.
	 * 	Also initialize the status (INSERT, UPDATE or DELETE) for the ingredient
	 * 	It's done here to try and keep things current for the ignredients &
	 * 	to cenralize where the setting is done for debugging purposes.
	 */
	private EzMenuMealIngredient createIngredientFromUI() {
		
		EzMenuMealIngredient ingredient = null;
		if (newIngredient) {
			ingredient = new EzMenuMealIngredient(meal.getMealId(),
					txtIngredient.getText(), cmbQuantity.getItem(cmbQuantity.getSelectionIndex()),
					cmbUnitMeasure.getItem(cmbUnitMeasure.getSelectionIndex()));
			ingredient.markForInsert();
		} else {			
			
			ingredient = new EzMenuMealIngredient(meal.getMealId(),
					meal.getIngredient(ingredientIndex).getIngredientId(),txtIngredient.getText(), 
					cmbQuantity.getItem(cmbQuantity.getSelectionIndex()),
					cmbUnitMeasure.getItem(cmbUnitMeasure.getSelectionIndex()));
			/*
			 * IF meal is already defined in database THEN
			 * 	*	UPDATE the meal 
			 */
			if ( meal.isExistingMeal() &&
				 ingredient.isExistingIngredient()) {
				ingredient.markForUpdate();
			}	
		}
		return ingredient;

	}
}
