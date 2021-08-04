package net.bobs.own.ezmenu.dbload.tests.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bobs.own.db.h2.pool.H2Database;
import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.MealCategory;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.PrepTimes;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay.WeekDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;

/**
 * A class to generate <code>EzMenuProfile</code> objects for testing.  
 * Two mechanisms are provided for generating the objects:
 * <ul><li>Generate a large number of profiles using default values.</li> 
 * <li>Generate a specific list of profiles based on specific input.</li>
 * </ul>
 * 
 * The main purpose of this class is to provide a facility to generate 
 * test profiles for Menu Plan generation and validation.
 * 
 */
public class ProfileDataGenerator {
	
		/*
		 *	Generate test data for load testing the EzMenuExplorer & 
		 * 	Database process performance.  
		 * 
		 * 	Existing Data is DELETED before the test data is generated.
		 * 
		 */
		
		static private Logger logger = LogManager.getLogger(ProfileDataGenerator.class.getName());
		private EzMenuProfileMapper profileMapper = null;
		private final boolean LOG = false;

		private H2Database db = null;
      String[] categories = null;
      String[] prepTimes = null;

		
		public ProfileDataGenerator(H2Database db) {			
			
			this.db = db;
			profileMapper = EzMenuProfileMapper.makeMapper(this.db);
         categories = EzMenuProfileDay.getCategoryConstants();
         prepTimes = EzMenuProfileDay.getPrepTimeConstants();

		}
		
		
		/**
		 * <p>Generate 0 to n <code>EzMenuProfile</code> objects for an input of both categories and Preparation Times.
		 * The inputs are in the form of two dimensional arrays. </p> 
		 * 
		 * <p>For the categoryCounts, 1 row in the array will generate 1 profile.  Each column in the row specifies the 
		 * number of days that will be used for that profile. Example: {#Beef,#Chicken,#Fish,#Pasta,#Pork,#Turkey,#Veggie},
		 * as there are only 7 days in a week, the sum of all the numbers should not exceed 7 in a particular row.  Use 0 to indicate a category 
		 * should be skipped.  The categories will be used for a day of the week starting with Sunday, then Monday, then 
		 * Tuesday .... until Saturday.</p>
		 * 
		 * <p>For the prepTimeMatrix, the order of rows in the array corresponds to order of categories in the categoryCounts 
		 * array.  1st row in the prepTimeMatrix = the 1st row, 1st column in the categoryCounts array.  Each column in 
		 * the row specifies the number of preparation time(s) that will be used for the corresponding category. Ex 
		 * {#0-15,#16-30,#31-45,#46-60,#61+}.  Use a 0 to indicate a prep time range should be skipped.</p>
		 * 
		 * <b>Usage Example</b>
       * <pre>
       * private static int[][] profCategories = {{2,0,1,3,0,1,0}
       *                                          {0,2,0,0,1,1,3}
       *                                          {7,0,0,0,0,0,0}};
       *                                          
       * private static int[][] profPrepTimes = {{1,0,0,1,0},   //Beef Prep Times (1st categoriesMatrix)
       *                                        {0,0,1,0,0},   //Fish Prep Times (1st categoriesMatrix) 
       *                                        {0,2,0,0,1},   //Pasta Prep Times (1st categoriesMatrix)
       *                                        {0,0,1,0,0},   //Tukey Prep Times (1st categoriesMatrix)
       *                                        {1,0,0,1,0},   //Chicken Prep Times (2nd categoriesMatrix)
       *                                        {0,0,1,0,0},   //Pork Prep Times (2nd categoriesMatrix)
       *                                        {1,0,0,0,0},   //Tukey Prep Times (2nd categoriesMatrix)
       *                                        {1,0,1,0,1},   //Veggie Prep Times (2nd categoriesMatrix)
       *                                        {1,2,1,2,1}    //Beef Prep Times (3rd categoriesMatrix)};  
       * </pre>
       * will result in the following profiles being generated
       * <pre> 
       * 1st Profile 
       *    Day         Category       Prep Time
       *    Sunday      Beef           0-15
       *    Monday      Beef           46-60
       *    Tuesday     Fish           31-45
       *    Wednesday   Pasta          16-30
       *    Thursday    Pasta          16-30
       *    Friday      Pasta          61+
       *    Saturday    Turkey         31-45
       *    
       * 2nd Profile 
       *    Day         Category       Prep Time
       *    Sunday      Chicken        0-15
       *    Monday      Chicken        46-60
       *    Tuesday     Pork           31-45
       *    Wednesday   Turkey         0-15 
       *    Thursday    Veggie         0-15 
       *    Friday      Veggie         31-45
       *    Saturday    Veggie         61+  
       *
       * 3rd Profile 
       *    Day         Category       Prep Time
       *    Sunday      Beef           0-15
       *    Monday      Beef           16-30
       *    Tuesday     Beef           16-30
       *    Wednesday   Beef           31-45
       *    Thursday    Beef           46-60
       *    Friday      Beef           46-60
       *    Saturday    Beef           61+  
      </pre>

		 * @param categoryCounts - the category counts to be used.  1 row will generate 1 profile
		 * @param prepTimeMatrix  - the prepration time matrix to be used
		 * @return - number of rows inserted into the database
		 */
		@Deprecated
		//TODO: Remove this method once the new generateProfiles is working.
		public int generateProfiles(int[][] categoryCounts, int[][] prepTimeMatrix) {
			
			EzMenuProfile profile = null;
			int numberInserted = 0;
			int prepMatrixIx = 0;
			
			//Validate Category Constants Array Size
			if ((categoryCounts.length <= 0) ||
			    (categoryCounts.length > categories.length)) {
			   throw new IllegalArgumentException("Category counts must be > 0 AND <= " + categories.length);
			}
			
	      //Validate PrepTimes Matrix Array Sizes
         if (prepTimeMatrix.length <= 0) {
            throw new IllegalArgumentException("Preparation time maxtrix is empty, it MUST be specified. "); 
         }

			for (int prepIx = 0; prepIx < prepTimeMatrix.length;prepIx++) {
			   if (prepTimeMatrix[prepIx].length > prepTimes.length) {
			      throw new IllegalArgumentException("Preparation time matrix row [" + prepIx + "]" + " must specify " + 
			                  prepTimes.length + " entries");
			   }
			   
			}
			
			//Sanity Checks to make sure that the number of Categories = # of Preparation Times 
			int numberPrepTimes = 0;
         for (int row = 0;row  < categoryCounts.length;row++) {
            for (int col = 0; col < categoryCounts[row].length;col++) {
               if (categoryCounts[row][col] != 0) {
                  numberPrepTimes++;
               }
            }
         }
         
         int numberCategories = 0;
			for (int row = 0;row  < categoryCounts.length;row++) {
			   for (int col = 0; col < categoryCounts[row].length;col++) {
   			   if (categoryCounts[row][col] != 0) {
   			      numberCategories++;
   			   }
			   }
			}
			
			if (numberPrepTimes != numberCategories) {
            throw new IllegalArgumentException("Preparation time matrix must have same number of rows " + prepTimes.length + 
                  " as the number of categories being generated " + numberCategories);
			}
			
			for (int prepIx = 0; prepIx < prepTimeMatrix.length;prepIx++) {
            if (prepTimeMatrix[prepIx].length > prepTimes.length) {
               throw new IllegalArgumentException("Preparation time matrix rows must specify " + prepTimes.length + 
                                                  " entries");
            }
         }
			
			try {
			   EzMenuProfile[] gennedProfs = makeProfiles(categoryCounts,prepTimeMatrix);
   			for (int profIx = 0;profIx < gennedProfs.length; profIx++) {
   			   profile = gennedProfs[profIx];
      			RunDMLRequestFactory.makeInsertRequest(profileMapper, profile);
      			numberInserted++;
      			writeToLog("Profile= " + profile.getName() + "added");
   			}
   			prepMatrixIx++;
				} catch (RunDMLException hex) {
					logger.debug(hex.getMessage(), hex);
				}
			
			return numberInserted;
		}
		
		
		public int generateProfiles(Object[][] profilesDefinition) {
		   
		   EzMenuProfile profile = null;
         int numberInserted = 0;
         int prepMatrixIx = 0;
         
         //Validate Category Constants Array Size
         if (profilesDefinition.length <= 0) {
            throw new IllegalArgumentException("Profile Definition is empty, it MUST be specified");
         }
         
         try {
            ArrayList<EzMenuProfile> gennedProfs = makeProfiles(profilesDefinition);
            for (int profIx = 0;profIx < gennedProfs.size(); profIx++) {
               profile = gennedProfs.get(profIx);
               RunDMLRequestFactory.makeInsertRequest(profileMapper, profile);
               numberInserted++;
               writeToLog("Profile= " + profile.getName() + "added");
            }
            prepMatrixIx++;
            } catch (RunDMLException hex) {
               logger.debug(hex.getMessage(), hex);
            }
         
         return numberInserted;
		   
		}
		
		/**
		 * Generate a specified number of "default" profiles. The "default" profile has Beef as a category and 0-15
		 * for the preparation time for all 7 days.
		 * 
		 * @param numberProfiles
		 * @return
		 */
		public int generateProfiles(int numberProfiles) {
		   
		   EzMenuProfile profile = null;
		   int profileDbCount = 0;
		      
		      while (numberProfiles > 0) {
		         
		         profile = new EzMenuProfile("Profile_Name_" + numberProfiles);
		         try {
		            RunDMLRequestFactory.makeInsertRequest(profileMapper, profile);
		            profileDbCount++;
		         } catch (RunDMLException hex) {
		            logger.debug(hex.getMessage(), hex);
		         }
		         numberProfiles--;
		         writeToLog("profile count= " + numberProfiles);
		         writeToLog("Profile= " + profile.getName() + "added");
		      }
		      
		      return profileDbCount;
		   
		}
		
		/**
		 *  Deletes ALL profiles from the database.
		 */
		public void deleteProfiles() {
			
			try {
				writeToLog("deleteProfiles method ENTER:");
				List<ITable> profilesList = RunDMLRequestFactory.makeSelectRequest(profileMapper);
				writeToLog("# of profiles in database= " + profilesList.size());
				
				for (int profilesIx = 0; profilesIx < profilesList.size(); profilesIx++) {
					EzMenuProfile profile = (EzMenuProfile) profilesList.get(profilesIx);
					writeToLog("Calling profile.delete() method for profile= " + profile.getName());
					RunDMLRequestFactory.makeDeleteRequest(profileMapper, profile);
					writeToLog("Profile=" + profile.getName() + " deleted");
				}
			} catch (RunDMLException hex) {
				hex.printStackTrace();
				System.exit(16);
			}
			writeToLog("deletes method EXIT:");
		}
		
		@Deprecated
		//TODO: Remove this method once the new makeProfiles method is working.
		private EzMenuProfile[] makeProfiles(int[][] categoriesMatrix, int[][] prepTimeMatrix) {

		   int day = 0;
		   int prepRow = 0;
		   int prepCol = 0;
		   int categoryRow = -1;
         EzMenuProfile profile = null;

		   String strPrepTime = prepTimes[0];
         EzMenuProfile[] profiles = new EzMenuProfile[categoriesMatrix.length];  //OLD_WAY  
         ArrayList<EzMenuProfile> profileList = new ArrayList<EzMenuProfile>();
         
         boolean NEW_WAY = false;
         if (NEW_WAY) {
            for (categoryRow = 0;categoryRow < categoriesMatrix.length ;categoryRow++) {
               
               profile = new EzMenuProfile(-1,"Profile_Name_" + categoryRow);
               
               for (int categoryCol = 0;categoryCol < categoriesMatrix[categoryRow].length;categoryCol++) {
                  
                  for (int numbDays = categoriesMatrix[categoryRow][categoryCol];
                       numbDays > 0; numbDays--) {
                     String category = categories[categoryCol];
                     int numbPrepTime = prepTimeMatrix[prepRow][prepCol];
                     
                     /* Each non zero # in prepTimeMatrix = Day of week,
                      *    1st non-zero # = Sunday
                      *    2nd non-zero # = Monday
                      *    3rd non-zero # = Tuesday
                      *    ...
                      *    6th non-zero # = Saturday   
                      */

                     //TODO: Verify prepCol & prepRow won't exceed bounds of the arrays they reference
                     //Generate the Profile Day - Category & Preparation Time
                     if (numbPrepTime > 0) {
                        strPrepTime = prepTimes[prepCol];
                     } else {

                        //find next non-zero entry
                        boolean found = false;
                        for (int nonZeroRow = prepRow, nonZeroCol = prepCol;
                             nonZeroCol <= prepTimes.length; nonZeroCol++) {
                           
                           if (prepTimeMatrix[prepRow][nonZeroCol] != 0) {
                              found = true;
                              break;
                           } 
                        }  //END for(...;prepCol <= prepTimes.length....)
                        
                        if (found) {
                           numbPrepTime = prepTimeMatrix[prepRow][prepCol];
                           strPrepTime = prepTimes[prepCol];                           
                        }
                     }  //END if(numbPrepTime > 0)

                     EzMenuProfileDay profDay = new EzMenuProfileDay(WeekDay.toDay(day),
                                                                     MealCategory.toCategory(category),
                                                                     PrepTimes.toPrepTime(strPrepTime));
                     profile.addProfileDay(profDay);
                     day++;

                     }   //END for (....; numbDays > 0; 
//                     prepRow++;
//                     prepCol = 0;
                     
//                  }   //END for

                  }   //END for (int categoryCol = 0;categoryCol <= categoriesMatrix[categoryRow].length;categoryCol++)
               
               profileList.add(profile);
               
            }   //END for (categoryRow = 0; categoryRow <=             
         } else {

         //OLD_WAY
         //================================================================
         int count = 0; 
		   for (categoryRow = 0;categoryRow < count;categoryRow++) {

		      if (day > WeekDay.SATURDAY.getDay()) {
		         day = 0;
		      }
            profile = new EzMenuProfile(-1,"Profile_Name_" + categoryRow);
		      for (int categoryCol = 0;categoryCol < categoriesMatrix[categoryRow].length;categoryCol++) {
		         
		         int numbDays = categoriesMatrix[categoryRow][categoryCol];
	            if (numbDays > 0) {
   		         String category = categories[categoryCol];
   		         int numbPrepTime = prepTimeMatrix[prepRow][prepCol];
   		         
   		         /* Each non zero # in prepTimeMatrix = Day of week,
   		          *    1st non-zero # = Sunday
   		          *    2nd non-zero # = Monday
   		          *    3rd non-zero # = Tuesday
   		          *    ...
   		          *    6th non-zero # = Saturday   
   		          */

   		         while (numbDays > 0) {
   		            //Generate the Profile Day - Category & Prep Time
   		            if (numbPrepTime > 0) {
   		               strPrepTime = prepTimes[prepCol];
   		            } else {
   		               //find next non-zero entry
   		               boolean done = false;
   		               while (done == false) {
   		                  prepCol++;
   		                  writeToLog("prepRow= " + prepRow);
   		                  writeToLog("prepCol= " + prepCol);
   		                  if (prepCol >= prepTimes.length) {
//   		                     done = true;
   		                     break;
   		                  }
   		                  if (prepTimeMatrix[prepRow][prepCol] != 0) {
   		                     done = true;
   		                  } 
   		               }
   		               //Make sure the loop found an entry and didn't end because there
   		               //the whole matrix was searched and no non-zero entries were found.
   		               if (done == true) {
      		               //Should I worry about prepCol > # of entries?
      		               numbPrepTime = prepTimeMatrix[prepRow][prepCol];
      		               strPrepTime = prepTimes[prepCol];
   		               }
   		            }
   		            EzMenuProfileDay profDay = new EzMenuProfileDay(WeekDay.toDay(day),
      	                                                            MealCategory.toCategory(category),
      	                                                            PrepTimes.toPrepTime(strPrepTime));
      	            profile.addProfileDay(profDay);
      	            day++;
                     numbPrepTime--;
                     numbDays--;
   		         }   //END while (numbDays > 0)
      		   
   		         //This works in my Unit Tests,but I am moving to the next row in PrepTimeMatrix before 
   		         //it should? IE. prepCol = 2 with all remaining entries are zero but I move to the next row in
   		         //PrepTimeMatrix. Not sure if this a problem...

      		      prepRow++;
                  prepCol = 0;
      		      
	            }   //END if (numbDays > 0

		      }   //END for (int categoryCol = 0;categoryCol <= categoriesMatrix[categoryRow].length;categoryCol++)
		      
            profiles[categoryRow] = profile;
            
		   }   //END for (categoryRow = 0; categoryRow <= 
        }
		   
		   return profiles;
		}
		
		private ArrayList<EzMenuProfile> makeProfiles(Object[][] profilesDefinition) {
         int currProfId = 0;
         int dayOfWeek = WeekDay.SUNDAY.getDay();
         EzMenuProfile prof = null;
         ArrayList<EzMenuProfile> profsList = new ArrayList<EzMenuProfile>();
         
         /*ASSUME: profilesDefition has 7 entries (1 for each day of week) 
          *        for each unique profileId.  If there are more or less 
          *        than 7 entries for each unique profileId then 
          *        UNKNOWN results may occur.
          */        
         for (int profRow = 0; profRow < profilesDefinition.length;profRow++) {
            
            Integer profId = (Integer) profilesDefinition[profRow][0];
            if (currProfId != profId.intValue()) {
               currProfId = profId.intValue();
               if (prof != null) {
                  profsList.add(prof);
               }
               prof = new EzMenuProfile(profRow,"DataGen_Profile_" + currProfId);
               dayOfWeek = WeekDay.SUNDAY.getDay();
            }              
            Integer dayCountInt = (Integer) profilesDefinition[profRow][1];
            MealCategory category = (MealCategory) profilesDefinition[profRow][2];
            PrepTimes prepTime = (PrepTimes) profilesDefinition[profRow][3];
            for (int dayCount = dayCountInt.intValue();dayCount > 0;dayCount--) {
               EzMenuProfileDay profDay = new EzMenuProfileDay(WeekDay.toDay(dayOfWeek),
                                                               category,prepTime);
               prof.addProfileDay(profDay);
               dayOfWeek++;
            }
            
         }
         
         if (!(profsList.contains(prof))) {
            profsList.add(prof);
         }
      
         return profsList;
		}
		
		private void writeToLog(String msg) {
		   if (LOG) {
		      logger.debug(msg);
		   }
		}

}
