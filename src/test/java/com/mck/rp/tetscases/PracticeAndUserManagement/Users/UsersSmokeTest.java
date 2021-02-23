package com.mck.rp.tetscases.PracticeAndUserManagement.Users;

import com.aventstack.extentreports.Status;
import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.ExtentReportListener;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.ClinicalContentPage;
import com.mck.rp.pageObjects.DataManagementPage;
import com.mck.rp.pageObjects.LoginPage;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.io.File;
import java.util.List;
import java.util.Random;
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
    ClinicalContentPage cp;
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
        cp = new ClinicalContentPage(driver);

        try {
            loginPage.doLogin(prop.getProperty("defaultUsername"), prop.getProperty("defaultPassword"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 1, groups = "smoke", description = "Practice And User Management - Users - Validation of search by users in Users Table " +
            " and Filter the table by Status, and Type. Validation of Export, Pagination and Sort functionality" +
            " Validation of View user details and upload users popup. ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice And User Management - Users - Validation of search by users in Users Table" +
            " and Filter the table by Status, and Type. Validation of Export, Pagination and Sort functionality" +
            " Validation of View user details and upload users popup.")
    public void userCreateSearchAndFilter() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Users");
            sa.assertEquals(rp.getPageHeading(), "Users", "Incorrect Page Heading");
            sa.assertEquals(rp.getGridHeading(), "Users", "incorrect table heading");

            //Export
            rp.clickButton("Export");
            rp.clickSubListItem("Export as Excel (.xlsx)"); //User Page
            eu.syncWait(3);
            isFileDownloaded("./src/test/resources/Downloads","RP_UserMgmt_" );
            sa.assertTrue(isFileDownloaded("./src/test/resources/Downloads","RP_UserMgmt_" ), "File not downloaded.");
            AllureReportListener.saveLogs("File exported");

            int beforeFilter = rp.getNumOfGridResults();
            String beforePagination = eu.getElement(rp.gridResults).getText();
            rp.srhRegDrugDiagAndEnter("rp");
            if (rp.getNumOfGridResults() > 0) {
                int afterFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterFilter, "Pagination - Results are not updated by grid search");

                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

               /* Type dropdown not available for non McK User -commenting this part
                rp.srhRegDrugDiagAndEnter("");
                rp.clickGridFilters("user-type-select", "Type");
                rp.selectFilterItemByIndex(0);
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Regimen Class filter criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the selected Regimen Class filter criteria: " + rp.getNumOfGridResults());
                int afterTypeFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterTypeFilter);
                //eu.clickWhenReady(rp.getFilterSelectClear("user-type-select"), 3);*/

                //filter by status
                rp.clickGridFilters("user-status-select", "Status");
                rp.selectFilterItemByName("Active");
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

                    //sort by name
                    rp.srhRegDrugDiagAndEnter("rp");
                    List<String> beforeSortList = rp.tableColumnList(dp.userTable, 0);
                    System.out.println("NameSort: Before: " + beforeSortList);
                    List<String> sortList = cp.sortItemsListIgnoreCase(beforeSortList, "desc");
                    rp.clickTableHeaderForSort("Last, First");
                    eu.syncWait(2);
                    List<String> afterSortList = rp.tableColumnList(dp.userTable, 0);
                    System.out.println("EmailSort: Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                    sa.assertEquals(sortList, afterSortList, "Incorrect sort by user name result");
                    rp.clickTableHeaderForSort("Last, First");

                    //sort by email
                    beforeSortList = rp.tableColumnList(dp.userTable, 1);
                    sortList = cp.sortItemsListIgnoreCase(beforeSortList, "desc");
                    rp.clickTableHeaderForSort("Email");
                    eu.syncWait(2);
                    afterSortList = rp.tableColumnList(dp.userTable, 1);
                    System.out.println("Email: Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                    sa.assertEquals(sortList, afterSortList, "Incorrect sort by user email result");
                    rp.clickTableHeaderForSort("Email");

                    //view user details
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
                /*
                eu.doClick(dp.createNew);
                eu.syncWait(2);
                sa.assertEquals(rp.getPageHeading(), "User Account");
                sa.assertEquals(rp.getGridHeading(), "User Details");
                eu.scrollToBottom();
                rp.clickButton("Cancel");
                 */
            } else {
                sa.fail("No records exists in table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException | StaleElementReferenceException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }


    @Test(priority = 2, groups = "regression", description = "Practice And User Management - Users - Create and Edit User in a practice.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice And User Management - Users - Create and Edit User in a practice.")
    public void userCreateUser() {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Users");
            sa.assertEquals(rp.getPageHeading(), "Users", "Invalid page heading" );
            sa.assertEquals(rp.getGridHeading(), "Users", "Invalid grid heading");

            int beforeCreatingUserCount = rp.getNumOfGridResults();
            eu.doClick(dp.createNew);
            eu.syncWait(2);
            sa.assertEquals(rp.getPageHeading(), "User Account", "Invalid user details page heading");
            sa.assertEquals(rp.getGridHeading(), "User Details", "Invalid user details grid heading");

            String lastName = "User" + new Random().nextInt(1000);
            String email = lastName + "@" + lastName + ".com";
            AllureReportListener.saveLogs("New user to add: " + lastName + " - Email -" + email);
            rp.sendKeysByAction("firstName", "Auto");
            rp.sendKeysByAction("lastName", lastName);
            rp.sendKeysByAction("email", email);
            sa.assertEquals(eu.getTextcontent(cp.getDropdownSelectedValue("default-location")),
               "Chicago Ridge, 10604 SW Hwy, Suite 200, Chicago Ridge, IL",  "Incorrect default location autoselected.");
            eu.scrollToBottom();
            eu.syncWait(2);
            rp.clickSpanText("Basic Practice Management");
            rp.clickSpanText("User Management");

            rp.clickSpanText("Practice Configurations");
            rp.clickSpanText("Practice Fee Schedules");
            rp.clickSpanText("Practice Supportive Care Regimen Management");

            rp.clickSpanText("Patient Analysis");
            rp.clickSpanText("Practice Analysis");

            eu.scrollToBottom();
            rp.clickSubmit("Save");
            eu.syncWait(3);
            eu.doClick(cp.getConfirmDialogBtn("Close"));
            eu.syncWait(1);

            //valiate for created user name, eamil, type, status etc.
            if (eu.getGridRowCount(dp.userTable) > 0) {
                System.out.println(email);
                sa.assertEquals(rp.getRowCellData(dp.userTable, 0)[0], lastName+", Auto", "Incorrect username in table");
                sa.assertEquals(rp.getRowCellData(dp.userTable, 0)[1], email, "Incorrect user email in table");
                sa.assertTrue(rp.getRowCellData(dp.userTable, 0)[4].contains("Not Invited"), "Incorrect user status in table" );
                sa.assertEquals(eu.getTextcontent(cp.getLinkInTable(0, 5)), "Send Invitation", "Incorrect invitation status");
            }

            //validate total user count is updated
            rp.srhRegDrugDiagAndEnter("");
            int afterCreatingUserCount = rp.getNumOfGridResults();
            AllureReportListener.saveLogs("Before user count " + beforeCreatingUserCount + " After user count: " + afterCreatingUserCount);
            sa.assertTrue(afterCreatingUserCount>beforeCreatingUserCount, "User count incorrect after adding user.");
            rp.clickLeftMenuItem("Practice & User Management");

            //Edit user
            rp.srhRegDrugDiagAndEnter(email);
            AllureReportListener.saveLogs("User to edit " + email);
            if(eu.getGridRowCount(dp.userTable) > 0) {
                rp.clickGridCell("table", 1, 1);
                eu.syncWait(1);
                eu.doSendKeys(rp.getInputField("lastName"), "Updated");
                String afterUpdatelastName = eu.getAttribute(rp.getInputField("lastName"), "defaultValue");
                AllureReportListener.saveLogs("User last name after edit " + afterUpdatelastName);
                eu.scrollToBottom();
                eu.syncWait(1);
                rp.clickSubmit("Save");
                eu.syncWait(3);
                eu.doClick(cp.getConfirmDialogBtn("Close"));
                eu.syncWait(1);

                rp.srhRegDrugDiagAndEnter(afterUpdatelastName);
                sa.assertEquals(eu.getGridRowCount(dp.userTable), 1, "User record was not updated.");
                eu.syncWait(1);
            }else{
                AllureReportListener.saveLogs("Unable to search for created user. Check. ");
                sa.fail("Unable to search for created user.");
            }

        } catch (NoSuchElementException | InterruptedException | StaleElementReferenceException e) {
            //ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();

    }

    public boolean isFileDownloaded(String downloadPath, String fileName) {
        File dir = new File(downloadPath);
        File[] dirContents = dir.listFiles();

        for (int i = 0; i < dirContents.length; i++) {
            if (dirContents[i].getName().contains(fileName)) {
                AllureReportListener.saveLogs("File Exported: " + dirContents[i].getName());
                return true;
            }
        }
        return false;
    }

}
