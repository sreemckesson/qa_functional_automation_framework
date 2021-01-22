package com.mck.rp.tetscases.Misc;

import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.LoginPage;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.List;


@Listeners(AllureReportListener.class)
public class PracticeAnalysisTablesTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    LoginPage lp;
    public static Logger log = LogManager.getLogger(PracticeAnalysisTablesTest.class);

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void pracAnalysisSetUp() throws InterruptedException {
        rp = new RegimenAnalysisPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);
    }


    @Test(priority = 1, description = "Practice Analysis - Get table rows, columns count and cell data")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Search")
    public void pracTableRowsColumns() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        log.info("LoggerFile");
        log.debug("LoggerFile");
        log.fatal("LoggerFile");
        log.error("LoggerFile");
        log.trace("LoggerFile");
        log.warn("WarnLog");
        //extentReportLog("info", "Prac Test Started");
        rp.clickTabs("Practice Analysis");
        rp.srhRegDrugDiagAndEnter("ABVD");
        Thread.sleep(2000);
        //rp.getNumOfGridResults();
        System.out.println("Row Count " + eu.getGridRowCount(rp.pracTable));

        System.out.println("Col Count " + eu.getGridColumnCount(rp.pracTable));

        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 1)[1]);
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 2)[1]);
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 3)[1]);
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 4)[1]);
        System.out.println("Column Data: " + eu.getGridColumnData(rp.pracTable, 5)[0]);

        System.out.println("Cell  Data: " + rp.getRowCellData(rp.pracTable, 1)[1]);
        System.out.println("Cell Data: " + rp.getRowCellData(rp.pracTable, 1)[2]);
        System.out.println("cell Data: " + rp.getRowCellData(rp.pracTable, 1)[3]);
        System.out.println("cell Data: " + rp.getRowCellData(rp.pracTable, 1)[4]);
        System.out.println("cell Data: " + rp.getRowCellData(rp.pracTable, 1)[5]);

        for (int i =0;i<21;i++ ) {
            System.out.println("Cell  Data: " + rp.getCellData(rp.pracTable)[2][i]);
        }
        System.out.println("Cell Data: " + rp.getCellData(rp.pracTable)[1][3]);
        System.out.println("Cell  Data: " + rp.getRowCellData(rp.pracTable, 1)[1]);
        System.out.println("Cell Data: " + rp.getRowCellData(rp.pracTable, 1)[2]);
        System.out.println("cell Data: " + rp.getRowCellData(rp.pracTable, 1)[3]);
        System.out.println("cell Data: " + rp.getRowCellData(rp.pracTable, 1)[4]);
        System.out.println("cell Data: " + rp.getRowCellData(rp.pracTable, 1)[5]);
        sa.assertTrue(rp.getRowCellData(rp.pracTable, 1)[1].contains("ABVD"));
        sa.assertAll();

    }

    @Test(priority = 2, description = "Patient Analysis - Search for a drug and add assertion for the drug existence in all the rows")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Table")
    public void patTableValidate() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        //extentReportLog("info", "Prac Test Started");
        rp.clickTabs("Patient Analysis");
        eu.doClick(rp.useDefaults);
        //Thread.sleep(1000);
        rp.clickSubmit("Calculate");

        rp.srhRegDrugDiagAndEnter("Bevacizumab");
        Thread.sleep(2000);
        //rp.getNumOfGridResults();
        System.out.println("Row Count " + eu.getGridRowCount(rp.patTable));

        System.out.println("Col Count " + eu.getGridColumnCount(rp.patTable));

        System.out.println("Column Data: " + eu.getGridColumnData(rp.patTable, 1)[0]);

        int a = eu.getGridColumnCount(rp.patTable);
        for (int b = 0; b<a; b =b+3){
            //System.out.println("Column " + b + " Data: " +  eu.getGridColumnData(rp.patTable, b)[0]);
            sa.assertTrue(eu.getGridColumnData(rp.patTable, b)[3].contains("Bevacizumab"));
            //System.out.println("Column Data: " + eu.getGridColumnData(rp.patTable, 10));
        }

        System.out.println("Cell Data: " + rp.getCellData(rp.patTable)[1][0]);
        sa.assertTrue(rp.getRowCellData(rp.patTable, 1)[0].contains("Bevacizumab"));
        sa.assertAll();

    }

    @Test(priority = 3, description = "Practice Analysis - Get table rows, columns count and cell data")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice Analysis Page - Search")
    public void getFilterItemsCount() throws InterruptedException {
        SoftAssert sa = new SoftAssert();
        //extentReportLog("info", "Prac Test Started");
        rp.clickTabs("Practice Analysis");
        rp.clickGridFilters("break-even-select", "Break Even");
        System.out.println("Filter Items Count: " + rp.filterItemsCount());

        rp.clickGridFilters("diagnosis-select", "Diagnosis");
        System.out.println("Filter Items Count: " + rp.filterItemsCount());

        rp.clickGridFilters("drugs-select", "Drug");;
        System.out.println("Filter Items Count: " + rp.filterItemsCount());

    }

    public static boolean verifyTextWebtableSpecificColAndRows(WebElement mytable, String testString, int colNum,
                                                               int startingRowNum, int endRowNum) {
        boolean flag = false;
        try {
            // to locate rows of table
            List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));
            // loop will execute till the last row of table
            for (int row = startingRowNum; row < endRowNum; row++) {
                // to locate columns(cell) of that specific row
                List<WebElement> columns_row = rows_table.get(row).findElements(By.tagName("td"));
                // to calculate nu, of columns(cells) in that specific row
                int columns_count = columns_row.size();
                System.out.println("Number of cells in the row " + row + " are " + columns_count);

                // loop will execute till the last cell of that specific row

                // To retrieve text from that specific cell
                String celtext = columns_row.get(colNum).getText().trim();
                System.out.println("Cell value of row number " + row + " and columns number 2 is " + celtext);

                if (celtext.equalsIgnoreCase(testString)) {
                    System.out.println("Verfication passed. Cell value " + celtext + " test string " + testString);
                    flag = true;
                } else {
                    System.out.println("Verfication failed. Cell value " + celtext + " test string " + testString);
                    flag = false;
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return flag;

    }

}
