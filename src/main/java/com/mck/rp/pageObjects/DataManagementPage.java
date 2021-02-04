package com.mck.rp.pageObjects;

import com.mck.rp.base.BasePage;
import com.mck.rp.utilities.ElementUtil;
import org.openqa.selenium.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class DataManagementPage extends BasePage {

    private final WebDriver driver;
    //BaseForumulary Objects
    public By drugFormularyTable = By.xpath("//table[@data-testid='drug-formulary-table']");
    public By nonDrugFormularyTable = By.xpath("//table[@data-testid='non-drug-formulary-table']");
    public By drugDetailsTable = By.xpath("//table[@data-testid='drug-details-table']");
    public By feeScheduleTable = By.xpath("//table[@data-testid='fee-schedules-table']");
    public By feeScheduleEditTable = By.xpath("//table[@data-testid='table']");
    public By supportiveCareTable = By.xpath("//table[@data-testid='supportive-care-table']");
    public By calculationMethodDropdown = By.xpath("//div[@data-testid='select-calculation-method']");
    public By ddTheraputicClass = By.xpath("//div[@data-testid='select-therapeutic-classes']");
    public By ddClassification = By.xpath("//div[@data-testid='filter-classification']");
    //Works for both Drug and nonDrug Page in BaseFormulary
    public By ddDrugCostCal = By.xpath("//div[@data-testid='select-calculation-method']");
    public By drugDtsTableRows = By.xpath("//table[@data-testid='drug-details-table']//tr");
    //dropdowns/filters in Supportive Care Regimen
    public By ddRegimenClass = By.xpath("//div[@data-testid='regimen-class-select']");
    public By btnDrugDtsCustomRadio = By.xpath("//table[@data-testid='custom-cost-table']//input[@type = 'radio' and not(@disabled)]");
    //Base Formulary
    //table[@data-testid='custom-cost-table']//div//span - Custom button in expanded row
    //td[@data-testid='table-row-details']//p//strong
    public By ddLastUpdatedBy = By.xpath("//div[@data-testid='last-updated-by-select']");
    public By ddStatus = By.xpath("//div[@data-testid='status-select']");
    public By suppCareRegimenSummaryName = By.xpath("//div[@data-testid='regimen-name']");
    //Practice and User Management - Objects
    public By userTable = By.xpath("//table[@data-testid='table']");


    //This is for getting the message from Regimen Summary page Supportive care message
    // div[@data-testid='regimen-summary-supportive-care']//div//span[contains(@class,'colorPrimary')]
    //div[@data-testid='regimen-summary-supportive-care']//div[@data-testname='growthFactors']

    //Need to add remove drugs/nondrugs table items - Waiting for table ids change in both tables.
    //div[@data-testid='drug-name']
    //button[@data-testid='remove-button']
    //form[@data-testid='drug-table-container']//button[@data-testid='configure-fields']
    //div[@data-testid='configure-fields']//span[contains(@class,'labelSmall')] -- Fields inside the configurefields
    public By createNew = By.xpath("//a[@role='button']//span[contains(text(),'Create New')]");
    ElementUtil elementUtil;
    //input[@type='radio' and @checked] - To check which button is checked in customer and mckesson

    //span[contains(text(),'McKesson User')]

    // constructor of the page class:
    public DataManagementPage(WebDriver driver) {
        this.driver = driver;
        elementUtil = new ElementUtil(driver);
    }

    //drug formulary table row arrow - provide odd numbers for the table rows starting from 1
    // 1st row- 1, 2nd Row -3 etc
    //
    public By drugFormularytableArrow(int row) {
        By arrow = By.xpath("//tr[" + row + "]//td[8]//*[local-name()='svg']");
        return arrow;
    }

    /**
     * This is to return userType for adding new users in User Management
     * values -  McKesson User, Customer and Primary Contact
     *
     * @param userType
     * @return
     */
    public By getUserType(String userType) {
        By user = By.xpath("//span[contains(text(),'" + userType + "')]");
        return user;
    }

    //For clicking on the Drug Formulary Table grid arrows, provide grid row
    public void clickDrugFormularyTableArrow(int row) {
        elementUtil.doClick(drugFormularytableArrow(row));
    }

    /**
     * This is to click the submenu items under CreateNew in Supportive Care page
     *
     * @param value
     */

    public void dataMngClickSubListItem(String value) throws InterruptedException {
        List<WebElement> liItem = driver.findElements(By.xpath("//li[@role='menuitem']//div"));
        try {
            for (WebElement element : liItem) {
                if (element.getAttribute("textContent").equals(value)) {
                    element.click();
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        } catch (StaleElementReferenceException e) {
           // e.printStackTrace();
        }
    }

    //Below methods are particularly for Drug Details table as the table is designed with rows and radio buttons etc

    /**
     * This is for returning the boolean if the given text is matched int he table
     * For example to find Lowest, Invoiced or Custom from the sub table in Base Formulary Drug table
     *
     * @param value
     */

    public boolean bfDrugDtsTableGridRows(String value) throws InterruptedException {
        boolean a = false;
        List<WebElement> liItem = driver.findElements(drugDtsTableRows);
        for (WebElement element : liItem) {
            //System.out.println(element.getAttribute("textContent"));
            if (element.getAttribute("textContent").contains(value)) {
                //element.click();
                a = true;
                //System.out.println(element.getAttribute("textContent"));
                TimeUnit.SECONDS.sleep(1);
            }
        }
        return a;
    }

    /**
     * This is for return the row index that matches the given text
     * For example to find Lowest, Invoiced or Custom from the sub table in Base Formulary Drug table
     *
     * @param value
     * @return
     * @throws InterruptedException
     */
    public int bfDrugDtsTableGridRow(String value) throws InterruptedException {
        int row = 0;
        List<WebElement> liItem = driver.findElements(drugDtsTableRows);
        for (WebElement element : liItem) {
            if (element.getAttribute("textContent").contains(value)) {
                row = Integer.parseInt(element.getAttribute("rowIndex"));
                TimeUnit.SECONDS.sleep(1);
            }
        }
        return row;
    }

    /**
     * This is for getting row count from the sub table in Base Formulary Drug table
     *
     * @param locator
     * @return
     */

    public int bfGetGridRowCount(By locator) {
        WebElement table = driver.findElement(locator);
        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
        return tableRows.size();
    }

    /**
     * This is for getting column count from the sub table in Base Formulary Drug table
     *
     * @param locator
     * @return
     */
    public int bfGetGridColumnCount(By locator, int row) {

        WebElement table = driver.findElement(locator);
        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
        //for (int i = 1; i < tableRows.size(); i++) {
        List<WebElement> tableColumns = tableRows.get(row).findElements(By.tagName("td"));

        return tableColumns.size();
        //}
        //return 0;
    }

    /**
     * This is for getting row and column data from the sub table in Base Formulary Drug table
     *
     * @param locator
     * @param row
     * @param column
     * @return
     */
    public String bfGetRowColumnData(By locator, int row, int column) {
        String text = null;

        WebElement table = driver.findElement(locator);
        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
        if (tableRows.size() > 0) {
            List<WebElement> tableColumns = tableRows.get(row).findElements(By.xpath("//td[@data-row-index='" + row + "']"));
            if (tableColumns.size() > 0) {
                text = tableColumns.get(column).getAttribute("textContent");
                //System.out.println(text);
            }
        }
        return text;
    }

    //move this to regimen analysis page:
    public By getTextArea(String eleName) {
        By element = By.xpath("//div[@data-testid='"+ eleName + "']//textarea");
        return element;
    }

    //move this to regimen analysis page:
    public By getBtnTypeButton(String type, String name) {
        By btn = By.xpath("//button[@type='" + type + "' and @data-testid='"+ name + "']");
        return btn;
    }
}
