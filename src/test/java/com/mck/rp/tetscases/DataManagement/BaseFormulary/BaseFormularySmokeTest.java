package com.mck.rp.tetscases.DataManagement.BaseFormulary;

import com.aventstack.extentreports.Status;
import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.ExtentReportListener;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.LoginPage;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.pageObjects.DataManagementPage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Listeners(AllureReportListener.class)
public class BaseFormularySmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    LoginPage lp;
    String srhDrug = "liposomal"; //"sulfate"
    String srhHCPC = "j1454";
    String srhNonDrug = "drug";
    String calculationDrug = "bleomycin";
    String drugDetailsSrhDrug = "Granisetron injection";

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void baseFormularySetUp() {
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

    @Test(priority = 1, groups = "smoke", description = "Data Management - Base Formulary Page - Drug Formulary - Validation of search functionality by Drug and HCPC in Drug Formulary" +
            " Validation of HCPC search return a unique record or not . Validation of Pagination. Validation of Sort functionality by" +
            " a string and a numeric sort")
    @Severity(SeverityLevel.NORMAL)
    @Description("Data Management - Base Formulary Page - Drug Formulary - Validation of search functionality by Drug and HCPC in Drug Formulary " +
            "  Validation of HCPC search return a unique record or not. Validation of Pagination. Validation of Sort functionality by " +
            " a string and a numeric sort")
    public void baseFormularyDrugsTableSearch() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Data Management");
            rp.clickByLinkText("Base Formulary");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary");
            sa.assertEquals(rp.getGridHeading(), "Drug Formulary");

            if (rp.getNumOfGridResults() > 0) {
                //Pagination
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("10");
                eu.syncWait(2);
                String afterPagination = eu.getElement(rp.gridResults).getText();
                ExtentReportListener.test.get().log(Status.INFO, "Grid Results before Pagination: " + beforePagination);
                ExtentReportListener.test.get().log(Status.INFO, "Grid Results after Pagination: " + afterPagination);
                sa.assertNotEquals(beforePagination, afterPagination);

                rp.srhRegDrugDiagAndEnter(srhDrug);
                int rowCount = eu.getGridRowCount(dp.drugFormularyTable);
                //System.out.println("Cell Data: " + rp.getGridRowData(dp.drugFormularyTable, 1));
                for (int i = 0; i < rowCount; i++) {
                    sa.assertTrue(rp.getRowCellData(dp.drugFormularyTable, i)[0].toLowerCase().contains(srhDrug.toLowerCase()));
                }
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

                rp.srhRegDrugDiagAndEnter(srhHCPC);
                int rowCountHCPC = eu.getGridRowCount(dp.drugFormularyTable);
                if (eu.getGridRowCount(dp.drugFormularyTable) == 1) {
                    sa.assertTrue(rp.getRowCellData(dp.drugFormularyTable, 0)[2].toLowerCase().contains(srhHCPC.toLowerCase()));
                } else {
                    ExtentReportListener.test.get().log(Status.FAIL, "This search returned more than 1 row and not expected to return more than row. Pl check.");
                    AllureReportListener.saveLogs("This search returned more than 1 row and not expected to return more than row. Pl check. ");
                }
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

                rp.srhRegDrugDiagAndEnter(srhDrug);

                //Sort by Name
                List<String> beforeSortList = rp.tableColumnList(dp.drugFormularyTable, 0);
                List<String> sortList = eu.sortItemsList(beforeSortList, "desc");
                rp.clickTableHeaderForSort("Description");
                eu.syncWait(2);
                List<String> afterSortList = rp.tableColumnList(dp.drugFormularyTable, 0);
                //System.out.println("Before: " + beforeSortList +"\r\n" + "Sort: " + sortList +"\r\n"+ "After: " + afterSortList);
                sa.assertEquals(sortList, afterSortList);
                //sa.assertNotEquals(beforeSortList, afterSortList);
                rp.clickTableHeaderForSort("Description");
                eu.syncWait(2);

                //Sort by Insurer Responsibility
                List<Integer> beforeSortNumsList = rp.tableNumColumnList(dp.drugFormularyTable, 6);
                List<Integer> sortNumsList = eu.sortItemsNums(beforeSortNumsList, "desc");
                rp.clickTableHeaderForSort("Patient Allowable");
                eu.doClick(rp.srhRegimen);
                eu.syncWait(2);
                List<Integer> afterSortNumsList = rp.tableNumColumnList(dp.drugFormularyTable, 6);
                //System.out.println("Before: " + beforeSortNumsList + "\r\n" + "Sort: " + sortNumsList + "\r\n" + "After: " + afterSortNumsList);
                sa.assertEquals(sortNumsList, afterSortNumsList);
                rp.clickTableHeaderForSort("Patient Allowable");
                eu.syncWait(2);

            } else {
                sa.fail();
                ExtentReportListener.test.get().log(Status.INFO, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            ExtentReportListener.test.get().log(Status.FAIL, "Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 2, groups = "smoke", description = "Data Management - Base Formulary - Drug Formulary - Validation of filtering by Therapeutic Class and Classification filter Dropdowns" +
            "Validaiton of Export functionaity. Validation of Drug Table and sub table - if the drug details table's cost per billing unit column's " +
            " lowest value equals to the cost per billing unit in the drug formulary main table. Validate when you change the drug details table from lowest " +
            " to custom,  if the main table is showing the C instead of L")
    @Severity(SeverityLevel.NORMAL)
    @Description("Data Management - Base Formulary - Drug Formulary - Validation of filtering by Therapeutic Class and Classification filter Dropdowns" +
            "Validaiton of Export functionaity. Validation of Drug Table and sub table - if the drug details table's cost per billing unit column's " +
            " lowest value equals to the cost per billing unit in the drug formulary main table. Validate when you change the drug details table from lowest " +
            " to custom,  if the main table is showing the C instead of L")
    public void baseFormularyDrugTableFilters() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickLeftMenuItem("Data Management");
            rp.clickByLinkText("Base Formulary");
            rp.clickTabs("Drugs");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary");
            sa.assertEquals(rp.getGridHeading(), "Drug Formulary");

            //rp.clickByLinkText("Base Formulary");
            rp.clickButton("Export");
            dp.dataMngClickSubListItem("Export as Excel (.xlsx)"); //Drugs Main Page
            TimeUnit.SECONDS.sleep(2);

            int beforeFilter = rp.getNumOfGridResults();

            if (!rp.getTextContent(dp.drugFormularyTable).contains("search did not match any results")) {
                rp.clickGridFilters("select-therapeutic-classes", "Therapeutic Class");
                rp.selectFilterItemByIndex(1);
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Therapeutic Class filter criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the selected Therapeutic Class filter criteria: " + rp.getNumOfGridResults());
                int afterTherapeuticFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterTherapeuticFilter);
            } else {
                ExtentReportListener.test.get().log(Status.INFO, "Break Even filter search did not return any results");
                AllureReportListener.saveLogs("Break Even filter search did not return any results");
            }
            eu.clickWhenReady(rp.getFilterSelectClear("select-therapeutic-classes"), 5);

            if (!rp.getTextContent(dp.drugFormularyTable).contains("search did not match any results")) {
                rp.clickGridFilters("filter-classification", "Classification");
                rp.selectListItemByName("Classified Drugs");
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Classification filter criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the selected Classification filter criteria: " + rp.getNumOfGridResults());
                int afterClassificationFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterClassificationFilter);
            } else {
                ExtentReportListener.test.get().log(Status.INFO, "Break Even filter search did not return any results");
                AllureReportListener.saveLogs("Break Even filter search did not return any results");
            }
            eu.clickWhenReady(rp.getFilterSelectClear("filter-classification"), 5);


            //Filter the BF - Drug table for a drug and validate if the drug details table's cost per billing unit column's
            // lowest value equals to the cost per billing unit in the drug formulary main table
            rp.srhRegDrugDiagAndEnter(drugDetailsSrhDrug);
            String drugTableDesc = rp.getRowCellData(dp.drugFormularyTable, 0)[0];
            String drugTableCost = rp.getRowCellData(dp.drugFormularyTable, 0)[5];
            //System.out.println("Drug Table Cost: " + drugTableCost);
            rp.clickGridCell("drug-formulary-table", 1, 1);
            eu.waitForElementPresent(dp.drugDetailsTable, 5);
            String drugDetailsTableDesc = rp.getRowCellData(dp.drugDetailsTable, 0)[0];
            sa.assertEquals(drugTableDesc, drugDetailsTableDesc);

            if (dp.bfDrugDtsTableGridRows("lowest") && !(drugTableCost.contains("c"))) {
                //System.out.println("Details Cost"+ dp.bfGetRowColumnData(dp.drugDetailsTable, dp.bfDrugDtsTableGridRow("lowest") - 1, 7));
                //sa.assertEquals(dp.bfGetRowColumnData(dp.drugDetailsTable, dp.bfDrugDtsTableGridRow("lowest") - 1, 7), drugTableCost);
                sa.assertTrue(drugTableCost.contains(dp.bfGetRowColumnData(dp.drugDetailsTable, dp.bfDrugDtsTableGridRow("lowest") - 1, 7)));
            }

            String price = String.valueOf(new Random().nextInt(999));
            String billingUnit = String.valueOf(new Random().nextInt(500));

            eu.scrollToView(rp.getInputField("billingUnitsPerPackage"));
            rp.sendKeysByAction("pricePerPackage", price);
            rp.sendValue("billingUnitsPerPackage", billingUnit);
            eu.doClick(rp.getInputField("pricePerPackage"));
            eu.syncWait(2);
            eu.doClick(dp.btnDrugDtsCustomRadio);
            eu.syncWait(2);
            String drugTableCustomCost = rp.getRowCellData(dp.drugFormularyTable, 0)[5];
            //System.out.println(drugTableCustomCost);
            sa.assertNotEquals(drugTableCost, drugTableCustomCost);
            sa.assertTrue(drugTableCustomCost.toLowerCase().contains("c"));

            rp.clickGridCell("drug-details-table", dp.bfDrugDtsTableGridRow("lowest"), 1);
            eu.syncWait(2);
            //System.out.println("Drug Cost:" + rp.getRowCellData(dp.drugFormularyTable, 0)[5]);
            sa.assertFalse(rp.getRowCellData(dp.drugFormularyTable, 0)[5].toLowerCase().contains("c"));


             rp.srhRegDrugDiagAndEnter(calculationDrug);
                String beforeFilterDrugCost = rp.getGridRowData(dp.drugFormularyTable, 1);
                eu.clickWhenReady(dp.calculationMethodDropdown, 3);
                if (rp.getSelectedFilterItem("select-calculation-method").equals("Lowest Available Cost")) {
                    rp.selectListItemByName("Invoiced Cost (Where Available)");
                } else if (rp.getSelectedFilterItem("select-calculation-method").equals("Invoiced Cost (Where Available)")) {
                    rp.selectListItemByName("Lowest Available Cost");
                }
                if (!rp.getTextContent(dp.drugFormularyTable).contains("search did not match any results")) {
                    ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Choose Drug Cost Calculation filter criteria: " + rp.getNumOfGridResults());
                    AllureReportListener.saveLogs("Number of records that match the selected Choose Drug Cost Calculation filter criteria: " + rp.getNumOfGridResults());
                    String afterFilterDrugCost = rp.getGridRowData(dp.drugFormularyTable, 1);
                    rp.getGridRowData(dp.drugFormularyTable, 1);
                    System.out.println("Before " + beforeFilterDrugCost + "After " + afterFilterDrugCost);
                    sa.assertNotEquals(beforeFilterDrugCost, afterFilterDrugCost);
                } else {
                    ExtentReportListener.test.get().log(Status.INFO, "Drug Cost Calculation filter search did not return any results");
                    AllureReportListener.saveLogs("Drug Cost Calculation filter search did not return any results");
                }
                rp.srhRegDrugDiagAndEnter("");


        } catch (NoSuchElementException | InterruptedException e) {
            ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed");
            AllureReportListener.saveLogs("Test Method Failed");
        }
        sa.assertAll();
    }

    @Test(priority = 5, groups = "smoke", description = "Data Management - Base Formulary Page - Non-Drug Formulary - Validation of search by Drug in Drug Table " +
            "And Therapeutic Class Filter")
    @Severity(SeverityLevel.NORMAL)
    @Description("Data Management - Base Formulary Page - Non-Drug Formulary - Validation of search by Drug in Drug Table And Therapeutic Class Filter")
    public void baseFormularyNonDrugsSearchAndTherapeuticFilter() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickTabs("Non-Drugs");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary");
            sa.assertEquals(rp.getGridHeading(), "Non-Drug Formulary");

            int beforeFilter = rp.getNumOfGridResults();
            rp.srhRegDrugDiagAndEnter(srhNonDrug);
            eu.syncWait(3);
            if (rp.getNumOfGridResults() > 0) {
                int rowCount = eu.getGridRowCount(dp.nonDrugFormularyTable);
                //System.out.println("Cell Data: " + rp.getGridRowData(dp.nonDrugFormularyTable, 1));
                //System.out.println("Non Drug Name: " + rp.getRowCellData(dp.nonDrugFormularyTable, 1)[0]);
                for (int i = 0; i < rowCount; i++) {
                    sa.assertTrue(rp.getRowCellData(dp.nonDrugFormularyTable, i)[0].toLowerCase().contains(srhNonDrug));
                }
                ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the given search criteria: " + rp.getNumOfGridResults());
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());


                rp.clickTabs("Non-Drugs");
                rp.clickGridFilters("select-therapeutic-classes", "Therapeutic Class");
                rp.selectFilterItemByIndex(1);
                if (!rp.getTextContent(dp.nonDrugFormularyTable).contains("search did not match any results")) {
                    ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Therapeutic Class filter criteria: " + rp.getNumOfGridResults());
                    AllureReportListener.saveLogs("Number of records that match the selected Therapeutic Class filter criteria: " + rp.getNumOfGridResults());
                    int afterTherapeuticFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterTherapeuticFilter);
                } else {
                    ExtentReportListener.test.get().log(Status.INFO, "Break Even filter search did not return any results");
                    AllureReportListener.saveLogs("Break Even filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("select-therapeutic-classes"), 5);

                //Need to add validation specific to calculation method selection
                eu.clickWhenReady(dp.calculationMethodDropdown, 5);
                rp.selectListItemByName("Practice CPT Costs");
                eu.syncWait(3);
                if (!rp.getTextContent(dp.nonDrugFormularyTable).contains("search did not match any results")) {
                    ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Choose Non-Drug Cost Calculation filter criteria: " + rp.getNumOfGridResults());
                    AllureReportListener.saveLogs("Number of records that match the selected Choose Non-Drug Cost Calculation filter criteria: " + rp.getNumOfGridResults());
                    int afterCostFilter = rp.getNumOfGridResults();
                    //sa.assertNotEquals(beforeFilter, afterCostFilter);
                } else {
                    ExtentReportListener.test.get().log(Status.INFO, "Break Even filter search did not return any results");
                    AllureReportListener.saveLogs("Break Even filter search did not return any results");
                }


            } else {
                sa.fail();
                ExtentReportListener.test.get().log(Status.FAIL, "No Records exists in the table");
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            ExtentReportListener.test.get().log(Status.FAIL, "Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();

    }

    //@Test(priority = 2, groups = "smoke", description = "Data Management - Base Formulary - Drug Formulary - Validation of Therapeutic Class and Classification filter Dropdowns")
    @Severity(SeverityLevel.NORMAL)
    @Description("Data Management - Base Formulary - Drug Formulary - Validation of Therapeutic Class and Classification filter Dropdowns")
    public void baseFormularyNonDrugTableFilters() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            rp.clickLeftMenuItem("Data Management");
            rp.clickByLinkText("Base Formulary");
            rp.clickTabs("Non-Drugs");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary");
            sa.assertEquals(rp.getGridHeading(), "Non-Drug Formulary");

            int beforeFilter = rp.getNumOfGridResults();

            rp.clickGridFilters("select-therapeutic-classes", "Therapeutic Class");
            rp.selectFilterItemByIndex(1);
            //ExtentReportListener.test.get().log(Status.INFO, "Number of records that match the selected Therapeutic Class filter criteria: " + rp.getNumOfGridResults());
            AllureReportListener.saveLogs("Number of records that match the selected Therapeutic Class filter criteria: " + rp.getNumOfGridResults());
            Thread.sleep(3000);
            int afterBreakEvenFilter = rp.getNumOfGridResults();
            sa.assertNotEquals(beforeFilter, afterBreakEvenFilter);
            eu.clickWhenReady(rp.getFilterSelectClear("select-therapeutic-classes"), 5);


        } catch (NoSuchElementException | InterruptedException e) {
            //ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed");
            AllureReportListener.saveLogs("Test Method Failed");
        }
        sa.assertAll();
    }

    //li[@role='treeitem']//p


}
