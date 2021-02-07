package com.mck.rp.tetscases.ClinicalContent.RegimenManagement;

import com.aventstack.extentreports.Status;
import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.ExtentReportListener;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.ClinicalContentPage;
import com.mck.rp.pageObjects.LoginPage;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Listeners(AllureReportListener.class)
public class RegimenManagementSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    LoginPage lp;
    ClinicalContentPage cp;
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
            "Drug or last updated by Results. ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Regimen Management Page - Regimen Library - Validation of Search by Regimen Name, Diagnosis," +
            "Drug or last updated by Results.")
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
                for (int i = 0; i < rowCountLastUpdated; i++) {
                    //System.out.println(rp.getRowCellData(cp.regimenTable, i)[2].contains(srhLastUpdated)); - showing Angela's name when searched by System
                    sa.assertTrue(rp.getRowCellData(cp.regimenTable, i)[2].contains(srhLastUpdated), "Search by Last Updated By - Incorrect Results");
                }
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
        } catch (NoSuchElementException e) {
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
                rp.clickTableHeaderForSort("Date Last Updated");
                eu.syncWait(3);
                List<String> afterSortListDate = rp.tableColumnList(cp.regimenTable, 3);
                //System.out.println("Before: " + beforeSortListDate + "\r\n" + "Sort: " + sortListDate + "\r\n" + "After: " + afterSortListDate);
                sa.assertNotEquals(beforeSortListDate, afterSortListDate, "Sort by Date Last Updated Column - Not working as expected");
                //sa.assertEquals(sortListDate, afterSortListDate, "Sort by Date Last Updated Column - Not working as expected");

            } else {
                sa.fail();
                //ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
            //ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed");
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


    //@Test(priority = 2, groups = "smoke",description = "Practice Analysis - Practice Report - Regimen Library - Validation of results by Drug, Diagnosis and Break Even Dropdown filters")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis - Practice Report - Regimen Library - Validation of results by Drug, Diagnosis and Break Even Dropdown filters")
    public void pracAnalysisVerifyDrugDiagnosisBreakevenFilters() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickByLinkText("Practice Analysis");
            sa.assertEquals(rp.getPageHeading(), "Find a Regimen", "Incorrect Page Heading");
            sa.assertEquals(rp.getGridHeading(), "Analysis Criteria", "Incorrect Grid Heading");
            eu.syncWait(2);

            if (rp.getNumOfGridResults() > 0) {
                int beforeFilter = rp.getNumOfGridResults();
                rp.clickGridFilters("break-even-select", "Break Even");
                rp.selectFilterItemByIndex(1);
                if (!rp.getTextContent(rp.pracTable).contains("search did not match any results")) {
                    int afterBreakEvenFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterBreakEvenFilter, "Break Even Filter - Not working as expected");
                    AllureReportListener.saveLogs("Number of records that match the selected Break Even filter criteria: " + rp.getNumOfGridResults());

                } else {
                    AllureReportListener.saveLogs("Break Even filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("break-even-select"), 5);

                rp.clickGridFilters("diagnosis-select", "Diagnosis");
                rp.selectFilterItemByName("Amyloidosis");
                if (!rp.getTextContent(rp.pracTable).contains("search did not match any results")) {
                    AllureReportListener.saveLogs("Number of records that match the selected Diagnosis filter criteria: " + rp.getNumOfGridResults());
                    int afterDiagFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDiagFilter,
                            "Diagnosis Filter - Not working as expected");
                } else {
                    AllureReportListener.saveLogs("Diagnosis filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("diagnosis-select"), 5);

                rp.clickGridFilters("drugs-select", "Drug");
                rp.selectFilterItemByIndex(2);
                if (!rp.getTextContent(rp.pracTable).contains("search did not match any results")) {
                    //ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Drug filter criteria: " + rp.getNumOfGridResults());
                    AllureReportListener.saveLogs("Number of records that match the selected Drug filter criteria: " + rp.getNumOfGridResults());
                    int afterDrugFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDrugFilter, "Drug Filter - Not working as expected");
                } else {
                    //ExtentReportListener.test.get().log(Status.INFO, "Drug filter search did not return any results");
                    AllureReportListener.saveLogs("Drug filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("drugs-select"), 5);


                //pagination
                eu.syncWait(2);
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("50");
                eu.syncWait(2);
                String afterPagination = eu.getElement(rp.gridResults).getText();
                sa.assertNotEquals(beforePagination, afterPagination, "Pagination - Not working as expected");

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

    /* @Test(priority = 6, description = "Practice Analysis - Regimen Summary - Validation of Total Cost, Insurer Responsibility, Patient Responsibility and Marin are updating " +
             "with the Antiemetics, Growth Factor dropdowns items change and Cycles value change " + "\r\n" +
             "Validation of Export functionality in Practice Report page and Regimen Summary Page.")*/
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis - Regimen Summary - Validation of Total Cost, Insurer Responsibility, Patient Responsibility and Marin are updating " +
            " with the Antiemetics, Growth Factor dropdowns items change and Cycles value change " + "\r\n" +
            "Validation of Export functionality in Practice Report page and Regimen Summary Page")
    public void pracAnalysisSummaryAntiGfactorsCycles() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();

        try {
            rp.clickByLinkText("Practice Analysis");
            sa.assertEquals(rp.getPageHeading(), "Find a Regimen", "Incorrect Page Heading");
            sa.assertEquals(rp.getGridHeading(), "Analysis Criteria", "Incorrect Grid Heading");
            rp.srhRegDrugDiagAndEnter(srhDrug);

            if (rp.getNumOfGridResults() > 0) {
                //Export - Practice Report Main Page
                rp.clickButton("Export");
                rp.clickSubListItem("Export as Excel (.xlsx)");

                AllureReportListener.saveLogs("Row Count: " + eu.getGridRowCount(rp.pracTable) + " and " +
                        "Column Count " + eu.getGridColumnCount(rp.pracTable));

                String regLibCost, regLibInsuRes, regLibPatRes, regLibMargin;
                regLibCost = rp.getRowCellData(rp.pracTable, 0)[2];
                regLibInsuRes = rp.getRowCellData(rp.pracTable, 0)[3];
                regLibPatRes = rp.getRowCellData(rp.pracTable, 0)[4];
                regLibMargin = rp.getRowCellData(rp.pracTable, 0)[5];

                //System.out.println("Row Detail costs: " + regLibCost + regLibInsuRes + regLibPatRes + regLibMargin);

                //Rows are in odd numbers 1, 3, 5 etc, even numbers are the separators in the grid
                rp.srhRegDrugDiagAndEnter(srhDrug);
                String gridRowText = eu.getText(rp.getGridcell("practice-analysis-table", 1, 3));
                rp.clickGridCell("practice-analysis-table", 1, 3);
                eu.scrollToView(rp.btnViewEditDts);
                eu.doClick(rp.btnViewEditDts);
                eu.syncWait(5);
                String drugViewDts = eu.getTextcontent(rp.drugDetailsTitle);
                sa.assertEquals(gridRowText, drugViewDts.substring(0, drugViewDts.length() - 1),
                        "Practice Analysis selected row View and Edit details page doesnt show the selected item's name");

                String antiemeticSelect = rp.summaryAntieGfactorSelectItem("antiemetic", 1);
                //sa.assertNotEquals(rp.summaryfields(0), regLibCost);
                sa.assertNotEquals(rp.summaryfields(1), regLibInsuRes,
                        "Summary Antiemetic filter - Insurer Responsibility - Not working as expected ");
                sa.assertNotEquals(rp.summaryfields(2), regLibPatRes,
                        "Summary Antiemetic filter - Patient Responsibility - Not working as expected");
                sa.assertNotEquals(rp.summaryfields(3), regLibMargin,
                        "Summary Antiemetic filter - Margin - Not working as expected");
                sa.assertTrue(rp.getTextContent(rp.summaryDrugTable).toLowerCase().contains(rp.subString(antiemeticSelect, " ", 0).toLowerCase()));

                String growthFactorSelect = rp.summaryAntieGfactorSelectItem("growthFactor", 1);
                TimeUnit.SECONDS.sleep(2);
                sa.assertNotEquals(rp.summaryfields(0), regLibCost,
                        "Summary Growth Factor filter - Cost - Not working as expected ");
                sa.assertNotEquals(rp.summaryfields(1), regLibInsuRes,
                        "Summary Antiemetic filter - Insurer Responsibility - Not working as expected ");
                sa.assertNotEquals(rp.summaryfields(2), regLibPatRes,
                        "Summary Antiemetic filter - Patient Responsibility - Not working as expected ");
                sa.assertNotEquals(rp.summaryfields(3), regLibMargin,
                        "Summary Antiemetic filter - Margin - Not working as expected");
                sa.assertTrue(rp.getTextContent(rp.summaryDrugTable).toLowerCase().contains(rp.subString(growthFactorSelect, " ", 0).toLowerCase()));

                rp.sendKeysByAction("numberOfCycles", "10");
                eu.getElement(rp.getInputField("numberOfCycles")).sendKeys(Keys.TAB);
                TimeUnit.SECONDS.sleep(2);
                sa.assertNotEquals(rp.summaryfields(0), regLibCost,
                        "Summary Number of Cycles - Cost - Not working as expected");
                sa.assertNotEquals(rp.summaryfields(1), regLibInsuRes,
                        "Summary Antiemetic filter - Insurer Responsibility - Not working as expected");
                sa.assertNotEquals(rp.summaryfields(2), regLibPatRes,
                        "Summary Antiemetic filter - Patient Responsibility - Not working as expected ");
                sa.assertNotEquals(rp.summaryfields(3), regLibMargin,
                        "Summary Antiemetic filter - Margin - Not working as expected ");

                //Export - Summary Page
                rp.clickButton("Export");
                rp.clickSubListItem("Detailed Practice Report (.xlsx)");

                //Add Drug and Non Drug
                eu.scrollToView(rp.summaryAddDrug);
                eu.clickWhenReady(rp.summaryAddDrug, 6);
                sa.assertTrue(rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Add Drug"));
                if (rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Add Drug")) {
                    rp.clickButton("Cancel");
                }

                eu.scrollToView(rp.summaryAddNondrug);
                eu.clickWhenReady(rp.summaryAddNondrug, 6);
                sa.assertTrue(rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Add Non-Drug"));
                if (rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Add Non-Drug")) {
                    rp.clickButton("Cancel");
                }

                eu.clickWhenReady(rp.getConfigFieldsButton("drug"), 5);
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test method Failed");
        }
        sa.assertAll();
    }
}

