package com.test.houta;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class NavigationTest {
    private WebDriver driver;
    private String baseUrl = "https://haoutastore.com/";

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @Test
    public void testCategoryNavigation() {
        WebElement categoryLink = driver
                .findElement(By.cssSelector("a[href='https://haoutastore.com/product-category/xiaomi/']"));
        categoryLink.click();

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("xiaomi"), "La redirection vers la catégorie xiaomi a échoué");

        WebElement categoryTitle = driver.findElement(By.className("heading-title page-title entry-title")); // À
                                                                                                             // adapter
        Assert.assertTrue(categoryTitle.isDisplayed(), "La page de catégorie Xiaomi ne s'affiche pas correctement");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}