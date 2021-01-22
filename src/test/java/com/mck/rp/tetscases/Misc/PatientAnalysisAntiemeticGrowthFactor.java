package com.mck.rp.tetscases.Misc;

import com.aventstack.extentreports.Status;
import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.ExtentReportListener;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.LoginPage;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Epic("Module- Regimen Analysis - Patient Analysis")
@Feature("RP-: Smoke Test for Patient Analysis Page")
@Listeners(AllureReportListener.class)
public class PatientAnalysisAntiemeticGrowthFactor extends BaseTest {
    ElementUtil eu;
    RegimenAnalysisPage rp;
    LoginPage lp;
    String srhDrug = "alemtuzumab";

    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void patAnalysisSetUp() {
        rp = new RegimenAnalysisPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);

    }
/*
    @Description("Patient Analysis - Patient Analysis - Validation of Use Defaults and Calculate and Sort results by Regimen Name and " +
            "Patient Responsibility using Columns Sort functionality")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 1, description = "Patient Analysis - Patient Analysis - Validation of Use Defaults and Calculate and Sort Results by" +
            "Regimen Name and Patient Responsibility using Columns Sort functionality")
    public void patientAnalysisSearch() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickTabs("Patient Analysis");
            sa.assertEquals(rp.getGridHeading(), "Patient Information");

            eu.doClick(rp.useDefaults);
            rp.clickSubmit("Calculate");
            rp.srhRegDrugDiagAndEnter(srhDrug);

            if (rp.getNumOfGridResults() > 0) {
                sa.assertTrue(eu.isElementPresent(rp.patTable));
                int rowCount = eu.getGridRowCount(rp.patTable);
                for (int i = 0; i < rowCount; i++) {
                    //System.out.println("Cell Date: " + rp.getRowCellData(rp.patTable, i)[0]);
                    sa.assertTrue(rp.getRowCellData(rp.patTable, i)[0].toLowerCase().contains(srhDrug));
                }
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

                //Sort by Name
                List<String> beforeSortList = rp.tableColumnList(rp.patTable, 0);
                List<String> sortList = eu.sortItemsList(beforeSortList, "desc");
                rp.clickTableHeaderForSort("Regimen Name");
                eu.syncWait(3);
                List<String> afterSortList = rp.tableColumnList(rp.patTable, 0);
                System.out.println("Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                sa.assertEquals(sortList, afterSortList);
                rp.clickTableHeaderForSort("Regimen Name");
                eu.syncWait(3);

                //Sort by Insurer Responsibility
                List<Integer> beforeSortNumsList = rp.tableNumColumnList(rp.patTable, 1);
                List<Integer> sortNumsList = eu.sortItemsNums(beforeSortNumsList, "desc");
                rp.clickTableHeaderForSort("Patient Responsibility");
                eu.syncWait(3);
                List<Integer> afterSortNumsList = rp.tableNumColumnList(rp.patTable, 1);
                System.out.println("Before: " + beforeSortNumsList + "\r\n" + "Sort: " + sortNumsList + "\r\n" + "After: " + afterSortNumsList);
                sa.assertEquals(sortNumsList, afterSortNumsList);
            } else {
                sa.fail();
                ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Description("Patient Analysis - Patient Report - Regimen Library - Validation of Regimen Library results grid filtering by Drug and Diagnosis filters")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 2, description = "Patient Analysis - Patient Report - Regimen Library - Validation of Regimen Library results grid filtering by Drug and Diagnosis filters")
    public void patAnalysisGridFilterByDrugAndDiagnosis() throws NoSuchElementException {
        //log.info("PatientAnalysis Page - Search the table grid");
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickTabs("Patient Analysis");
            sa.assertEquals(rp.getGridHeading(), "Patient Information");

            eu.doClick(rp.useDefaults);
            rp.clickSubmit("Calculate");
            if (rp.getNumOfGridResults() > 0) {
                sa.assertTrue(eu.isElementPresent(rp.patTable));
                int beforeFilter = rp.getNumOfGridResults();

                rp.clickGridFilters("diagnosis-select", "Diagnosis");
                rp.selectFilterItemByName("Amyloidosis");
                if (!rp.getTextContent(rp.pracTable).contains("search did not match any results")) {
                    ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Diagnosis filter criteria: " + rp.getNumOfGridResults());
                    AllureReportListener.saveLogs("Number of records that match the selected Diagnosis filter criteria: " + rp.getNumOfGridResults());
                    int afterDiagFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDiagFilter);
                } else {
                    ExtentReportListener.test.get().log(Status.INFO, "Diagnosis filter search did not return any results");
                    AllureReportListener.saveLogs("Diagnosis filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("diagnosis-select"), 5);

                rp.clickGridFilters("drugs-select", "Drug");
                rp.selectFilterItemByIndex(1);
                if (!rp.getTextContent(rp.pracTable).contains("search did not match any results")) {
                    ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Drug filter criteria: " + rp.getNumOfGridResults());
                    AllureReportListener.saveLogs("Number of records that match the selected Drug filter criteria: " + rp.getNumOfGridResults());
                    int afterDrugFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterDrugFilter);
                } else {
                    ExtentReportListener.test.get().log(Status.INFO, "Drug filter search did not return any results");
                    AllureReportListener.saveLogs("Drug filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("drugs-select"), 5);

                //pagination
                eu.syncWait(3);
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("50");
                eu.syncWait(3);
                String afterPagination = eu.getElement(rp.gridResults).getText();
                sa.assertNotEquals(beforePagination, afterPagination);

            } else {
                sa.fail();
                ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();

    }

    @Description("Patient Analysis - Patient Report - Regimen Summary - Validation of View And Edit Details functionality")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 3, description = "Patient Analysis - Patient Report - Regimen Summary - Validation of View And Edit Details functionality")
    public void patAnalysisTableRowDetails() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();

        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickTabs("Patient Analysis");
            sa.assertEquals(rp.getGridHeading(), "Patient Information");

            eu.doClick(rp.useDefaults);
            rp.clickSubmit("Calculate");

            if (rp.getNumOfGridResults() > 0) {
                rp.srhRegDrugDiagAndEnter(srhDrug);
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());


                //Rows are in odd numbers 1, 3, 5 etc, even numbers are the separators in the grid
                String gridRowText = eu.getText(rp.getGridcell("patient-analysis-table", 1, 1));
                rp.clickGridCell("patient-analysis-table", 1, 1);
                eu.scrollToView(rp.btnViewEditDts);
                eu.scrollToView(rp.btnViewEditDts);
                eu.doClick(rp.btnViewEditDts);
                String drugViewDts = eu.getTextcontent(rp.drugDetailsTitle);
                sa.assertEquals(gridRowText, drugViewDts.substring(0, drugViewDts.length() - 1));
                sa.assertTrue(eu.isElementPresent(rp.getBtnTypeButton("Add Lump Sum")));

                //WebElement btnAddLumpSum = eu.getElement(rp.getBtnTypeButton("Add Lump Sum"));

            } else {
                sa.fail();
                ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");

            }
        } catch (NoSuchElementException e) {
            ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }
    */

    @Test(priority = 4, description = "Patient Analysis - Regimen Summary - Validation of the Patient Responsibility value when Antiemetics and Growth Factor dropdown" +
            " items are changed and Cycles value is changed." + "\r\n" + " Validation of Export functionality in Patient Report page" + "\r\n" + "Validation of Add Lump Sum functionality" + "\r\n" +
            " Validation of Drug and Non drug modal windows are opening when clicked on Add buttons ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Patient Analysis - Regimen Summary - Validation of the Patient Responsibility value when Antiemetics and Growth Factor dropdown" +
            " items are changed and Cycles value is changed." + "\r\n" + " Validation of Export functionality in Patient Report page " + "\r\n" + "Validation of Add Lump Sum functionality" + "\r\n" +
            " Validation of Drug and Non drug modal windows are opening when clicked on Add buttons ")
    public void patAnalysisSummaryAntiGfactorsCycles() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();

        try {
            rp.clickLeftMenuItem("Regimen Analysis");
            rp.clickByLinkText("Patient Analysis");
            sa.assertEquals(rp.getGridHeading(), "Patient Information");

            eu.doClick(rp.useDefaults);
            rp.clickSubmit("Calculate");
            rp.srhRegDrugDiagAndEnter(srhDrug);

            if (rp.getNumOfGridResults() > 0) {

                String regName, regLibPatRes;
                regName = rp.getRowCellData(rp.patTable, 0)[0];
                regLibPatRes = rp.getRowCellData(rp.patTable, 0)[1];

                System.out.println("Row Detail Name: " + regName + " Patient Responsibility: " + regLibPatRes);

                //Rows are in odd numbers 1, 3, 5 etc, even numbers are the separators in the grid
                String gridRowText = eu.getText(rp.getGridcell("patient-analysis-table", 1, 1));
                System.out.println("Row Text" + gridRowText);
                rp.clickGridCell("patient-analysis-table", 1, 1);
                eu.scrollToView(rp.btnViewEditDts);
                eu.scrollToView(rp.btnViewEditDts);
                //Thread.sleep(4000);
                eu.doClick(rp.btnViewEditDts);
                String drugViewDts = eu.getTextcontent(rp.drugDetailsTitle);
                sa.assertEquals(gridRowText, drugViewDts.substring(0, drugViewDts.length() - 1));

                String antiemeticSelect = rp.summaryAntieGfactorSelectItem("antiemetic", 1);
                String antiemeticPatRes = rp.getTextContent(rp.patSummaryPatResp);
                if(eu.getTextcontent(rp.suppCareSummary).contains("Antiemetics are not required for this regimen")) {
                    ExtentReportListener.test.get().log(Status.INFO, "Antimetics not required for this");
                    AllureReportListener.saveLogs("Antimetics not required for this");
                }
                else{
                    //System.out.println("Anti: " + antiemeticPatRes);
                    sa.assertNotEquals(antiemeticPatRes, regLibPatRes);
                    //System.out.println("AntiName1: " + rp.getTextContent(rp.summaryDrugTable).toLowerCase());
                    //System.out.println("AntiName1: "+ rp.subString(antiemeticSelect, " ", 0).toLowerCase());
                    sa.assertTrue(rp.getTextContent(rp.summaryDrugTable).toLowerCase().contains(rp.subString(antiemeticSelect, " ", 0).toLowerCase()));

                }

                String growthFactorSelect = rp.summaryAntieGfactorSelectItem("growthFactor", 1);
                String growthFacPatRes = rp.getTextContent(rp.patSummaryPatResp);
                System.out.println("GF: " + growthFacPatRes);
                sa.assertNotEquals(growthFacPatRes, regLibPatRes);
                sa.assertTrue(rp.getTextContent(rp.summaryDrugTable).toLowerCase().contains(rp.subString(growthFactorSelect, " ", 0).toLowerCase()));

                rp.sendKeysByAction("numberOfCycles", "10");
                eu.getElement(rp.getInputField("numberOfCycles")).sendKeys(Keys.TAB);
                TimeUnit.SECONDS.sleep(2);
                String cyclesPatRes = rp.getTextContent(rp.patSummaryPatResp);
                //System.out.println("Cyc: " + cyclesPatRes);
                sa.assertNotEquals(cyclesPatRes, regLibPatRes);

                rp.clickButton("Add Lump Sum");
                String amount = String.format("%.2f", (new Random().nextInt(99) / new Random().nextDouble()));
                rp.sendKeysByAction("fundings[0].name", "Funding");
                rp.sendKeysByAction("fundings[0].amount", amount);
                eu.getElement(rp.getInputField("fundings[0].amount")).sendKeys(Keys.TAB);
                TimeUnit.SECONDS.sleep(2);
                Double funding = Double.parseDouble(amount);
                NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                sa.assertTrue(rp.getTextContent(rp.additionalFunding).contains(n.format(funding)));


            } else {
                sa.fail();
                ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException | StaleElementReferenceException e) {
            ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed.");
            AllureReportListener.saveLogs("Test method Failed");
        }
        sa.assertAll();
    }

    @Test(priority = 5, description = "Patient Analysis - Patient Information - Validation of Primary Insurance - CoInsurance, BSA, Insurace Fee Schedule, " +
            "Out Of Pocket, Deductible, CoPays - Amount and cycles are working with different values selection." + "\r\n" +
            "Validation of Secondary Insurance - Secondary Fee Schedule, Coinsurnace, Out Of Pocket, Deductible and Secondary pays Primary Deductible are " +
            "working with different values.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Patient Analysis - Patient Information - Validation of Primary Insurance - CoInsurance, BSA, Insurace Fee Schedule," +
            "Out Of Pocket, Deductible, CoPays - Amount and cycles are working with different values selection." + "\r\n" +
            "Validation of Secondary Insurance - Secondary Fee Schedule, Coinsurnace, Out Of Pocket, Deductible and Secondary pays Primary Deductible are " +
            "working with different values")
    public void patAnalysisPatientInfoFilters() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickByLinkText("Patient Analysis");
            sa.assertEquals(rp.getGridHeading(), "Patient Information");

            eu.doClick(rp.useDefaults);
            rp.clickSubmit("Calculate");
            rp.srhRegDrugDiagAndEnter(srhDrug);
            //Thread.sleep(5000);
            if (rp.getNumOfGridResults() > 0) {
                ExtentReportListener.test.get().log(Status.INFO, "Regimen Search: " + srhDrug + " returned: " + eu.getGridRowCount(rp.patTable) + " rows ");
                AllureReportListener.saveLogs("Number of rows returned for the search - " + srhDrug + " : " + eu.getGridRowCount(rp.patTable));

                String beforeSrhRowValues = rp.getGridRowData(rp.patTable, 1);
                /*
                String regName, regLibPatRes;
                regName = rp.getRowCellData(rp.patTable, 0)[0];
                regLibPatRes = rp.getRowCellData(rp.patTable, 0)[1];
                */

                //Changing the Fee Schedule and validating
                rp.clickImg(rp.patientInformationCollapse);
                rp.analysisCriAntieGfactorSelectItem("primaryInsurance.feeSchedule", 1);
                rp.clickSubmit("Calculate");
                String afterFeeScheValues = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(beforeSrhRowValues, afterFeeScheValues);

                //Changing CoInsurance
                rp.clickImg(rp.patientInformationCollapse);
                rp.sendKeysByAction("primaryInsurance.coinsurance", "30");
                rp.clickSubmit("Calculate");
                String afterCoInsValues = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(afterFeeScheValues, afterCoInsValues);

                //Validating with providing Deductible amount
                rp.clickImg(rp.patientInformationCollapse);
                rp.sendKeysByAction("primaryInsurance.remainingDeductible.remaining", "300");
                rp.clickSubmit("Calculate");
                String afterDeductibleValues = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(afterCoInsValues, afterDeductibleValues);

                //Validating with providing OutOfPocket amount
                rp.clickImg(rp.patientInformationCollapse);
                rp.sendKeysByAction("primaryInsurance.remainingDeductible.remaining", "");
                rp.sendKeysByAction("primaryInsurance.remainingOutOfPocket.remaining", "400");
                rp.clickSubmit("Calculate");
                String afterOutOfPocValues = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(afterDeductibleValues, afterOutOfPocValues);

                //Validating with providing CoPay and Cycles values
                rp.clickImg(rp.patientInformationCollapse);
                rp.sendKeysByAction("primaryInsurance.remainingOutOfPocket.remaining", "");
                rp.sendKeysByAction("primaryInsurance.copay", "100");
                rp.sendKeysByAction("primaryInsurance.copaysPerCycle", "5");
                rp.clickSubmit("Calculate");
                String afterCoPayCycValues = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(afterOutOfPocValues, afterCoPayCycValues);

                rp.clickImg(rp.patientInformationCollapse);
                rp.clickButton("Secondary insurance");
                eu.scrollToView(rp.getBtnTypeSubmit("Calculate"));
                rp.analysisCriAntieGfactorSelectItem("secondaryInsurance.feeSchedule", 1);
                rp.sendKeysByAction("secondaryInsurance.coinsurance", "25");
                rp.clickSubmit("Calculate");
                String afterSecondaryFeeScheCoInsValues = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(afterCoPayCycValues, afterSecondaryFeeScheCoInsValues);

                //Validating with providing Deductible amount
                rp.clickImg(rp.patientInformationCollapse);
                eu.scrollToView(rp.getBtnTypeSubmit("Calculate"));
                rp.sendKeysByAction("secondaryInsurance.remainingDeductible.remaining", "150");
                rp.clickSubmit("Calculate");
                String afterSecondaryDeductibleValues = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(afterSecondaryFeeScheCoInsValues, afterSecondaryDeductibleValues);

                //Validating with providing OutOfPocket amount
                rp.clickImg(rp.patientInformationCollapse);
                eu.scrollToView(rp.getBtnTypeSubmit("Calculate"));
                rp.sendKeysByAction("secondaryInsurance.remainingDeductible.remaining", "");
                rp.sendKeysByAction("secondaryInsurance.remainingOutOfPocket.remaining", "100");
                rp.clickSubmit("Calculate");
                String afterSecondaryOutOfPocValues = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(afterSecondaryDeductibleValues, afterSecondaryOutOfPocValues);

                rp.clickImg(rp.patientInformationCollapse);
                eu.scrollToView(rp.getBtnTypeSubmit("Calculate"));
                //eu.scrollByPixel(180);
                rp.sendKeysByAction("secondaryInsurance.remainingOutOfPocket.remaining", "");
                rp.sendKeysByAction("primaryInsurance.remainingDeductible.remaining", "400");
                rp.sendKeysByAction("secondaryInsurance.remainingDeductible.remaining", "250");
                eu.doClick(rp.secondaryPaysPrimaryDeductible);
                rp.clickSubmit("Calculate");
                String afterSecondaryPayPrimaryDed = rp.getGridRowData(rp.patTable, 1);
                sa.assertNotEquals(afterSecondaryOutOfPocValues, afterSecondaryPayPrimaryDed);

            } else {
                sa.fail();
                ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed");
            AllureReportListener.saveLogs("Test Method Failed");
        }
        sa.assertAll();
    }

}



