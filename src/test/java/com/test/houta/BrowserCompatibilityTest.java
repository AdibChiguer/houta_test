package com.test.houta;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class BrowserCompatibilityTest {
    private WebDriver driver;
    private String baseUrl = "https://haoutastore.com/";
    private WebDriverWait wait;

    @BeforeMethod
    @Parameters("browser")
    public void setUp(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Navigateur non supporté : " + browser);
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @Test
    public void testHomePageLoad() {
        String pageTitle = driver.getTitle();
        Assert.assertFalse(pageTitle.isEmpty(), "Le titre de la page d'accueil est vide sur " + driver.getClass().getSimpleName());

        WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("img.normal-logo")));
        Assert.assertTrue(logo.isDisplayed(), "Le logo de la page d'accueil n'est pas affiché sur " + driver.getClass().getSimpleName());
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}