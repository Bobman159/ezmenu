package net.bobs.own.ezmenu.meals.editors.ui;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.ezmenu.constants.ui.EzMenuConstants;
import net.bobs.own.ezmenu.meals.ListIngredients;
import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.db.EzMenuMealIngredient;
import net.bobs.own.ezmenu.meals.db.EzMenuMealMapper;
import net.bobs.own.ezmenu.meals.dialogs.ui.IngredientsDialog;
import net.bobs.own.ezmenu.meals.resources.ui.Messages;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;
import net.bobs.own.ezmenu.validators.ui.IValidatorCallback;
import net.bobs.own.ezmenu.validators.ui.TextValidator;
import net.bobs.own.ezmenu.validators.ui.TextValidatorStatus;

@SuppressWarnings("restriction")
  public class MealEditor implements IValidatorCallback {

	private EzMenuMeal meal = null;
	private Composite formComposite = null;
	private Composite mealComposite = null;
	private Composite directionComposite = null;
	private Composite ingredientComposite = null;
	private TableViewer ingredViewer = null;
	private StyledText stxtDirections = null;
	private final int NAME_DBLIMIT = 48;
	private final String MEAL_NAME_DEFAULT = "meal_name";
	private Logger logger = LogManager.getLogger(MealEditor.class.getName());

	@Inject
	EPartService partService = null;
	@Inject
	EModelService modelService;
	@Inject
	ESelectionService selectionService;
	@Inject
	IEclipseContext context;
	@Inject
	MDirtyable dirty;

	private String validationString = "[a-zA-Z0-9_& ]";

	private boolean bulletActive = false;

	// TODO: Attribute the icons used by this editor in Help
	// http://eclipse-icons.i24.cc/index.html
	// editor from http://eclipse-icons.i24.cc/eclipse-icons-09.html

	// add_obj, delete_obj, and save_edit from
	// http://eclipse-icons.i24.cc/eclipse-icons-11.html
	private final Image ICON_ADD = new Image(Display.getCurrent(),
			MealEditor.class.getResourceAsStream("/icons/add_obj.gif"));
	private final Image ICON_DELETE = new Image(Display.getCurrent(),
			MealEditor.class.getResourceAsStream("/icons/delete_obj.gif"));
	private final Image ICON_EDIT = new Image(Display.getCurrent(),
			MealEditor.class.getResourceAsStream("/icons/editor.gif"));
	private final Image ICON_SAVE = new Image(Display.getCurrent(),
			MealEditor.class.getResourceAsStream("/icons/save_edit.gif"));

	// edit_list.png from => http://p.yusukekamiyamane.com/icons/attribution/
	private final Image ICON_LIST = new Image(Display.getCurrent(),
			MealEditor.class.getResourceAsStream("/icons/edit-list.png"));
	private Text txtName;
	private Combo cmbCategory;
	private Combo cmbPrepTime;

	public MealEditor() {
		meal = new EzMenuMeal(MEAL_NAME_DEFAULT, "Beef", "0-15");
	}

	public void setInput(EzMenuMeal input) {
		meal = input;
		loadMeal();
	}

	public void addIngredient() {

		IngredientsDialog dialog = new IngredientsDialog(new Shell(), meal);
		if (dialog.open() == TitleAreaDialog.OK) {
			ingredViewer.refresh();
			dirty.setDirty(true);
		}
		dialog.close();

	}

	public void editIngredient() {
		int index = ingredViewer.getTable().getSelectionIndex();
		IngredientsDialog dialog = new IngredientsDialog(new Shell(), meal, index);
		if (dialog.open() == Window.OK) {
			ingredViewer.refresh();
			dirty.setDirty(true);
		}
	}

	public void deleteIngredient() {
		
		boolean isExisting = false;
		int index = ingredViewer.getTable().getSelectionIndex();
		EzMenuMealIngredient ingredient = meal.getIngredient(index);
		MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText(Messages.MealEditor_DeleteIngredient_Title);
		/*	IF the ingredient is already in the database THEN 
		 * 		*	mark it for delete
		 * 		*	keep it in the list so that it will be processed by the update...
		 * 	ELSE 
		 * 		* remove the ingredient from the list
		 * 
		 * 	In both cases confirm the action with the user.
		 */
		isExisting = ingredient.isExistingIngredient();
		if (isExisting) {
			messageBox.setMessage(Messages.bind(Messages.MealEditor_ConfirmDelete_Ingredient, ingredient.getIngredient(),
					"database"));
		} else {
			messageBox.setMessage(Messages.bind(Messages.MealEditor_ConfirmDelete_Ingredient, ingredient.getIngredient(),
					"table"));
		}
		if (messageBox.open() == SWT.YES) {
			if (isExisting) {
				ingredient.markForDelete();
			} else {
				meal.deleteIngredient(index);
			}
			
			ingredViewer.refresh();
			dirty.setDirty(true);
		}
	}

	@PostConstruct
	public void createPartControl(Composite parent, EMenuService menuService) {

		// This is the "uber" composite & layout, it contains both meal & ingredients
		// composites
		formComposite = parent;
		GridLayout formLayout = new GridLayout(1, false);
		formLayout.marginTop = 5;
		formComposite.setLayout(formLayout);

		// Start meals section (Heading & Toolbar)
		FormToolkit toolkit = new FormToolkit(formComposite.getDisplay());
		Section mealSection = toolkit.createSection(formComposite, Section.TITLE_BAR);
		GridData sectionData = new GridData(SWT.FILL, SWT.FILL, true, false);
		mealSection.setLayoutData(sectionData);
		mealSection.setText(Messages.MealEditor_Mealhdg);
		ToolBar tbMeals = new ToolBar(mealSection, SWT.FLAT | SWT.PUSH);
		ToolItem tiMealSave = new ToolItem(tbMeals, SWT.PUSH | SWT.FLAT);
		tiMealSave.setImage(ICON_SAVE);
		tiMealSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				execE4Command("net.bobs.own.ezmenu.filesave.command");
			}
		});
		mealSection.setTextClient(tbMeals);

		// Meal Name Widgets
		mealComposite = new Composite(parent, SWT.NONE);
		GridLayout mealLayout = new GridLayout(4, false);
		mealLayout.marginBottom = 5;
		GridData mealData = new GridData(SWT.FILL, SWT.FILL, true, false);
		mealComposite.setLayout(mealLayout);
		mealComposite.setLayoutData(mealData);

		Label lblName = new Label(mealComposite, SWT.NONE);
		lblName.setText(Messages.MealEditor_LblName);
		txtName = new Text(mealComposite, SWT.NONE);
		GridData nameData = new GridData(SWT.FILL, SWT.FILL, true, false);
		nameData.horizontalSpan = 3;
		txtName.setLayoutData(nameData);
		txtName.setTextLimit(NAME_DBLIMIT);
		TextValidator validator = new TextValidator(txtName, this, validationString);
		validator.addVerifyListener();

		// Category & Preparation Time Widgets
		Label lblCategory = new Label(mealComposite, SWT.NONE);
		lblCategory.setText(Messages.MealEditor_LblCategory);
		GridData catgyData = new GridData(SWT.FILL, SWT.FILL, false, false);
		lblCategory.setLayoutData(catgyData);

		cmbCategory = new Combo(mealComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbCategory.setItems(EzMenuProfileDay.getCategoryConstants());
		cmbCategory.select(0);
		GridData catgyData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		cmbCategory.setLayoutData(catgyData2);
		cmbCategory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				dirty.setDirty(true);
			}
		});

		Label lblPrepTime = new Label(mealComposite, SWT.NONE);
		lblPrepTime.setText(Messages.MealEditor_LblPrepTime);
		GridData prepData = new GridData(SWT.CENTER, SWT.FILL, false, false);
		lblPrepTime.setLayoutData(prepData);

		cmbPrepTime = new Combo(mealComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbPrepTime.setItems(EzMenuProfileDay.getPrepTimeConstants());
		cmbPrepTime.select(0);
		GridData prepData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		cmbPrepTime.setLayoutData(prepData2);
		cmbPrepTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				dirty.setDirty(true);
			}
		});

		// Start Ingredients section (Heading & Toolbar)
		Section ingredSection = toolkit.createSection(formComposite, Section.TITLE_BAR);
		GridData ingredSectData = new GridData(SWT.FILL, SWT.FILL, true, false);
		ingredSection.setLayoutData(ingredSectData);
		ingredSection.setText(Messages.IngredientsHdg);
		ToolBar tbIngred = new ToolBar(ingredSection, SWT.FLAT | SWT.PUSH);
		ToolItem tiIngredAdd = new ToolItem(tbIngred, SWT.PUSH | SWT.FLAT);
		tiIngredAdd.setImage(ICON_ADD);
		tiIngredAdd.addSelectionListener(new SelectionAdapter() {
			@Override()
			public void widgetSelected(SelectionEvent event) {
				execE4Command("net.bobs.own.ezmenu.command.addingredient");
			}
		});

		ToolItem tiIngredEdit = new ToolItem(tbIngred, SWT.PUSH | SWT.FLAT);
		tiIngredEdit.setImage(ICON_EDIT);
		tiIngredEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isValidSelection()) {
					execE4Command("net.bobs.own.ezmenu.command.editingredient");
				}
			}
		});

		ToolItem tiIngredDelete = new ToolItem(tbIngred, SWT.PUSH | SWT.FLAT);
		tiIngredDelete.setImage(ICON_DELETE);
		tiIngredDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isValidSelection()) {
					execE4Command("net.bobs.own.ezmenu.command.deleteingredient");
				}
			}
		});
		ingredSection.setTextClient(tbIngred);

		// Ingredients Table Composite
		ingredientComposite = new Composite(formComposite, SWT.NONE);
		GridData ingredData = new GridData(SWT.FILL, SWT.FILL, true, true);
		ingredientComposite.setLayoutData(ingredData);

		TableColumnLayout tableLayout = new TableColumnLayout();

		// Ingredients Table & Columns
		ingredViewer = new TableViewer(ingredientComposite, SWT.FULL_SELECTION | SWT.MULTI);
		ingredViewer.getTable().setHeaderVisible(true);
		ingredViewer.getTable().setLinesVisible(true);

		// Ingredient Column
		TableViewerColumn ingredientViewerCol = new TableViewerColumn(ingredViewer, SWT.NONE);
		ingredientViewerCol.getColumn().setText(Messages.IngredientsHdg);
		tableLayout.setColumnData(ingredientViewerCol.getColumn(), new ColumnWeightData(40));
		ingredientViewerCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String text;
				EzMenuMealIngredient ingredient = (EzMenuMealIngredient) element;
				text = ingredient.isMarkedDelete() ?  null : ingredient.getIngredient() ;
				return text;
			}
		});

		// Quantity Column
		TableViewerColumn quantityViewerCol = new TableViewerColumn(ingredViewer, SWT.NONE);
		quantityViewerCol.getColumn().setText(Messages.QuantityHdg);
		tableLayout.setColumnData(quantityViewerCol.getColumn(), new ColumnWeightData(33));
		quantityViewerCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String text;
				EzMenuMealIngredient ingredient = (EzMenuMealIngredient) element;
				text = ingredient.isMarkedDelete() ? null : ingredient.getQuantity();
				return text;
			}
		});

		// Unit of Measure Column
		TableViewerColumn unitMeasureViewerCol = new TableViewerColumn(ingredViewer, SWT.NONE);
		unitMeasureViewerCol.getColumn().setText(Messages.UnitMeasureHdg);
		tableLayout.setColumnData(unitMeasureViewerCol.getColumn(), new ColumnWeightData(33));
		unitMeasureViewerCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String text;
				EzMenuMealIngredient ingredient = (EzMenuMealIngredient) element;
				text = ingredient.isMarkedDelete() ? null : ingredient.getUnitOfMeasure();
				return text;
			}
		});

		ingredViewer.setContentProvider(new ArrayContentProvider());
		if (meal.getIngredients() != null) {
			ingredViewer.setInput(meal.getIngredients());
		}

		ingredientComposite.setLayout(tableLayout);

		// Directions Section
		Section directionSection = toolkit.createSection(formComposite, Section.TITLE_BAR);
		GridData directionSectionData = new GridData(SWT.FILL, SWT.FILL, true, false);
		directionSection.setLayoutData(directionSectionData);
		directionSection.setText(Messages.MealEditor_DirectionsHdg);
		ToolBar tbDirection = new ToolBar(directionSection, SWT.RIGHT | SWT.FLAT | SWT.PUSH);
		ToolItem tiEditList = new ToolItem(tbDirection, SWT.FLAT | SWT.CHECK);
		tiEditList.setImage(ICON_LIST);

		directionSection.setTextClient(tbDirection);

		directionComposite = new Composite(formComposite, SWT.NONE);
		GridLayout directionLayout = new GridLayout(1, false);
		GridData directionData = new GridData(SWT.FILL, SWT.FILL, true, true);
		directionComposite.setLayoutData(directionData);

		stxtDirections = new StyledText(directionComposite, SWT.WRAP | SWT.FULL_SELECTION | SWT.MULTI);
		stxtDirections.setTextLimit(EzMenuConstants.MAX_DIRECTIONS_LENGTH);
		GridData stxtData = new GridData(SWT.FILL, SWT.FILL, true, true);
		stxtDirections.setLayoutData(stxtData);
		directionComposite.setLayout(directionLayout);

		stxtDirections.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
			
				if (!dirty.isDirty()) {
					dirty.setDirty(true);
				}
			}
		});

		// Add a key listener so when <ENTER> (CR) is pressed,
		// the style from the previous line is used for the new line
		stxtDirections.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.CR) {
					Point selection = stxtDirections.getSelectionRange();
					// ASSUME: This is only one line @ at a time
					int currLine = stxtDirections.getLineAtOffset(selection.x);
					if (currLine > 0) {
						currLine -= 1;
					}
					if (lineHasBullet(currLine) == true) {
						addBulletStyle();
					}
				}

			}

			public void keyReleased(KeyEvent event) {

			}
		});

		// Add a caret listener to toggle the edit list toolitem for
		// bullet points.
		stxtDirections.addCaretListener(new CaretListener() {
			public void caretMoved(CaretEvent event) {
				if (lineHasBullet(event.caretOffset) == true) {
					tiEditList.setSelection(true);
				} else {
					tiEditList.setSelection(false);
				}
			}
		});

		// Setup ToolItem Selection listener for on/off of Bullet Points
		tiEditList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				if (bulletActive) {
					bulletActive = false;
					removeBulletStyle();
				} else {
					bulletActive = true;
					addBulletStyle();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (bulletActive) {
					bulletActive = false;
				} else {
					bulletActive = true;
				}
			}

		});

	}

	@Persist
	public void doSave() {
		
		EzMenuMeal oldMeal = null;
		EzMenuMealMapper mealMapper = EzMenuMealMapper.getMapper();
		
		/*
		 * TextValidator will not allow invalid characters for the meal name SO ASSUME
		 * meal name is valid.
		 */
		
		//Make sure the user didn't forget a meal name
		if (txtName.getText().length() == 0) {
			MessageDialog errMsg = new MessageDialog(new Shell(), Messages.MealEditor_Warn_Title, null,
					Messages.MealEditor_EmptyMealName, MessageDialog.WARNING,
					new String[] { "Ok" }, 0);
			errMsg.open();
			txtName.setFocus();
			return;
		}
		
		if (dirty.isDirty()) {
			try {
				//
				if (meal.isExistingMeal() == false) {
					oldMeal = meal;

					//Persist Bullet Point formatting from the StyledText widget for the database.
					StringBuilder fmtDirections = new StringBuilder();
					for (int x = 0; x < stxtDirections.getLineCount();x++) {
						String line = stxtDirections.getLine(x);
						int offset = stxtDirections.getOffsetAtLine(x);
						if (lineHasBullet(offset)) {
							fmtDirections.append("\u2022").append(line).append("\r\n");
						} else {
							fmtDirections.append(line).append("\r\n");
						}
					}
					
					this.meal = new EzMenuMeal(txtName.getText(),cmbCategory.getItem(cmbCategory.getSelectionIndex()),
							cmbPrepTime.getItem(cmbCategory.getSelectionIndex()),
							new ListIngredients(oldMeal.getIngredients()),fmtDirections.toString() );
					mealMapper.insert(meal);
					//Trusting that this is enough for Garbage Collection...
					oldMeal = null;
				} else {
					oldMeal = meal;
					//Tested Add Ingredient, Update Ingredient & Delete Ingredient for existing meal 
					//looks good 11/16/2017
					this.meal = new EzMenuMeal(oldMeal.getMealId(), txtName.getText(),
							cmbCategory.getItem(cmbCategory.getSelectionIndex()), 
							cmbPrepTime.getItem(cmbCategory.getSelectionIndex()),
							new ListIngredients(oldMeal.getIngredients()), stxtDirections.getText());
					mealMapper.update(meal);
				}
				dirty.setDirty(false);
				
				//Refresh EzMenuExplorer tree 
				/*
				 *	This code currently updates ALL MEALS in the MEALS section of the explorer.  Since the user 
				 * 	can update a Meal Name and add, delete, insert ingredients for an EXISTING meal the safe 
				 * 	play is to refresh the entire meals list.  This is especially so since the meals list is returned 
				 * 	in alphabetical order and updating the meal name could cause the meal_name_a to become zzzzzz and 
				 * 	thus be at the end of the list
				 */
				
				mealMapper.fireTableUpdated();
								
				/*
				 *	This code currently only updates the MEALS section of the explorer, profiles will also need to be
				 * 	updated as well.
				 *
				 */

			} catch (RunDMLException hdbex) {
				MessageDialog errMsg = new MessageDialog(new Shell(), Messages.MealEditor_DBError_Title, null,
						Messages.bind(Messages.MealEditor_DBError, 
									  hdbex.getCause().getMessage()), MessageDialog.ERROR,
						new String[] { "Ok" }, 0);
				errMsg.open();
				logger.error(hdbex.getMessage(),hdbex);
			}
			logger.debug("Persist EzMenuMeal");
		}
	}
	
	@Override
	public void validated(IStatus status) {
		if (status instanceof TextValidatorStatus) {
			TextValidatorStatus txtStatus = (TextValidatorStatus) status;
			if (txtStatus.getSeverity() != TextValidatorStatus.OK) {
				//TODO: Change to use ExceptionMessageDialogUtility class to display error message?
				//Modify other places to do the same?
				MessageDialog errMsg = new MessageDialog(new Shell(), Messages.MealEditor_InvalidChar_Title, null,
						txtStatus.getMessage(), MessageDialog.ERROR, new String[] { "Ok" }, 0);
				errMsg.open();
			} else {
				dirty.setDirty(true);
			}
		}
	}
	
	private void loadMeal() {
		
		if (!(meal.getMealName().equals(MEAL_NAME_DEFAULT))) {
			txtName.setText(meal.getMealName());			
			int cmbIx = cmbCategory.indexOf(meal.getMealCatgy());
			cmbCategory.select(cmbIx);
			int prepIx = cmbPrepTime.indexOf(meal.getMealPrepTime());
			cmbPrepTime.select(prepIx);
			stxtDirections.setText(meal.getMealDirections());
			ingredViewer.setInput(meal.getIngredients());
		}
		dirty.setDirty(false);

	}
	
	@PreDestroy
	private void dispose() {
		mealComposite.dispose();
		directionComposite.dispose();
		ingredientComposite.dispose();
		formComposite.dispose();
	}



	private boolean lineHasBullet(int offset) {
		boolean hasBullet = false;

		int line = stxtDirections.getLineAtOffset(offset);
		Bullet bullet = stxtDirections.getLineBullet(line);
		if (bullet != null) {
			hasBullet = true;
		}

		return hasBullet;
	}

	private void addBulletStyle() {

		/* Define the bullet point to be added */
		StyleRange style = new StyleRange();
		style.metrics = new GlyphMetrics(0, 0, 40);
		Bullet bullet = new Bullet(style);

		/*
		 * Use getSelectionRange to handle no selection or multiple row selection of
		 * text conditions selection.x = start offset selection.y = length, 0 if one
		 * row, otherwise
		 */

		Point selection = stxtDirections.getSelectionRange();
		int startLine = stxtDirections.getLineAtOffset(selection.x);
		int lastLine = stxtDirections.getLineAtOffset(selection.y);
		int lineCount = (lastLine - startLine) + 1;
		
		String text = stxtDirections.getLine(startLine);
		
		/*
		 * If the line count is negative, force selection of 1 line this handles logic
		 * for only 1 line of selection data.
		 */
		if (lineCount <= 0) {
			lineCount = 1;
		}

		stxtDirections.setLineBullet(startLine, lineCount, bullet);

	}

	private void removeBulletStyle() {

		Point selection = stxtDirections.getSelectionRange();
		int startLine = stxtDirections.getLineAtOffset(selection.x);
		int lastLine = stxtDirections.getLineAtOffset(selection.y);
		int lineCount = (lastLine - startLine) + 1;
		if (lineCount <= 0) {
			lineCount = 1;
		}

		/*
		 * Use getSelectionRange to handle no selection or multiple row selection of
		 * text conditions selection.x = start offset selection.y = length, 0 if one
		 * row, otherwise
		 */
		stxtDirections.setLineBullet(startLine, lineCount, null);

	}

	private void execE4Command(String cmdID) {

		ECommandService commandService = context.get(ECommandService.class);
		Command cmd = commandService.getCommand(cmdID);
		EHandlerService handlerService = context.get(EHandlerService.class);
		ParameterizedCommand parmCmd = new ParameterizedCommand(cmd, null);
		handlerService.executeHandler(parmCmd);

	}
	
	private boolean isValidSelection() {
		boolean valid = false;
		
		int selectIx = ingredViewer.getTable().getSelectionIndex();
		if (selectIx != -1) {
			valid = true;
		} else {
			MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING| SWT.OK);
			messageBox.setText(Messages.MealEditor_NoSelection_Title);	
			messageBox.setMessage(Messages.MealEditor_NoSelection_Msg);
			messageBox.open();
		}
		
		return valid;
		
	}

}
