package com.test.houta;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

@Epic("Website Navigation")
@Feature("Menu Navigation")
@Owner("Ayoub")
public class NavigationTest extends ChromeTestBase {

        @Test(groups = { "navigation", "menu-functionality",
                        "user-experience" }, description = "Test de navigation par survol du menu principal vers la sous-catégorie Xiaomi avec validation de l'URL et du titre")
        public void testCategoryNavigation() {
                // Survoler le menu "Smartphone" pour rendre les sous-catégories visibles
                WebElement smartphoneMenu = wait.until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("li#menu-item-7445 a[href='https://haoutastore.com/product-category/smartphone/']")));
                new Actions(driver).moveToElement(smartphoneMenu).perform();

                // Attendre que le sous-menu soit visible (optionnel, dépend du comportement du
                // site)
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
                Assert.assertTrue(categoryTitle.isDisplayed(),
                                "La page de catégorie Xiaomi ne s'affiche pas correctement");
                Assert.assertTrue(categoryTitle.getText().contains("Xiaomi"),
                                "Le titre de la catégorie Xiaomi est incorrect");
        }

        @Test(groups = { "navigation", "error-handling",
                        "negative-testing" }, description = "Test de navigation vers une categorie inexistante pour vérifier la gestion d'erreur 404")
        public void testNonExistentPageNavigation() {
                // Naviguer vers une page inexistante
                String nonExistentUrl = baseUrl + "product-category/xxxxxxxxxxxx/";
                driver.get(nonExistentUrl);

                // Vérifier que l'URL actuelle correspond à celle demandée
                String currentUrl = driver.getCurrentUrl();
                Assert.assertTrue(currentUrl.contains("xxxxxxxxxxxx"),
                                "L'URL de la page inexistante ne correspond pas à celle demandée");

                // Vérifier la présence d'une page d'erreur 404 ou d'un message d'erreur
                boolean hasErrorIndicator = false;

                // Option 1: Vérifier le titre de la page pour les mots-clés d'erreur
                String pageTitle = driver.getTitle().toLowerCase();
                if (pageTitle.contains("404") || pageTitle.contains("not found") ||
                                pageTitle.contains("page not found") || pageTitle.contains("erreur")) {
                        hasErrorIndicator = true;
                }

                // Option 2: Chercher des éléments d'erreur communs
                if (!hasErrorIndicator) {
                        try {
                                WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                                                By.xpath("//*[contains(text(), '404') or contains(text(), 'not found') or "
                                                                +
                                                                "contains(text(), 'Not Found') or contains(text(), 'page introuvable') or "
                                                                +
                                                                "contains(text(), 'Page non trouvée')]")));
                                hasErrorIndicator = errorMessage.isDisplayed();
                        } catch (Exception e) {
                                // Continuer si aucun élément d'erreur n'est trouvé
                        }
                }

                // Option 3: Vérifier les classes CSS communes pour les pages d'erreur
                if (!hasErrorIndicator) {
                        try {
                                WebElement errorPage = driver.findElement(
                                                By.cssSelector(".error-404, .not-found, .page-404, .error-page"));
                                hasErrorIndicator = errorPage.isDisplayed();
                        } catch (Exception e) {
                                // Continuer si aucune classe d'erreur n'est trouvée
                        }
                }

                // Option 4: Vérifier le contenu du body pour des indicateurs d'erreur
                if (!hasErrorIndicator) {
                        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
                        hasErrorIndicator = bodyText.contains("404") || bodyText.contains("not found") ||
                                        bodyText.contains("page introuvable") || bodyText.contains("erreur");
                }

                Assert.assertTrue(hasErrorIndicator,
                                "La page devrait afficher une erreur 404 ou un message d'erreur approprié pour une URL inexistante");

        }

        @AfterMethod
        public void tearDown() {
                if (driver != null) {
                        driver.quit();
                }
        }
}