package fr.unice.polytech.stepDefs.back;
import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderManager;
import fr.unice.polytech.orderManagement.OrderStatus;
import fr.unice.polytech.paymentProcessing.*;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.TimeSlot;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalTime;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class e2eWorkflowSteps {
    private StudentAccount currentStudent;
    private Restaurant currentRestaurant;
    private DeliveryLocation currentDeliveryLocation;
    private TimeSlot currentTimeSlot;
    private final Map<String, Dish> availableDishes = new HashMap<>();
    private final List<Dish> selectedDishes = new ArrayList<>();
    private Order currentOrder;
    private double initialBalance;

    private IPaymentService mockPaymentService;
    private PaymentProcessorFactory paymentProcessorFactory;
    private OrderManager orderManager;


    @Before
    public void setupScenario() throws Exception {
        // Reset state before each scenario
        currentStudent = null;
        currentRestaurant = null;
        currentDeliveryLocation = null;
        currentTimeSlot = null;
        currentOrder = null;
        availableDishes.clear();
        selectedDishes.clear();

        mockPaymentService = mock(IPaymentService.class);
        paymentProcessorFactory = new PaymentProcessorFactory(mockPaymentService);
        orderManager = new OrderManager(paymentProcessorFactory);


    }

    @Given("a student named {string} with ID {string} and email {string} exists")
    public void a_student_named_with_id_and_email_exists(String nameSurname, String studentId, String email) {
        String[] names = nameSurname.split(" ", 2);
        currentStudent = new StudentAccount.Builder(names[0], names[1])
                .studentId(studentId)
                .email(email)
                .build();
    }

    @Given("the student has a saved delivery location {string} at {string}")
    public void the_student_has_a_saved_delivery_location_at(String locName, String addressDetails) {
        assertNotNull(currentStudent, "Student context should be set");
        String[] parts = addressDetails.split(", ");
        if (parts.length < 3) fail("Address details format is incorrect: " + addressDetails);
        currentDeliveryLocation = new DeliveryLocation(locName, parts[0], parts[1], parts[2]);
        currentStudent.addDeliveryLocation(currentDeliveryLocation);
    }

    @Given("the student has bank info {string}, CVV {int}, expiring {string}")
    public void the_student_has_bank_info_cvv_expiring(String cardNumber, Integer cvv, String expiryDate) {
        assertNotNull(currentStudent, "Student context should be set");
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        currentStudent = new StudentAccount.Builder(currentStudent.getName(), currentStudent.getSurname())
                .studentId(currentStudent.getStudentID())
                .email(currentStudent.getEmail())
                .balance(currentStudent.getBalance())
                .deliveryLocations(currentStudent.getDeliveryLocations())
                .bankInfo(cardNumber, cvv, month, 2000 + year) // Assuming year is YY format
                .build();
    }

    @Given("the student has an internal balance of {double} euros")
    public void the_student_has_an_internal_balance_of_euros(Double balance) {
        assertNotNull(currentStudent, "Student context should be set");
        initialBalance = balance;
        currentStudent = new StudentAccount.Builder(currentStudent.getName(), currentStudent.getSurname())
                .studentId(currentStudent.getStudentID())
                .email(currentStudent.getEmail())
                .bankInfo(
                        currentStudent.getBankInfo().getCardNumber(),
                        currentStudent.getBankInfo().getCVV(),
                        currentStudent.getBankInfo().getExpirationDate().getMonthValue(),
                        currentStudent.getBankInfo().getExpirationDate().getYear()
                )
                .deliveryLocations(currentStudent.getDeliveryLocations())
                .balance(balance)
                .build();
    }

    @Given("a restaurant named {string} exists")
    public void a_restaurant_named_exists(String restaurantName) {
        currentRestaurant = new Restaurant(restaurantName);
    }

    @Given("{string} offers the following dishes:")
    public void offers_the_following_dishes(String restaurantName, DataTable dataTable) {
        assertNotNull(currentRestaurant, "Restaurant context should be set");
        assertEquals(restaurantName, currentRestaurant.getRestaurantName());
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String name = row.get("name");
            String description = row.get("description");
            double price = Double.parseDouble(row.get("price"));
            Dish dish = new Dish(name, description, price);
            currentRestaurant.addDish(dish);
            availableDishes.put(name, dish);
        }
    }

    @Given("{string} has an available time slot {string} with capacity {int}")
    public void has_an_available_time_slot_with_capacity(String restaurantName, String timeRange, Integer capacity) {
        assertNotNull(currentRestaurant, "Restaurant context should be set");
        String[] times = timeRange.split("-");
        currentTimeSlot = new TimeSlot(LocalTime.parse(times[0]), LocalTime.parse(times[1]));
        currentRestaurant.setCapacity(currentTimeSlot, capacity);
        assertTrue(currentRestaurant.getAvailableTimeSlots().contains(currentTimeSlot), "Time slot should be initially available");
    }

    @Given("{word} has selected the following items from {string}:")
    public void user_has_selected_following_items(String userName, String restaurantName, DataTable dataTable) {
        assertNotNull(currentStudent, "Student context must be set");
        assertNotNull(currentRestaurant, "Restaurant context must be set");

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String itemName = row.get("item");
            int quantity = Integer.parseInt(row.get("quantity"));
            Dish dish = availableDishes.get(itemName);
            assertNotNull(dish, "Dish " + itemName + " should exist in the available dishes map for restaurant " + restaurantName);
            for (int i = 0; i < quantity; i++) {
                selectedDishes.add(dish);
            }
        }
        assertFalse(selectedDishes.isEmpty(), "Selected dishes list should not be empty");
    }

    @When("{word} creates an order for {string} with delivery to {string}")
    public void user_creates_an_order(String userName, String restaurantName, String locationName) throws IllegalAccessException {
        assertNotNull(currentStudent, "Student context must be set");
        assertNotNull(currentRestaurant, "Restaurant context must be set");
        assertNotNull(currentDeliveryLocation, "Delivery location context must be set");
        assertTrue(currentStudent.hasDeliveryLocation(currentDeliveryLocation), "Student must have the specified delivery location saved");
        assertFalse(selectedDishes.isEmpty(), "Cannot create an order with empty selection");

        orderManager.createOrder(selectedDishes, currentStudent, currentDeliveryLocation, currentRestaurant);

        List<Order> pendingOrders =orderManager.getPendingOrders();
        Optional<Order> foundOrder = pendingOrders.stream()
                .filter(o -> o.getStudentAccount().equals(currentStudent))
                .findFirst();
        assertTrue(foundOrder.isPresent(), "Order should be found in pending orders after creation");
        currentOrder = foundOrder.get();
    }

    @When("the external payment system approves the payment on the first attempt")
    public void external_payment_approves_first_attempt() {
        assertNotNull(currentOrder, "Order must exist for payment approval simulation");
        when(mockPaymentService.processExternalPayment(any(Order.class))).thenReturn(true);
    }

    @When("the external payment system rejects the payment on all attempts")
    public void external_payment_rejects_on_all_attempts() {
        assertNotNull(currentOrder, "Order must exist for payment rejection simulation");
        when(mockPaymentService.processExternalPayment(any(Order.class))).thenReturn(false);
    }

    @When("{word} initiates the payment for the order using {word} method")
    public void user_initiates_payment(String userName, String paymentMethodStr) {
        assertNotNull(currentOrder, "Order must exist before initiating payment");
        assertEquals(OrderStatus.PENDING, currentOrder.getOrderStatus(), "Order must be pending before initiating payment");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr.toUpperCase());
        orderManager.initiatePayment(currentOrder, paymentMethod);
    }

    @Then("a validated order should exist for {word} with total amount {double}")
    public void a_validated_order_should_exist(String userName, Double expectedAmount) {
        assertNotNull(currentOrder, "A pending order for the student should exist");
        assertEquals(OrderStatus.VALIDATED, currentOrder.getOrderStatus(), "Order status should initially be PENDING");
        assertEquals(expectedAmount, currentOrder.getAmount(), 0.01, "Order amount should match calculated total");
        assertEquals(currentRestaurant, currentOrder.getRestaurant(), "Order restaurant should match");
        assertEquals(currentDeliveryLocation, currentOrder.getDeliveryLocation(), "Order delivery location should match");
        assertEquals(selectedDishes.size(), currentOrder.getDishes().size(), "Number of dishes in order should match selection");
    }

    @Then("the order status should become {word}")
    public void order_status_should_become(String expectedStatusStr) {
        assertNotNull(currentOrder, "Current order should not be null");
        OrderStatus expectedStatus = OrderStatus.valueOf(expectedStatusStr.toUpperCase());
        assertEquals(expectedStatus, currentOrder.getOrderStatus());
    }

    @Then("the order should be registered successfully with {string}")
    public void order_should_be_registered(String restaurantName) throws IllegalAccessException {
        assertNotNull(currentOrder, "Order context must be set");
        assertEquals(OrderStatus.VALIDATED, currentOrder.getOrderStatus(), "Order must be VALIDATED to be registered");
        boolean registered = orderManager.registerOrder(currentOrder, currentRestaurant);
        assertTrue(registered, "Order registration should return true for a validated order");

        List<Order> pendingOrders = orderManager.getPendingOrders();
        assertFalse(pendingOrders.contains(currentOrder), "Order should be removed from pending list after registration");

        List<Order> registeredOrders = orderManager.getRegisteredOrders();
        assertTrue(registeredOrders.contains(currentOrder), "Order should be added to registered list after registration");
    }

    @Then("the payment should be debited from {word}'s balance")
    public void payment_should_be_debited(String userName) {
        assertNotNull(currentOrder, "Order context must be set");
        assertEquals(OrderStatus.VALIDATED, currentOrder.getOrderStatus(), "Order must be validated if debit was successful");
        assertTrue(currentStudent.getBalance() < initialBalance, "Balance should have decreased");
    }

    @Then("{word}'s balance should be {double} euros")
    public void user_balance_should_be(String userName, Double expectedBalance) {
        assertEquals(expectedBalance, currentStudent.getBalance(), 0.01, "Student balance should be the expected value");
    }

    @Then("the payment attempt should fail due to insufficient balance")
    public void payment_attempt_should_fail_insufficient_balance() {
        assertNotNull(currentOrder, "Order context must be set");
        assertEquals(OrderStatus.CANCELED, currentOrder.getOrderStatus(), "Order status should be CANCELED after failed internal payment");
    }

    @Then("the payment attempt should fail due to external decline")
    public void payment_attempt_should_fail_due_to_external_decline() {
        assertNotNull(currentOrder, "Order context must be set");
        assertEquals(OrderStatus.CANCELED, currentOrder.getOrderStatus(), "Order status should be CANCELED after failed external payment");
    }

    @Then("{word}'s balance should remain {double} euros")
    public void user_balance_should_remain(String userName, Double expectedBalance) {
        assertEquals(expectedBalance, currentStudent.getBalance(), 0.01, "Balance should not have changed from initial");
    }

    @Then("the order should not be registered with {string}")
    public void order_should_not_be_registered(String restaurantName) throws IllegalAccessException {
        assertNotNull(currentOrder, "Order context must be set");
        assertNotEquals(OrderStatus.VALIDATED, currentOrder.getOrderStatus(), "Order status should not be VALIDATED");

        boolean registered = orderManager.registerOrder(currentOrder, currentRestaurant);
        assertFalse(registered, "Order registration should return false for non-validated orders");

        List<Order> registeredOrders = orderManager.getRegisteredOrders();
        assertFalse(registeredOrders.contains(currentOrder), "Order should not be in the registered list");

        List<Order> pendingOrders = orderManager.getPendingOrders();
        assertFalse(pendingOrders.contains(currentOrder), "Canceled order should be removed from pending list");
    }

}
