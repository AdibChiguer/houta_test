package com.test.houta;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class NavigationTest extends ChromeTestBase {

        @Test(groups = { "navigation", "menu-functionality",
                        "user-experience" }, description = "Test de navigation via 'Tous les catégories' vers 'Casque & écouteur'")
        public void testNavigationVersCasqueEtEcouteur() {
                // 1. Cliquer sur "Tous les catégories"
                WebElement tousLesCategoriesBtn = wait.until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("a#main-cat")));
                tousLesCategoriesBtn.click();

                // 2. Cliquer sur "Tv - Son - Photo"
                WebElement tvSonPhotoMenu = wait.until(ExpectedConditions.elementToBeClickable(
                                By.id("ui-id-152")));
                tvSonPhotoMenu.click();

                // 3. Cliquer sur "Audio & Hifi - Musique"
                WebElement audioHifiMusiqueMenu = wait.until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//span[contains(text(), 'Audio & Hifi - Musique')]")));
                audioHifiMusiqueMenu.click();

                // 4. Cliquer sur "Casque & écouteur"
                WebElement casqueEcouteurLink = wait.until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//span[contains(text(), 'Casque & écouteur')]")));
                casqueEcouteurLink.click();

                // Vérifier que l'URL contient "casque-ecouteur"
                String currentUrl = driver.getCurrentUrl();
                Assert.assertTrue(currentUrl.contains("casque-ecouteur"),
                                "La redirection vers la catégorie 'Casque & écouteur' a échoué");

                // Vérifier que le titre de la page contient "Casque & écouteur"
                assertEquals("Casques & Écouteurs - Plongez dans un Son Immersif | marjanemall Maroc", driver.getTitle());
        }

        @AfterClass
        public void tearDown() {
                if (driver != null) {
                        driver.quit();
                }
        }
}