package com.mck.rp.tetscases.DataManagement.FeeSchedule;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.configuration.Theme;
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
public class FeeScheduleSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    LoginPage lp;
    String srhSchedule = "medicare";

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void feeScheduleSetUp() {
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

    @Test(priority = 1, groups = {"smoke"}, description = "Data Management - Fee Schedule - Validation of Fee Schedules search in Fee Schedule Table. " +
            "Validation of filter functionality of the table by Status filter. " +
            "Validation of sort functionality by Fee Schedule name.  Validation of Pagination functionality." +
            "Validation of Create New functionality to check whether the dialog is opening or not. ")
    @Severity(SeverityLevel.NORMAL)
    @Description("DData Management - Fee Schedule - Validation of Fee Schedules search in Fee Schedule Table. " +
            "Validation of filter functionality of the table by Status filter. " +
            "Validation of sort functionality by Fee Schedule name.  Validation of Pagination functionality. " +
            "Validation of Create New functionality to check whether the dialog is opening or not. ")
    public void feeSchedulesSearchAndFilters() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Data Management");
            rp.clickByLinkText("Fee Schedules");
            sa.assertEquals(rp.getPageHeading(), "Fee Schedules", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Fee Schedules", "incorrect table heading");

            rp.getNumOfGridResults();
            if (eu.getElements(rp.getFilterSelectClear("status-select")).size() > 0) {
                eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 5);
            }
            int beforeFilter = rp.getNumOfGridResults();
            rp.srhRegDrugDiagAndEnter(srhSchedule);

            if (rp.getNumOfGridResults() > 0) {
                eu.syncWait(1);
                int rowCount = eu.getGridRowCount(dp.feeScheduleTable);
                //System.out.println("Cell Data: " + rp.getGridRowData(dp.feeScheduleTable, 1));
                for (int i = 0; i < rowCount; i++) {
                    sa.assertTrue(rp.getRowCellData(dp.feeScheduleTable, i)[0].toLowerCase().contains(srhSchedule));
                }
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

                List<String> beforeSortListDates = rp.tableColumnList(dp.feeScheduleTable, 1);
                List<String> sortListDates = eu.sortItemsList(beforeSortListDates, "desc");
                rp.clickTableHeaderForSort("Effective Date");
                eu.syncWait(2);
                List<String> afterSortListDates = rp.tableColumnList(dp.feeScheduleTable, 1);
                System.out.println("Before: " + beforeSortListDates + "\r\n" + "Sort: " + sortListDates + "\r\n" + "After: " + afterSortListDates);
                sa.assertNotEquals(beforeSortListDates, afterSortListDates);
                rp.clickTableHeaderForSort("Effective Date");
                eu.syncWait(2);

                //Sort by Name
                List<String> beforeSortList = rp.tableColumnList(dp.feeScheduleTable, 0);
                List<String> sortList = eu.sortItemsList(beforeSortList, "desc");
                rp.clickTableHeaderForSort("Name");
                eu.syncWait(2);
                List<String> afterSortList = rp.tableColumnList(dp.feeScheduleTable, 0);
                //System.out.println("Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                sa.assertEquals(sortList, afterSortList, "Fee Schedule table sort by Name is not working");
                rp.clickTableHeaderForSort("Name");
                eu.syncWait(2);

                //Filer the results by Status filter
                rp.srhRegDrugDiagAndEnter("");
                rp.clickGridFilters("status-select", "Status");
                rp.selectListItemByName("Active");
                AllureReportListener.saveLogs("Number of records that match the selected Status filter criteria: " + rp.getNumOfGridResults());
                int afterStatusFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterStatusFilter);
                eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 3);

                //Pagination
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("10");
                eu.syncWait(2);
                String afterPagination = eu.getElement(rp.gridResults).getText();
                sa.assertNotEquals(beforePagination, afterPagination);

                //Create New Fee Schedule - validate and cancel the dialog that comes
                rp.clickButton("Create New");
                sa.assertTrue(rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Create New Fee Schedule"));
                if (rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Create New Fee Schedule")) {
                    rp.clickButton("Cancel");
                }
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();

    }
    @Test(priority = 2, groups = {"smoke"}, description = "Data Management - Fee Schedule - Validation of Fee Schedules creation. " +
            "Validation of below items in Edit fee Schedule page and FeeScheudleEdit table for Drugs- . " +
            "Validation of Reimbursement Values section. Validation of percentage change values" +
            "Validation of calculate drug values by average sales price filter (pricing types awp ,asp,  medicare which is asp+6%)" +
            "Validation of date selection change.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Data Management - Fee Schedule - Data Management - Fee Schedule - Validation of Fee Schedules creation." +
            "Validation of below items in Edit fee Schedule page and FeeScheudleEdit table - " +
            "Validation of Reimbursement Values section. Validation of percentage change values" +
            "Validation of calculate drug values by average sales price filter (pricing types awp ,asp,  medicare which is asp+6%)" +
            "Validation of date selection change.")
    public void feeSchedulesCreateNew() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            //rp.clickLeftMenuItem("Data Management");
            rp.clickByLinkText("Fee Schedules");
            sa.assertEquals(rp.getPageHeading(), "Fee Schedules","Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Fee Schedules", "Incorrect table heading");

            //Creating new fee schedule with the name feeScheduleName
            rp.clickButton("Create New");
            sa.assertTrue(rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Create New Fee Schedule"));
            if (rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Create New Fee Schedule")) {
                String feeScheduleName = "Automation FeeSchedule " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
                rp.sendValue("name", feeScheduleName);
                rp.clickSubmit("Save");
                eu.syncWait(5);

                if (rp.getNumOfGridResults() > 0) {

                    //checking the Calculate drug values by pricing filter - Medicare(ASP + 6%)
                    rp.clickButton("Calculate");
                    eu.syncWait(3);
                    List<String> beforeCalculate = rp.tableColumnList(dp.feeScheduleEditTable, 3);
                    int rowCount = eu.getGridRowCount(dp.feeScheduleEditTable);
                    for (int i = 0; i < 5; i++) {
                        sa.assertTrue(rp.getRowCellData(dp.feeScheduleEditTable, i)[3].contains("100% of Medicare (ASP + 6%)"));
                    }

                    //Checking the Calculate drug values by pricing filter - ASP and validate the table
                    rp.clickGridFilters("calculation-method-select", "Medicare");
                    eu.syncWait(1);
                    rp.selectListItemByIndex(1);
                    rp.clickButton("Calculate");
                    eu.syncWait(3);
                    List<String> afterCalculate = rp.tableColumnList(dp.feeScheduleEditTable, 3);
                    for (int i = 0; i < rowCount; i++) {
                        sa.assertTrue(rp.getRowCellData(dp.feeScheduleEditTable, i)[3].contains("100% of ASP"));
                    }

                    //Adding assertion for checking ASP and Medicare(ASP+6%) are not same
                    sa.assertNotEquals(beforeCalculate, afterCalculate, "Calculate drug value by pricing filter did not update values in table");

                    //Change percentage and validate
                    String beforeRowValues = rp.getGridRowData(dp.feeScheduleEditTable, 1);
                    String percent = "75";
                    rp.sendKeysByAction("percent", percent);
                    rp.clickButton("Calculate");
                    eu.syncWait(3);
                    String afterRowValues = rp.getGridRowData(dp.feeScheduleEditTable, 1);
                    //System.out.println("Before" + beforeRowValues + "\n" + "After:" + afterRowValues );
                    if (rp.getNumOfGridResults() > 0) {
                        sa.assertTrue(rp.getRowCellData(dp.feeScheduleEditTable, 0)[3].contains(percent));
                        sa.assertNotEquals(afterRowValues, beforeRowValues, "Percentage change did not change the table values");
                    }

                    //Select a different date from date dropdown and validate
                    String calculationYear = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
                    String calculationDate = rp.getGridFilters("calculation-effective-date-select", calculationYear).getText();
                    String beforeCalculationDate = rp.getRowCellData(dp.feeScheduleEditTable, 0)[3];
                    rp.clickGridFilters("calculation-effective-date-select", calculationYear);
                    rp.selectListItemByIndex(1);
                    rp.clickButton("Calculate");
                    eu.syncWait(3);
                    String afterCalculationDate = rp.getRowCellData(dp.feeScheduleEditTable, 0)[3];
                    if (rp.getNumOfGridResults() > 0) {
                        sa.assertNotEquals(beforeCalculationDate, afterCalculationDate, "Date change did not change the table values");
                    }

                    //Save the fee schedule and verify whether its saved with inactive status
                    //RowcellData for column Status is returning null(it should return Active/Inactive like other
                    // columns but its not) so got the count before and after for assertion
                    rp.clickSubmit("Save");
                    eu.syncWait(3);
                    rp.srhRegDrugDiagAndEnter(feeScheduleName);
                    int feeScheCountBefore = eu.getGridRowCount(dp.feeScheduleTable);
                    eu.syncWait(3);
                    eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 3);
                    rp.srhRegDrugDiagAndEnter(feeScheduleName);
                    int feeScheCountAfter = eu.getGridRowCount(dp.feeScheduleTable);
                    if (rp.getNumOfGridResults() > 0) {
                        sa.assertTrue(rp.getRowCellData(dp.feeScheduleTable, 0)[0].equals(feeScheduleName));
                        sa.assertNotEquals(feeScheCountAfter, feeScheCountBefore, "Create New Fee Schedule is not working");
                    }

                }
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 3, groups = {"smoke"}, description = "Data Management - Fee Schedule - " +
            "Validation of below items in Edit fee Schedule page and FeeScheudleEdit table for Non-Drugs- . " +
            "Validation of Reimbursement Values section. Validation of percentage change values" +
            "Validation of date selection change.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Data Management - Fee Schedule - Data Management - Fee Schedule - " +
            "Validation of below items in Edit fee Schedule page and FeeScheudleEdit table - " +
            "Validation of Reimbursement Values section. Validation of percentage change values" +
            "Validation of date selection change.")
    public void feeSchedulesCreateNewNonDrugs() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            //rp.clickLeftMenuItem("Data Management");
            rp.clickByLinkText("Fee Schedules");
            sa.assertEquals(rp.getPageHeading(), "Fee Schedules","Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Fee Schedules", "Incorrect table heading");

            //Creating new fee schedule with the name feeScheduleName
            rp.clickButton("Create New");
            sa.assertTrue(rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Create New Fee Schedule"),"Incorrect modal window heading");
            if (rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Create New Fee Schedule")) {
                String feeScheduleName = "Automation FeeSchedule " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
                rp.sendValue("name", feeScheduleName);
                rp.clickSubmit("Save");
                eu.syncWait(5);
                rp.clickTabs("Non-Drugs");

                if (rp.getNumOfGridResults() > 0) {

                    //checking the Calculate drug values by pricing filter - Medicare
                    rp.clickButton("Calculate");
                    eu.syncWait(3);
                    //List<String> beforeCalculate = rp.tableColumnList(dp.feeScheduleEditTable, 3);
                    int rowCount = eu.getGridRowCount(dp.feeScheduleEditTable);
                    for (int i = 0; i < 5; i++) {
                        sa.assertTrue(rp.getRowCellData(dp.feeScheduleEditTable, i)[2].contains("100% of Medicare"),
                                "Non-Drugs - Medicare - Calculate - The row values does not contain 100% of Medicare after calculating values ");
                    }

                    //Change percentage and validate
                    String beforeRowValues = rp.getGridRowData(dp.feeScheduleEditTable, 1);
                    String percent = "80";
                    rp.sendKeysByAction("percent", percent);
                    rp.clickButton("Calculate");
                    eu.syncWait(3);
                    String afterRowValues = rp.getGridRowData(dp.feeScheduleEditTable, 1);
                    //System.out.println("Before" + beforeRowValues + "\n" + "After:" + afterRowValues );
                    if (rp.getNumOfGridResults() > 0) {
                        sa.assertTrue(rp.getRowCellData(dp.feeScheduleEditTable, 0)[2].contains(percent),
                                "Non-Drug - Calculate - Percentage - Fee Schedule Edit table does not contain the updated percentage.");
                        sa.assertNotEquals(afterRowValues, beforeRowValues, "Non-Drugs: Percentage change did not change the table values");
                    }

                    //Select a different date from date dropdown and validate
                    String calculationYear = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
                    String calculationDate = rp.getGridFilters("calculation-effective-date-select", calculationYear).getText();
                    String beforeCalculationDate = rp.getRowCellData(dp.feeScheduleEditTable, 0)[2];
                    rp.clickGridFilters("calculation-effective-date-select", calculationYear);
                    rp.selectListItemByIndex(1);
                    rp.clickButton("Calculate");
                    eu.syncWait(3);
                    String afterCalculationDate = rp.getRowCellData(dp.feeScheduleEditTable, 0)[2];
                    if (rp.getNumOfGridResults() > 0) {
                        sa.assertNotEquals(beforeCalculationDate, afterCalculationDate, "Non-Drugs: Date dropdown change did not change the table values");
                    }

                    //Save the fee schedule and verify whether its saved with inactive status
                    //RowcellData for column Status is returning null(it should return Active/Inactive like other
                    // columns but its not) so got the count before and after for assertion
                    rp.clickSubmit("Save");
                    eu.syncWait(3);
                    rp.srhRegDrugDiagAndEnter(feeScheduleName);
                    int feeScheCountBefore = eu.getGridRowCount(dp.feeScheduleTable);
                    eu.syncWait(3);
                    eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 3);
                    rp.srhRegDrugDiagAndEnter(feeScheduleName);
                    int feeScheCountAfter = eu.getGridRowCount(dp.feeScheduleTable);
                    if (rp.getNumOfGridResults() > 0) {
                        sa.assertTrue(rp.getRowCellData(dp.feeScheduleTable, 0)[0].equals(feeScheduleName),
                                "Non-Drugs: Fee Schedule grid does not contain the newly created Fee Scheudle name");
                        sa.assertNotEquals(feeScheCountAfter, feeScheCountBefore, "Non-Drugs: Create New Fee Schedule is not working");
                    }

                }
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }
}






