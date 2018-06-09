package net.bobs.own.ezmenu.profile.db;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;

import net.bobs.own.db.rundml.mapper.ITable;

/**
 * Model class for an EzMenu Profile.  An EzMenu profile consists of 7 <code>
 * ProfileModelRow</code> rows.  Each row contains a day of the week, 
 * category and preparation time information.  
 * 
 * @see EzMenuProfileDay
 * @author Robert Anderson
 *
 */
public class EzMenuProfile implements ITable  {
	
	final private short NUM_DAYS = 7;
	private int		profId;
	private String	profName;
	private List<EzMenuProfileDay>  profileDays = new ArrayList<EzMenuProfileDay>(NUM_DAYS);
		
	/**
	 * Create a new EzMenu profile with a specified name and initialize
	 * a default list of profile days.
	 * 
	 * @param name - The name of the new profile.
	 */
	public EzMenuProfile(String name) {
		profId = -1;
		profName = name;
		createDefaultProfile();
	}
	
	public EzMenuProfile(int profId, String name) {
		this.profId = profId;
		this.profName = name;
	}
	
	public int getId() {
		return this.profId;
	}
	
	public String getName() {
		return this.profName;
	}
		
	public void addProfileDay(EzMenuProfileDay day) {
		profileDays.add(day);
	}
	
	public EzMenuProfileDay getProfileDay(int day) {
		return profileDays.get(day);
	}
		
	public void setTableInput(TableViewer viewer) {
		viewer.setInput(profileDays);					
		viewer.refresh();
	}
	
	/**
	 * Returns the total number of categories in this profile matching the specified category string.
	 * 
	 * @param profCategory
	 * @return - the number of categories in the profile, 0 if no matches found
	 */
	public int numberOfCategories(String profCategory) {
	   int countCategory = 0;
	   String[] categories = EzMenuProfileDay.getCategoryConstants();
	   
	   for (EzMenuProfileDay day : profileDays) {
	      if (day.getCategory().toString().equals(profCategory)) {
	         countCategory++;
	      }
	   }
	   
	   return countCategory;
	}
	
	
	private void createDefaultProfile() {
		
		profileDays.add(new EzMenuProfileDay(EzMenuProfileDay.day.Sunday,
											EzMenuProfileDay.category.Beef,"0-15"));
		profileDays.add(new EzMenuProfileDay(EzMenuProfileDay.day.Monday,
											EzMenuProfileDay.category.Beef,"0-15"));
		profileDays.add(new EzMenuProfileDay(EzMenuProfileDay.day.Tuesday,
											EzMenuProfileDay.category.Beef,"0-15"));
		profileDays.add(new EzMenuProfileDay(EzMenuProfileDay.day.Wednesday,
											EzMenuProfileDay.category.Beef,"0-15"));
		profileDays.add(new EzMenuProfileDay(EzMenuProfileDay.day.Thursday,
											EzMenuProfileDay.category.Beef,"0-15"));
		profileDays.add(new EzMenuProfileDay(EzMenuProfileDay.day.Friday,
											EzMenuProfileDay.category.Beef,"0-15"));
		profileDays.add(new EzMenuProfileDay(EzMenuProfileDay.day.Saturday,
											EzMenuProfileDay.category.Beef,"0-15"));
	}
	
}
