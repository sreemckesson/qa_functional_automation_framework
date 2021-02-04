package com.mck.rp.tetscases.PracticeAndUserManagement.Users;

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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


@Listeners(AllureReportListener.class)
public class UsersSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    LoginPage lp;
    String srhUser = "rpautomation11";

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

    @Test(priority = 1, groups = "smoke", description = "Practice And User Management - Users - Validation of search by users in Users Table " +
            " and Filter the table by Status, and Type and Create New users. Validation of Export, Pagination and Sort functionality" +
            " Validation of View/Edit user details and upload users popup. ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice And User Management - Users - Validation of search by users in Users Table" +
            " and Filter the table by Status, and Type and Create New users. Validation of Export, Pagination and Sort functionality" +
            " Validation of View/Edit user details and upload users popup.")
    public void userCreateSearchAndFilter() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Users");
            sa.assertEquals(rp.getPageHeading(), "Users", "Incorrect Page Heading");
            sa.assertEquals(rp.getGridHeading(), "Users", "incorrect table heading");


            int beforeFilter = rp.getNumOfGridResults();
            String beforePagination = eu.getElement(rp.gridResults).getText();
            rp.srhRegDrugDiagAndEnter("rp");
            if (rp.getNumOfGridResults() > 0) {
                int afterFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterFilter, "Pagination - Results are not updated by grid search");

                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());
                rp.clickButton("Export");
                rp.clickSubListItem("Export as Excel (.xlsx)"); //User Page

               /* Type dropdown not available for non McK User -commenting this part
                rp.srhRegDrugDiagAndEnter("");
                rp.clickGridFilters("user-type-select", "Type");
                rp.selectFilterItemByIndex(0);
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Regimen Class filter criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the selected Regimen Class filter criteria: " + rp.getNumOfGridResults());
                int afterTypeFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterTypeFilter);
                //eu.clickWhenReady(rp.getFilterSelectClear("user-type-select"), 3);*/

                rp.clickGridFilters("user-status-select", "Status");
                rp.selectFilterItemByName("Expired");
                AllureReportListener.saveLogs("Number of records that match the selected Last Updated By filter criteria: " + rp.getNumOfGridResults());
                int afterStatusFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterStatusFilter, "Status Filter - Results are not updating");
                eu.clickWhenReady(rp.getFilterSelectClear("user-status-select"), 3);

                if ((rp.getNumOfGridResults() > 0)) {
                    //Pagination - need to update for number of rows if <10 results
                    rp.selectPaginationRows("10");
                    eu.syncWait(1);
                    String afterPagination = eu.getElement(rp.gridResults).getText();
                    AllureReportListener.saveLogs("Grid Results before Pagination: " + beforePagination);
                    AllureReportListener.saveLogs("Grid Results after Pagination: " + afterPagination);
                    sa.assertNotEquals(beforePagination, afterPagination, "Pagination - Results are not updated for pagination");

                    rp.srhRegDrugDiagAndEnter(srhUser);
                    String nameFromTable = rp.getRowCellData(dp.userTable, 0)[0];
                    eu.syncWait(2);
                    rp.clickGridCell("table", 1, 1);
                    eu.syncWait(2);
                    sa.assertEquals(rp.getPageHeading(), "User Account");
                    sa.assertEquals(rp.getGridHeading(), "User Details");
                    String firstName = eu.getAttribute(rp.getInputField("firstName"), "defaultValue");
                    String lastName = eu.getAttribute(rp.getInputField("lastName"), "defaultValue");
                    String nameFromDetails = lastName + ", " + firstName;
                    sa.assertEquals(nameFromTable, nameFromDetails, "Name - Table name and Details page name are not matching");
                    eu.scrollToBottom();
                    rp.clickButton("Cancel");
                    eu.syncWait(1);
                }
                eu.doClick(dp.createNew);
                eu.syncWait(2);
                sa.assertEquals(rp.getPageHeading(), "User Account");
                sa.assertEquals(rp.getGridHeading(), "User Details");
                eu.scrollToBottom();
                rp.clickButton("Cancel");
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException | StaleElementReferenceException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }


}
