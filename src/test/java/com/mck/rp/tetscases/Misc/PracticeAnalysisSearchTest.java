package com.mck.rp.tetscases.Misc;

import com.aventstack.extentreports.Status;
import com.mck.rp.listeners.ExtentReportListener;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.SeverityLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;

import com.mck.rp.base.BaseTest;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Listeners(AllureReportListener.class)
public class PracticeAnalysisSearchTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    LoginPage lp;

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void pracAnalysisSetUp() throws InterruptedException {
        rp = new RegimenAnalysisPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);
    }


    @Test(priority = 1, description = "Practice Analysis - Search for a drug")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Search")
    public void pracSearch() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        extentReportLog("info", "Prac Test Started");
        rp.clickTabs("Practice Analysis");
        rp.sendKeysByAction("height", "120");
        rp.srhRegDrugDiagAndEnter("albumin");
        //Thread.sleep(2000);
        ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
        AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());
        sa.assertAll();

    }

    @Test(priority = 2, description = "Practice Analysis - Vertical Menu")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Search")
    public void leftMenuSelect() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        rp.clickLeftMenuItem("Regimen Analysis");

        rp.clickLeftMenuItem("Patient Analysis");
        sa.assertEquals(rp.getPageHeading(), "Create a Patient Report");
        sa.assertEquals(rp.getGridHeading(), "Patient Information");

        rp.clickLeftMenuItem("Practice Analysis");
        sa.assertEquals(rp.getPageHeading(), "Find a Regimen");
        sa.assertEquals(rp.getGridHeading(), "Analysis Criteria");
        sa.assertAll();

    }

    @Test(priority = 3, description = "Practice Analysis - Dropdowns")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Search")
    public void pracAnalysisDropdownSelectItems() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        rp.clickTabs("Practice Analysis");
        rp.sendKeysByAction("height", "120");

        rp.clickGridFilters("break-even-select", "Break Even");
        rp.selectFilterItemByIndex(1);
        //rp.getNumOfGridResults();

        //Thread.sleep(3000);
        rp.clickGridFilters("diagnosis-select", "Diagnosis");
        rp.selectFilterItemByIndex(1);
        //rp.getNumOfGridResults();

        rp.clickGridFilters("drugs-select", "Drug");
        Thread.sleep(2000);
        rp.selectFilterItemByIndex(1);
        Thread.sleep(2000);
        ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
        AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());
        ;


        sa.assertAll();

    }

    @Test(priority = 4, description = "Practice Analysis - Dropdowns Select By Text")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Search")
    public void pracAnalysisDropdownSelectItemsbyText() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        rp.clickLeftMenuItem("Regimen Analysis");
        rp.clickByLinkText("Patient Analysis");
        //sa.assertEquals(rp.getPageHeading(), "Find a Regimen");
        //sa.assertEquals(rp.getGridHeading(), "Analysis Criteria");

        eu.doClick(rp.useDefaults);
        rp.clickSubmit("Calculate");
       // rp.srhRegDrugDiagAndEnter("amyloidosis");
        Thread.sleep(5000);

        String result = eu.getElement(rp.gridResults).getText();
        System.out.println("Before: " + result);
        int a = eu.getGridRowCount(rp.patTable);
        System.out.println("Num of Rows: "+ a);
        //select[@aria-label='select rows per page']//option[@value='50']

        rp.selectPaginationRows("10");
        Thread.sleep(4000);
        String result2 = eu.getElement(rp.gridResults).getText();
        System.out.println("Before: " + result2);

        // select an option by index method
       // s.selectByIndex(0);
        //Thread.sleep(1000);

        /*List<String> vowels = new ArrayList<>();
        for (int i = 0; i<a;i++){
            vowels.add(rp.getRowColumnData(rp.patTable, i, 0));
        }
        System.out.println("Original List: "+ vowels);

        List<String> sortlist = new ArrayList<>();
        sortlist = eu.sortItemsList(vowels, "desc");
        System.out.println("Sorted List: " + sortlist);

        rp.clickTableHeaderForSort("Regimen Name");
        Thread.sleep(5000);

        List<String> li2 = new ArrayList<>();
        for (int i = 0; i<a;i++){
            li2.add(rp.getRowColumnData(rp.patTable, i, 0));
        }
        System.out.println("App Sorted List: " + li2);

        sa.assertEquals(sortlist, li2);*/

        //rp.tableColumnSort(rp.patTable, "Regimen Name");
/*
        //System.out.println(rp.tableColumnList(rp.patTable, "Regimen Name", 0));
        List<String> beforeSortList = rp.tableColumnList(rp.patTable, 0);
        List<String> sortlist = eu.sortItemsList(beforeSortList, "desc");
        rp.clickTableHeaderForSort("Regimen Name");
        Thread.sleep(5000);
        List<String> afterSortList = rp.tableColumnList(rp.patTable, 0);
        sa.assertEquals(beforeSortList, afterSortList);

        rp.clickTableHeaderForSort("Regimen Name");
        Thread.sleep(5000);

        List<String> num = new ArrayList<>();
        List<String> newNum = new ArrayList<>();
        List<Integer> intNum = new ArrayList<>();

        for (int i = 0; i<9;i++){
            num.add(rp.getRowColumnData(rp.patTable, i, 1));
           //num.get(i).replace("$","");
           newNum.add(num.get(i).replaceAll("[$,.]",""));
           intNum.add(Integer.parseInt(newNum.get(i)));
        }

        System.out.println("Pat Res List: "+ intNum);

        List<Integer> sortlistnum = new ArrayList<>();
        sortlistnum =  eu.sortItemsNums(intNum, "desc");
        System.out.println("Sorted List: " + sortlistnum);



        rp.clickTableHeaderForSort("Patient Responsibility");
        Thread.sleep(5000);

        List<String> num2 = new ArrayList<>();
        List<String> newNum2 = new ArrayList<>();
        List<Integer> newNum3 = new ArrayList<>();
        for (int i = 0; i<9;i++){
            num2.add(rp.getRowColumnData(rp.patTable, i, 1));
            //num2.get(i).replace("$","");
            newNum2.add(num2.get(i).replaceAll("[$,.]",""));
            newNum3.add(Integer.parseInt(newNum2.get(i)));
        }

        System.out.println("Pat Res List After: "+ newNum3);

        sa.assertNotEquals(intNum, newNum3);



        System.out.println("Column Data: " + rp.getRowsColumnData(rp.pracTable, 1, 1)[1]);
        System.out.println("Column Data: " +rp.getRowsColumnData(rp.pracTable, 1, 2)[1]);
        System.out.println("Column Data: " +rp.getRowsColumnData(rp.pracTable, 2, 3)[0]);
        System.out.println("Column Data: " +rp.getRowsColumnData(rp.pracTable, 3, 4)[0]);

        //To verify a specific text present or not in the table
        //rp.verifyTextWebtableSpecificColAndRows(eu.getElement(rp.pracTable), "ABVD", 2, 1, 6);
/*
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 1)[1]);
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 2)[1]);
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 3)[1]);
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 4)[1]);
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 5)[0]);

        WebElement table = driver.findElement(rp.pracTable);
        List<WebElement> tableRows = table.findElements(By.xpath("//tr[contains(@class, 'TableRow-hover')]"));
        System.out.println("Table Rows: "+ tableRows.size());
        if (tableRows.size() > 0) {

            String[] columnData = new String[tableRows.size()];
            for (int i = 0; i < tableRows.size(); i++) {
                List<WebElement> tableColumns = tableRows.get(i).findElements(By.xpath("//td[@data-row-index='"+i+"']"));
                System.out.println("Table Columns: " + tableColumns.size());

                if (tableColumns.size() > 0) {
                    String text = tableColumns.get(1).getAttribute("textContent");
                    System.out.println("Col Data: "+ i +  text);
                    columnData[i] = text;

                }
            }
*/

            sa.assertAll();


    }
}



