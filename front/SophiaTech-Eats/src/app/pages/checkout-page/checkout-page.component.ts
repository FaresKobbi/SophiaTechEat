import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService, CartItem } from '../../services/cart/cart.service';
import { RestaurantService, OpeningHours, TimeSlot } from '../../services/restaurant/restaurant.service';
import { OrderService } from '../../services/order/order.service';
import { StudentAccountService, DeliveryLocation } from '../../services/student/student-account-service.service';

@Component({
    selector: 'app-checkout-page',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './checkout-page.component.html',
    styleUrls: ['./checkout-page.component.css']
})
export class CheckoutPageComponent implements OnInit {
    cartItems: CartItem[] = [];
    totalAmount: number = 0;
    restaurantId: string | null = null;

    openingHours: OpeningHours[] = [];
    availableSlots: { day: string, slot: TimeSlot }[] = [];
    selectedSlot: { day: string, slot: TimeSlot } | null = null;

    deliveryLocations: DeliveryLocation[] = [];
    selectedLocation: DeliveryLocation | null = null;

    // UPDATED: Default to 'EXTERNAL' to match backend Enum
    paymentMethod: string = 'EXTERNAL';

    constructor(
        private cartService: CartService,
        private restaurantService: RestaurantService,
        private orderService: OrderService,
        private studentService: StudentAccountService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.cartService.cart$.subscribe(items => this.cartItems = items);
        this.cartService.total$.subscribe(total => this.totalAmount = total);
        this.restaurantId = this.cartService.getRestaurantId();

        if (this.restaurantId) {
            this.loadOpeningHours(this.restaurantId);
        } else {
            this.router.navigate(['/student/homepage']);
        }

        const student = this.studentService.getSelectedStudent();
        if (student) {
            this.studentService.getDeliveryLocations(student.studentID).subscribe(locations => {
                this.deliveryLocations = locations;
                if (this.deliveryLocations.length > 0) {
                    this.selectedLocation = this.deliveryLocations[0];
                }
            });
        }
    }

    loadOpeningHours(restaurantId: string): void {
        this.restaurantService.getOpeningHours(restaurantId).subscribe(hours => {
            this.openingHours = hours;
            this.processAvailableSlots();
        });
    }

    processAvailableSlots(): void {
        this.availableSlots = [];
        this.openingHours.forEach(oh => {
            oh.slots.forEach(slot => {
                if (slot.capacity > 0) {
                    this.availableSlots.push({ day: oh.day, slot: slot });
                }
            });
        });
    }

    placeOrder(): void {
        if (!this.selectedSlot || !this.restaurantId || !this.selectedLocation) return;

        const student = this.studentService.getSelectedStudent();
        if (!student) {
            alert('No student selected!');
            return;
        }

        const orderPayload = {
            dishes: this.cartItems.map(item => item.dish),
            studentId: student.studentID,
            deliveryLocation: this.selectedLocation,
            restaurantId: this.restaurantId,
            timeSlot: {
                day: this.selectedSlot.day,
                startTime: this.selectedSlot.slot.startTime,
                endTime: this.selectedSlot.slot.endTime
            },
            paymentMethod: this.paymentMethod
        };

        this.orderService.createOrder(orderPayload).subscribe({
            next: (res) => {
                alert('Order placed successfully!');
                this.cartService.clearCart();
                this.router.navigate(['/student/orders']);
            },
            error: (err) => {
                console.error(err);
                // Show specific error from backend if available
                alert('Failed to place order: ' + (err.error?.error || 'Unknown error'));
            }
        });
    }
}