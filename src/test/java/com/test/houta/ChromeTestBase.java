package com.test.houta;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class ChromeTestBase {
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected String baseUrl = "https://haoutastore.com/";

    @BeforeClass(alwaysRun = true)
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();

        // Add Chrome options for better performance and stability
        org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-images"); // Skip image loading for faster tests
        options.addArguments("--disable-javascript"); // Only if your tests don't require JS
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(8)); // Reduced from 10
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3)); // Reduced from 5
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15)); // Reduced from 30
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        // Clear cookies and cache to ensure clean state between tests
        driver.manage().deleteAllCookies();
        // Don't navigate to base URL here - let individual tests navigate where needed
    }

    @AfterClass(alwaysRun = true)
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}