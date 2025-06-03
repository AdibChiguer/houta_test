package com.test.houta;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;


@Epic("Tests Haouta Store")
@Feature("Mise à jour du compte utilisateur")
@Owner("Adib")
public class UserAccountTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        // Add Chrome options to handle potential issues
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        js = (JavascriptExecutor) driver;
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testProfileUpdate() {
        try {
            driver.get("https://haoutastore.com/my-account/");

            WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
            usernameField.clear();
            usernameField.sendKeys("norkidikni@gufum.com");

            WebElement passwordField = driver.findElement(By.id("password"));
            passwordField.clear();
            passwordField.sendKeys("Test1234@test");
            // passwordField.sendKeys("NewPass456@test");

            WebElement loginButton = driver.findElement(By.name("login"));
            loginButton.click();

            WebElement accountDetailsLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Account details")));
            accountDetailsLink.click();

            WebElement firstNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("account_first_name")));
            firstNameInput.clear();
            firstNameInput.sendKeys("FirstName");

            WebElement lastNameInput = driver.findElement(By.id("account_last_name"));
            lastNameInput.clear();
            lastNameInput.sendKeys("LastName");

            WebElement displayNameInput = driver.findElement(By.id("account_display_name"));
            displayNameInput.clear();
            displayNameInput.sendKeys("BidaNext");

            WebElement currentPasswordField = driver.findElement(By.name("password_current"));
            currentPasswordField.clear();
            currentPasswordField.sendKeys("Test1234@test");
            // currentPasswordField.sendKeys("NewPass456@test");

            WebElement newPassword1Field = driver.findElement(By.name("password_1"));
            newPassword1Field.clear();
            // newPassword1Field.sendKeys("Test1234@test");
            newPassword1Field.sendKeys("NewPass456@test");

            WebElement newPassword2Field = driver.findElement(By.name("password_2"));
            newPassword2Field.clear();
            // newPassword2Field.sendKeys("Test1234@test");
            newPassword2Field.sendKeys("NewPass456@test");

            WebElement saveButton = driver.findElement(By.name("save_account_details"));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", saveButton);

            Thread.sleep(1000);

            try {
                wait.until(ExpectedConditions.elementToBeClickable(saveButton));
                saveButton.click();
            } catch (Exception e1) {
                try {
                    js.executeScript("arguments[0].click();", saveButton);
                } catch (Exception e2) {
                    WebElement form = driver.findElement(By.cssSelector("form.woocommerce-EditAccountForm"));
                    form.submit();
                }
            }

            WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".woocommerce-message, .wc-block-components-notice-banner")));
            Assert.assertTrue(successMsg.getText().toLowerCase().contains("account")
                    || successMsg.getText().toLowerCase().contains("success"));

            WebElement dashboardLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Dashboard")));
            dashboardLink.click();

            try {
                WebElement directLogoutLink = driver.findElement(By.xpath("//a[contains(text(), 'Log out') or contains(text(), 'Logout') or contains(@href, 'logout')]"));
                if (directLogoutLink.isDisplayed()) {
                    System.out.println("Found direct logout link, clicking it");
                    directLogoutLink.click();
                } else {
                    throw new Exception("Direct logout link not visible");
                }
            } catch (Exception e) {
                System.out.println("Direct logout not found, trying Dashboard approach");

                WebElement logoutLink = null;
                try {
                    logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout")));
                } catch (Exception e1) {
                    try {
                        logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Log out")));
                    } catch (Exception e2) {
                        try {
                            logoutLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, 'logout')]")));
                        } catch (Exception e3) {
                            System.out.println("Available links on page:");
                            var links = driver.findElements(By.tagName("a"));
                            for (WebElement link : links) {
                                if (link.getText().trim().length() > 0) {
                                    System.out.println("- " + link.getText() + " (href: " + link.getAttribute("href") + ")");
                                }
                            }
                            throw new Exception("No logout link found");
                        }
                    }
                }
                logoutLink.click();
            }

            WebElement usernameFieldNew = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
            usernameFieldNew.clear();
            usernameFieldNew.sendKeys("norkidikni@gufum.com");

            WebElement passwordFieldNew = driver.findElement(By.id("password"));
            passwordFieldNew.clear();
            // passwordFieldNew.sendKeys("Test1234@test");
            passwordFieldNew.sendKeys("NewPass456@test");

            WebElement loginButtonNew = driver.findElement(By.name("login"));
            loginButtonNew.click();

            wait.until(ExpectedConditions.urlContains("/my-account"));
            Assert.assertTrue(driver.getCurrentUrl().contains("/my-account"));

            WebElement logoutLinkVerify = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[contains(text(), 'Log out') or contains(text(), 'Logout')]")));
            Assert.assertTrue(logoutLinkVerify.isDisplayed());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assert.fail("Test was interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Test failed with error: " + e.getMessage());
        }
    }
}
