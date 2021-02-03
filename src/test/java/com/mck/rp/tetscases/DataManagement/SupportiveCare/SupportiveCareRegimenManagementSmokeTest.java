package com.mck.rp.tetscases.DataManagement.SupportiveCare;

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
import java.util.Random;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


@Listeners(AllureReportListener.class)
public class SupportiveCareRegimenManagementSmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    LoginPage lp;
    String srhDrug = "granisetron";

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void supportiveCareSetUp() {
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

    @Test(priority = 1, groups = "smoke", description = "Data Management - Supportive Care Regimen - Validation of search by Regimen in Supportive Care Regimen Table " +
            " and Filter the table by Status, Regimen Class and Last Updated By Filters. Validation of Pagination functionality. " +
            "Validation of Edit Regimen details page. Validation of Create New - Antiemetic Regimen and Create New - Growth Factor Regimen pages. ")
    @Severity(SeverityLevel.NORMAL)
    @Description("Data Management - Supportive Care Regimen - Validation of search by Regimen in Supportive Care Regimen Table " +
            " and Filter the table by Status, Regimen Class and Last Updated By Filters. Validation of Pagination functionality. " +
            "Validation of Edit Regimen details page. Validation of Create New - Antiemetic Regimen and Create New - Growth Factor Regimen pages.")
    public void suppCareRgimenSearchAndFilters() throws NoSuchElementException {

        SoftAssert sa = new SoftAssert();
        Random rand = new Random();
        try {
            sa.assertTrue(rp.isLogoutExist(), "Error finding logout");
            rp.clickLeftMenuItem("Data Management");
            rp.clickByLinkText("Supportive Care Regimen Management");
            sa.assertEquals(rp.getPageHeading(), "Supportive Care Regimen Management", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Supportive Care Regimens", "Incorrect grid heading");

            rp.srhRegDrugDiagAndEnter(srhDrug);
            //System.out.println("Name: "+rp.getRowCellData(dp.supportiveCareTable, 1)[0]);
            if (rp.getNumOfGridResults() > 0) {
                int rowCount = eu.getGridRowCount(dp.supportiveCareTable);
                System.out.println("Row Data: " + rp.getGridRowData(dp.supportiveCareTable, 1));
                for (int i = 0; i < rowCount; i++) {
                    sa.assertTrue(rp.getRowCellData(dp.supportiveCareTable, i)[0].toLowerCase().contains(srhDrug), "Incorrect search drug");
                }

               // ExtentReportListener.test.get().log(Status.INFO,
               //     "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

                //Export
                rp.clickButton("Export");
                rp.clickSubListItem("Export as Excel (.xlsx)");

                //Verify when clicking on Create New - Growth Factor and Antiemetic Regimen opens the pages and the
                //page heading are matching accordingly
                rp.clickButton("Create New");
                dp.dataMngClickSubListItem("Growth Factor Regimen"); //Main Page
                sa.assertEquals(rp.getPageHeading(), "Create a Growth Factor Regimen", "Incorrect detail page heading");
                sa.assertEquals(rp.getGridHeading(), "Regimen Summary", "Incorrect grid heading");
                eu.scrollToBottom();
                rp.clickButton("Cancel");

                rp.clickButton("Create New");
                dp.dataMngClickSubListItem("Antiemetic Regimen"); //Main Page
                sa.assertEquals(rp.getPageHeading(), "Create an Antiemetic Regimen", "Incorrect detail page heading");
                sa.assertEquals(rp.getGridHeading(), "Regimen Summary", "Incorrect grid heading");
                String addRegimen = "Test Auto SCR Antiemetic " + rand.nextInt(9999);
                eu.doSendKeys(dp.getTextArea("regimen-name"), addRegimen);
                eu.scrollToBottom();
                eu.doClick(dp.getBtnTypeButton("button", "formButton"));
                rp.clickSubmit("Save");


                //Verify when filtering by Regimen Class, Last Updated By and Status filtering the results accordingly
                rp.srhRegDrugDiagAndEnter("");
                int beforeFilter = rp.getNumOfGridResults();
                rp.clickGridFilters("regimen-class-select", "Regimen Class");
                rp.selectListItemByName("Antiemetic");
              //  ExtentReportListener.test.get().log(Status.INFO,
               //     "Number of records that match the selected Regimen Class filter criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the selected Regimen Class filter criteria: " + rp.getNumOfGridResults());
                int afterRegimenFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterRegimenFilter, "Incorrect regimen class filter count");
                eu.clickWhenReady(rp.getFilterSelectClear("regimen-class-select"), 2);

                rp.clickGridFilters("last-updated-by-select", "Last Updated By");
                rp.selectFilterItemByIndex(0);
               // ExtentReportListener.test.get().log(Status.INFO,
               //     "Number of records that match the selected Last Updated By filter criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the selected Last Updated By filter criteria: " + rp.getNumOfGridResults());
                int afterLastUpdatedByFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterLastUpdatedByFilter, "Incorrect last updated by filter count ");
                eu.clickWhenReady(rp.getFilterSelectClear("last-updated-by-select"), 2);

                rp.clickGridFilters("status-select", "Status");
                rp.selectListItemByName("Active");
              //  ExtentReportListener.test.get().log(Status.INFO,
              //      "Number of records that match the selected Status filter criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the selected Status filter criteria: " + rp.getNumOfGridResults());
                int afterStatusFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterStatusFilter, "Incorrect status filter count");
                eu.clickWhenReady(rp.getFilterSelectClear("status-select"), 2);

                //Pagination
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("10");
                eu.syncWait(2);
                String afterPagination = eu.getElement(rp.gridResults).getText();
              //  ExtentReportListener.test.get().log(Status.INFO, "Grid Results before Pagination: " + beforePagination);
              //  ExtentReportListener.test.get().log(Status.INFO, "Grid Results after Pagination: " + afterPagination);
                sa.assertNotEquals(beforePagination, afterPagination, "Incorrect pagination count");


                //Verify whether Regimen Summary page description matches the selected Regimen description
                String a = rp.getRowCellData(dp.supportiveCareTable, 0)[0];
                String regimenDesc = ElementUtil.removeLastChars(a, 7);
                rp.clickGridCell("supportive-care-table", 1, 1);
                eu.waitForElementPresent(dp.suppCareRegimenSummaryName, 3);
                sa.assertEquals(rp.getPageHeading(), "Edit an Antiemetic Regimen", "Incorrect Edit page heading");
                sa.assertEquals(rp.getGridHeading(), "Regimen Summary", "Incorrect Edit page grid heading");
                String summaryDesc = rp.getTextContent(dp.suppCareRegimenSummaryName);
                //System.out.println("Main Table Data: "+ regimenDesc + "Summary: "+ summaryDesc);
                sa.assertEquals(regimenDesc, summaryDesc, "Incorrect desc in summary");
                eu.scrollByPixel(500);
                rp.clickButton("Cancel");

            } else {
                sa.fail();
               // ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (InterruptedException e) {
           // ExtentReportListener.test.get().log(Status.FAIL, "Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();

    }

}
