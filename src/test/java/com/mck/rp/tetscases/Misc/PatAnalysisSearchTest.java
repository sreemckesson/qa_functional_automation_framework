package com.mck.rp.tetscases.Misc;

import com.aventstack.extentreports.Status;
import com.mck.rp.listeners.ExtentReportListener;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.LoginPage;
import io.qameta.allure.*;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mck.rp.base.BaseTest;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import org.testng.asserts.SoftAssert;


//@Epic("Epic - 101 : design login page features...")
//@Feature("US - 201: desgin login page title, sign up link and login form modules...")
@Listeners(AllureReportListener.class)
public class PatAnalysisSearchTest extends BaseTest {
    ElementUtil eu;
    RegimenAnalysisPage rp;
    LoginPage lp;

    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void patAnalysisSetUp() throws InterruptedException {
        rp = new RegimenAnalysisPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);

    }

    @Description("Patient Analysis Search ...")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 1, groups = "smoke", description = "Patient Analysis -  View Patient information")
    public void patientAnalysisSearch() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        allureReportLog("Patient Analysis Search Test with 3 test cases");
        Assert.assertTrue(rp.isLogoutExist());
        //Thread.sleep(1000);
        rp.clickTabs("Patient Analysis");
        sa.assertEquals(rp.getPageHeading(), "Create a Patient Report");
        //sa.assertEquals(rp.getGridHeading(), "Patient Information");
        sa.assertEquals(rp.getGridHeading(), "Patient Informtion");

        eu.doClick(rp.useDefaults);
        //Thread.sleep(1000);
        rp.clickSubmit("Calculate");
        //Thread.sleep(1000);
        sa.assertTrue(eu.isElementPresent(rp.patTable));
        sa.assertAll();
    }

    @Description("Patient Analysis Grid Search by providing the drug name...")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 2, description = "Patient Analysis - Search Drugs and return results")
    public void patAnalysisGridDrugSearch() throws InterruptedException {
        //log.info("PatientAnalysis Page - Search the table grid");
        SoftAssert sa = new SoftAssert();
        rp.srhRegDrugDiagAndEnter("ABVD");
        ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
        AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());;

        //Thread.sleep(3000);
        sa.assertAll();

    }

    @Test(priority = 3, description = "Patient Analysis - Check Table Row Details")
    public void patAnalysisTableRowDetails() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        //log.info("PatientAnalysis Page - Table Row Details");
        //Thread.sleep(3000);
        rp.srhRegDrugDiagAndEnter("inflix");
        ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
        AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());;


        //Rows are in odd numbers 1, 3, 5 etc, even numbers are the separators in the grid
        String gridRowText = eu.getText(rp.getGridcell("patient-analysis-table", 1,1));
        //Thread.sleep(1000);
        rp.clickGridCell("patient-analysis-table",1,1);
    /*
        //Need to find unique grid value to rpint out the details
        WebElement gridResultNum = getDriver().findElement(By.xpath("//td[@data-testid = 'table-row-details']"));
        String rowResults = gridResultNum.getAttribute("textContent");
        log.info("Table Row Grid Details: " + rowResults);

     */

        eu.scrollToView(rp.btnViewEditDts);
        eu.doClick(rp.btnViewEditDts);
        //Thread.sleep(5000);
        String drugViewDts = eu.getTextcontent(rp.drugDetailsTitle);
        //System.out.println(drugViewDts);
        sa.assertEquals(gridRowText, drugViewDts.substring(0, drugViewDts.length() -1));

        sa.assertTrue(eu.isElementPresent(rp.getBtnTypeButton("Add Lump Sum")));

        if (gridRowText.equals(drugViewDts.substring(0, drugViewDts.length() - 1))) {
            extentReportLog("info", "After searching by the drug name and selecting a row from the results, the drug details page" +
                    "matches with the searched drug name " + drugViewDts);
            allureReportLog("After searching by the drug name and selecting a row from the results, the drug details page" +
                    "matches with the searched drug name " + drugViewDts);
        }

        WebElement btnAddLumpSum = eu.getElement(rp.getBtnTypeButton("Add Lump Sum"));

        if (eu.isElementPresent(rp.getBtnTypeButton("Add Lump Sum"))) {
            extentReportLog("info","Able to click on the table row drug and able to view the details and lumpsum button");
            allureReportLog("Able to click on the table row drug and able to view the details and lumpsum button");

        }
        sa.assertAll();
    }


}


