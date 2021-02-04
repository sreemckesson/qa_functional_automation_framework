package com.mck.rp.tetscases.RegimenAnalysis.PracticeAnalysis;

import com.aventstack.extentreports.Status;
import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.ExtentReportListener;
import com.mck.rp.listeners.allure.AllureReportListener;
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
public class PracticeAnalysisSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    LoginPage lp;
    String srhDrug = "Brentuximab";
    public static Logger log = LogManager.getLogger(PracticeAnalysisSmokeTest.class);

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void pracAnalysisSetUp() {
        rp = new RegimenAnalysisPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);

        try {
            loginPage.doLogin(prop.getProperty("defaultUsername"), prop.getProperty("defaultPassword"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 1, groups = "smoke", description = "Practice Analysis Page - Regimen Library - Validation of Search by Regimen Name, Diagnosis or Drug and Sort Results by " +
            "Regimen Name and Insurers Responsibility using Columns Sort functionality")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Regimen Library - Validation of Search by Regimen Name, Diagnosis or Drug and Sort Results by" +
            "Regimen Name and Insurers Responsibility using Columns Sort functionality")
    public void pracAnalysisGridSearch() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Regimen Analysis");
            rp.clickByLinkText("Practice Analysis");
            sa.assertEquals(rp.getPageHeading(), "Find a Regimen", "Incorrect page Heading");
            sa.assertEquals(rp.getGridHeading(), "Analysis Criteria", "Incorrect Grid Heading");

            rp.srhRegDrugDiagAndEnter(srhDrug);
            if (rp.getNumOfGridResults() > 0) {
                int rowCount = eu.getGridRowCount(rp.pracTable);
                for (int i = 0; i < rowCount; i++) {
                    sa.assertTrue(rp.getRowCellData(rp.pracTable, i)[1].contains(srhDrug));
                }
                //ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

                //Sort by Name
                List<String> beforeSortList = rp.tableColumnList(rp.pracTable, 1);
                List<String> sortList = eu.sortItemsList(beforeSortList, "desc");
                rp.clickTableHeaderForSort("Regimen Name");
                eu.syncWait(5);
                List<String> afterSortList = rp.tableColumnList(rp.pracTable, 1);
                //System.out.println("Before: " + beforeSortList +"\r\n" + "Sort: " + sortList +"\r\n"+ "After: " + afterSortList);
                sa.assertEquals(sortList, afterSortList, "Sort by Name - Sorting by Name is not working");
                rp.clickTableHeaderForSort("Regimen Name");
                eu.syncWait(5);

                //Sort by Insurer Responsibility
                List<Integer> beforeSortNumsList = rp.tableNumColumnList(rp.pracTable, 3);
                List<Integer> sortNumsList = eu.sortItemsNums(beforeSortNumsList, "desc");
                rp.clickTableHeaderForSort("Insurer Responsibility");
                eu.syncWait(5);
                List<Integer> afterSortNumsList = rp.tableNumColumnList(rp.pracTable, 3);
                //System.out.println("Before: " + beforeSortNumsList + "\r\n" + "Sort: " + sortNumsList + "\r\n" + "After: " + afterSortNumsList);
                sa.assertEquals(sortNumsList, afterSortNumsList, "Sort by Insurer Responsibility - Not working as expected");

            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
                log.info("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            log.error("Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();

    }

    @Test(priority = 4,groups = "smoke", description = "Practice Analysis - Practice Report - Analysis Criteria - Validation of Antiemetics, " +
            "Growth Factors and Insurer Fee Schedule filters are working or not with different filter values selection")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis - Practice Report - Analysis Criteria - Validation of Antiemetics, " +
            "Growth Factors and Insurer Fee Schedule filters are working or not with different filter values selection")
    public void pracAnalysisVerifyAnalysisCriteriaFilters() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickByLinkText("Practice Analysis");
            sa.assertEquals(rp.getPageHeading(), "Find a Regimen", "Incorrect Page Heading");
            sa.assertEquals(rp.getGridHeading(), "Analysis Criteria", "Incorrect Grid Heading");

            rp.srhRegDrugDiagAndEnter(srhDrug);

            if (rp.getNumOfGridResults() > 0) {
                AllureReportListener.saveLogs("Number of rows returned for the search - " + srhDrug + " : " + eu.getGridRowCount(rp.pracTable));

                String beforeSrhRowValues = rp.getGridRowData(rp.pracTable, 1);
                /*
                String regLibCost, regLibInsuRes, regLibPatRes, regLibMargin;
                regLibCost = rp.getRowCellData(rp.pracTable, 0)[2];
                regLibInsuRes = rp.getRowCellData(rp.pracTable, 0)[3];
                regLibPatRes = rp.getRowCellData(rp.pracTable, 0)[4];
                regLibMargin = rp.getRowCellData(rp.pracTable, 0)[5];
                */

                rp.analysisCriAntieGfactorSelectItem("feeSchedule", 1);
                rp.clickButton("Recalculate");
                sa.assertNotEquals(beforeSrhRowValues, rp.getGridRowData(rp.pracTable, 1), "Analysis Criteria Fee Schedule - Not working as expected");

                rp.clickImg(rp.analysisCriteriaCollapse);
                rp.analysisCriAntieGfactorSelectItem("antiemetic", 2);
                rp.clickButton("Recalculate");
                sa.assertNotEquals(beforeSrhRowValues, rp.getGridRowData(rp.pracTable, 1),"Analysi Criteria Antiemetic - Not working as expected" );
                rp.clickImg(rp.analysisCriteriaCollapse);
                rp.analysisCriAntieGfactorSelectItem("growthFactor", 1);
                rp.clickButton("Recalculate");
                sa.assertNotEquals(beforeSrhRowValues, rp.getGridRowData(rp.pracTable, 1), "Analysis Criteria Growth Factor - Not working as expected");

            } else {
                sa.fail();
                //ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            //ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed");
            AllureReportListener.saveLogs("Test Method Failed");
        }
        sa.assertAll();
    }

    @Test(priority = 3,groups = "smoke", description = "Practice Analysis - Regimen Library - Validation of table values from Regimen library" +
            " like Cost, Patient Responsibility, Insurance Responsibility, Margin match with values in Regimen Summary details page")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis - Regimen Library - Validation of table values from Regimen library" +
            " like Cost, Patient Responsibility, Insurance Responsibility, Margin match with values in Regimen Summary details page")
    public void pracAnalysisVerifyRegLibAndRegSummary() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickTabs("Practice Analysis");
            sa.assertEquals(rp.getPageHeading(), "Find a Regimen", "Incorrect Page Heading");
            sa.assertEquals(rp.getGridHeading(), "Analysis Criteria", "Incorrect Grid Heading");

            rp.srhRegDrugDiagAndEnter(srhDrug);

            if (rp.getNumOfGridResults() > 0) {
                AllureReportListener.saveLogs("Row Count: " + eu.getGridRowCount(rp.pracTable) + " and " +
                        "Column Count " + eu.getGridColumnCount(rp.pracTable));

                //ExtentReportListener.test.get().log(Status.INFO, "Column Data: " + eu.getGridColumnData(rp.pracTable, 1)[0]);
                AllureReportListener.saveLogs("Column Data: " + eu.getGridColumnData(rp.pracTable, 1)[0]);

                String regLibCost, regLibInsuRes, regLibPatRes, regLibMargin;
                regLibCost = rp.getRowCellData(rp.pracTable, 0)[2];
                regLibInsuRes = rp.getRowCellData(rp.pracTable, 0)[3];
                regLibPatRes = rp.getRowCellData(rp.pracTable, 0)[4];
                regLibMargin = rp.getRowCellData(rp.pracTable, 0)[5];

                // System.out.println("Row Detail costs: " + regLibCost + regLibInsuRes + regLibPatRes + regLibMargin);

                //Rows are in odd numbers 1, 3, 5 etc, even numbers are the separators in the grid
                String gridRowText = eu.getText(rp.getGridcell("practice-analysis-table", 1, 3));
                rp.clickGridCell("practice-analysis-table", 1, 3);
                eu.scrollToView(rp.btnViewEditDts);
                eu.doClick(rp.btnViewEditDts);
                String drugViewDts = eu.getTextcontent(rp.drugDetailsTitle);
                sa.assertEquals(gridRowText, drugViewDts.substring(0, drugViewDts.length() - 1),
                        "Practice Analysis Table row value and Details value are not matching");

                sa.assertEquals(rp.summaryfields(0), regLibCost,
                        "Practice Analysis Table Cost and Details Table cost are matching");
                sa.assertEquals(rp.summaryfields(1), regLibInsuRes,
                        "Practice Analysis Table Insurer Responsibility and Details Insurer Responsibility are matching");
                sa.assertEquals(rp.summaryfields(2), regLibPatRes,
                        "Practice Analysis Table Patient Responsibility and Details Patient Responsibility are matching");
                sa.assertEquals(rp.summaryfields(3), regLibMargin,
                        "Practice Analysis Table Margin and Details Margin are matching");

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


    @Test(priority = 2, groups = "smoke",description = "Practice Analysis - Practice Report - Regimen Library - Validation of results by Drug, Diagnosis and Break Even Dropdown filters")
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

    @Test(priority = 5,groups = "smoke", description = "Practice Analysis - Practice Report - Compare Regimens - Validation of compare Regimens functionality.")
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

    @Test(priority = 6, description = "Practice Analysis - Regimen Summary - Validation of Total Cost, Insurer Responsibility, Patient Responsibility and Marin are updating " +
            "with the Antiemetics, Growth Factor dropdowns items change and Cycles value change " + "\r\n" +
            "Validation of Export functionality in Practice Report page and Regimen Summary Page.")
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
                        "Summary Antiemetic filter - Margin - Not working as expected" );
                sa.assertTrue(rp.getTextContent(rp.summaryDrugTable).toLowerCase().contains(rp.subString(growthFactorSelect, " ", 0).toLowerCase()));

                rp.sendKeysByAction("numberOfCycles", "10");
                eu.getElement(rp.getInputField("numberOfCycles")).sendKeys(Keys.TAB);
                TimeUnit.SECONDS.sleep(2);
                sa.assertNotEquals(rp.summaryfields(0), regLibCost,
                        "Summary Number of Cycles - Cost - Not working as expected");
                sa.assertNotEquals(rp.summaryfields(1), regLibInsuRes,
                        "Summary Antiemetic filter - Insurer Responsibility - Not working as expected" );
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
