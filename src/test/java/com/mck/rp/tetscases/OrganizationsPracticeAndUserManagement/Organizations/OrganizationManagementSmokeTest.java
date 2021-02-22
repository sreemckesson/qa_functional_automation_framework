package com.mck.rp.tetscases.OrganizationsPracticeAndUserManagement.Organizations;

import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.ClinicalContentPage;
import com.mck.rp.pageObjects.DataManagementPage;
import com.mck.rp.pageObjects.LoginPage;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.util.Random;
import org.openqa.selenium.NoSuchElementException;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(AllureReportListener.class)
public class OrganizationManagementSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    LoginPage lp;
    ClinicalContentPage cp;

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.NORMAL)
    @BeforeClass
    public void UsersSmokeTestSetUp() {
        rp = new RegimenAnalysisPage(driver);
        dp = new DataManagementPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);
        cp = new ClinicalContentPage(driver);

        try {
            loginPage.doLogin(prop.getProperty("mckessonUser"), prop.getProperty("mckessonPassword"));
            //check current selected location and navigate to All Practices
            String selectedLocation = eu.getElement(cp.selectedLocation).getText();
            if (!selectedLocation.equals("All practices")) {
                eu.doClick(cp.selectedLocation);
                eu.doClick(cp.allPractices);
                selectedLocation = eu.getElement(cp.selectedLocation).getText();
                System.out.println("selectedLocation: " + selectedLocation);
            }
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Organizations, Practices, & Users");
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
    }


    @Test(priority = 1, groups = "smoke", description = "All Practices - Organizations Management - Create Organization. Search for created "
        + "organization.")
    @Severity(SeverityLevel.NORMAL)
    @Description("All Practices - Organizations Management - Create Organization. Search for created organization. ")
    public void createOrginizationTest(ITestContext context) {

        SoftAssert sa = new SoftAssert();
        Random rand = new Random();
        int expectedLevelInModel;
        String newOrg;

        try {

            sa.assertEquals(rp.getPageHeading(), "Organizations, Practices, & Users", "Page Header incorrect in Organizations tab.");
            eu.syncWait(1);
            sa.assertEquals(rp.getGridHeading(), "Organizations & Practices", "Grid Header incorrect in Organizations tab.");

            //check if organization tree is in expanded or collaspsed view
            if(eu.getElements(cp.orgList).size()==1){
                AllureReportListener.saveLogs("Expanding Org Tree view.");
                cp.expandCollapseOrgByName("Regimen Profiler");
            }
            eu.syncWait(1);

            //check if testorg1 is already created if not create new org testorg1. If already exists create sub org under testorg1
            if(cp.isOrgPresent(cp.orgListNames,"TestOrg1")){
                newOrg = "TestSubOrg" + rand.nextInt(1000);
                createOrgAtLevel("TestOrg1", newOrg);
                expectedLevelInModel = 2;
                AllureReportListener.saveLogs("Creating new sub org " + newOrg);
            }else{
                newOrg = "TestOrg1";
                createOrgAtLevel("Regimen Profiler", newOrg);
                expectedLevelInModel = 1;
                AllureReportListener.saveLogs("Creating org TestOrg1 under regimen profiler");
            }
            sa.assertEquals(eu.getElements(cp.levelInModel).size(), expectedLevelInModel, "Level displayed is incorrect in create org modal.");
            sa.assertEquals(rp.getSelectedFilterItem("status-select"), "Active", "Incorrect default status");
            System.out.println("status selected is " + rp.getSelectedFilterItem("status-select"));
            //rp.clickButton("Cancel");
            //rp.clickButton("Yes");
            rp.clickSubmit("Save");
            rp.clickButton("Close");
            context.setAttribute("NewOrgCreated", newOrg);

            //search for created org
            eu.syncWait(2);
            rp.srhRegDrugDiagAndEnter(newOrg);
            sa.assertTrue(cp.isOrgPresent(cp.orgPracListNames, newOrg), "Unable to search for newly created organization." + newOrg);
            AllureReportListener.saveLogs("Search for Org " + newOrg);

            //search for existing practice
            eu.syncWait(2);
            rp.srhRegDrugDiagAndEnter("Demo Practice 1");
            sa.assertTrue(cp.isOrgPresent(cp.orgPracListNames,"Demo Practice 1"), "Unable to search for existing practice - Demo Practice 1.");
            AllureReportListener.saveLogs("Search for Practice Demo Practice 1");
            rp.srhRegDrugDiagAndEnter("Regimen Profiler");

        }catch (NoSuchElementException | InterruptedException e) {
            sa.fail(e.getMessage());
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 2, groups = "smoke", description = "All Practices - Organizations Management - Edit Organization. Search for edited "
        + "organization.", dependsOnMethods = { "createOrginizationTest" })
    @Severity(SeverityLevel.NORMAL)
    @Description("All Practices - Organizations Management - Edit Organization. Search for edited organization. ")
    public void editOrginizationTest(ITestContext context) {

        SoftAssert sa = new SoftAssert();
        Random rand = new Random();
        int expectedLevelInModel;

        try {
            //check if organization tree is in expanded or collaspsed view
            eu.syncWait(1);
            if(eu.getElements(cp.orgList).size()==1){
                AllureReportListener.saveLogs("Expanding Org Tree view.");
                cp.expandCollapseOrgByName("Regimen Profiler");
            }
            eu.syncWait(1);

            String newOrg = (String) context.getAttribute("NewOrgCreated");
            if (newOrg.equals("TestOrg1")){
                //create sub org to edit
                newOrg = "TestSubOrg" + rand.nextInt(1000);
                createOrgAtLevel("TestOrg1", newOrg);
                rp.clickSubmit("Save");
                rp.clickButton("Close");
                AllureReportListener.saveLogs("New org created to edit." + newOrg);
            }


          //  String newOrg = "TestSubOrg228";
            eu.syncWait(2);
            rp.srhRegDrugDiagAndEnter(newOrg);
            cp.clickOrgPracticeByName(newOrg);
            eu.doSendKeys(rp.getInputField("name"), "Updated");
            rp.clickSpanText("Active");
            cp.selectFilterItemByName("Inactive");
            rp.clickSubmit("Save");
            rp.clickButton("Close");


            //search for updated org
            eu.syncWait(2);
            rp.srhRegDrugDiagAndEnter(newOrg+"Updated");
            sa.assertTrue(cp.isOrgPresent(cp.orgPracListNames, newOrg+"Updated"), "Unable to search for updated organization." + newOrg+"Updated");
            sa.assertEquals(rp.getTextContent(cp.getOrgPracStatus(newOrg+"Updated")), "inactive",
                "Status not updated to Inactive for org " + newOrg+"Updated");
            AllureReportListener.saveLogs("Search for Org " + newOrg+"Updated");
            rp.srhRegDrugDiagAndEnter("Regimen Profiler");

        }catch (NoSuchElementException | InterruptedException e) {
            sa.fail(e.getMessage());
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }


    public void createOrgAtLevel(String parentOrgName, String newChildOrgName) throws InterruptedException {
        cp.clickOrgByName(parentOrgName);
        cp.clickCreateNewOrgButtonByName(parentOrgName);
        cp.selectFilterItemByName("Organization");
        eu.syncWait(1);
        eu.doSendKeys(rp.getInputField("name"), newChildOrgName);
    }

}
