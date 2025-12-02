import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { RestaurantService, Dish, Topping } from '../../services/restaurant/restaurant.service';
import { CartService, CartItem } from '../../services/cart/cart.service';
import { DishDetailModalComponent } from '../../components/dish-detail-modal/dish-detail-modal.component';
import { StudentAccountService } from '../../services/student/student-account-service.service';

@Component({
  selector: 'app-restaurant-menu-page',
  standalone: true,
  
  imports: [CommonModule, RouterLink, DishDetailModalComponent],
  templateUrl: './restaurant-menu-page.component.html',
  styleUrls: ['./restaurant-menu-page.component.css']
})
export class RestaurantMenuPageComponent implements OnInit {
  restaurantId: string = '';
  menuDishes: Dish[] = [];

  
  studentName: string = 'Student';
  studentSurname: string = '';

  
  cartItems: CartItem[] = [];
  cartTotal: number = 0;

  
  isModalOpen = false;
  selectedDishForModal: Dish | null = null;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private cartService: CartService,
    private studentService: StudentAccountService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.restaurantId = this.route.snapshot.paramMap.get('restaurantId') || '';
    if (this.restaurantId) {
      this.loadMenu();
    }

    
    const student = this.studentService.getSelectedStudent();
    if (student) {
      this.studentName = student.name;
      this.studentSurname = student.surname;
    }

    
    this.cartService.cart$.subscribe(items => this.cartItems = items);
    this.cartService.total$.subscribe(total => this.cartTotal = total);
  }

  loadMenu(): void {
    this.restaurantService.getDishesByRestaurantId(this.restaurantId).subscribe({
      next: (data) => this.menuDishes = data,
      error: (err) => console.error(err)
    });
  }

  openDishModal(dish: Dish): void {
    const currentRestId = this.cartService.getRestaurantId();
    if (currentRestId && currentRestId !== this.restaurantId) {
      if (confirm('Your cart contains items from another restaurant. Clear cart to start a new order?')) {
        this.cartService.clearCart();
      } else {
        return;
      }
    }

    this.selectedDishForModal = dish;
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedDishForModal = null;
  }

  onAddToCartFromModal(event: { dish: Dish, toppings: Topping[] }): void {
    this.cartService.addToCart(event.dish, event.toppings, this.restaurantId);
    this.closeModal();
  }

  removeItem(index: number): void {
    this.cartService.removeFromCart(index);
  }

  proceedToCheckout(): void {
    console.log('Proceeding to checkout...');
    this.router.navigate(['/checkout']);
  }
}
