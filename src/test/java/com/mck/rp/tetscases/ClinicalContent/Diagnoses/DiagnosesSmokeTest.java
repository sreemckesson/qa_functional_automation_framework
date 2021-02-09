package com.mck.rp.tetscases.ClinicalContent.Diagnoses;

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
import java.util.List;
import java.util.Random;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(AllureReportListener.class)
public class DiagnosesSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    ClinicalContentPage cp;
    LoginPage lp;
    String srhDiagnoses = "Mesothelioma";

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.NORMAL)
    @BeforeClass
    public void baseFormularySetUp() {
        rp = new RegimenAnalysisPage(driver);
        dp = new DataManagementPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);
        cp = new ClinicalContentPage(driver);

        try {
            loginPage.doLogin(prop.getProperty("mckessonUser"), prop.getProperty("mckessonPassword"));
            // eu.syncWait(2);
            //check current selected location and navigate to All Practices
            String selectedLocation = eu.getElement(cp.selectedLocation).getText();
            if (!selectedLocation.equals("All practices")) {
                eu.doClick(cp.selectedLocation);
                eu.doClick(cp.allPractices);
                // eu.syncWait(2);
                selectedLocation = eu.getElement(cp.selectedLocation).getText();
                AllureReportListener.saveLogs("Selected location " + selectedLocation);
                // eu.syncWait(2);
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
    }

    @Test(priority = 1,
          groups = "smoke",
          description = "Clinical Content - Diagnoses Page - Validation of list view functionality -"
              + " Default filter, Default records per page, columns displayed")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Clinical Content - Diagnoses Page - Validation of list view functionality -" + "Default filter, Default records per page, columns displayed")
    public void diagnosesListView() {

        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Clinical Content");
            // eu.syncWait(1);
            rp.clickByLinkText("Diagnoses");
            sa.assertEquals(rp.getPageHeading(), "Diagnoses", "Incorrect page heading");

            System.out.println("xxxxxxxxxxxxxxx");
            if (rp.getNumOfGridResults() > 0) {
                System.out.println("xxxxxxxxxxxxxxx");
                //default number of rows per page
                String beforePagination = eu.getElement(rp.gridResults).getText();
                System.out.println("0 " + beforePagination.contains("1-20"));
                sa.assertTrue(beforePagination.contains("1-20"), "Incorrect default number of rows per page");

                //default row count in diagnoses table
                System.out.println("1 " + eu.getGridRowCount(cp.diagnosesTable));
                sa.assertEquals(eu.getGridRowCount(cp.diagnosesTable),20, "Incorrect records per page");

                //default filter applied
                System.out.println("2 check... ");
                System.out.println("2 " + rp.getGridFilters("status-select", "Active").isDisplayed());
                sa.assertTrue(rp.getGridFilters("status-select", "Active").isDisplayed(), "Incorrect default filter status");

                //table columns
                System.out.println("3 " + cp.getColumnHeader("table-sort-by_name").getAttribute("textContent").equals("Diagnosis"));
                sa.assertTrue(cp.getColumnHeader("table-sort-by_name").getAttribute("textContent").equals("Diagnosis"), "Incorrect column desc");
                System.out.println("4 " + cp.getColumnHeader("table-sort-by_modified-by").getAttribute("textContent").equals("Last Updated By"));
                sa.assertTrue(cp.getColumnHeader("table-sort-by_modified-by").getAttribute("textContent").equals("Last Updated By"),
                    "Incorrect column Date Last Updated By");
                System.out.println("5 " + cp.getColumnHeader("table-sort-by_modified-at").getAttribute("textContent").equals("Date Last Updated"));
                sa.assertTrue(cp.getColumnHeader("table-sort-by_modified-at").getAttribute("textContent").equals("Date Last Updated"),
                    "Incorrect column Date Last Updated");
                System.out.println("6 " + cp.getColumnHeader("table-sort-by_status").getAttribute("textContent").equals("Status"));
                sa.assertTrue(cp.getColumnHeader("table-sort-by_status").getAttribute("textContent").equals("Status"), "Incorrect column status");

                //default sort applied to name field
                rp.clickLeftMenuItem("Clinical Content");

            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in diagnosis grid table. Check");
            }
        } catch (NoSuchElementException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 2,
          groups = "smoke",
          description = "Clinical Content - Diagnoses - Validation of search functionality by "
              + "Diagnosis. Validation of Pagination. Validation of Sort functionality.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Diagnoses - Validation of search functionality by Diagnosis. Validation of "
                     + "Pagination. Validation of Sort functionality.")
    public void DiagnosesSearch() {

        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist(), "Missing logout button");
            rp.clickLeftMenuItem("Clinical Content");
            // eu.syncWait(1);
            rp.clickByLinkText("Diagnoses");
            sa.assertEquals(rp.getPageHeading(), "Diagnoses", "Incorrect Page Heading");

            if (rp.getNumOfGridResults() > 0) {
                //Pagination
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("10");
                //  eu.syncWait(2);
                String afterPagination = eu.getElement(rp.gridResults).getText();
                sa.assertNotEquals(beforePagination, afterPagination, "Incorrect grid result count after changing page");

                rp.srhRegDrugDiagAndEnter(srhDiagnoses);
                //  eu.syncWait(1);
                int rowCount = eu.getGridRowCount(cp.diagnosesTable);
                for (int i = 0; i < rowCount; i++) {
                    System.out.println("act: " + rp.getRowCellData(cp.diagnosesTable, i)[0].toLowerCase());
                    System.out.println("exp: " + srhDiagnoses.toLowerCase());
                    System.out.println("2nd : " + rp.getRowCellData(cp.diagnosesTable, i)[0].toLowerCase().contains(srhDiagnoses.toLowerCase()));
                    sa.assertTrue(rp.getRowCellData(cp.diagnosesTable, i)[0].toLowerCase().contains(srhDiagnoses.toLowerCase()),
                        "Incorrect search diagnosis name");
                }
                AllureReportListener.saveLogs(
                    "Number of records that match the given search criteria for diagnosis name " + srhDiagnoses + " : " + rp.getNumOfGridResults());

                rp.srhRegDrugDiagAndEnter("Leukemia");

                //Sort by diagnosis name
                List<String> beforeSortList = rp.tableColumnList(cp.diagnosesTable, 0);
                List<String> sortList = eu.sortItemsList(beforeSortList, "desc");
                rp.clickTableHeaderForSort("Diagnosis");
                eu.syncWait(2);
                List<String> afterSortList = rp.tableColumnList(cp.diagnosesTable, 0);
                System.out.println("NameSort: Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                sa.assertEquals(sortList, afterSortList, "Incorrect sort by dignosis name result");

                //sa.assertNotEquals(beforeSortList, afterSortList);
                rp.clickTableHeaderForSort("Diagnosis");
                //  eu.syncWait(2);

                //Sort by date last updated by
                beforeSortList = rp.tableColumnList(cp.diagnosesTable, 2);
                sortList = eu.sortItemsList(beforeSortList, "desc");
                rp.clickTableHeaderForSort("Last Updated By");
                //eu.doClick(rp.srhRegimen);
                eu.syncWait(2);
                afterSortList = rp.tableColumnList(cp.diagnosesTable, 2);
                System.out.println(
                    "last updated by sort: Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                sa.assertEquals(beforeSortList, afterSortList, "Incorrect sort by date last updated by result");
                rp.clickTableHeaderForSort("Last Updated By");
                //  eu.syncWait(2);
                rp.clickLeftMenuItem("Clinical Content");

            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 3, groups = "smoke", description = "Clinical Content - Diagnoses Page - Add Diagnoses")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Diagnoses Page - Add Diagnoses")
    public void addDiagnoses() throws NoSuchElementException {

        SoftAssert sa = new SoftAssert();
        Random rand = new Random();
        String addDiagnoses = "TestDiagnosesAuto" + rand.nextInt(9999);

        try {
            Assert.assertTrue(rp.isLogoutExist(), "Missing logout");
            rp.clickLeftMenuItem("Clinical Content");
            //  eu.syncWait(1);
            rp.clickByLinkText("Diagnoses");
            sa.assertEquals(rp.getPageHeading(), "Diagnoses", "Incorrect page heading");

            //  rp.srhRegDrugDiagAndEnter("TestAutoDiagnoses34567");

            eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 2);
            int beforeGridResults = rp.getNumOfGridResults();
            rp.clickButton("Create New");
            // eu.syncWait(1);
            rp.sendValue("name", addDiagnoses);
            rp.clickDialogBtn("Save");
            eu.clickWhenReady(cp.getConfirmDialogBtn("Close"), 4);
            //eu.doClick(cp.getConfirmDialogBtn("Yes"));
            // eu.syncWait(2);

            //Verify Diagnoses is added
            int afterGridResults = rp.getNumOfGridResults();
            rp.clickByLinkText("Diagnoses");
            eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 2);
            rp.srhRegDrugDiagAndEnter(addDiagnoses);
            if (eu.getGridRowCount(cp.diagnosesTable) == 1) {
                sa.assertTrue(rp.getRowCellData(cp.diagnosesTable, 0)[0].contains(addDiagnoses), "Incorrect new diagnosis name");
                AllureReportListener.saveLogs("Diagnosis added: " + addDiagnoses);
            } else {
                sa.fail();
                AllureReportListener.saveLogs("This search returned more than 1 row and not expected to return more than row. Pl check. ");
            }

            //eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 2);
            //eu.syncWait(2);
            rp.srhRegDrugDiagAndEnter("");
            sa.assertTrue(afterGridResults > beforeGridResults, "Added diagnosis is not returned in the grid table");
            AllureReportListener.saveLogs(
                "Grid results before diagnosis is added: " + beforeGridResults + "after diagnosis is added: " + afterGridResults);
            rp.clickLeftMenuItem("Clinical Content");

        } catch (NoSuchElementException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 4, groups = "smoke", description = "Clinical Content - Diagnoses - Validation of filtering by Status.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Diagnoses - Validation of filtering by Status.")
    public void diagnosesTableFilters() {

        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Clinical Content");
            //  eu.syncWait(2);
            rp.clickByLinkText("Diagnoses");
            sa.assertEquals(rp.getPageHeading(), "Diagnoses");

            int beforeFilter = rp.getNumOfGridResults();
            if (!rp.getTextContent(cp.diagnosesTable).contains("search did not match any results")) {
                rp.clickGridFilters("status-select", "Active");
                rp.selectListItemByName("Inactive");
                AllureReportListener.saveLogs(
                    "Number of records that match the selected Status filter criteria (Active): " + rp.getNumOfGridResults());
                int afterClassificationFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterClassificationFilter, "Incorrect status filter count");
                rp.clickLeftMenuItem("Clinical Content");
            } else {
                AllureReportListener.saveLogs("Diagnoses table has no results");
            }
            eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 5);
        } catch (NoSuchElementException e) {
            AllureReportListener.saveLogs("Test Method Failed");
        }
        sa.assertAll();
    }

    @Test(priority = 5, groups = "smoke", description = "Clinical Content - Diagnoses - Edit Diagnoses")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Diagnoses - Edit Diagnoses")
    public void editDiagnoses() throws NoSuchElementException, InterruptedException {

        SoftAssert sa = new SoftAssert();
        String updatedName = "Updated";

        try {
            rp.clickLeftMenuItem("Clinical Content");
            // eu.syncWait(1);
            rp.clickByLinkText("Diagnoses");
            sa.assertEquals(rp.getPageHeading(), "Diagnoses", "Incorrect page heading");

            if (rp.getNumOfGridResults() > 0) {
                rp.srhRegDrugDiagAndEnter(srhDiagnoses);
                if (eu.getGridRowCount(cp.diagnosesTable) == 1) {
                    String beforeEditname = rp.getRowCellData(cp.diagnosesTable, 0)[0];
                    rp.clickGridCell("diagnoses-formulary-table", 1, 1);
                    eu.getTextcontent(cp.editDrugHeader);
                    sa.assertEquals(eu.getText(cp.editDrugHeader), "Edit Diagnosis", "Incorrect edit diagnoses header");
                    String beforeEditDescValInModal = eu.getAttribute(rp.getInputField("name"), "value");

                    //edit name
                    rp.sendValue("name", beforeEditDescValInModal + updatedName);
                    rp.clickDialogBtn("Save");
                    eu.syncWait(2);
                    String afterEditName = rp.getRowCellData(cp.diagnosesTable, 0)[0];
                    sa.assertNotEquals(beforeEditname, afterEditName, "Diagnoses name is not updated.");

                    AllureReportListener.saveLogs("Diagnoses name Edited: " + afterEditName);

                    //reset edit
                    rp.clickGridCell("diagnoses-formulary-table", 1, 1);
                    rp.sendValue("name", beforeEditDescValInModal);
                    rp.clickDialogBtn("Save");
                } else {
                    AllureReportListener.saveLogs("This search returned more than 1 row and not expected to return more than row. Pl check. ");
                }
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No diagnoses Records exists in diagnoses page. Check");
            }
        } catch (NoSuchElementException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }
}

