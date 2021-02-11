package com.mck.rp.tetscases.PracticeAndUserManagement.PracticeConfigurations;

import com.aventstack.extentreports.Status;
import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.ExtentReportListener;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.DataManagementPage;
import com.mck.rp.pageObjects.LoginPage;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Random;

@Listeners(AllureReportListener.class)
public class PracticeConfigurationsSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    LoginPage lp;

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void UsersSmokeTestSetUp() {
        rp = new RegimenAnalysisPage(driver);
        dp = new DataManagementPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);

        try {
            loginPage.doLogin(prop.getProperty("defaultUsername"), prop.getProperty("defaultPassword"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 1, groups = "smoke", description = "Practice And User Management - Practice Configurations - ")
    @Severity(SeverityLevel.NORMAL)
    @Description("")
    public void practiceConfigurationsCalculations() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Practice Configurations");
            eu.syncWait(6);
            sa.assertEquals(rp.getPageHeading(), "Practice Configurations", "Incorrect Page Heading");
            List<WebElement> headings = eu.getElements(rp.headingGrids);
            sa.assertEquals(headings.get(0).getAttribute("textContent"), "Analysis Setup", "incorrect widget page heading");
            sa.assertEquals(headings.get(1).getAttribute("textContent"), "Calculations", "incorrect Widget heading");
            sa.assertEquals(headings.get(2).getAttribute("textContent"), "Defaults", "incorrect Widget heading");
            sa.assertEquals(headings.get(3).getAttribute("textContent"), "Configure Analysis Views", "incorrect Widget heading");
            eu.syncWait(2);

            //Calculation changes and validation
            String drugCalcMethodBefore = eu.getText(dp.calculationMethodDropdown);
            eu.clickWhenReady(dp.calculationMethodDropdown, 3);
            eu.syncWait(2);
            rp.selectListItemByName("Practice CPT Costs");
            eu.syncWait(2);

            String unitOfMeasureBefore = eu.getText(dp.calculationMethodDropdown);
            eu.clickWhenReady(dp.unitOfMeasure, 5);
            eu.syncWait(2);
            rp.selectListItemByName("Metric");
            eu.syncWait(5);

            /*if (eu.getElement(rp.getInputField("treatmentCostPerHour")).isDisplayed()) {
                rp.sendKeysByAction("treatmentCostPerHour", String.valueOf(new Random().nextInt(200)));

                eu.syncWait(2);
            }*/

            rp.sendKeysByAction("drugInfo.percent", String.valueOf(new Random().nextInt(50)));
            eu.syncWait(2);

            rp.sendKeysByAction("procedureInfo.percent", String.valueOf(new Random().nextInt(30)));
            eu.syncWait(2);




        } catch (NoSuchElementException | InterruptedException | StaleElementReferenceException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

}
