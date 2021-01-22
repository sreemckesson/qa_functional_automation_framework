package com.mck.rp.tetscases.PracticeAndUserManagement.Users;

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
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Random;


@Listeners(AllureReportListener.class)
public class UsersCreateUser extends BaseTest {

    ElementUtil eu;
    RegimenAnalysisPage rp;
    DataManagementPage dp;
    LoginPage lp;
    String srhUser = "admin";

    @Description("Set up and Initialization")
    @Severity(SeverityLevel.CRITICAL)
    @BeforeClass
    public void UsersCreateUserSetUp() {
        rp = new RegimenAnalysisPage(driver);
        dp = new DataManagementPage(driver);
        lp = new LoginPage(driver);
        eu = new ElementUtil(driver);
    }

    @Test(priority = 1, groups = "regression", description = "Practice And User Management - Users - Create User of type" +
            "Customer and as Primary Contact.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Practice And User Management - Users - Create User of type Customer and as Primary Contact.")
    public void userCreateUser() throws NoSuchElementException {
        SoftAssert sa = new SoftAssert();
        try {
            Assert.assertTrue(rp.isLogoutExist());
            rp.clickLeftMenuItem("Practice & User Management");
            rp.clickByLinkText("Users");
            sa.assertEquals(rp.getPageHeading(), "Users");
            sa.assertEquals(rp.getGridHeading(), "Users");


            eu.doClick(dp.createNew);
            eu.syncWait(2);
            sa.assertEquals(rp.getPageHeading(), "User Account");
            sa.assertEquals(rp.getGridHeading(), "User Details");

            rp.sendKeysByAction("firstName", "Automation");
            rp.sendKeysByAction("lastName", "User" + new Random().nextInt(200));
            rp.sendKeysByAction("email", "rpautomation@gmail.com");
            rp.clickSpanText("Customer");
            rp.clickSpanText("Primary");
            eu.scrollToBottom();
            eu.syncWait(2);
            rp.clickSpanText("Basic Practice Management");
            rp.clickSpanText("User Management");

            rp.clickSpanText("Practice Configurations");
            rp.clickSpanText("Practice Fee Schedules");
            rp.clickSpanText("Practice Supportive Care Regimen Management");

            rp.clickSpanText("Patient Analysis");
            rp.clickSpanText("Practice Analysis");

            eu.scrollToBottom();
            rp.clickButton("Cancel");
            rp.clickDialogBtn("Yes");


        } catch (NoSuchElementException | InterruptedException | StaleElementReferenceException e) {
            //ExtentReportListener.test.get().log(Status.INFO, "Test Method Failed!!");
            AllureReportListener.saveLogs("Test Method Failed!!");
        }
        sa.assertAll();

    }




}
