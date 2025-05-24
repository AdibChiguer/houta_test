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

public class CartRemovalTest {
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
    public void testRemoveFromCart() {
        driver.get(baseUrl + "product/batterie-rechargeable-usb-prumyls-1-5v-5100mwh-2-piece/"); // À adapter
        WebElement addToCartButton = driver.findElement(By.name("add-to-cart")); // À adapter
        addToCartButton.click();

        driver.get(baseUrl + "cart-2/"); // À adapter
        WebElement cartItem = driver.findElement(By.className("product-name")); // À adapter
        Assert.assertTrue(cartItem.isDisplayed(), "L'article n'a pas été ajouté au panier");

        WebElement totalBefore = driver.findElement(By.className("woocommerce-Price-amount amount")); // À adapter
        String totalBeforeText = totalBefore.getText();

        WebElement removeButton = driver.findElement(By.cssSelector("a[data-product_id='10303']")); // À adapter
        removeButton.click();

        boolean isCartEmpty = driver.findElements(By.className("woocommerce-cart-form__cart-item cart_item")).isEmpty();
        Assert.assertTrue(isCartEmpty, "L'article n'a pas été supprimé du panier");

        WebElement totalAfter = driver.findElement(By.className("woocommerce-Price-amount amount")); // À adapter
        String totalAfterText = totalAfter.getText();
        Assert.assertNotEquals(totalAfterText, totalBeforeText,
                "Le total du panier n'a pas été mis à jour après suppression");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}