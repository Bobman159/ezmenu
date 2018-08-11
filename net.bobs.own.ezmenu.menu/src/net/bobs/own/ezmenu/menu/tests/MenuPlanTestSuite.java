package net.bobs.own.ezmenu.menu.tests;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

//Launch Config for this suite must use JUnit 4 test runner in Eclipse Oxygen
@RunWith(JUnitPlatform.class)
@SelectClasses({NotEnoughMealsAllCategoriesTest.class, 
                ExactNumberMealsMenuPlanTest.class,
                ExactMoreThanMealsMenuPlanTest.class,
                MoreThanMealsMenuPlanTest.class
               })


public class MenuPlanTestSuite {

}
