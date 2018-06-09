package net.bobs.own.ezmenu.profile.db.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.bobs.own.db.h2.pool.H2ConnectionPoolFactory;
import net.bobs.own.db.h2.pool.H2Database;
import net.bobs.own.db.h2.pool.IH2ConnectionPool;
import net.bobs.own.db.rundml.exception.RunDMLException;
import net.bobs.own.db.rundml.factory.RunDMLRequestFactory;
import net.bobs.own.db.rundml.mapper.ITable;
import net.bobs.own.ezmenu.profile.db.EzMenuProfile;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileDay;
import net.bobs.own.ezmenu.profile.db.EzMenuProfileMapper;

class EzMenuProfileDBTest {

	//TODO: Add additional tests to improve code coverage
	//	*	failure tests, right now this tests mostly the "happy path"
	//	*	multiple rows for Insert, Update & Delete tests right now only 1 row is tested.
	//	*	duplicate profile name
	
	private static H2Database ezMenuDbTest;
	private static EzMenuProfileMapper profMapper = null;
	private static Logger logger = LogManager.getLogger(EzMenuProfileDBTest.class.getName());

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		IH2ConnectionPool pool = H2ConnectionPoolFactory.getInstance()
		                            .makePool(H2ConnectionPoolFactory.PoolTypes.MYOWN, 
		                                      "D:\\Java\\EzMenu_Workspace\\net.bobs.own.ezmenu\\db\\ezmenu_test",
		                                      "EzMenuUser", "Aqpk3728", "10", "ezmenuTest.pool");
		
		ezMenuDbTest = new H2Database(pool);
		profMapper = EzMenuProfileMapper.makeMapper(ezMenuDbTest);
		deleteAllRows();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ezMenuDbTest.reset();
	}

	@BeforeEach
	void setUp() throws Exception {
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testInsert() {
		//Test Default Insert of Profile
		EzMenuProfile profile = new EzMenuProfile("InsertProfileTest");
		try {
			RunDMLRequestFactory.makeInsertRequest(profMapper, profile);
			List<ITable> profList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			assertEquals(profList.size(),1);

			assertEquals("InsertProfileTest",((EzMenuProfile) profList.get(0)).getName());

		} catch (RunDMLException rdex) {
			failException(rdex);
		}

	}
	
	@Test
	void testUpdate() {
		//Test Update of Profile
		EzMenuProfileDay[] updateDays = {new EzMenuProfileDay(EzMenuProfileDay.day.Sunday,EzMenuProfileDay.category.Veggie,"61+"),
										 new EzMenuProfileDay(EzMenuProfileDay.day.Monday,EzMenuProfileDay.category.Beef,"0-15"),
										 new EzMenuProfileDay(EzMenuProfileDay.day.Tuesday,EzMenuProfileDay.category.Turkey,"46-60"),
										 new EzMenuProfileDay(EzMenuProfileDay.day.Wednesday,EzMenuProfileDay.category.Pasta,"16-30"),
										 new EzMenuProfileDay(EzMenuProfileDay.day.Thursday,EzMenuProfileDay.category.Fish,"31-45"),
										 new EzMenuProfileDay(EzMenuProfileDay.day.Friday,EzMenuProfileDay.category.Pasta,"0-15"),
										 new EzMenuProfileDay(EzMenuProfileDay.day.Saturday,EzMenuProfileDay.category.Chicken,"46-60")
										};
		
		try {
			List<ITable> profList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			assertEquals(profList.size(),1);
			EzMenuProfile prof = (EzMenuProfile) profList.get(0);
			String profName = "UpdatedProfileName";
						
			EzMenuProfile newProfile = new EzMenuProfile(prof.getId(),profName);
			newProfile.addProfileDay(updateDays[0]);
			newProfile.addProfileDay(updateDays[1]);
			newProfile.addProfileDay(updateDays[2]);
			newProfile.addProfileDay(updateDays[3]);					
			newProfile.addProfileDay(updateDays[4]);
			newProfile.addProfileDay(updateDays[5]);
			newProfile.addProfileDay(updateDays[6]);			
			RunDMLRequestFactory.makeUpdateRequest(profMapper, newProfile);
			
			List<ITable> updatedProfList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			assertEquals(updatedProfList.size(),1);
			
			for (ITable table : updatedProfList) {
				EzMenuProfile updatedProf= (EzMenuProfile) table;
				assertEquals(updatedProf.getId(),prof.getId());				
				assertEquals(updatedProf.getName(),profName);
				
				for (int day = EzMenuProfileDay.SUNDAY; day < updateDays.length;day++) {
					
					assertEquals(updatedProf.getProfileDay(day).getDay(),updateDays[day].getDay());
					assertEquals(updatedProf.getProfileDay(day).getCategory(),updateDays[day].getCategory());					
					assertEquals(updatedProf.getProfileDay(day).getprepTime(),updateDays[day].getprepTime());

				}
			}
			
			
		} catch (RunDMLException rdex) {
			failException(rdex);
		}

	}
	
	@Test
	void testDelete() {
				
		try {
			//Test Default Insert of Profile
			EzMenuProfile profile = new EzMenuProfile("InsertProfileTest");
			RunDMLRequestFactory.makeInsertRequest(profMapper, profile);
			List<ITable> profList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			assertEquals(profList.size(),1);

			//Test Delete of Profile
			List<ITable> deleteProfList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			for ( ITable table : deleteProfList) {
				EzMenuProfile prof = (EzMenuProfile) table;
				RunDMLRequestFactory.makeDeleteRequest(profMapper, prof);
			}
			//Should have an empty table.
			List<ITable> deleteList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			assertEquals(deleteList.size(),0);

		} catch (RunDMLException rdex) {
			failException(rdex);
		}

	}
	
	private static void deleteAllRows() {
		try {
			List<ITable> profList = RunDMLRequestFactory.makeSelectRequest(profMapper);
			for(ITable prof :  profList) {
				EzMenuProfile prof2 = (EzMenuProfile) prof;
				RunDMLRequestFactory.makeDeleteRequest(profMapper, prof2);
			}
		} catch (RunDMLException rdex) {
			failException(rdex);
		}
	}
	
	private static void failException(RunDMLException rdex) {

		logger.error(rdex.getMessage(), rdex);
		fail("RunDMLException");

	}

}
