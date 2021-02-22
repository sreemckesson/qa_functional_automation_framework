package com.mck.rp.tetscases.OrganizationsPracticeAndUserManagement.Users;

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
import java.io.File;
import java.util.List;
import java.util.Random;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(AllureReportListener.class)
public class UserManagementSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    LoginPage lp;
    ClinicalContentPage cp;
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
            loginPage.doLogin(prop.getProperty("mckessonUser"), prop.getProperty("mckessonPassword"));
            //check current selected location and navigate to All Practices
            String selectedLocation = eu.getElement(cp.selectedLocation).getText();
            if (!selectedLocation.equals("All practices")) {
                eu.doClick(cp.selectedLocation);
                eu.doClick(cp.allPractices);
                selectedLocation = eu.getElement(cp.selectedLocation).getText();
                System.out.println("selectedLocation: " + selectedLocation);
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
    }


    @Test(priority = 1, groups = "smoke", description = "All Practices - User Management - Users - Validation of search by users in Users Table and Filter the table by Status, Type and Level. \"\n"
        + "Validation of Export. Validation of Pagination and Sort functionality. "
        + "Upload users popup. ")
    @Severity(SeverityLevel.NORMAL)
    @Description("All Practices - User Management - Users - Validation of search by users in Users Table and Filter the table by Status, Type and Level. "
                     + "Validation of Export. Validation of Pagination and Sort functionality. "
                     + "Upload users popup. ")
    public void userSearchSortAndFilter() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Organizations, Practices, & Users");
            rp.clickTabs("Users");
            sa.assertEquals(rp.getPageHeading(), "Organizations, Practices, & Users", "Page Header incorrect in users tab.");
            sa.assertEquals(rp.getGridHeading(), "Users", "Grid Header incorrect in users tab.");

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
                sa.assertNotEquals(beforeFilter, afterFilter, "Incorrect search results for text 'rp'.");
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

                //search for username
                rp.srhRegDrugDiagAndEnter("automation10");
                if (rp.getNumOfGridResults() == 1) {
                    sa.assertEquals(rp.getNumOfGridResults(), 1, "Incorrect search by name results for text 'automation10'.");
                    AllureReportListener.saveLogs("Number of records that match the given username search criteria: " + rp.getNumOfGridResults());
                }
                rp.srhRegDrugDiagAndEnter("");

                //search for email
                rp.srhRegDrugDiagAndEnter("Rpautomation10@gmail.com");
                if (rp.getNumOfGridResults() == 1) {
                    sa.assertEquals(rp.getNumOfGridResults(), 1, "Incorrect search by name results for text 'rpautomation10@gmail.com'.");
                    AllureReportListener.saveLogs("Number of records that match the given email search criteria: " + rp.getNumOfGridResults());
                }


                // Type Filter
                rp.srhRegDrugDiagAndEnter("");
                rp.clickGridFilters("user-type-select", "Type");
                rp.selectFilterItemByIndex(0);
                AllureReportListener.saveLogs("Number of records that match the selected type filter criteria: " + rp.getNumOfGridResults());
                int afterTypeFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterTypeFilter, "Incorrect filter results for 'Type' filter. ");
                eu.clickWhenReady(rp.getFilterSelectClear("user-type-select"), 3);

                rp.clickGridFilters("user-status-select", "Status");
                rp.selectFilterItemByName("Expired");
                AllureReportListener.saveLogs("Number of records that match the selected status filter criteria: " + rp.getNumOfGridResults());
                int afterStatusFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterStatusFilter, "Incorrect filter results for 'Status' filter. ");
                eu.clickWhenReady(rp.getFilterSelectClear("user-status-select"), 3);

                rp.clickGridFilters("user-level-select", "Level");
                cp.selectFilterItemByIndex(cp.levelRadioButton, 1);
                AllureReportListener.saveLogs("Number of records that match the selected level filter criteria: " + rp.getNumOfGridResults());
                int afterLevelFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterLevelFilter, "Incorrect filter results for 'Level' filter. ");
                eu.clickWhenReady(cp.getFilterSelectClear("user-level-select"), 3);

                if ((rp.getNumOfGridResults() > 0)) {
                    //Pagination - need to update for number of rows
                    rp.selectPaginationRows("10");
                    eu.syncWait(1);
                    String afterPagination = eu.getElement(rp.gridResults).getText();
                    sa.assertNotEquals(beforePagination, afterPagination, "Incorrect number of records per page.");

                    //sort by name
                    rp.srhRegDrugDiagAndEnter("rp");
                    List<String> beforeSortList = rp.tableColumnList(dp.userTable, 0);
                    List<String> sortList = cp.sortItemsListIgnoreCase(beforeSortList, "desc");
                    rp.clickTableHeaderForSort("Last, First");
                    eu.syncWait(2);
                    List<String> afterSortList = rp.tableColumnList(dp.userTable, 0);
                    System.out.println("Name Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                    sa.assertEquals(sortList, afterSortList, "Incorrect sort by user name result");
                    rp.clickTableHeaderForSort("Last, First");

                    //sort by email
                    rp.srhRegDrugDiagAndEnter("");
                    rp.srhRegDrugDiagAndEnter("automation");
                    eu.syncWait(2);
                    beforeSortList = rp.tableColumnList(dp.userTable, 1);
                    sortList = cp.sortItemsListIgnoreCase(beforeSortList, "desc");
                    rp.clickTableHeaderForSort("Email");
                    eu.syncWait(2);
                    afterSortList = rp.tableColumnList(dp.userTable, 1);
                    System.out.println("Email Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                    sa.assertEquals(sortList, afterSortList, "Incorrect sort by user email result");
                    rp.clickTableHeaderForSort("Email");

                    //View user
                    rp.srhRegDrugDiagAndEnter(srhUser);
                    String nameFromTable = rp.getRowCellData(dp.userTable, 0)[0];
                    eu.syncWait(1);
                    rp.clickGridCell("table", 1, 1);
                    eu.syncWait(1);
                    sa.assertEquals(rp.getPageHeading(), "User Account", "Incorrect page heading");
                    sa.assertEquals(rp.getGridHeading(), "User Details", "Incorrect grid heading");
                    String firstName = eu.getAttribute(rp.getInputField("firstName"), "defaultValue");
                    String lastName = eu.getAttribute(rp.getInputField("lastName"), "defaultValue");
                    String nameFromDetails = lastName + ", " + firstName;
                    sa.assertEquals(nameFromTable, nameFromDetails, "Incorrect selected user name in details");
                    eu.scrollToBottom();
                    rp.clickButton("Cancel");
                    eu.syncWait(1);
                    rp.clickLeftMenuItem("Practice & User Management");
                }
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }

        } catch (NoSuchElementException | InterruptedException | StaleElementReferenceException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }


    @Test(priority = 2, groups = "smoke", description = "All Practices - User Management - Users - Validation of Create New users. ",
          dataProvider = "user-type")
    @Severity(SeverityLevel.NORMAL)
    @Description("All Practices - User Management - Users - Validation of Create New users. ")
    public void userCreate(String userType) {
        SoftAssert sa = new SoftAssert();
        Random rand = new Random();
        try {
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Organizations, Practices, & Users");
            rp.clickTabs("Users");
            sa.assertEquals(rp.getPageHeading(), "Organizations, Practices, & Users", "Page Header incorrect in users tab.");
            sa.assertEquals(rp.getGridHeading(), "Users", "Grid Header incorrect in users tab.");

            int beforeCreatingUserCount = rp.getNumOfGridResults();
            String newUser = "testautouser" + rand.nextInt(1000);
            String email = newUser + "@" + newUser + ".com";
            AllureReportListener.saveLogs("New " + userType + " user to add: " + newUser + " - Email -" + email);
            eu.doClick(dp.createNew);
            eu.syncWait(1);
            sa.assertEquals(rp.getPageHeading(), "User Account", "User Account page not displayed");
            sa.assertEquals(rp.getGridHeading(), "User Details", "User details not displayed");
            eu.doSendKeys(rp.getInputField("firstName"), newUser);
            eu.doSendKeys(rp.getInputField("lastName"), newUser);
            eu.doSendKeys(rp.getInputField("email"), email);
            if(userType.equals("McKesson")) {
                rp.clickSpanText("McKesson User");
            }else {
                rp.clickSpanText("Customer");
            }
            eu.doClick(cp.getDropdownByName("organization-practice-level"));
            cp.selectCheckboxFilterItemByName("Regimen Profiler");
            eu.doActionsClick(cp.userDetails);
            eu.doClick(cp.getDropdownByName("default-location"));
            cp.selectDropdownListValueByName("Adams County Cancer Center");
            cp.selectRadioSelectFilterItemByName("Adams County Cancer Center, 285 Medical Center Dr., Seaman, OH");

            //validate permissions for Mckesson User
            if(userType.equals("McKesson")) {
                sa.assertEquals(eu.getElements(cp.permissionsBlock).get(1).getText(), "Clinical Content",
                    "Missing permissions block - Clinical Content");
                sa.assertEquals(eu.getElements(cp.permissionsBlock).get(2).getText(), "Organizations, Practices and Users",
                    "Missing permissions block - Organizations, Practices and Users");
                sa.assertEquals(eu.getElements(cp.permissionsBlock).get(3).getText(), "Practice Administration",
                    "Missing permissions block - Practice Administration");
                sa.assertEquals(eu.getElements(cp.permissionsBlock).get(4).getText(), "Analysis and Reports",
                    "Missing permissions block - Analysis and Reports");
            }else{
                sa.assertEquals(eu.getElements(cp.permissionsBlock).get(1).getText(), "Organizations, Practices and Users",
                    "Missing permissions block - Organizations, Practices and Users");
                sa.assertEquals(eu.getElements(cp.permissionsBlock).get(2).getText(), "Practice Administration",
                    "Missing permissions block - Practice Administration");
                sa.assertEquals(eu.getElements(cp.permissionsBlock).get(3).getText(), "Analysis and Reports",
                    "Missing permissions block - Analysis and Reports");
            }


            //select permissions
            if(userType.equals("McKesson")) {
                eu.scrollToView(cp.getSpanTextElement("Regimen Management"));
                eu.doClick(cp.getSpanTextElement("Regimen Management"));
                eu.doClick(cp.getSpanTextElement("Organization Management"));
            } else{
                eu.scrollToView(cp.getSpanTextElement("Basic Practice Management"));
                eu.doClick(cp.getSpanTextElement("Basic Practice Management"));
            }
            eu.doClick(cp.getSpanTextElement("Patient Analysis"));
            eu.scrollToBottom();
            rp.clickSubmit("Save");
            eu.syncWait(5);
            eu.doClick(cp.getConfirmDialogBtn("Close"));
           // eu.doClick(cp.getConfirmDialogBtn("Yes"));
           // eu.doClick(cp.getConfirmDialogBtn("Yes"));
            eu.syncWait(1);

            //valiate for created user name, eamil, type, status etc.
            if (eu.getGridRowCount(dp.userTable) > 0) {
                System.out.println(email);
                sa.assertEquals(rp.getRowCellData(dp.userTable, 0)[0], newUser+", "+newUser, "Incorrect username in table");
                sa.assertEquals(rp.getRowCellData(dp.userTable, 0)[1], email, "Incorrect user email in table");
                sa.assertEquals(rp.getRowCellData(dp.userTable, 0)[2], userType, "Incorrect user type in table");
                sa.assertTrue(rp.getRowCellData(dp.userTable, 0)[5].contains("Not Invited"), "Incorrect user status in table" );
                sa.assertEquals(eu.getTextcontent(cp.getLinkInTable(0, 6)), "Send Invitation", "Incorrect invitation status");
            }

            //validate total user count is updated
            rp.srhRegDrugDiagAndEnter("");
            int afterCreatingUserCount = rp.getNumOfGridResults();
            AllureReportListener.saveLogs("Before user count " + beforeCreatingUserCount + " After user count: " + afterCreatingUserCount);
            sa.assertTrue(afterCreatingUserCount>beforeCreatingUserCount, "User count incorrect after adding user.");
            rp.clickLeftMenuItem("Practice & User Management");

        } catch (NoSuchElementException | InterruptedException | StaleElementReferenceException e) {
            sa.fail();
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }



    @Test(priority = 3, groups = "smoke", description = "All Practices - User Management - Users - Validation of edit user. ")
    @Severity(SeverityLevel.NORMAL)
    @Description("All Practices - User Management - Users - Validation of edit user. ")
    public void userEdit() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Organizations, Practices, & Users");
            rp.clickTabs("Users");
            sa.assertEquals(rp.getPageHeading(), "Organizations, Practices, & Users", "Page Header incorrect in users tab.");
            sa.assertEquals(rp.getGridHeading(), "Users", "Grid Header incorrect in users tab.");
            eu.syncWait(1);
            rp.clickGridFilters("user-type-select", "Type");
            rp.selectFilterItemByIndex(0);
            rp.srhRegDrugDiagAndEnter("testautouser");

            if(eu.getGridRowCount(dp.userTable) > 0) {
                String nameInUserList = rp.getRowCellData(dp.userTable, 0)[0];
                AllureReportListener.saveLogs("User to edit " + nameInUserList);
                rp.clickGridCell("table", 1, 1);
                eu.syncWait(1);
                String firstName = eu.getAttribute(rp.getInputField("firstName"), "defaultValue");
                String lastName = eu.getAttribute(rp.getInputField("lastName"), "defaultValue");
                String nameFromDetails = lastName + ", " + firstName;
                sa.assertEquals(nameInUserList, nameFromDetails, "Selected user account name is not displayed in user account page.");
                eu.doSendKeys(rp.getInputField("firstName"), "Updated");
                String afterUpdatefirstName = eu.getAttribute(rp.getInputField("firstName"), "defaultValue");
                AllureReportListener.saveLogs("User name after edit " + afterUpdatefirstName);
                eu.scrollToBottom();
                eu.syncWait(1);
                rp.clickSubmit("Save");
                eu.syncWait(3);
                eu.doClick(cp.getConfirmDialogBtn("Close"));
                eu.syncWait(1);

                rp.srhRegDrugDiagAndEnter(afterUpdatefirstName);
                sa.assertEquals(eu.getGridRowCount(dp.userTable), 1, "User record was not updated.");
                eu.syncWait(1);

                //revert changes
                rp.clickGridCell("table", 1, 1);
                eu.syncWait(1);
                rp.sendKeysByAction("firstName", firstName);
                eu.scrollToBottom();
                rp.clickSubmit("Save");
                eu.syncWait(3);
                eu.doClick(cp.getConfirmDialogBtn("Close"));

            }


        } catch (NoSuchElementException | StaleElementReferenceException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }



    public boolean isFileDownloaded(String downloadPath, String fileName) {
        File dir = new File(downloadPath);
        File[] dirContents = dir.listFiles();

        for (int i = 0; i < dirContents.length; i++) {
            if (dirContents[i].getName().contains(fileName)) {
                // File has been found, it can now be deleted:
               // dirContents[i].delete();
                AllureReportListener.saveLogs("File Exported: " + dirContents[i].getName());
                return true;
            }
        }
        return false;
    }

    @DataProvider(name = "user-type")
    public Object[][] userType(){
        return new Object[][] {{"McKesson"}, {"Customer"}};
    }

}

