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
            sa.assertEquals(rp.getPageHeading(), "Fee Schedules");
            sa.assertEquals(rp.getGridHeading(), "Fee Schedules");

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
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
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
                sa.assertEquals(sortList, afterSortList);
                rp.clickTableHeaderForSort("Name");
                eu.syncWait(2);

                //Filer the results by Status filter
                rp.srhRegDrugDiagAndEnter("");
                rp.clickGridFilters("status-select", "Status");
                rp.selectListItemByName("Active");
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Status filter criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the selected Status filter criteria: " + rp.getNumOfGridResults());
                int afterStatusFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterStatusFilter);
                eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 3);

                //Pagination
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("10");
                eu.syncWait(2);
                String afterPagination = eu.getElement(rp.gridResults).getText();
                ExtentReportListener.test.get().log(Status.INFO, "Grid Results before Pagination: " + beforePagination);
                ExtentReportListener.test.get().log(Status.INFO, "Grid Results after Pagination: " + afterPagination);
                sa.assertNotEquals(beforePagination, afterPagination);

                //Create New Fee Schedule - validate and cancel the dialog that comes
                rp.clickButton("Create New");
                sa.assertTrue(rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Create New Fee Schedule"));
                if (rp.getTextContent(rp.addDrugNondrugDialogH2).contains("Create New Fee Schedule")) {
                    rp.clickButton("Cancel");
                }
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


}
