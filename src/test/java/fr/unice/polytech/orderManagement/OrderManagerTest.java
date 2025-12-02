package fr.unice.polytech.orderManagement;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.paymentProcessing.*;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderManagerTest {

    private OrderManager orderManager;
    private StudentAccount mockStudentAccount;
    private Restaurant mockRestaurant;
    private DeliveryLocation mockDeliveryLocation;
    private List<Dish> mockDishes;
    private Dish mockDish1;
    private Dish mockDish2;
    private BankInfo mockBankInfo;

    @BeforeEach
    void setUp() {
        orderManager = new OrderManager(mock(HttpClient.class));
        mockStudentAccount = mock(StudentAccount.class);
        mockRestaurant = mock(Restaurant.class);
        mockDeliveryLocation = mock(DeliveryLocation.class);
        mockBankInfo = mock(BankInfo.class);

        mockDish1 = mock(Dish.class);
        mockDish2 = mock(Dish.class);
        when(mockDish1.getPrice()).thenReturn(15.50);
        when(mockDish2.getPrice()).thenReturn(12.00);

        when(mockBankInfo.getExpirationDate()).thenReturn(YearMonth.from(LocalDate.now().plusYears(2)));
        when(mockBankInfo.getCardNumber()).thenReturn("1234567890123456");
        when(mockBankInfo.getCVV()).thenReturn(123);

        when(mockStudentAccount.getBankInfo()).thenReturn(mockBankInfo);
        when(mockStudentAccount.getStudentID()).thenReturn("student123");
        when(mockRestaurant.getRestaurantId()).thenReturn("rest123");

        mockDishes = Arrays.asList(mockDish1, mockDish2);
    }

    @Test
    void testCreateOrder() throws Exception {
        orderManager.createOrder(mockDishes, "student123", mockDeliveryLocation, "rest123");

        List<Order> pendingOrders = orderManager.getPendingOrders();

        assertEquals(1, pendingOrders.size());
        Order createdOrder = pendingOrders.get(0);
        assertEquals("student123", createdOrder.getStudentId());
        assertEquals(27.50, createdOrder.getAmount());
        assertEquals(mockDishes, createdOrder.getDishes());
        assertEquals(mockDeliveryLocation, createdOrder.getDeliveryLocation());
        assertEquals("rest123", createdOrder.getRestaurantId());
        assertEquals(OrderStatus.PENDING, createdOrder.getOrderStatus());
    }

    @Test
    void testInitiatePaymentInvokesFactoryAndProcessor() throws Exception {
        PaymentProcessorFactory factory = mock(PaymentProcessorFactory.class);
        IPaymentProcessor processor = mock(IPaymentProcessor.class);
        when(factory.createProcessor(any(Order.class), eq(PaymentMethod.EXTERNAL))).thenReturn(processor);
        when(processor.processPayment(any(Order.class))).thenReturn(OrderStatus.VALIDATED);

        OrderManager manager = new OrderManager(factory);

        manager.createOrder(mockDishes, "student123", mockDeliveryLocation, "rest123");

        List<Order> pending = manager.getPendingOrders();
        assertEquals(1, pending.size());
        Order order = pending.get(0);

        manager.initiatePayment(order, PaymentMethod.EXTERNAL);

        verify(factory).createProcessor(order, PaymentMethod.EXTERNAL);
        verify(processor).processPayment(order);
        assertEquals(OrderStatus.VALIDATED, order.getOrderStatus());
        
        assertEquals(0, manager.getPendingOrders().size());
        assertEquals(1, manager.getRegisteredOrders().size());
    }

    @Test
    void testCalculateTotalAmount() {
        orderManager.createOrder(mockDishes, "student123", mockDeliveryLocation, "rest123");

        verify(mockDish1).getPrice();
        verify(mockDish2).getPrice();
    }

    @Test
    void initiatePaymentUsesFactoryAndUpdatesOrderStatus() throws NoSuchFieldException, IllegalAccessException {
        PaymentProcessorFactory factory = mock(PaymentProcessorFactory.class);
        OrderManager managerWithFactory = new OrderManager(factory);

        
        Order order = managerWithFactory.createOrder(mockDishes, "student123", mockDeliveryLocation, "rest123");

        IPaymentProcessor processor = mock(IPaymentProcessor.class);
        when(factory.createProcessor(order, PaymentMethod.EXTERNAL)).thenReturn(processor);
        when(processor.processPayment(order)).thenReturn(OrderStatus.VALIDATED);

        managerWithFactory.initiatePayment(order, PaymentMethod.EXTERNAL);

        verify(factory).createProcessor(order, PaymentMethod.EXTERNAL);
        verify(processor).processPayment(order);
        assertEquals(OrderStatus.VALIDATED, order.getOrderStatus());
    }

    @Test
    void initiatePaymentThrowsWhenPaymentMethodMissing() {
        Order order = orderManager.createOrder(mockDishes, "student123", mockDeliveryLocation, "rest123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderManager.initiatePayment(order, null));

        assertTrue(exception.getMessage().contains("Payment method must be provided"));
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
    }
}