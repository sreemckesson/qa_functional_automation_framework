package com.mck.rp.tetscases.ClinicalContent.RegimenManagement;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Listeners(AllureReportListener.class)
public class RegimenManagementSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    LoginPage lp;
    ClinicalContentPage cp;
    DataManagementPage dp;
    String srhDrug = "Abatacept";
    String srhDiag = "Amyloidosis";
    String srhLastUpdated = "System";
    String srhRegimen = "ABVD Q28D";

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void pracAnalysisSetUp() {
        rp = new RegimenAnalysisPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);
        cp = new ClinicalContentPage(driver);
        dp = new DataManagementPage(driver);

        try {
            loginPage.doLogin(prop.getProperty("mckessonUser"), prop.getProperty("mckessonPassword"));
            eu.syncWait(2);

            //check current selected location and navigate to All Practices
            String selectedLocation = eu.getElement(cp.selectedLocation).getText();
            if (!selectedLocation.equals("All practices")) {
                eu.doClick(cp.selectedLocation);
                eu.doClick(cp.allPractices);
                eu.syncWait(2);
                selectedLocation = eu.getElement(cp.selectedLocation).getText();
                System.out.println("selectedLocation: " + selectedLocation);
                eu.syncWait(2);
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }

    }

    @Test(priority = 1, groups = "smoke", description = "Regimen Management Page - Regimen Library - Validation of Search by Regimen Name, Diagnosis, " +
            "Drug or last updated by Results. Pagination")
    @Severity(SeverityLevel.NORMAL)
    @Description("Regimen Management Page - Regimen Library - Validation of Search by Regimen Name, Diagnosis," +
            "Drug or last updated by Results. Pagination")
    public void regManagementGridSearch() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Regimen Management");
            sa.assertEquals(rp.getPageHeading(), "Regimen Management", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Regimens", "Incorrect results grid heading");


            if (rp.getNumOfGridResults() > 0) {
                int rowCountBefore = eu.getGridRowCount(cp.regimenTable);

                //pagination
                eu.syncWait(3);
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("50");
                eu.syncWait(3);
                String afterPagination = eu.getElement(rp.gridResults).getText();
                sa.assertNotEquals(beforePagination, afterPagination, "Pagination - Not working as expected");

                //Search by Drug
                rp.srhRegDrugDiagAndEnter(srhDrug);
                int rowCountDrug = eu.getGridRowCount(cp.regimenTable);
                for (int i = 0; i < rowCountDrug; i++) {
                    sa.assertTrue(rp.getRowCellData(cp.regimenTable, i)[0].contains(srhDrug), "Search by Drug - Incorrect Results");
                }
                AllureReportListener.saveLogs("Search by Drug " + srhDrug + " returned: " + rp.getNumOfGridResults() + " results.");


                //Search by Diagnosis
                rp.srhRegDrugDiagAndEnter(srhDiag);
                int rowCountDiagnosis = eu.getGridRowCount(cp.regimenTable);
                sa.assertNotEquals(rowCountBefore, rowCountDiagnosis, "Search by Diagnosis - Results are not updating");
                //sa.assertTrue(rp.getRowCellData(cp.regimenTable, i)[1].contains(srhDiag), "Search by Diagnosis - Incorrect Results");
                AllureReportListener.saveLogs("Search by Diagnosis " + srhDiag + " returned: " + rp.getNumOfGridResults() + " results.");

                //Search by Last Updated by
                rp.srhRegDrugDiagAndEnter(srhLastUpdated);
                int rowCountLastUpdated = eu.getGridRowCount(cp.regimenTable);
                /*
                 //- showing Angela's name when searched by System as System exists in Regimen Name so adding assertion to the first row only
                for (int i = 0; i < rowCountLastUpdated; i++) {
                    System.out.println(rp.getRowCellData(cp.regimenTable, i)[2].contains(srhLastUpdated)); //showing Angela's name when searched by System
                    sa.assertTrue(rp.getRowCellData(cp.regimenTable, i)[2].contains(srhLastUpdated), "Search by Last Updated By - Incorrect Results");
                }*/
                sa.assertTrue(rp.getRowCellData(cp.regimenTable, 1)[2].contains(srhLastUpdated), "Search by Last Updated By - Incorrect Results");
                AllureReportListener.saveLogs("Search by Drug " + srhLastUpdated + " returned: " + rp.getNumOfGridResults() + " results.");

                //Search by Drug
                rp.srhRegDrugDiagAndEnter(srhRegimen);
                int rowCountRegimen = eu.getGridRowCount(cp.regimenTable);
                System.out.println("Rows:" + rowCountRegimen);
                for (int i = 0; i < rowCountRegimen; i++) {
                    sa.assertTrue(rp.getRowCellData(cp.regimenTable, i)[0].contains(srhRegimen), "Search by Regimen - Incorrect Results");
                }
                AllureReportListener.saveLogs("Search by Drug " + srhRegimen + " returned: " + rp.getNumOfGridResults() + " results.");

            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();

    }

    @Test(priority = 2,groups = "smoke", description = "Clinical Content - Regimen Management - Sort by Regimen Name, Type and " +
            "Date Last Updated columns")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Regimen Management - Sort by Regimen Name, Type and Date Last Updated columns")
    public void regimenManagementSortRegimens() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            //rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Regimen Management");
            sa.assertEquals(rp.getPageHeading(), "Regimen Management", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Regimens", "Incorrect results grid heading");
            rp.srhRegDrugDiagAndEnter(srhDrug);

            if (rp.getNumOfGridResults() > 0) {

                //Sort by Name
                List<String> beforeSortListName = rp.tableColumnList(cp.regimenTable, 0);
                List<String> sortListName = eu.sortItemsList(beforeSortListName, "desc");
                rp.clickTableHeaderForSort("Regimen Name");
                eu.syncWait(3);
                List<String> afterSortListName = rp.tableColumnList(cp.regimenTable, 0);
                //System.out.println("Before: " + beforeSortListName +"\r\n" + "Sort: " + sortListName +"\r\n"+ "After: " + afterSortListName);
                sa.assertEquals(sortListName, afterSortListName, "Sort by Regimen Name Column - Not working");
                rp.clickTableHeaderForSort("Regimen Name");
                eu.syncWait(3);

                //Sort by Type
                List<String> beforeSortListType = rp.tableColumnList(cp.regimenTable, 1);
                List<String> sortListType = eu.sortItemsList(beforeSortListType, "desc");
                rp.clickTableHeaderForSort("Type");
                eu.syncWait(3);
                List<String> afterSortListType = rp.tableColumnList(cp.regimenTable, 1);
                //System.out.println("Before: " + beforeSortListType + "\r\n" + "Sort: " + sortListType + "\r\n" + "After: " + afterSortListType);
                sa.assertEquals(sortListType, afterSortListType, "Sort by Type Column - Not working as expected");
                rp.clickTableHeaderForSort("Type");
                eu.syncWait(3);

                //Sort by Date Last Updated - Need to check
                List<String> beforeSortListDate = rp.tableColumnList(cp.regimenTable, 3);
                List<String> sortListDate = eu.sortItemsList(beforeSortListDate, "desc");
                rp.clickTableHeaderForSort("Date Last Updated");
                //rp.clickTableHeaderForSort("Date Last Updated");
                eu.syncWait(3);
                List<String> afterSortListDate = rp.tableColumnList(cp.regimenTable, 3);
                //System.out.println("Before: " + beforeSortListDate + "\r\n" + "Sort: " + sortListDate + "\r\n" + "After: " + afterSortListDate);
                sa.assertNotEquals(beforeSortListDate, afterSortListDate, "Sort by Date Last Updated Column - Not working as expected");
                //sa.assertEquals(sortListDate, afterSortListDate, "Sort by Date Last Updated Column - Not working as expected");

            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
            AllureReportListener.saveLogs("Test Method Failed");
        }
        sa.assertAll();
    }

    @Test(priority = 3,groups = "smoke", description = "Clinical Content - Regimen Management - Filter by Diagnosis, Drug , Last updated by "+
            " Status and Type grid filters")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Regimen Management - Filter by Diagnosis, Drug , Last updated by " +
            " Status and Type grid filters")
    public void regimenManagementTableFilters() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Regimen Management");
            sa.assertEquals(rp.getPageHeading(), "Regimen Management", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Regimens", "Incorrect results grid heading");

            if (rp.getNumOfGridResults() > 0) {
                AllureReportListener.saveLogs("Rgimens Row Count: " + eu.getGridRowCount(cp.regimenTable) + " and " +
                        "Column Count " + eu.getGridColumnCount(cp.regimenTable));
                int beforeFilter = rp.getNumOfGridResults();

                //Filter results by Diagnosis filter
                rp.clickGridFilters("diagnosis-select", "Diagnosis");
                rp.selectFilterItemByName("Amyloidosis");
                if (!rp.getTextContent(cp.regimenTable).contains("search did not match any results")) {
                    AllureReportListener.saveLogs("Number of records that match the selected Diagnosis filter criteria: " + rp.getNumOfGridResults());
                    int afterDiagFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDiagFilter,
                            "Diagnosis Filter - Not working as expected");
                } else {
                    AllureReportListener.saveLogs("Diagnosis filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("diagnosis-select"), 5);

                //Filter results by Drug filter
                rp.clickGridFilters("drugs-select", "Drug");
                rp.selectFilterItemByIndex(2);
                if (!rp.getTextContent(cp.regimenTable).contains("search did not match any results")) {
                    AllureReportListener.saveLogs("Number of records that match the selected Drug filter criteria: " + rp.getNumOfGridResults());
                    int afterDrugFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDrugFilter, "Drug Filter - Not working as expected");
                } else {
                    AllureReportListener.saveLogs("Drug filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("drugs-select"), 5);

                //Filter results by Last Updated By filter
                rp.clickGridFilters("last-updated-by-select", "Last Updated By");
                rp.selectFilterItemByName("System");
                if (!rp.getTextContent(cp.regimenTable).contains("search did not match any results")) {
                    AllureReportListener.saveLogs("Number of records that match the selected Diagnosis filter criteria: " + rp.getNumOfGridResults());
                    int afterDiagFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDiagFilter,
                            "Last Updated By Filter - Not working as expected");
                } else {
                    AllureReportListener.saveLogs("Last Updated By filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("last-updated-by-select"), 5);

                //Filter results by Status filter
                eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 5);
                rp.clickGridFilters("status-select", "Status");
                rp.selectFilterItemByIndex(1);
                if (!rp.getTextContent(cp.regimenTable).contains("search did not match any results")) {
                    AllureReportListener.saveLogs("Number of records that match the selected Drug filter criteria: " + rp.getNumOfGridResults());
                    int afterDrugFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDrugFilter, "Status Filter - Not working as expected");
                } else {
                    AllureReportListener.saveLogs("Status filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 5);

                //Filter results by Type filter
                rp.clickGridFilters("regimen-type-select", "Type");
                rp.selectFilterItemByName("Partial");
                if (!rp.getTextContent(cp.regimenTable).contains("search did not match any results")) {
                    AllureReportListener.saveLogs("Number of records that match the selected Drug filter criteria: " + rp.getNumOfGridResults());
                    int afterDrugFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDrugFilter, "Type Filter - Not working as expected");
                } else {
                    AllureReportListener.saveLogs("Type filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("regimen-type-select"), 5);

            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 4, groups = "smoke",description = "RM- Create a Single Regimen. Edit the created Regimen. Delete the Regimen")
    @Severity(SeverityLevel.NORMAL)
    @Description("RM- Create a Single Regimen. Edit the created Regimen. Delete the Regimen")
    public void regimenManagementCreateEditDeleteRegimen() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Regimen Management");
            sa.assertEquals(rp.getPageHeading(), "Regimen Management", "RM Page - Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Regimens", "RM Page - Incorrect results grid heading");

            //Create Regimen - Single Regimen
            rp.clickButton("Create New");
            dp.dataMngClickSubListItem("Create Single Regimen"); //Main Page
            sa.assertEquals(rp.getPageHeading(), "Create a Single Regimen", "Create Regimen - Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Regimen Summary", "Create Regimen - Incorrect grid heading");
            String regimenName = "Automation - Single Regimen " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
            eu.doSendKeys(cp.getTextareaField("regimen-name"), regimenName );
            eu.doSendKeys(cp.getTextareaField("administration-notes"), "Created by Automation");
            eu.syncWait(2);
            rp.clickButtonTypeStrong("Add Diagnoses");
            rp.selectFilterItemByName("Amyloidosis");

            eu.syncWait(2);
            eu.scrollByPixel(200);

            eu.doSendKeys(cp.editRegimenSelectDrug, "j7060");
            eu.syncWait(2);
            eu.getElement(cp.editRegimenSelectDrug).sendKeys(Keys.ENTER);
            eu.syncWait(2);

            rp.clickSubmit("Save");
            eu.syncWait(5);
            //rp.clickButton("Yes");
            //rp.clickButton("Yes");

            sa.assertEquals(rp.getPageHeading(), "Regimen Management", "RM Page - After hitting Cancel in the Create Regimen Page the page did not navigate back to Regimen" +
                    "Management page");
            sa.assertEquals(rp.getGridHeading(), "Regimens", "RM Page - Incorrect results grid heading");

            if (rp.getNumOfGridResults() > 0) {
                //eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 5);
                String gridRowText = rp.getRowCellData(cp.regimenTable, 0)[0];
                sa.assertEquals(gridRowText, regimenName, "Create Regimen - Regimen name is not matching with the given name");
                int beforeFilter = rp.getNumOfGridResults();

                rp.clickGridCell("regimen-formulary-table", 1, 1);
                eu.scrollToView(cp.editRegimenButton);
                eu.doClick(cp.editRegimenButton);
                eu.syncWait(5);
                //System.out.println("Regimen Name:" + "Text"+gridRowText + "Regimen Summary Name:" + rp.getTextContent(cp.editRegimenRegimenName));
                sa.assertTrue(rp.getPageHeading().contains("Edit a"), "Edit Regimen - Edit Regimen page is not showing after clicking");
                sa.assertEquals(rp.getGridHeading(), "Regimen Summary", "Edit Regimen - Edit Regimen grid with edit details is not showing after clicking");
                sa.assertEquals(gridRowText, rp.getTextContent(cp.editRegimenRegimenName),
                 "Edit Regimen - The name from the Regimen grid and Regimen Summary are not matching");

                eu.doSendKeys(cp.getTextareaField("regimen-name"), " + Edited by Automation");
                eu.syncWait(2);
                String editedRegimenName = rp.getTextContent(cp.editRegimenRegimenName);
                System.out.println("Edited name"+editedRegimenName);
                rp.clickSubmit("Save");
                eu.syncWait(5);
                String gridRowTextAfterEdit = rp.getRowCellData(cp.regimenTable, 0)[0];
                sa.assertEquals(gridRowTextAfterEdit, editedRegimenName, "Edit Regimen - Regimen name is not matching with the given name after modifying the Regimen");
                eu.syncWait(5);

                rp.srhRegDrugDiagAndEnter(editedRegimenName);
                rp.clickGridRowWithCheckBox("regimen-formulary-table", 1);
                eu.syncWait(3);
                eu.doClick(rp.getBtnTypeByTestId("delete-button"));
                eu.syncWait(5);
                rp.clickButton("Delete");
                eu.syncWait(5);
                eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 5);
                rp.srhRegDrugDiagAndEnter(editedRegimenName);
                sa.assertTrue(rp.getTextContent(cp.regimenTable).contains("search did not match any results"),"Regimen Delete - Delete is not working as expected");

            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed");
        }
        sa.assertAll();
    }

    // @Test(priority = 5,groups = "smoke", description = "Practice Analysis - Practice Report - Compare Regimens - Validation of compare Regimens functionality.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Practice Report - Compare Regimens - Validation of compare Regimens functionality.")
    public void pracAnalysisCompareRegimens() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        int rowsToCompare = 4;
        try {
            rp.clickTabs("Practice Analysis");
            sa.assertEquals(rp.getPageHeading(), "Find a Regimen", "Incorrect Page Heading");
            sa.assertEquals(rp.getGridHeading(), "Analysis Criteria", "Incorrect Grid Heading");
            rp.srhRegDrugDiagAndEnter(srhDrug);
            if (rp.getNumOfGridResults() > 0) {
                rp.pracTableAddToCompare(rp.pracTable, rowsToCompare);
                rp.clickByPartialLinkText("Compare");
                String drugViewDts = eu.getTextcontent(rp.drugDetailsTitle);
                sa.assertEquals(drugViewDts, "Comparison", "Incorrect heading for Comparison page Details page");
                sa.assertEquals(eu.getElement(rp.comparisonTableH1).getText(), String.valueOf(rowsToCompare),
                        "Comparison page details page doesnt show the right number of selected rows to compare ");
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException e) {
            //ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed.");
            AllureReportListener.saveLogs("Test method Failed");
        }
        sa.assertAll();
    }


}

