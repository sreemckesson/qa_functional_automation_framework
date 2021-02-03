package com.mck.rp.pageObjects;

import com.mck.rp.base.BasePage;
import com.mck.rp.utilities.ElementUtil;
import io.qameta.allure.Step;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

}
