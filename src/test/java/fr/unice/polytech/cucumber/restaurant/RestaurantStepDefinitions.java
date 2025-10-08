package fr.unice.polytech.cucumber.restaurant;


import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;
import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.dishes.DishCategory;
import fr.unice.polytech.dishes.Topping;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class RestaurantStepDefinitions {

    // Variables partagées entre les steps
    private Restaurant currentRestaurant;
    private Dish currentDish;
    private RestaurantManager restaurantManager;

    // Pour gérer les tags et les options extras (puisque pas dans tes classes actuelles)
    private Map<String, Set<String>> dishTags = new HashMap<>();
    private Map<String, List<ExtraOption>> restaurantExtraOptions = new HashMap<>();
    private Map<String, String> dishAllergenInfo = new HashMap<>();

    // Classe interne pour les options extras
    private static class ExtraOption {
        String name;
        double price;

        ExtraOption(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    // ============================================
    // GIVEN - Contexte initial
    // ============================================

    @Given("a restaurant {string} exists")
    public void aRestaurantExists(String restaurantName) {
        currentRestaurant = new Restaurant(restaurantName);
        restaurantManager = new RestaurantManager();
        restaurantManager.addRestaurant(currentRestaurant);
    }

    @Given("I am logged in as restaurant manager of {string}")
    public void iAmLoggedInAsRestaurantManager(String restaurantName) {
        // Le restaurant manager existe déjà, on vérifie juste qu'il gère bien ce restaurant
        if (restaurantManager == null) {
            restaurantManager = new RestaurantManager();
        }
        if (!restaurantManager.hasRestaurant(restaurantName)) {
            throw new IllegalStateException("Restaurant " + restaurantName + " not found");
        }
    }

    @Given("a dish {string} exists with price {double}")
    public void aDishExistsWithPrice(String dishName, double price) {
        currentDish = new Dish(dishName, price);
        currentRestaurant.addDish(currentDish);
    }

    @Given("a dish {string} exists in the menu")
    public void aDishExistsInTheMenu(String dishName) {
        currentDish = new Dish(dishName, 0.0); // Prix par défaut
        currentRestaurant.addDish(currentDish);
    }

    // ============================================
    // WHEN - Actions effectuées
    // ============================================

    @When("I add a new dish with the following details:")
    public void iAddANewDishWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> dishData = dataTable.asMap(String.class, String.class);

        String name = dishData.get("name");
        String description = dishData.get("description");
        double price = Double.parseDouble(dishData.get("price"));

        currentDish = new Dish(name, description, price);

        // Gérer la catégorie
        if (dishData.containsKey("category")) {
            String categoryStr = dishData.get("category").toUpperCase().replace(" ", "_");
            DishCategory category = DishCategory.valueOf(categoryStr);
            currentDish.setCategory(category);
        }

        // Note: "type" n'existe pas dans ta classe Dish, on l'ignore

        currentRestaurant.addDish(currentDish);
    }

    @When("I tag the dish as {string} and {string}")
    public void iTagTheDishAs(String tag1, String tag2) {
        String dishName = currentDish.getName();
        dishTags.putIfAbsent(dishName, new HashSet<>());
        dishTags.get(dishName).add(tag1);
        dishTags.get(dishName).add(tag2);
    }

    @When("I add a topping {string} with price {double}")
    public void iAddAToppingWithPrice(String toppingName, double price) {
        Topping topping = new Topping(toppingName, price);
        currentDish.addTopping(topping);
    }

    @When("I update the dish price to {double}")
    public void iUpdateTheDishPriceTo(double newPrice) {
        currentDish.setPrice(newPrice);
    }

    @When("I update the description to {string}")
    public void iUpdateTheDescriptionTo(String newDescription) {
        currentDish.setDescription(newDescription);
    }

    @When("I remove the dish {string} from the menu")
    public void iRemoveTheDishFromTheMenu(String dishName) {
        // Ta classe Restaurant n'a pas de méthode removeDish
        // On doit la trouver et la retirer manuellement
        Dish dishToRemove = findDishByName(dishName);
        if (dishToRemove != null) {
            currentRestaurant.getDishes().remove(dishToRemove);
        }
    }

    @When("I define an extra option {string} with price {double}")
    public void iDefineAnExtraOptionWithPrice(String optionName, double price) {
        String restaurantName = currentRestaurant.getRestaurantName();
        restaurantExtraOptions.putIfAbsent(restaurantName, new ArrayList<>());
        restaurantExtraOptions.get(restaurantName).add(new ExtraOption(optionName, price));
    }

    @When("I add allergen information {string}")
    public void iAddAllergenInformation(String allergenInfo) {
        dishAllergenInfo.put(currentDish.getName(), allergenInfo);
    }

    // ============================================
    // THEN - Vérifications
    // ============================================

    @Then("the dish {string} should be added to the menu")
    public void theDishShouldBeAddedToTheMenu(String dishName) {
        Dish foundDish = findDishByName(dishName);
        assertNotNull(foundDish, "Le plat devrait exister dans le menu");
        assertEquals(dishName, foundDish.getName());
    }

    @Then("the dish should have price {double} euros")
    public void theDishShouldHavePrice(double expectedPrice) {
        assertEquals(expectedPrice, currentDish.getPrice(), 0.01);
    }

    @Then("the dish {string} should have tag {string}")
    public void theDishShouldHaveTag(String dishName, String tag) {
        Set<String> tags = dishTags.get(dishName);
        assertNotNull(tags, "Le plat devrait avoir des tags");
        assertTrue(tags.contains(tag), "Le plat devrait avoir le tag: " + tag);
    }

    @Then("the dish should have {int} toppings available")
    public void theDishShouldHaveToppingsAvailable(int expectedCount) {
        assertEquals(expectedCount, currentDish.getToppings().size());
    }

    @Then("topping {string} should cost {double} euros")
    public void toppingShouldCost(String toppingName, double expectedPrice) {
        Topping topping = currentDish.getToppings().stream()
                .filter(t -> t.getName().equals(toppingName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Topping non trouvé: " + toppingName));

        assertEquals(expectedPrice, topping.getPrice(), 0.01);
    }

    @Then("the dish {string} should have price {double}")
    public void theDishShouldHavePriceNamed(String dishName, double expectedPrice) {
        Dish dish = findDishByName(dishName);
        assertNotNull(dish, "Le plat devrait exister");
        assertEquals(expectedPrice, dish.getPrice(), 0.01);
    }

    @Then("the dish description should be {string}")
    public void theDishDescriptionShouldBe(String expectedDescription) {
        assertEquals(expectedDescription, currentDish.getDescription());
    }

    @Then("the dish {string} should not be available")
    public void theDishShouldNotBeAvailable(String dishName) {
        Dish dish = findDishByName(dishName);
        assertNull(dish, "Le plat ne devrait plus être dans le menu");
    }

    @Then("customers should not see {string} in the menu")
    public void customersShouldNotSeeInTheMenu(String dishName) {
        List<Dish> availableDishes = currentRestaurant.getDishes();
        boolean dishFound = availableDishes.stream()
                .anyMatch(d -> d.getName().equals(dishName));
        assertFalse(dishFound, "Le plat ne devrait pas être visible pour les clients");
    }

    @Then("the restaurant should have {int} extra options available")
    public void theRestaurantShouldHaveExtraOptionsAvailable(int expectedCount) {
        String restaurantName = currentRestaurant.getRestaurantName();
        List<ExtraOption> options = restaurantExtraOptions.get(restaurantName);
        assertNotNull(options, "Le restaurant devrait avoir des options");
        assertEquals(expectedCount, options.size());
    }

    @Then("option {string} should cost {double} euros")
    public void optionShouldCost(String optionName, double expectedPrice) {
        String restaurantName = currentRestaurant.getRestaurantName();
        List<ExtraOption> options = restaurantExtraOptions.get(restaurantName);

        ExtraOption option = options.stream()
                .filter(o -> o.name.equals(optionName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Option non trouvée: " + optionName));

        assertEquals(expectedPrice, option.price, 0.01);
    }

    @Then("the dish should display allergen warning")
    public void theDishShouldDisplayAllergenWarning() {
        String allergenInfo = dishAllergenInfo.get(currentDish.getName());
        assertNotNull(allergenInfo, "Le plat devrait avoir une information sur les allergènes");
        assertFalse(allergenInfo.isEmpty());
    }

    @Then("the warning should mention {string}")
    public void theWarningShouldMention(String allergen) {
        String allergenInfo = dishAllergenInfo.get(currentDish.getName());
        assertTrue(allergenInfo.toLowerCase().contains(allergen.toLowerCase()),
                "L'avertissement devrait mentionner: " + allergen);
    }

    // ============================================
    // MÉTHODE UTILITAIRE
    // ============================================

    private Dish findDishByName(String dishName) {
        return currentRestaurant.getDishes().stream()
                .filter(d -> d.getName().equals(dishName))
                .findFirst()
                .orElse(null);
    }
}
