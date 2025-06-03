package com.test.houta;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Tests Haouta Store")
@Feature("Formulaires de connexion et d'inscription")
@Owner("Adib")
public class FormTests {

    WebDriver driver;

    @BeforeMethod
    public void setUp() {
      WebDriverManager.chromedriver().setup();
      driver = new ChromeDriver();
      driver.manage().window().maximize();
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }
    
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }    

    
    @Test(priority = 1)
    public void testLoginForm() {
        driver.get("https://haoutastore.com/my-account/");

        WebElement loginEmail = driver.findElement(By.id("username"));
        WebElement loginPassword = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.name("login"));

        loginEmail.sendKeys("norkidikni@gufum.com"); // Use real test account
        loginPassword.sendKeys("Test1234@test");
        // loginPassword.sendKeys("NewPass456@test");
        loginButton.click();

        WebElement logoutLink = driver.findElement(By.xpath("//a[contains(text(), 'Log out')]"));
        assert driver.getCurrentUrl().contains("/my-account");
        assert logoutLink.isDisplayed();
    }

    @Test(priority = 2)
    public void testRegisterForm() {
        driver.get("https://haoutastore.com/my-account/");

        WebElement registerEmail = driver.findElement(By.id("reg_email"));
        WebElement registerButton = driver.findElement(By.name("register"));

        // Unique email for registration
        String email = "newuser" + System.currentTimeMillis() + "@example.com";
        // String email = "newuser@example.com";
        registerEmail.sendKeys(email);
        registerButton.click();

        // Confirm redirect to /my-account-2 and check for logout link
        WebElement logoutLink = driver.findElement(By.xpath("//a[contains(text(), 'Log out')]"));
        assert driver.getCurrentUrl().contains("/my-account");
        assert logoutLink.isDisplayed();
    }

    // @Test(priority = 3)
    // public void testContactForm() throws InterruptedException {
    //     driver.get("https://haoutastore.com/contact/");
    
    //     driver.findElement(By.name("your-name")).sendKeys("Adib");
    //     driver.findElement(By.name("your-email")).sendKeys("adib.chiguer@gmail.com");
    //     driver.findElement(By.name("your-subject")).sendKeys("Test du formulaire");
    //     driver.findElement(By.name("your-message")).sendKeys("Ceci est un test automatisé.");
    
    //     // Use specific selector for the Contact Form 7 submit button
    //     WebElement submitButton = new WebDriverWait(driver, Duration.ofSeconds(10))
    //         .until(ExpectedConditions.elementToBeClickable(
    //             By.cssSelector("input.wpcf7-submit")
    //         ));
    
    //     Thread.sleep(1000); // optional: allow page JS to initialize
    //     ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
    //     ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
    
    //     // Wait for confirmation message
    //     WebElement confirmation = new WebDriverWait(driver, Duration.ofSeconds(10))
    //         .until(ExpectedConditions.visibilityOfElementLocated(
    //             By.cssSelector("div.wpcf7-response-output")
    //         ));
    
    //     System.out.println("Current URL: " + driver.getCurrentUrl());
    //     System.out.println("Confirmation message: " + confirmation.getText());
    
    //     assert confirmation.getText().contains("Thank you for your message. It has been sent.");
    // }       
}
