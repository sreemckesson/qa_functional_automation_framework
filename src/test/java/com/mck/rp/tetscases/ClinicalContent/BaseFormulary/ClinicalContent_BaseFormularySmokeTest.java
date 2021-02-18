package com.mck.rp.tetscases.ClinicalContent.BaseFormulary;

import com.mck.rp.base.BaseTest;
import com.mck.rp.listeners.allure.AllureReportListener;
import com.mck.rp.pageObjects.ClinicalContentPage;
import com.mck.rp.pageObjects.DataManagementPage;
import com.mck.rp.pageObjects.LoginPage;
import com.mck.rp.pageObjects.RegimenAnalysisPage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(AllureReportListener.class)
public class ClinicalContent_BaseFormularySmokeTest extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    ClinicalContentPage cp;
    LoginPage lp;
    String srhDrug = "liposomal"; //"sulfate";
    String srhHCPC = "j1454";
    String srhNonDrug = "drug";
    String calculationDrug = "bleomycin";
    String drugDetailsSrhDrug = "Granisetron injection";
    String editDrug = "J1568";
    String editNonDrug = "52000";

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void baseFormularySetUp() {
        rp = new RegimenAnalysisPage(driver);
        dp = new DataManagementPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);
        cp = new ClinicalContentPage(driver);

        try {
            loginPage.doLogin(prop.getProperty("mckessonUser"), prop.getProperty("mckessonPassword"));
            // eu.syncWait(2);
            //check current selected location and navigate to All Practices
            String selectedLocation = eu.getElement(cp.selectedLocation).getText();
            if (!selectedLocation.equals("All practices")) {
                eu.doClick(cp.selectedLocation);
                eu.doClick(cp.allPractices);
                //   eu.syncWait(2);
                selectedLocation = eu.getElement(cp.selectedLocation).getText();
                System.out.println("selectedLocation: " + selectedLocation);
                //   eu.syncWait(2);
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
    }

    @Test(priority = 1,
          groups = "smoke",
          description = "Clinical Content - Base Formulary Page - Drug Formulary - Validation of list view "
              + "functionality - Default filter, Default records per page, columns displayed, drug expanded view")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Base Formulary Page - Drug Formulary - Validation of list view functionality - Default filter, Default records"
                     + " per page, columns displayed, drug expanded view")
    public void baseFormularyDrugsListView() {

        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Drug Formulary", "Incorrect results grid heading");
            if (rp.getNumOfGridResults() > 0) {
                //default number of rows per page
                String beforePagination = eu.getElement(rp.gridResults).getText();
                sa.assertTrue(beforePagination.contains("1-20"), "Incorrect default number of rows per page");

                //default row count in drug table
                sa.assertTrue(eu.getGridRowCount(dp.drugFormularyTable) == 20, "Incorrect records per page");

                //default filter applied
                sa.assertTrue(rp.getGridFilters("filter-status", "Active").isDisplayed(), "Incorrect default filter status");
                sa.assertTrue(rp.getGridFilters("filter-classification", "Classification").isDisplayed(), "Incorrect default filter classification");
                sa.assertTrue(rp.getGridFilters("select-therapeutic-classes", "Therapeutic Class").isDisplayed(),
                    "Incorrect default filter therapeutic class");

                //table columns
                sa.assertTrue(cp.getColumnHeader("table-sort-by_description").getAttribute("textContent").equals("Description"),
                    "Incorrect column desc");
                sa.assertTrue(cp.getColumnHeader("table-sort-by_therapeutic-class").getAttribute("textContent").equals("Therapeutic Class"),
                    "Incorrect column therapeutic class");
                sa.assertTrue(cp.getColumnHeader("table-sort-by_hcpc").getAttribute("textContent").equals("HCPC"), "Incorrect column hcpc");
                sa.assertTrue(cp.getColumnHeader("table-sort-by_patient-allowable").getAttribute("textContent").equals("Allowable"),
                    "Incorrect column PA");
                sa.assertTrue(cp.getColumnHeader("table-sort-by_status").getAttribute("textContent").equals("Status"), "Incorrect column status");

                //default sort applied to description field

                //expand drug view
                String drugTableDesc = rp.getRowCellData(dp.drugFormularyTable, 0)[0];
                rp.clickGridCell("drug-formulary-table", 1, 1);
                eu.waitForElementPresent(dp.drugDetailsTable, 2);
                eu.syncWait(2);
                String drugDetailsTableDesc = rp.getRowCellData(dp.drugDetailsTable, 0)[0];
                sa.assertEquals(drugTableDesc, drugDetailsTableDesc, "Incorrect Desc for select record");
                sa.assertTrue(eu.doIsDisplayed(cp.editDrug), "Missing edit drug button");
                rp.clickGridCell("drug-formulary-table", 1, 1);

                //Select drug to delete
                eu.syncWait(2);
                String drugToDelete = cp.getRowCellDataAndSelectRow(dp.drugFormularyTable, 1)[0].toLowerCase();
                AllureReportListener.saveLogs("Selected drug to delete: " + drugToDelete);
                //   eu.syncWait(5);
                eu.waitForElementToBeVisible(cp.deleteButton,5);
                sa.assertTrue(eu.isElementPresent(cp.deleteButton), "Missing delete button");
                sa.assertEquals(eu.getText(cp.selectedItems), "1 Selected", "Missing number of selected items for delete");
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 2,
          groups = "smoke",
          description = "Clinical Content - Base Formulary Page - Drug Formulary - Validation of search "
              + "functionality by Drug and HCPC in Drug Formulary. Validation of HCPC search return a unique record or not . "
              + "Validation of Pagination. Validation of Sort functionality by a string and a numeric sort")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Base Formulary Page - Drug Formulary - Validation of search functionality by Drug and HCPC in Drug "
                     + "Formulary. Validation of HCPC search return a unique record or not. Validation of Pagination. Validation of Sort "
                     + "functionality by a string and a numeric sort")
    public void baseFormularyDrugsTableSearch() {

        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist(), "Missing logout button");
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary", "Incorrect Page Heading");
            sa.assertEquals(rp.getGridHeading(), "Drug Formulary", "Incorrect grid heading");

            if (rp.getNumOfGridResults() > 0) {
                //Pagination
                String beforePagination = eu.getElement(rp.gridResults).getText();
                rp.selectPaginationRows("10");
                // eu.syncWait(2);
                String afterPagination = eu.waitForElementToBeVisible(rp.gridResults, 2).getText();
                sa.assertNotEquals(beforePagination, afterPagination, "Incorrect grid result count after changing page");

                rp.srhRegDrugDiagAndEnter(srhDrug);
                // eu.syncWait(1);
                int rowCount = eu.getGridRowCount(dp.drugFormularyTable);
                for (int i = 0; i < rowCount; i++) {
                    System.out.println("act: " + rp.getRowCellData(dp.drugFormularyTable, i)[0].toLowerCase());
                    System.out.println("exp: " + srhDrug.toLowerCase());
                    System.out.println("2nd : " + rp.getRowCellData(dp.drugFormularyTable, i)[0].toLowerCase().contains(srhDrug.toLowerCase()));
                    sa.assertTrue(rp.getRowCellData(dp.drugFormularyTable, i)[0].toLowerCase().contains(srhDrug.toLowerCase()),
                        "Incorrect search drug desc");
                }
                AllureReportListener.saveLogs(
                    "Number of records that match the given search criteria for drug name " + srhDrug + " : " + rp.getNumOfGridResults());

                rp.srhRegDrugDiagAndEnter(srhHCPC);
                int rowCountHCPC = eu.getGridRowCount(dp.drugFormularyTable);
                if (eu.getGridRowCount(dp.drugFormularyTable) == 1) {
                    System.out.println("3rd : " + rp.getRowCellData(dp.drugFormularyTable, 0)[2].toLowerCase().contains(srhHCPC.toLowerCase()));
                    sa.assertTrue(rp.getRowCellData(dp.drugFormularyTable, 0)[2].toLowerCase().contains(srhHCPC.toLowerCase()),
                        "Incorrect search HCPC");
                } else {
                    AllureReportListener.saveLogs("Search returned more than 1 row and not expected to return more than row. Pl check. ");
                }
                AllureReportListener.saveLogs(
                    "Number of records that match the given search criteria for drug hcpc " + srhHCPC + " : " + rp.getNumOfGridResults());

                rp.srhRegDrugDiagAndEnter(srhDrug);
                //Sort by drug description
                List<String> beforeSortList = rp.tableColumnList(dp.drugFormularyTable, 0);
                List<String> sortList = eu.sortItemsList(beforeSortList, "desc");
                rp.clickTableHeaderForSort("Description");
                eu.syncWait(2);
                List<String> afterSortList = rp.tableColumnList(dp.drugFormularyTable, 0);
                System.out.println("DescSort: Before: " + beforeSortList + "\r\n" + "Sort: " + sortList + "\r\n" + "After: " + afterSortList);
                sa.assertEquals(sortList, afterSortList, "Incorrect sort by drug desc result");

                //sa.assertNotEquals(beforeSortList, afterSortList);
                rp.clickTableHeaderForSort("Description");
                eu.syncWait(2);

                //Sort by patient allowable
                List<Integer> beforeSortNumsList = rp.tableNumColumnList(dp.drugFormularyTable, 3);
                List<Integer> sortNumsList = eu.sortItemsNums(beforeSortNumsList, "desc");
                rp.clickTableHeaderForSort("Allowable");
                eu.doClick(rp.srhRegimen);
                eu.syncWait(2);
                List<Integer> afterSortNumsList = rp.tableNumColumnList(dp.drugFormularyTable, 3);
                System.out.println(
                    "PA sort: Before: " + beforeSortNumsList + "\r\n" + "Sort: " + sortNumsList + "\r\n" + "After: " + afterSortNumsList);
                sa.assertEquals(sortNumsList, afterSortNumsList, "Incorrect sort by PA result");
                rp.clickTableHeaderForSort("Patient Allowable");
                //eu.syncWait(2);
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 3,
          groups = "smoke",
          description = "Clinical Content - Base Formulary - Drug Formulary - Validation of filtering by "
              + "Therapeutic Class, Classification and Status filter Dropdowns. Validation of Export functionality.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Base Formulary - Drug Formulary - Validation of filtering by Therapeutic Class, "
                     + "Classification and Status filter Dropdowns. Validation of Export functionality.")
    public void baseFormularyDrugTableFilters() {

        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary");
            sa.assertEquals(rp.getGridHeading(), "Drug Formulary");

            //rp.clickByLinkText("Base Formulary");
            rp.clickButton("Export");
            dp.dataMngClickSubListItem("Export as Excel (.xlsx)"); //Drugs Main Page
            TimeUnit.SECONDS.sleep(2);

            int beforeFilter = rp.getNumOfGridResults();
            AllureReportListener.saveLogs("Number of records before applying filter 'Therapeutic Class' : " + rp.getNumOfGridResults());
            if (!rp.getTextContent(dp.drugFormularyTable).contains("search did not match any results")) {
                rp.clickGridFilters("select-therapeutic-classes", "Therapeutic Class");
                rp.selectFilterItemByIndex(1);
                AllureReportListener.saveLogs(
                    "Number of records that match the selected Therapeutic Class filter criteria (1st value in " + "filter: "
                        + rp.getNumOfGridResults());
                int afterTherapeuticFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterTherapeuticFilter, "Incorrect Therapeutic Class filter count");
            } else {
                AllureReportListener.saveLogs("Search did not return any results");
            }
            eu.clickWhenReady(rp.getFilterSelectClear("select-therapeutic-classes"), 5);

            if (!rp.getTextContent(dp.drugFormularyTable).contains("search did not match any results")) {
                rp.clickGridFilters("filter-classification", "Classification");
                rp.selectListItemByName("Classified Drugs");
                AllureReportListener.saveLogs(
                    "Number of records that match the selected Classification filter criteria (Classified Drugs): " + rp.getNumOfGridResults());
                int afterClassificationFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterClassificationFilter, "Incorrect Classification filter count");
            } else {
                AllureReportListener.saveLogs("Search did not return any results");
            }
            eu.clickWhenReady(rp.getFilterSelectClear("filter-classification"), 5);

            if (!rp.getTextContent(dp.drugFormularyTable).contains("search did not match any results")) {
                rp.clickGridFilters("filter-status", "Active");
                rp.selectListItemByName("Inactive");
                eu.syncWait(1);
                AllureReportListener.saveLogs(
                    "Number of records that match the selected Status filter criteria (InActive): " + rp.getNumOfGridResults());
                int afterClassificationFilter = rp.getNumOfGridResults();
                sa.assertNotEquals(beforeFilter, afterClassificationFilter, "Incorrect status filter count");
            } else {
                AllureReportListener.saveLogs("Search did not return any results");
            }
            eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 5);
            rp.clickLeftMenuItem("Clinical Content");

        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed");
        }
        sa.assertAll();
    }

    @Test(priority = 4,
          groups = "smoke",
          description = "Clinical Content - Base Formulary Page - Non-Drug Formulary - "
              + "Validation of search by Non-Drug in Drug Table,  Therapeutic Class and Status Filter")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Base Formulary Page - Non-Drug Formulary - Validation of search by Non-Drug in Drug Table "
                     + ", Therapeutic Class and Status Filter")
    public void baseFormularyNonDrugsSearchAndFilter() {

        SoftAssert sa = new SoftAssert();
        try {

            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            rp.clickTabs("Non-Drugs");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Non-Drug Formulary", "Incorrect grid heading");

            //rp.clickByLinkText("Base Formulary");
            rp.clickButton("Export");
            dp.dataMngClickSubListItem("Export as Excel (.xlsx)");
            TimeUnit.SECONDS.sleep(2);

            int beforeFilter = rp.getNumOfGridResults();
            AllureReportListener.saveLogs("Number of records before applying filter or search : " + rp.getNumOfGridResults());
            rp.srhRegDrugDiagAndEnter(srhNonDrug);
            // eu.syncWait(3);
            if (rp.getNumOfGridResults() > 0) {
                int rowCount = eu.getGridRowCount(dp.nonDrugFormularyTable);
                for (int i = 0; i < rowCount; i++) {
                    sa.assertTrue(rp.getRowCellData(dp.nonDrugFormularyTable, i)[0].toLowerCase().contains(srhNonDrug),
                        "Incorrect search " + "non-drug results");
                }
                AllureReportListener.saveLogs("Number of records that match the given search criteria: " + rp.getNumOfGridResults());

                rp.clickTabs("Non-Drugs");
                rp.clickGridFilters("select-therapeutic-classes", "Therapeutic Class");
                rp.selectFilterItemByIndex(1);
                if (!rp.getTextContent(dp.nonDrugFormularyTable).contains("search did not match any results")) {
                    AllureReportListener.saveLogs(
                        "Number of records that match the selected Therapeutic Class filter criteria: " + rp.getNumOfGridResults());
                    int afterTherapeuticFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterTherapeuticFilter, "Incorrect Therapeutic class filter count");
                } else {
                    AllureReportListener.saveLogs("Therapeutic Class filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("select-therapeutic-classes"), 5);

                rp.clickGridFilters("filter-status", "Active");
                rp.selectListItemByName("Inactive");
                eu.syncWait(1);
                if (!rp.getTextContent(dp.nonDrugFormularyTable).contains("search did not match any results")) {
                    AllureReportListener.saveLogs("Number of records that match the selected Status filter criteria: " + rp.getNumOfGridResults());
                    int afterTherapeuticFilter = rp.getNumOfGridResults();
                    sa.assertNotEquals(beforeFilter, afterTherapeuticFilter, "Incorrect status filter count");
                } else {
                    AllureReportListener.saveLogs("Status Active filter search did not return any results");
                }
                eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 5);

                //Select drug to delete
                eu.syncWait(2);
                String drugToDelete = cp.getRowCellDataAndSelectRow(dp.nonDrugFormularyTable, 1)[0].toLowerCase();
                AllureReportListener.saveLogs("Selected non-drug to delete: " + drugToDelete);
                //  eu.syncWait(5);
                sa.assertTrue(eu.isElementPresent(cp.deleteButton), "Missing delete button");
                sa.assertEquals(eu.getText(cp.selectedItems), "1 Selected", "Incorrect selected Items for delete non-drug");
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Records exists in the table");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            System.out.println(e.getMessage());
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 5, groups = "smoke", description = "Clinical Content - Base Formulary Page - Drug Formulary - Add NocDrug")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Clinical Content - Base Formulary Page - Drug Formulary - Add NocDrug")
    public void baseFormularyAddNocDrug() {

        SoftAssert sa = new SoftAssert();
        Random rand = new Random();
        String drugDesc = "TestNocDrugAuto-" + rand.nextInt(9999);
        String ndc = "11111-" + rand.nextInt(9999) + "11";

        try {
            Assert.assertTrue(rp.isLogoutExist(), "Missing logout");
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Drug Formulary", "Incorrect grid heading");

            eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 2);
            int beforeNumOfGridResults = rp.getNumOfGridResults();
            rp.clickButton("Create New");
            rp.clickDialogBtn("Drug");
            rp.sendValue("hcpc", "J9999");
            //   eu.syncWait(1);
            rp.sendValue("description", drugDesc);
            //   eu.syncWait(1);
            rp.sendValue("ndc", ndc);
            rp.sendValue("billingUnit", "1");
            rp.sendValue("medicarePatientAllowable", "1");
            eu.doClick(rp.getInputField("sdvSwitch"));
            rp.sendValue("sdvSize", "1");
            cp.clickFilter("therapeuticClass");
            cp.selectFilterItemByName("Chemo");
            //   eu.syncWait(1);
            cp.clickFilter("drugUnitOfMeasure");
            cp.selectFilterItemByName("mg");
            //   eu.syncWait(1);
            rp.clickDialogBtn("Save");
            eu.clickWhenReady(cp.getConfirmDialogBtn("Close"), 4);
            eu.syncWait(2);

            //Verify NOC drug is added
            System.out.println("1");
            if (eu.getGridRowCount(dp.drugFormularyTable) == 1) {
                System.out.println("2");
                System.out.println("rp.getRowCellData(dp.drugFormularyTable, 0)[0].toLowerCase().contains(drugDesc.toLowerCase()");
                sa.assertTrue(rp.getRowCellData(dp.drugFormularyTable, 0)[0].toLowerCase().contains(drugDesc.toLowerCase()),
                    "Incorrect new non drug desc");
                AllureReportListener.saveLogs("NOC drug added: " + ndc);
            } else {
                sa.fail();
                AllureReportListener.saveLogs("This search returned more than 1 row and not expected to return more than row. Pl check. ");
            }
            System.out.println("3");
            rp.clickTabs("Drugs");
            System.out.println("4");
            eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 2);
            System.out.println("5");
            //    eu.syncWait(2);
            int afterNumOfGridResults = rp.getNumOfGridResults();
            System.out.println("6");
            System.out.println("Grid results before noc drug is added: " + beforeNumOfGridResults + "after noc drug added: " + afterNumOfGridResults);
            sa.assertTrue(afterNumOfGridResults > beforeNumOfGridResults, "Added noc drug is not returned in the grid table");
            AllureReportListener.saveLogs(
                "Grid results before noc drug is added: " + beforeNumOfGridResults + "after noc drug added: " + afterNumOfGridResults);

            //Cancel Save
            /*rp.clickDialogBtn("Cancel");
             eu.doClick(cp.getConfirmDialogBtn("Yes"));
             rp.srhRegDrugDiagAndEnter(drugDesc);
             int rowCountHCPC = eu.getGridRowCount(dp.drugFormularyTable);
            if(rowCountHCPC == 0){
                sa.assertTrue(true);
            }else{
                sa.fail();
                AllureReportListener.saveLogs("This search returned >0 row/s and not expected to return any rows. Noc Drug got created. ");
            }
            */

        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 6, groups = "smoke", description = "Clinical Content - Base Formulary Page - Drug Formulary - Add Drug and cancel")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Base Formulary Page - Drug Formulary - Add Drug and cancel")
    public void baseFormularyAddDrug() {

        SoftAssert sa = new SoftAssert();
        Random rand = new Random();
        String drugDesc = "-TestDrugAuto-" + rand.nextInt(1000);
        String addHCPC = "J1940";

        try {
            Assert.assertTrue(rp.isLogoutExist(), "Missing logout");
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary", "Invalid page heading");
            sa.assertEquals(rp.getGridHeading(), "Drug Formulary", "Invalid grid heading");

            eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 3);
            int beforeNumOfGridResults = rp.getNumOfGridResults();
            rp.clickButton("Create New");
            rp.clickDialogBtn("Drug");
            rp.sendValue("hcpc", addHCPC);
            //   eu.syncWait(1);
            rp.sendValue("description", drugDesc);
            //    eu.syncWait(1);
            cp.clickFilter("therapeuticClass");
            cp.selectFilterItemByName("Chemo");
            //    eu.syncWait(1);

            //Cancel Save
            rp.clickDialogBtn("Cancel");
            eu.doClick(cp.getConfirmDialogBtn("Yes"));
            rp.srhRegDrugDiagAndEnter(drugDesc);
            //   eu.syncWait(1);
            int rowCountHCPC = eu.getGridRowCount(dp.drugFormularyTable);
            sa.assertTrue(rowCountHCPC == 0, "This search returned >0 row/s and not expected to return any rows. Drug got created.");

            rp.clickTabs("Drugs");
            eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 2);
            //    eu.syncWait(2);
            int afterNumOfGridResults = rp.getNumOfGridResults();
            sa.assertTrue(afterNumOfGridResults == beforeNumOfGridResults, "Added drug is saved in the grid table instead of cancelling.");
            AllureReportListener.saveLogs("Drug added and cancelled: " + addHCPC + " " + drugDesc);

            //Verify drug is added as part of regression once we have db connection added.
            /*
            rp.clickDialogBtn("Save");
            eu.clickWhenReady(cp.getConfirmDialogBtn("Close"), 4);
            eu.syncWait(2);

            if (eu.getGridRowCount(dp.drugFormularyTable) == 1) {
                sa.assertTrue(rp.getRowCellData(dp.drugFormularyTable, 0)[0].toLowerCase().contains(drugDesc.toLowerCase()));
            } else {
                sa.fail();
                AllureReportListener.saveLogs("This search returned more than 1 row and not expected to return more than row. Pl check. ");
            }

            rp.clickTabs("Drugs");
            eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 2);
            eu.syncWait(2);
            int afterNumOfGridResults = rp.getNumOfGridResults();
            sa.assertTrue(afterNumOfGridResults > beforeNumOfGridResults, "Added noc drug is not returned in the grid table");
             */

        } catch (NoSuchElementException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 7, groups = "smoke", description = "Clinical Content - Base Formulary Page - Drug Formulary - Add Non-Drug and cancel")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Base Formulary Page - Drug Formulary - Add Non-Drug and cancel")
    public void baseFormularyAddNonDrug() {

        SoftAssert sa = new SoftAssert();
        Random rand = new Random();
        String drugDesc = "-TestNonDrugAuto-" + rand.nextInt(1000);
        String addHCPC = "85270";

        try {
            Assert.assertTrue(rp.isLogoutExist(), "Missing logout");
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            rp.clickTabs("Non-Drugs");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Non-Drug Formulary", "Incorrect grid heading");

            eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 2);
            int beforeNumOfGridResults = rp.getNumOfGridResults();
            rp.clickButton("Create New");
            rp.clickDialogBtn("Non-Drug");
            rp.sendValue("hcpc", addHCPC);
            //  eu.syncWait(1);
            rp.sendValue("description", drugDesc);
            //  eu.syncWait(1);
            cp.clickFilter("therapeuticClass");
            cp.selectFilterItemByName("Lab");
            //   eu.syncWait(1);

            //Cancel Save
            rp.clickDialogBtn("Cancel");
            eu.doClick(cp.getConfirmDialogBtn("Yes"));
            rp.srhRegDrugDiagAndEnter(drugDesc);
            int rowCountHCPC = eu.getGridRowCount(dp.drugFormularyTable);
            sa.assertTrue(rowCountHCPC == 0, "This search returned >0 row/s and not expected to return any rows. Non-Drug got created.");

            rp.clickTabs("Non-Drugs");
            eu.clickWhenReady(rp.getFilterSelectClear("filter-status"), 2);
            //   eu.syncWait(2);
            int afterNumOfGridResults = rp.getNumOfGridResults();
            sa.assertTrue(afterNumOfGridResults == beforeNumOfGridResults, "Added non-drug is saved in the grid table instead of cancelling.");
            AllureReportListener.saveLogs("Non-Drug added and cancelled: " + addHCPC + " " + drugDesc);

            //Verify non-drug is added as part of regression once we have db connection added.

        } catch (NoSuchElementException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 8, groups = "smoke", description = "Clinical Content - Base Formulary Page - Drug Formulary - Edit Drug")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Base Formulary Page - Drug Formulary - Edit drug")
    public void baseFormularyEditDrug() {

        SoftAssert sa = new SoftAssert();
        String updatedDrugDesc = "UpdatedDrugAuto";

        try {
            Assert.assertTrue(rp.isLogoutExist(), "Missing logout");
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Drug Formulary", "Incorrect grid heading");

            if (rp.getNumOfGridResults() > 0) {

                rp.srhRegDrugDiagAndEnter(editDrug);
                int rowCountHCPC = eu.getGridRowCount(dp.drugFormularyTable);
                if (eu.getGridRowCount(dp.drugFormularyTable) == 1) {

                    String beforeEditDrugTableDesc = rp.getRowCellData(dp.drugFormularyTable, 0)[0];
                    String beforeEditDrugTableTherapeuticClass = rp.getRowCellData(dp.drugFormularyTable, 0)[1];
                    rp.clickGridCell("drug-formulary-table", 1, 1);
                    eu.waitForElementPresent(dp.drugDetailsTable, 2);
                    eu.waitForElementToBeVisible(cp.editDrug, 2);
                    if (eu.doIsDisplayed(cp.editDrug)) {
                        eu.scrollToView(cp.editDrug);
                        // eu.syncWait(1);
                        eu.doClick(cp.editDrug);

                        eu.getTextcontent(cp.editDrugHeader);
                        sa.assertEquals(eu.getText(cp.editDrugHeader), "Edit " + beforeEditDrugTableDesc, "Incorrect edit drug header");
                        String beforeEditDescValInModal = eu.getAttribute(rp.getInputField("description"), "value");
                        String beforeEditTherapeuticClassSelectedInModal = eu.getText(cp.getFilter("therapeuticClass"));

                        //edit desc and therapeuticclass
                        rp.sendValue("description", beforeEditDescValInModal + updatedDrugDesc);
                        cp.clickFilter("therapeuticClass");
                        cp.selectFilterItemByName("Chemo");
                        //  eu.syncWait(1);
                        rp.clickDialogBtn("Save");
                        eu.syncWait(2);
                        String afterEditDrugTableDesc = rp.getRowCellData(dp.drugFormularyTable, 0)[0];
                        String afterEditDrugTableTherapeuticClass = rp.getRowCellData(dp.drugFormularyTable, 0)[1];
                        sa.assertNotEquals(beforeEditDrugTableDesc, afterEditDrugTableDesc, "Drug Desc is not updated.");
                        sa.assertNotEquals(beforeEditDrugTableTherapeuticClass, afterEditDrugTableTherapeuticClass,
                            "TherapeuticClass is not updated in edit drug modal.");

                        //Reset fields after test
                        eu.doClick(cp.editDrug);
                        eu.syncWait(2);
                        cp.clearInputField(eu.getElement(rp.getInputField("description")));
                        rp.sendValue("description", beforeEditDescValInModal);
                        cp.clickFilter("therapeuticClass");
                        cp.selectFilterItemByName(beforeEditTherapeuticClassSelectedInModal);
                        rp.clickDialogBtn("Save");
                        AllureReportListener.saveLogs("Drug Edited and reset edit: " + editDrug + " " + beforeEditDescValInModal);
                    } else {
                        sa.fail();
                        AllureReportListener.saveLogs("Edit drug button is missing in drug expanded view. Check ");
                    }
                } else {
                    AllureReportListener.saveLogs("This search returned more than 1 row and not expected to return more than row. Pl check. ");
                }
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Drug Records exists in the drugs tab");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }

    @Test(priority = 9, groups = "smoke", description = "Clinical Content - Base Formulary Page - Drug Formulary - Edit Non Drug")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clinical Content - Base Formulary Page - Drug Formulary - Edit Non drug")
    public void baseFormularyEditNonDrug() {

        SoftAssert sa = new SoftAssert();
        String updatedNonDrugDesc = "UpdatedNonDrugAuto";

        try {
            rp.clickLeftMenuItem("Clinical Content");
            rp.clickByLinkText("Base Formulary");
            rp.clickTabs("Non-Drugs");
            sa.assertEquals(rp.getPageHeading(), "Base Formulary", "Incorrect page heading");
            sa.assertEquals(rp.getGridHeading(), "Non-Drug Formulary", "Incorrect grid heading");

            if (rp.getNumOfGridResults() > 0) {

                rp.srhRegDrugDiagAndEnter(editNonDrug);
                int rowCountHCPC = eu.getGridRowCount(dp.nonDrugFormularyTable);
                if (eu.getGridRowCount(dp.nonDrugFormularyTable) == 1) {

                    String beforeEditNonDrugTableDesc = rp.getRowCellData(dp.nonDrugFormularyTable, 0)[0];
                    String beforeEditNonDrugTableTherapeuticClass = rp.getRowCellData(dp.nonDrugFormularyTable, 0)[1];
                    rp.clickGridCell("non-drug-formulary-table", 1, 1);
                    eu.getTextcontent(cp.editDrugHeader);
                    sa.assertEquals(eu.getText(cp.editDrugHeader), "Edit " + beforeEditNonDrugTableDesc, "Incorrect non drug header");
                    String beforeEditDescValInModal = eu.getAttribute(rp.getInputField("description"), "value");
                    String beforeEditTherapeuticClassSelectedInModal = eu.getText(cp.getFilter("therapeuticClass"));

                    //edit desc and therapeuticclass
                    rp.sendValue("description", beforeEditDescValInModal + updatedNonDrugDesc);
                    cp.clickFilter("therapeuticClass");
                    if(!beforeEditNonDrugTableTherapeuticClass.equals("E & M")) {
                        cp.selectFilterItemByName("E & M");
                    } else { cp.selectFilterItemByName("Drug Administration");}
                    //  eu.syncWait(1);
                    rp.clickDialogBtn("Save");
                    eu.syncWait(3);
                    String afterEditDrugTableDesc = rp.getRowCellData(dp.nonDrugFormularyTable, 0)[0];
                    String afterEditDrugTableTherapeuticClass = rp.getRowCellData(dp.nonDrugFormularyTable, 0)[1];
                    sa.assertNotEquals(beforeEditNonDrugTableDesc, afterEditDrugTableDesc, "Non-Drug Desc is not updated.");
                    sa.assertNotEquals(beforeEditNonDrugTableTherapeuticClass, afterEditDrugTableTherapeuticClass,
                        "TherapeuticClass for non-drug is not updated.");

                    //Reset fields after test
                    rp.clickGridCell("non-drug-formulary-table", 1, 1);
                    eu.syncWait(1);
                    cp.clearInputField(eu.getElement(rp.getInputField("description")));
                    rp.sendValue("description", beforeEditDescValInModal);
                    cp.clickFilter("therapeuticClass");
                    cp.selectFilterItemByName(beforeEditTherapeuticClassSelectedInModal);
                    rp.clickDialogBtn("Save");
                    AllureReportListener.saveLogs("Non-Drug Edited and reset edit: " + editNonDrug + " " + beforeEditDescValInModal);
                } else {
                    AllureReportListener.saveLogs("This search returned more than 1 row and not expected to return more than row. Pl check. ");
                }
            } else {
                sa.fail();
                AllureReportListener.saveLogs("No Non-drug Records exists in the non-drugs tab");
            }
        } catch (NoSuchElementException | InterruptedException e) {
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();
    }
}
