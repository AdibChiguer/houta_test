package com.test.houta;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class CartRemovalTest extends ChromeTestBase {

    @Test(groups = { "cart-functionality", "e-commerce", "regression" },
          description = "Test de suppression d'un article du panier avec vérification du titre produit")
    public void testRemoveFromCart() {
        // Navigate to the product page
        driver.get(baseUrl + "souris-gaming-mifoy-m10-filaire-rgb-1-35m-usb-aaaql82890");

        // Wait for the product page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product-info-main")));

        // Wait for the product title and verify it
        WebElement productTitleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("h1.page-title")));
        String expectedProductTitle = "Souris Gaming - MIFOY - M10 - Filaire - RGB - 1.35M USB";
        String actualProductTitle = productTitleElement.getText();
        Assert.assertEquals(actualProductTitle, expectedProductTitle,
            "Le titre du produit ne correspond pas à celui attendu");

        // Add product to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button#product-addtocart-button")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartButton);
        addToCartButton.click();

        // Wait for the confirmation modal and close it if present
        try {
            WebElement modalCloseButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.action-close")));
            modalCloseButton.click();
        } catch (Exception e) {
            // Modal not present; proceed
        }

        // Navigate to cart page
        driver.get(baseUrl + "checkout/cart/");

        // Wait for cart page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.cart-container")));

        // Check if the cart is empty
        boolean isCartEmpty = driver.findElements(By.cssSelector("div.cart-empty")).size() > 0;
        Assert.assertFalse(isCartEmpty, "Le panier est vide après l'ajout du produit.");

        // Proceed to remove the item from the cart
        WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a.action-delete")));
        removeButton.click();

        // Wait for the cart to be updated and verify it's empty
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("div.cart-empty")));

        // Verify empty cart message is displayed
        WebElement emptyCartMessage = driver.findElement(By.cssSelector("div.cart-empty"));
        Assert.assertTrue(emptyCartMessage.isDisplayed(), "Le message 'Panier vide' n'est pas affiché");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
