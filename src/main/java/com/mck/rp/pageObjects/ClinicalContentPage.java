package com.mck.rp.pageObjects;

import com.mck.rp.base.BasePage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Step;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ClinicalContentPage extends BasePage {

    private final WebDriver driver;
    ElementUtil elementUtil;

    //Header Location Objects
    public By selectedLocation = By.xpath("//div[@data-testid='account-location-select']//div/span");
    public By allPractices = By.xpath("//li[@role='treeitem']//p[.='All Practices']");

    //Clinical Content > BF Objects
    public By columnHeaders = By.xpath("//span[@role= 'button' and not(contains(@data-testid, 'clear-button'))]");
    public By editDrug = By.xpath("//span//span[.='Edit Drug']");
    public By deleteButton = By.xpath("//button[contains(@data-testid,'delete')]//div");
    public By selectedItems = By.xpath("//div[contains(@data-testid,'selected-items')]//div");
    public By editDrugHeader = By.xpath("//div[@role='dialog']//div[contains(@class,'colorPrimary')]//h2");

    //Clinical Content - Regimen Manangement
    public By regimenTable = By.xpath("//table[@data-testid='regimen-formulary-table']");
    public By editRegimenButton = By.xpath("//a[@role='button']//span[contains(text(),'Edit Regimen')]");
    public By editRegimenRegimenName = By.xpath("//div[@data-testid='regimen-name']//textarea");
    public By editRegimenSelectDrug = By.xpath(" //div[@role='combobox']//input[@name='items[0].drug']");



    //Clinical Content > Diagnosis Objects
    public By diagnosesTable = By.xpath("//table[@data-testid='diagnoses-formulary-table']");

    //Organization and user management
    public By levelRadioButton = By.xpath("//input[@type='radio']");
    public By levelRadioButtonName = By.xpath("//input[@type='radio']//following::p");
    public By levelCheckBoxs = By.xpath("//li//input[@type='checkbox']");
    public By levelCheckBoxNames = By.xpath("//li//input[@type='checkbox']//following::p");
    public By defaultLocationsRadioButton = By.xpath("//li//input[@type='radio']");
    public By userDetails = By.xpath("//h2[contains(text(), 'User Details')]");
    public By permissionsBlock = By.xpath("//p[contains(@class, 'MuiTypography-colorInherit')]");
    public By orgList = By.xpath("//div[contains(@class,'MuiTreeItem-content')]//div[contains(@class, 'MuiTreeItem-iconContainer')]");
    public By orgListNames = By.xpath("//div[contains(@class,'MuiTreeItem-content')]//p//strong");
    public By orgPracListNames = By.xpath("//div[contains(@class,'MuiTreeItem-content')]//p");
    public By levelInModel = By.xpath("//div[contains(@class, 'MuiGrid-container MuiGrid-item')]//div[@class='MuiGrid-root MuiGrid-item']//p");


    // constructor of the page class:
    public ClinicalContentPage(WebDriver driver) {
        this.driver = driver;
        elementUtil = new ElementUtil(driver);
    }

    // get column headers inside table grid
    @Step("Get grid results column header. FilterName: {0}")
    public WebElement getColumnHeader(String datatestid) {
        WebElement element = driver.findElement(By.xpath("//span[@data-testid='" + datatestid + "']"));
        return element;
    }

    //Get selected grid row data
    @Step("Get selected grid row data.")
    public String[] getRowCellDataAndSelectRow(By locator, int row) {
        WebElement table = driver.findElement(locator);
        String[] webTableData = null;
        List<WebElement> tableRows = table.findElements(By.xpath("//tr[contains(@class, 'TableRow-hover')]"));

        //select row
        String elementPath = "//td[@data-row-index =" + row + " and @data-hoverable='true']/preceding-sibling::td[contains(@class, 'Checkbox')" + "]//span//span";
        WebElement rowToSelect = tableRows.get(row).findElement(By.xpath(elementPath));
        rowToSelect.click();

        //get row data
        List<WebElement> tableColumns1 = tableRows.get(row).findElements(By.xpath("//td[@data-row-index =" + row + " and @data-hoverable='true']"));
        webTableData = new String[tableColumns1.size()];
        for (int j = 0; j < tableColumns1.size() - 1; j++) {
            if (tableColumns1.size() > 0) {
                String text = tableColumns1.get(j).getAttribute("textContent");
                webTableData[j] = text;
            } else {
                break;
            }
        }
        return webTableData;
    }

    @Step("Clicking on the filters in modal. FilterName: {0}")
    public void clickFilter(String dataTestName) {
        try {
            WebElement element = driver.findElement(getFilter(dataTestName));
            element.click();
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Get selected filter in modal. FilterName: {0}")
    public By getFilter(String dataTestName) {
        return By.xpath("//div[@data-testname='" + dataTestName + "']//span//span");
    }

    @Step("Select Filter Item by Name. FilterName: {0}")
    public void selectFilterItemByName(String name) {
        try {
            elementUtil.clickWhenReady(By.xpath("//div[contains(text(),'" + name + "')]"), 8);
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {

            e.printStackTrace();
        }
    }

    @Step("Get Confirmation Dialog Btn. FilterName: {0}")
    public By getConfirmDialogBtn(String buttonName) {
        By button = By.xpath("//div[@role='dialog']//button[@type='button']//span[(text()='" + buttonName + "')]");
        return button;
    }

    //Move this method to ElementUtil.java file
    @Step("Clear input field text.")
    public void clearInputField(WebElement element){
        element.clear();
    }

    //move this method to RegimenPage
    @Step("Clicking Level Radio by name : {0}")
    public void selectRadioSelectFilterItemByName(String name) {
        try {
            //elementUtil.clickWhenReady(By.xpath("//p[contains(text(), '"+ name +"')]//preceding::span[2]/input[@type='radio']"), 8);
            elementUtil.doClick(By.xpath("//p[contains(text(), '"+ name +"')]//preceding::span[2]/input[@type='radio']"));
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Clicking Level by index: {1}")
    public void selectFilterItemByIndex(By locator, int index) {

        List<WebElement> levelList = elementUtil.getElements(locator);

        try {
            for (int i=0; i < levelList.size(); i++) {
                if (i==index) {
                    levelList.get(i).click();
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

    }

    //move this method to RegimenPage
    @Step("Clicking Level Checkbox by name : {0}")
    public void selectCheckboxFilterItemByName(String name) {
        try {
            elementUtil.doClick(By.xpath("//p[contains(text(), '"+ name +"')]//preceding::span[1]/input[@type='checkbox']"));
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //org expand/collapse by name
    @Step("Clicking Org expand collapse icon by name : {0}")
    public void expandCollapseOrgByName(String name) {
        try {
            elementUtil.doClick(By.xpath("//p//strong[contains(text(), '" + name + "')]//preceding::div[1][contains(@class, 'MuiTreeItem-iconContainer')]"));
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step("Clicking Org by name : {0}")
    public void clickOrgByName(String name) {
        try {
            elementUtil.scrollToView(By.xpath("//p//strong[contains(text(), '" + name + "')]"));
            elementUtil.doClick(By.xpath("//p//strong[contains(text(), '" + name + "')]"));
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Double Click Org/Practice by name : {0}")
    public void clickOrgPracticeByName(String name) {
        try {
            elementUtil.scrollToView(By.xpath("//p[contains(text(), '" + name + "')]"));
            //elementUtil.doClick(By.xpath("//p[contains(text(), '" + name + "')]"));
            Actions a = new Actions(driver);
            a.doubleClick(elementUtil.getElement(By.xpath("//p[contains(text(), '" + name + "')]"))).build().perform();
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    //org create new by name
    @Step("click create new org button by name : {0}")
    public void clickCreateNewOrgButtonByName(String name) {
        try {
            elementUtil.doClick(By.xpath("//p//strong[contains(text(), '" + name + "')]//following::span[2][contains(text(), 'Create New')]"));
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step("find org from list of orgs: {1}")
    public boolean isOrgPresent(By locator, String name) {
        boolean isOrgPresent = false;
        List<WebElement> elements = elementUtil.getElements(locator);
        try {
            for (WebElement element : elements) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                if (element.getAttribute("textContent").equals(name)) {
                    isOrgPresent = true;
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return isOrgPresent;

    }

    @Step("Get org/prac status: {0}")
    public By getOrgPracStatus(String name) {
        return By.xpath("//p[contains(text(), '" + name +"')]//following::span[1]");
    }


    //move this method to RegimenPage
    @Step("Click list value from dropdown field by Name: {0}")
    public void selectDropdownListValueByName(String name) {
        try {
            elementUtil.clickWhenReady(By.xpath("//p[contains(text(), '" + name + "')] "), 8);
            TimeUnit.SECONDS.sleep(1);
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Select dropdown : {0}")
    public By getDropdownByName(String dataTestId) {
        By element = By.xpath("//div[@data-testid = '" + dataTestId + "']");
        return element;
    }

    @Step("Get Dropdown selected value : {0}")
    public By getDropdownSelectedValue(String dataTestId) {
        By element = By.xpath("//div[@data-testid = '" + dataTestId + "']//span//span");
        return element;
    }

    public By getSpanTextElement(String text){
        By element = By.xpath("//span[contains(text(), '"+ text + "')]");
        return element;
    }


    @Step("Clear selected Level")  //filter level does not have data-testid="clear-button". Get this added for level filter
    public By getFilterSelectClear(String dataTestId) {
        By element = By.xpath("//div[@data-testid='" + dataTestId + "']//span[@aria-label = 'clear value']");
        return element;
    }

    @Step("Get text area name: {0}")
    public By getTextareaField(String eleName) {
        By element = By.xpath("//div[@data-testid='"+ eleName +"']//textarea");

        return element;
    }

    public By getLinkInTable(int row, int col){

        By element = By.xpath("//table[@data-testid='table']//td[@data-row-index = " + row + " and @data-col-index= " + col + "]//button/span");
        return element;
    }

    //Move this to ElementUtil
    public List<String> sortItemsListIgnoreCase(List<String> listOfString, String order) {
        if (order.equalsIgnoreCase("descending") || order.equalsIgnoreCase("desc")) {
            Collections.sort(listOfString, String.CASE_INSENSITIVE_ORDER.reversed());
        } else {
            Collections.sort(listOfString, String.CASE_INSENSITIVE_ORDER);
        }
        return listOfString;
    }

}
