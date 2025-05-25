package com.test.houta;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class CartRemovalTest {
    private WebDriver driver;
    private String baseUrl = "https://haoutastore.com/";
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @Test
    public void testRemoveFromCart() {
        // Naviguer vers la page produit "Batterie Rechargeable USB Prumyls"
        driver.get(baseUrl + "product/batterie-rechargeable-usb-prumyls-1-5v-5100mwh-2-piece/");

        // Vérifier le nombre d'articles dans le panier avant l'ajout
        WebElement cartCountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("span.cart-number")));
        String initialCartCountText = cartCountElement.getText();
        int initialCartCount = initialCartCountText.isEmpty() ? 0 : Integer.parseInt(initialCartCountText);

        // Attendre que le bouton "Add to Cart" soit cliquable
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.single_add_to_cart_button[name='add-to-cart'][value='10303']")));

        // Faire défiler l'élément dans la vue pour éviter les obstructions
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartButton);

        // Essayer de cliquer normalement
        try {
            addToCartButton.click();
        } catch (Exception e) {
            // Si le clic échoue, utiliser JavaScript pour cliquer
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);
        }

        // Attendre que le nombre d'articles dans le panier soit mis à jour
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("span.cart-number"), String.valueOf(initialCartCount + 1)));

        // Re-locate the cart count element to avoid StaleElementReferenceException
        WebElement updatedCartCountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("span.cart-number")));
        String updatedCartCountText = updatedCartCountElement.getText();
        int updatedCartCount = updatedCartCountText.isEmpty() ? 0 : Integer.parseInt(updatedCartCountText);
        Assert.assertEquals(updatedCartCount, initialCartCount + 1, "Le nombre d'articles dans le panier n'a pas été mis à jour");

        // Aller à la page du panier
        driver.get(baseUrl + "cart-2/");

        // Vérifier que l'article est dans le panier
        WebElement cartItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("td.product-name a[href*='batterie-rechargeable-usb-prumyls']")));
        Assert.assertTrue(cartItem.isDisplayed(), "L'article n'a pas été ajouté au panier");

        // Supprimer l'article
        WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("td.product-remove a.remove[href*='remove_item'][data-product_id='10303']")));
        removeButton.click();

        // Attendre que le panier soit mis à jour
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("tr.cart_item")));

        // Vérifier que le panier est vide
        boolean isCartEmpty = driver.findElements(By.cssSelector("tr.cart_item")).isEmpty();
        Assert.assertTrue(isCartEmpty, "L'article n'a pas été supprimé du panier");

        // Vérifier le message "Panier vide"
        WebElement emptyCartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.cart-empty")));
        Assert.assertTrue(emptyCartMessage.isDisplayed(), "Le message 'Panier vide' n'est pas affiché");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}