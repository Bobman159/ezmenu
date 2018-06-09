package net.bobs.own.ezmenu.dbload.tests.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import net.bobs.own.db.h2.pool.H2ConnectionPoolFactory;
import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.constants.ui.EzMenuConstants;
import net.bobs.own.ezmenu.meals.db.EzMenuMeal;
import net.bobs.own.ezmenu.meals.db.EzMenuMealIngredient;
import net.bobs.own.ezmenu.meals.db.EzMenuMealMapper;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;

public class SWTTreePOC {

	//Based on SWT Snippet Create Virtual Tree (lazy)
	//http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet202.java
	
	/*  
	 * 	Wrote this as a POC on the best way to populate the EzMenuExplorer tree from the Meals & Profile tables in 
	 * 	the EzMenuDatabase.  
	 */
	private static TreeItem root;
	private static TreeItem mealsRoot;
	private static TreeItem profsRoot;
	
	public static void main(String[] args) {
		
		List<ITable> mealsList = null;
		List<ITable> profsList = null;
		EzMenuMealMapper mealMapper = EzMenuMealMapper.getMapper();
		EzMenuProfileMapper profMapper = EzMenuProfileMapper.getMapper();
		
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout (new FillLayout());
		
		Properties profileLoadConfig = new Properties();
		profileLoadConfig.put("dataSourceClassName", "org.h2.jdbcx.JdbcDataSource");
		profileLoadConfig.put("dataSource.url", "jdbc:h2:D:\\\\Java\\\\EzMenu_Workspace\\\\net.bobs.own.ezmenu\\\\db\\\\ezmenu");
		profileLoadConfig.put("dataSource.user","EzMenuUser");
		profileLoadConfig.put("dataSource.password","Aqpk3728");
		profileLoadConfig.put("maximumPoolSize","10");		
//		H2ConnectionPoolFactory.getInstance()
//		                       .makePool(H2ConnectionPoolFactory.PoolTypes.HIKARICP, 
//		                                 EzMenuConstants.POOLID,profileLoadConfig);
		
		final Tree tree = new Tree(shell, SWT.VIRTUAL | SWT.BORDER);
		root = new TreeItem(tree,SWT.CENTER);
		root.setText("EzMenu");
		
		mealsRoot = new TreeItem(root,SWT.NONE);
		mealsRoot.setText("Meals");
		
		profsRoot = new TreeItem(root,SWT.NONE);
		profsRoot.setText("Profiles");
		
		try {
			//These queries would probably be eclipse jobs...  
			//Not sure how I will handle waiting for the jobs to finish...
			System.out.println("*** Get Meals from Database ***");
			mealsList = RunDMLRequestFactory.makeSelectRequest(mealMapper);
			mealsRoot.setData(mealsList);
			System.out.println("*** Get Profiles from Database ***");
			profsList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			profsRoot.setData(profsList);
			System.out.println("number profiles= " + profsList.size());
		} catch (RunDMLException rdex) {
			System.out.println(rdex);
			rdex.printStackTrace();
		}

		System.out.println("*** add setData listener ***");
		tree.addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
				//Used http://www.eclipse.org/articles/Article-SWT-Virtual/Virtual-in-SWT.html as the basis for this code
				TreeItem item = (TreeItem) event.item;
				int index = tree.indexOf(item);
				System.out.println("index= " + index);
				TreeItem parent = item.getParentItem();
				if (parent == null) {
					System.out.println("*** parent == null ***");
					System.out.println("*** index= " + index);
//					if (index == 1) {
//						//Meals Index?
//						System.out.println("*** index == 1 ***");
//						List<ITable> mealsList2  = (List<ITable>) mealsRoot.getData();
//						EzMenuMeal meal = (EzMenuMeal) mealsList2.get(event.index);
//						System.out.println("*** mealname= " + meal.getMealName());
//						item.setText(meal.getMealName());
//					} else if (index == 2) {
//						System.out.println("*** index == 2 ***");
//						//Profiles Index?
//						List<ITable> profsList = (List<ITable>) profsRoot.getData();
//						EzMenuProfile profile = (EzMenuProfile) profsList.get(event.index);
//						System.out.println("*** profileName= " + profile.getName());
//						item.setText(profile.getName());
//					}
				} else {
					System.out.println("*** parent != null ***");
					if (parent.getText().equals("Meals")) {
						System.out.println("*** add to meals root ***");
						List<ITable> mealsList = (List<ITable>) mealsRoot.getData();
						EzMenuMeal meal = (EzMenuMeal) mealsList.get(event.index);
						item.setText(meal.getMealName());
						TreeItem ingredItem = new TreeItem(item,SWT.NONE);
						ingredItem.setText(" ");
					} else if (parent.getText().equals("Profiles")) {
						System.out.println("*** add to profiles root ***");
						List<ITable> profsList = (List<ITable>) profsRoot.getData();
						EzMenuProfile profile = (EzMenuProfile) profsList.get(event.index);
						item.setText(profile.getName());
					}
				}
			}
		});

		System.out.println("*** add expand listener ***");
		tree.addListener(SWT.Expand, new Listener() {
			@Override 
			public void handleEvent(Event event) {
				TreeItem mealItem = (TreeItem) event.item;
//				mealItem.removeAll();
				TreeItem parent = mealItem.getParentItem();
				if ((parent != null)  &&
					(parent.getText().equals("Meals"))) {						
					int index = mealsRoot.indexOf(mealItem);
					System.out.println("index of mealItem= " + index);
					List<ITable> mealsList = (List<ITable>) mealsRoot.getData();
					EzMenuMeal meal = (EzMenuMeal) mealsList.get(index);
					mealItem.setText(meal.getMealName());
					System.out.println("*** meal= " + meal.getMealName() + " expanded");
					
					ArrayList<EzMenuMealIngredient> listIngredients = meal.getIngredients();
					mealItem.setData(listIngredients);
					for (int ingredIx = 0; ingredIx < listIngredients.size(); ingredIx++) {
						if (ingredIx == 0) {
							TreeItem test = mealItem.getItem(0);
							System.out.println("test item text=" + test.getText());
							test.setText( ((EzMenuMealIngredient)listIngredients.get(0)).getIngredient());
						} else {
							TreeItem ingredientItem = new TreeItem(mealItem,SWT.NONE);
							ingredientItem.setText(((EzMenuMealIngredient)listIngredients.get(ingredIx)).getIngredient());
						}
					}
				}
			}
		});

//		tree.setItemCount(2);
		mealsRoot.setItemCount(3000);
		profsRoot.setItemCount(3000);
		//This would be total # meals + total # profiles?
//		tree.setItemCount(3000);	
		System.out.println("treeItemCount= " + tree.getItemCount());

		shell.setSize(400, 300);
		shell.open();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();

	}

}
