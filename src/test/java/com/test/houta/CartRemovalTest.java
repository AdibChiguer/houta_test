package com.test.houta;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class CartRemovalTest extends ChromeTestBase {

    @Test(groups = { "cart-functionality", "e-commerce", "regression" },
          description = "Test de suppression d'un article du panier avec vérification du titre produit")
    public void testRemoveFromCart() {
        // 1. Open product page
        driver.get(baseUrl + "souris-gaming-mifoy-m10-filaire-rgb-1-35m-usb-aaaql82890");

        // 2. Wait for title and validate
        WebElement productTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.page-title")));
        Assert.assertEquals(productTitle.getText(),
            "Souris Gaming - MIFOY - M10 - Filaire - RGB - 1.35M USB",
            "Le titre du produit ne correspond pas à celui attendu");

        // 3. Click Add to Cart directly if clickable
        WebElement addToCart = wait.until(ExpectedConditions.elementToBeClickable(By.id("product-addtocart-button")));
        addToCart.click();

        // 4. Close confirmation modal if appears within 3s
        wait.withTimeout(java.time.Duration.ofSeconds(3));
        try {
            WebElement closeModal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.action-close")));
            closeModal.click();
        } catch (Exception ignored) {
            // Modal didn’t appear—move on
        }

        // Restore default timeout
        wait.withTimeout(java.time.Duration.ofSeconds(10));

        // 5. Go directly to cart
        driver.get(baseUrl + "checkout/cart/");

        // 6. Assert cart is not empty
        boolean isCartEmpty = driver.findElements(By.cssSelector("div.cart-empty")).size() > 0;
        Assert.assertFalse(isCartEmpty, "Le panier est vide après l'ajout du produit.");

        // 7. Click remove
        WebElement removeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.action-delete")));
        removeBtn.click();

        // 8. Wait for empty cart
        WebElement emptyCart = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.cart-empty")));
        Assert.assertTrue(emptyCart.isDisplayed(), "Le message 'Panier vide' n'est pas affiché");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
