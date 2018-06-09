package net.bobs.own.ezmenu.profile.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bobs.own.db.h2.db.H2AbstractDatabaseService;
import net.bobs.own.db.h2.exceptions.NoPreferenceException;
import net.bobs.own.db.h2.pool.H2Database;
import net.bobs.own.db.rundml.exception.ExceptionMessageDialogUtility;
import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.mapper.AbstractTableMapper;
import net.bobs.own.db.rundml.mapper.ITable;


public class EzMenuProfileMapper extends AbstractTableMapper {

	static private EzMenuProfileMapper mapper = null;
	static private Logger 		logger = LogManager.getLogger(EzMenuProfileMapper.class.getName());
	
	static public EzMenuProfileMapper makeMapper(H2Database database) {
		
		mapper = new EzMenuProfileMapper(database);
		return mapper;
		
	}
	
	static public EzMenuProfileMapper getMapper() {

		return mapper;
		
	}
	
//	public EzMenuProfileMapper(H2Database database) {
	private EzMenuProfileMapper(H2Database database) {
		super(database);
	}
	
	@Override
	public List<ITable> select() throws RunDMLException {

		ResultSet results = null;
		EzMenuProfile profile = null;
		ArrayList<ITable> profilesList = new ArrayList<ITable>();
		
		final String SELECT_PROFILE = "select prof_id, prof_name, prof_catgy_sun, prof_catgy_mon, "
				+	"prof_catgy_tue, prof_catgy_wed, prof_catgy_thur, prof_catgy_fri, prof_catgy_sat, "
				+	"prof_prep_sun, prof_prep_mon, prof_prep_tue, prof_prep_wed, "
				+	"prof_prep_thu, prof_prep_fri, prof_prep_sat "
				+	"from ezmenu.profile "
				+	"order by prof_name "
				;
		try {
		   Connection conn = db.getConnection();
			db.setSQL(conn,SELECT_PROFILE);
			String category = null;
			String prepTime = null;

			results = db.executeQuery();
			
			while (results.next()) {
				int profId = results.getInt(1);
				String profName = results.getString(2);

				profile = new EzMenuProfile(profId,profName);
				
				//Sunday
				category= results.getString(3);
				prepTime = results.getString(10);
				EzMenuProfileDay sunday = new EzMenuProfileDay(EzMenuProfileDay.day.Sunday,
						EzMenuProfileDay.toCategory(category),prepTime);
				profile.addProfileDay(sunday);
				
				//Monday
				category = results.getString(4);
				prepTime = results.getString(11);
				EzMenuProfileDay monday = new EzMenuProfileDay(EzMenuProfileDay.day.Monday,
						EzMenuProfileDay.toCategory(category),prepTime);
				profile.addProfileDay(monday);
				
				//Tuesday
				category = results.getString(5);
				prepTime = results.getString(12);
				EzMenuProfileDay tuesday = new EzMenuProfileDay(EzMenuProfileDay.day.Tuesday,
						EzMenuProfileDay.toCategory(category),prepTime);
				profile.addProfileDay(tuesday);
				
				//Wednesday
				category = results.getString(6);
				prepTime = results.getString(13);
				EzMenuProfileDay wednesday = new EzMenuProfileDay(EzMenuProfileDay.day.Wednesday,
						EzMenuProfileDay.toCategory(category),prepTime);
				profile.addProfileDay(wednesday);
				
				//Thursday
				category = results.getString(7);
				prepTime = results.getString(14);
				EzMenuProfileDay thursday = new EzMenuProfileDay(EzMenuProfileDay.day.Thursday,
						EzMenuProfileDay.toCategory(category),prepTime);
				profile.addProfileDay(thursday);
				
				//Friday
				category = results.getString(8);
				prepTime = results.getString(15);
				EzMenuProfileDay friday = new EzMenuProfileDay(EzMenuProfileDay.day.Friday,
						EzMenuProfileDay.toCategory(category),prepTime);
				profile.addProfileDay(friday);
				
				//Saturday
				category = results.getString(9);
				prepTime = results.getString(16);
				EzMenuProfileDay saturday = new EzMenuProfileDay(EzMenuProfileDay.day.Saturday,
						EzMenuProfileDay.toCategory(category),prepTime);
				profile.addProfileDay(saturday);

				profilesList.add(profile);
			}

			db.reset(conn);

		} catch (SQLException | NoPreferenceException ex) {
			RunDMLException rdex = new RunDMLException(ex);
			ExceptionMessageDialogUtility.openExceptionMessageDialog(rdex);
			logger.error(ex.getMessage(), ex);
			throw rdex;
		}
		
		return profilesList;
		
	}

	@Override
	public List<ITable> selectById(int id) throws RunDMLException {
			return null;
	}

	//Commented out since this is not likely to be needed.
//	public List<ITable> selectByLimitOffset(int limit, int offset) throws RunDMLException {
//		ResultSet results = null;
//		EzMenuProfile profile = null;
//		ArrayList<ITable> profilesList = new ArrayList<ITable>();
//		
//		final String SELECT_PROFILE_LIMITOFFSET = "select prof_id, prof_name, prof_catgy_sun, prof_catgy_mon, "
//				+	"prof_catgy_tue, prof_catgy_wed, prof_catgy_thur, prof_catgy_fri, prof_catgy_sat, "
//				+	"prof_prep_sun, prof_prep_mon, prof_prep_tue, prof_prep_wed, "
//				+	"prof_prep_thu, prof_prep_fri, prof_prep_sat "
//				+	"from ezmenu.profile"
//				+	" limit ? offset ?"
//				;
//		try {
//			db.setSQL(SELECT_PROFILE_LIMITOFFSET);
//			String category = null;
//			String prepTime = null;
//
//			db.setInt(1,limit);
//			db.setInt(2, offset);
//			results = db.executeQuery();
//			while (results.next()) {
//				
//				int profId = results.getInt(1);
//				String profName = results.getString(2);
//				profile = new EzMenuProfile(profName);
//				
//				//Sunday
//				category= results.getString(3);
//				prepTime = results.getString(10);
//				EzMenuProfileDay sunday = new EzMenuProfileDay(EzMenuProfileDay.day.Sunday,
//						EzMenuProfileDay.toCategory(category),prepTime);
//				profile.addProfileDay(sunday);
//				
//				//Monday
//				category = results.getString(4);
//				prepTime = results.getString(11);
//				EzMenuProfileDay monday = new EzMenuProfileDay(EzMenuProfileDay.day.Monday,
//						EzMenuProfileDay.toCategory(category),prepTime);
//				profile.addProfileDay(monday);
//				
//				//Tuesday
//				category = results.getString(5);
//				prepTime = results.getString(12);
//				EzMenuProfileDay tuesday = new EzMenuProfileDay(EzMenuProfileDay.day.Tuesday,
//						EzMenuProfileDay.toCategory(category),prepTime);
//				profile.addProfileDay(tuesday);
//				
//				//Wednesday
//				category = results.getString(6);
//				prepTime = results.getString(13);
//				EzMenuProfileDay wednesday = new EzMenuProfileDay(EzMenuProfileDay.day.Wednesday,
//						EzMenuProfileDay.toCategory(category),prepTime);
//				profile.addProfileDay(wednesday);
//				
//				//Thursday
//				category = results.getString(7);
//				prepTime = results.getString(14);
//				EzMenuProfileDay thursday = new EzMenuProfileDay(EzMenuProfileDay.day.Thursday,
//						EzMenuProfileDay.toCategory(category),prepTime);
//				profile.addProfileDay(thursday);
//				
//				//Friday
//				category = results.getString(8);
//				prepTime = results.getString(15);
//				EzMenuProfileDay friday = new EzMenuProfileDay(EzMenuProfileDay.day.Friday,
//						EzMenuProfileDay.toCategory(category),prepTime);
//				profile.addProfileDay(friday);
//				
//				//Saturday
//				category = results.getString(9);
//				prepTime = results.getString(16);
//				EzMenuProfileDay saturday = new EzMenuProfileDay(EzMenuProfileDay.day.Saturday,
//						EzMenuProfileDay.toCategory(category),prepTime);
//				profile.addProfileDay(saturday);
//
//				profilesList.add(profile);
//			}
//
//			db.reset();
//
//		} catch (SQLException | NoPreferenceException ex) {
//			RunDMLException rdex = new RunDMLException(ex);
//			ExceptionMessageDialogUtility.openExceptionMessageDialog(rdex);
//			logger.error(ex.getMessage(), ex);
//			throw rdex;
//		}
//		
//		return profilesList;		
//	}

	@Override
	public void insert(ITable table) throws RunDMLException {
		int 	returnCode = 0;		
		EzMenuProfile profile = (EzMenuProfile) table;
		
		final String INSERT_PROF = "INSERT INTO EZMENU.PROFILE ("
				+ "PROF_NAME, PROF_CATGY_SUN, "
				+ "PROF_CATGY_MON, PROF_CATGY_TUE, PROF_CATGY_WED, "
				+ "PROF_CATGY_THUR, PROF_CATGY_FRI ,PROF_CATGY_SAT, "
				+ "PROF_PREP_SUN, PROF_PREP_MON, PROF_PREP_TUE, " 
				+ "PROF_PREP_WED, PROF_PREP_THU, PROF_PREP_FRI, "
				+ "PROF_PREP_SAT) VALUES "
				+ "(?,?,"
				+ "?,?,?,"
				+ "?,?,?,"
				+ "?,?,?,"
				+ "?,?,?,"
				+ "?)";

			try {
	         Connection conn = db.getConnection();
				db.setSQL(conn,INSERT_PROF);
				db.setString(1,profile.getName());
				bindDayValues(db,profile,2);
				
				returnCode = db.executeUpdate();
				if (returnCode> 1) {
					logger.debug("Profile name= " + profile.getName() + "inserted into database.");
				}
				
				db.reset(conn);

			} catch (SQLException | NoPreferenceException ex) {
				RunDMLException rdex = new RunDMLException(ex);
				ExceptionMessageDialogUtility.openExceptionMessageDialog(rdex);
				logger.error(ex.getMessage(), ex);
				throw rdex;
			}

	}

	@Override
	public void update(ITable table) throws RunDMLException {

		int 	returnCode = 0;
		EzMenuProfile profile = (EzMenuProfile) table;
		
		final String UPDATE_PROF = "UPDATE EZMENU.PROFILE "
				+ "SET PROF_NAME = ?, PROF_CATGY_SUN = ?, "
				+      "PROF_CATGY_MON = ?, PROF_CATGY_TUE = ?, "
				+      "PROF_CATGY_WED = ?, PROF_CATGY_THUR = ?," 
				+	   "PROF_CATGY_FRI = ?, PROF_CATGY_SAT = ?, "
				+      "PROF_PREP_SUN = ?,  PROF_PREP_MON = ?, " 
				+	   "PROF_PREP_TUE = ?,  PROF_PREP_WED = ?, " 
				+	   "PROF_PREP_THU = ?,  PROF_PREP_FRI = ?, "
				+ 	   "PROF_PREP_SAT = ? "
				+ "	WHERE PROF_ID = ?";
		
		try {
   	      Connection conn = db.getConnection();
   			db.setSQL(conn,UPDATE_PROF);
   			db.setString(1, profile.getName());
   			bindDayValues(db,profile,2);
   
   			/* Set value for PROF_NAME */
   			db.setInt(16, profile.getId());
   			
   			returnCode = db.executeUpdate();
   			if (returnCode == 1) {
   				logger.debug("Profile name= " + profile.getName() + " updated into database.");
   			}
   			db.reset(conn);

		} catch (SQLException | NoPreferenceException ex) {
			RunDMLException rdex = new RunDMLException(ex);
			ExceptionMessageDialogUtility.openExceptionMessageDialog(rdex);
			logger.error(ex.getMessage(), ex);
			throw rdex;
		}
		

	}

	@Override
	public void delete(ITable table) throws RunDMLException {
		int returnCode = 0;
		EzMenuProfile profile = null;
		
		final String DELETE_PROFILE = "delete from ezmenu.profile " + 
								   " where prof_id = ?"
								   ;

		profile = (EzMenuProfile) table;

		try {
         Connection conn = db.getConnection();
			db.setSQL(conn,DELETE_PROFILE);
			db.setInt(1, profile.getId());
			returnCode = db.executeUpdate();			
			logger.debug("returnCode = " + returnCode);
			if (returnCode > 1) {
				logger.debug("Profile= " + profile.getName() + "deleted from database.");
			}
			
			db.reset(conn);

		} catch (SQLException | NoPreferenceException ex) {
			RunDMLException rdex = new RunDMLException(ex);
			ExceptionMessageDialogUtility.openExceptionMessageDialog(rdex);
			logger.error(ex.getMessage(), ex);
			throw rdex;
		}

	}
	
	public boolean checkForExistence(String profName) {
		/*
		 * H2 Check for name existence of Profile with name
		 * 
		 * select TOP 1 prof_name from PROFILE where prof_name = 'ProfName2'
		 */
		boolean dbExists = false;
		
		final String CHECK_EXIST = "Select top 1 prof_name from ezmenu.profile where prof_name = ?";
		
		try {
		      Connection conn = db.getConnection();
   			db.setSQL(conn,CHECK_EXIST);
   			db.setString(1, profName);
   			ResultSet rs = db.executeQuery();
   			if (rs.next()) {
   				dbExists = true;
   			}
   			db.reset(conn);
		} catch (SQLException | NoPreferenceException ex) {
			RunDMLException rdex = new RunDMLException(ex);
			ExceptionMessageDialogUtility.openExceptionMessageDialog(rdex);
			logger.error(ex.getMessage(), ex);
		}

		return dbExists;
	}

	private void bindDayValues(H2AbstractDatabaseService service,EzMenuProfile profile,int startParmIx) throws SQLException {

		/*
		 * 0 = SUNDAY, 1 = MONDAY... 7 = SATURDAY
		 */
		for (int index = EzMenuProfileDay.SUNDAY; index <= EzMenuProfileDay.SATURDAY; index++) {

			try {
				EzMenuProfileDay day = profile.getProfileDay(index);
				day.bindCategory(service, startParmIx); // + 2);
				day.bindPrepTime(service, startParmIx + 7); // + 9);
				startParmIx++;
			} catch (SQLException sqlex) {
				throw new SQLException(sqlex);
			}
		}
	}
}
