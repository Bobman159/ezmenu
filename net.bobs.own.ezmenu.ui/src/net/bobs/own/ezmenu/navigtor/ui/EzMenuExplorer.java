package net.bobs.own.ezmenu.navigtor.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import net.bobs.own.db.h2.pool.H2ConnectionPoolFactory;
import net.bobs.own.db.h2.pool.H2Database;
import net.bobs.own.db.h2.pool.IH2ConnectionPool;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.constants.ui.EzMenuConstants;
import net.bobs.own.ezmenu.jobs.ui.SelectMealsJob;
import net.bobs.own.ezmenu.jobs.ui.SelectProfilesJob;
import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.db.EzMenuMealIngredient;
import net.bobs.own.ezmenu.meals.db.EzMenuMealMapper;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;
import net.bobs.own.ezmenu.resources.ui.Messages;
import net.bobs.own.ezmenu.rundml.listener.ITableUpdatedListener;

public class EzMenuExplorer {
	@Inject
	MPart part;
	@Inject
	EPartService partService;
	@Inject
	UISynchronize sync;
	@Inject
	EMenuService menuService;
	@Inject
	EModelService modelService;
	@Inject
	ESelectionService selectionService;	
	
	private 	Color 		backgrnd = null;
	private 	Composite 	explorerComp;
	private		Tree		tree;
	private 	Logger 		logger = LogManager.getLogger(EzMenuExplorer.class.getName());
	H2Database 	EzMenuDB = null;
	
	// TODO: Attribute the icons used by this editor in Help


	//	The icons should be link to per website:
	//	https://icons8.com/icon/39865/restaurant-menu
	private final Image ICON_MENU = new Image(Display.getCurrent(),
			EzMenuExplorer.class.getResourceAsStream("/icons/restaurantmenu-18.png"));
	
	// <a href="https://icons8.com/icon/13289/Meal">Meal icon credits</a>
	private final Image ICON_MEAL = new Image(Display.getCurrent(),
			EzMenuExplorer.class.getResourceAsStream("/icons/icons-meal-18.png"));
	
	//Attribute in help: <a href="https://icons8.com/icon/12898/Ingredients">Ingredients icon credits</a>
	private final Image ICON_INGREDIENT = new Image(Display.getCurrent(),
			EzMenuExplorer.class.getResourceAsStream("/icons/ingredients-18.png"));
	
	//Attribute in help: https://icons8.com/icon/45879/food
	private final Image ICON_FOOD = new Image(Display.getCurrent(),
			EzMenuExplorer.class.getResourceAsStream("/icons/food-18.png"));
	private TreeItem mealsRoot;
	private TreeItem profsRoot;
	private MMenu menuMeal;
	private MMenu menuProf;
	private EzMenuMealMapper mealMapper = null;
	private EzMenuProfileMapper profMapper = null;

	public EzMenuExplorer() {
		H2ConnectionPoolFactory factory = H2ConnectionPoolFactory.getInstance();
		IH2ConnectionPool pool = factory.findPool(EzMenuConstants.POOLID);
		if (pool == null) {
				pool = H2ConnectionPoolFactory.getInstance().makePool(H2ConnectionPoolFactory.PoolTypes.MYOWN,
						EzMenuConstants.POOLID,EzMenuConstants.EZMENU_MYOWN_CONFIG);		
				EzMenuDB = new H2Database(pool);
		}
		logger.debug("Mappers created");
		mealMapper = EzMenuMealMapper.makeMapper(EzMenuDB);
		profMapper = EzMenuProfileMapper.makeMapper(EzMenuDB);

	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@PostConstruct
	public void createPartControl(Composite parent) {
		
		//TODO: Create Splash Screen or Progress Screen while loading from database?
		explorerComp = parent;
		GridLayout gridLayout = new GridLayout(1,true);
		GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,true);
		explorerComp.setLayoutData(gridData);
		explorerComp.setLayout(gridLayout);
		
		Display display = Display.getCurrent();
		backgrnd = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		explorerComp.setBackground(backgrnd);
	
		//Creates the EzMenuExplorer Tree which creates and runs the SelectMealsJob & SelectProfilesJob
		createTree(); 
		/*
		 * 	Define the Popup menus used in the Explorer. The commands Open & Delete are the same for both 
		 * 	menus.  I chose to create separate menus since the object type (Meal & Profile) they act on are 
		 * 	different. 
		 */
		menuService.registerContextMenu(tree,EzMenuConstants.MEALS_POPUP_MENU_ID);
		List<MMenu> listMealsMenus = modelService.findElements(part, EzMenuConstants.MEALS_POPUP_MENU_ID,
				MMenu.class, Collections.emptyList(),EModelService.IN_PART);
		menuMeal = listMealsMenus.get(0);
		
		menuService.registerContextMenu(tree,EzMenuConstants.PROFILES_POPUP_MENU_ID);
		List<MMenu> listProfilesMenus = modelService.findElements(part, EzMenuConstants.PROFILES_POPUP_MENU_ID,
				MMenu.class, Collections.emptyList(),EModelService.IN_PART);
		menuProf = listProfilesMenus.get(0);
		
		/*
		 * Define a mouse listener so I can determine the TreeItem selected and set the appropriate menu 
		 * visible and invisible.
		 */
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				if (event.button == 3) {
					TreeItem[] selectedItems = tree.getSelection();
					/* Setting active selection based on 
					 * https://www.eclipse.org/forums/index.php/t/506326/
					 * 
					 * The active selection will be used by the handlers to determine the active TreeItem node
					 */
					if (selectedItems.length == 1) {	//was >= 1
						TreeItem selectedItem = selectedItems[0];
						TreeItem parent = selectedItem.getParentItem();
						menuMeal.setVisible(false);
						menuProf.setVisible(false);
						//Set the Active Selection  
						if (parent != null) {
							if (parent.getText().equals("Meals")) {
								menuMeal.setVisible(true);
								List<ITable> mealsList = (List <ITable>) mealsRoot.getData();
								int selIx = mealsRoot.indexOf(selectedItem);
								selectedItem.setData(mealsList.get(selIx));
								selectionService.setSelection(selectedItem);
							} else if (parent.getText().equals("Profiles")) {
								logger.debug("Profiles item selected");
								menuProf.setVisible(true);
								List<ITable> profsList = (List <ITable>) profsRoot.getData();
								int selIx = profsRoot.indexOf(selectedItem);
								selectedItem.setData(profsList.get(selIx));
								selectionService.setSelection(selectedItem);
							}
						}
					}
				}
			}
		});
//      EzMenuMealMapper mealMapper = EzMenuProfileMapper.getMapper();
		mealMapper.addTableUpdatedListener(new ITableUpdatedListener() {
			@Override
			public void tableUpdated() {
				loadMeals();
			}
		});

//		EzMenuProfileMapper profMapper = EzMenuProfileMapper.getMapper();
		profMapper.addTableUpdatedListener(new ITableUpdatedListener() {
			@Override
			public void tableUpdated() {
				loadProfiles();
			}
		});

		part.setLabel(Messages.EzMenuExplorer_Title);
		partService.showPart(part,PartState.ACTIVATE);
		
	}
	
	
	private void createTree() {
		
		GridLayout treeLayout = new GridLayout(1,true);
		GridData treeData = new GridData(SWT.FILL,SWT.FILL,true,true);
		explorerComp.setLayoutData(treeData);
		explorerComp.setLayout(treeLayout);
		
		//Define the Tree
		tree = new Tree(explorerComp,SWT.VIRTUAL | SWT.SINGLE | SWT.FULL_SELECTION);
		TreeItem root = new TreeItem(tree,SWT.CENTER);
		//Add Meals & Profiles as items underneath the root
		root.setText(Messages.EzMenuExplorer_Root);
		root.setImage(ICON_MENU);
		tree.setLayoutData(treeData);
		mealsRoot = new TreeItem(root,SWT.NONE);
		mealsRoot.setText(Messages.EzMenuExplorer_MealsRoot);
		loadMeals();
		
		profsRoot = new TreeItem(root,SWT.NONE);
		profsRoot.setText(Messages.EzMenuExplorer_ProfilesRoot);
		loadProfiles();
		
		/*	Since tree uses SWT.VIRTUAL, use the SetData listener to add entries for meals and profiles as scrolling
		 *	through the list.  Used http://www.eclipse.org/articles/Article-SWT-Virtual/Virtual-in-SWT.html 
		 *	as the basis for this code.  It looks like ALL the meals and profiles are read from the database and the 
		 *	tree uses the array list to index the entry it needs to add to the tree.
		 */
		tree.addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
				//Used http://www.eclipse.org/articles/Article-SWT-Virtual/Virtual-in-SWT.html as the basis for this 
				//handlEvent code
				TreeItem item = (TreeItem) event.item;
				TreeItem parent = item.getParentItem();
				if (parent == null) {
					;
				} else {
					if (parent.getText().equals("Meals")) {
						List<ITable> mealsList = (List<ITable>) mealsRoot.getData();
						EzMenuMeal meal = (EzMenuMeal) mealsList.get(event.index);
						item.setText(meal.getMealName());
						TreeItem ingredItem = new TreeItem(item,SWT.NONE);
						ingredItem.setText(" ");
					} else if (parent.getText().equals("Profiles")) {
						List<ITable> profsList = (List<ITable>) profsRoot.getData();
						EzMenuProfile profile = (EzMenuProfile) profsList.get(event.index);
						item.setText(profile.getName());
					}
				}
			}
		});

		tree.addListener(SWT.Expand, new Listener() {
			@Override 
			public void handleEvent(Event event) {
				TreeItem mealItem = (TreeItem) event.item;
				TreeItem parent = mealItem.getParentItem();
				if ((parent != null)  &&
					(parent.getText().equals("Meals"))) {						
					int index = mealsRoot.indexOf(mealItem);
					List<ITable> mealsList = (List<ITable>) mealsRoot.getData();
					EzMenuMeal meal = (EzMenuMeal) mealsList.get(index);
					mealItem.setText(meal.getMealName());
					
					ArrayList<EzMenuMealIngredient> listIngredients = meal.getIngredients();
					mealItem.setData(listIngredients);
					for (int ingredIx = 0; ingredIx < listIngredients.size(); ingredIx++) {
						if (ingredIx == 0) {
							TreeItem test = mealItem.getItem(0);
							test.setText( ((EzMenuMealIngredient)listIngredients.get(0)).getIngredient());
						} else {
							TreeItem ingredientItem = new TreeItem(mealItem,SWT.NONE);
							ingredientItem.setText(((EzMenuMealIngredient)listIngredients.get(ingredIx)).getIngredient());
						}
					}
				}
			}
		});
		
		/*	SWT.VIRTUAL needs a count of the number of items for root elements in this tree.
		 * 	Initially it was set here, but that needs to be filled in by the loadMeals & loadProfiles
		 * 	methods once the select from the database has been executed.
		 */
		
	}
	
	@PreDestroy
	private void dispose() {

		tree.dispose();
		backgrnd.dispose();
		explorerComp.dispose();

	}
	
	private void loadProfiles() {

		SelectProfilesJob profsJob = new SelectProfilesJob(profsRoot,profMapper);
		profsJob.schedule();
		
	}
	
	private void loadMeals() {

		SelectMealsJob mealsJob = new SelectMealsJob(mealsRoot,mealMapper);
		mealsJob.schedule();

	}
	
}
