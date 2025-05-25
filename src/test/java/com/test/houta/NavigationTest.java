package com.test.houta;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class NavigationTest {
    private WebDriver driver;
    private String baseUrl = "https://haoutastore.com/";
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @Test
    public void testCategoryNavigation() {
        // Survoler le menu "Smartphone" pour rendre les sous-catégories visibles
        WebElement smartphoneMenu = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("li#menu-item-7445 a[href='https://haoutastore.com/product-category/smartphone/']")));
        new Actions(driver).moveToElement(smartphoneMenu).perform();

        // Attendre que le sous-menu soit visible (optionnel, dépend du comportement du site)
        WebElement xiaomiLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("li#menu-item-7790 a[href='https://haoutastore.com/product-category/xiaomi/']")));
        
        // Cliquer sur la catégorie Xiaomi
        xiaomiLink.click();

        // Vérifier l'URL
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("xiaomi"), "La redirection vers la catégorie Xiaomi a échoué");

        // Vérifier le titre de la catégorie
        WebElement categoryTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("h1.entry-title")));
        Assert.assertTrue(categoryTitle.isDisplayed(), "La page de catégorie Xiaomi ne s'affiche pas correctement");
        Assert.assertTrue(categoryTitle.getText().contains("Xiaomi"), "Le titre de la catégorie Xiaomi est incorrect");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}