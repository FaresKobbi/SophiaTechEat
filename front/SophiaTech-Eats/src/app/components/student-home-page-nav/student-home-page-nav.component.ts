import {Component, Input} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {NgForOf} from '@angular/common';
import {CartService} from '../../services/cart/cart.service';

@Component({
  selector: 'app-student-home-page-nav',
  imports: [
    RouterLink,
    RouterLinkActive,
    NgForOf
  ],
  templateUrl: './student-home-page-nav.component.html',
  styleUrl: './student-home-page-nav.component.css'
})
export class StudentHomePageNavComponent {
  navLinks = [
    { label: 'Home', path: '/student/homepage' },
    { label: 'My Account', path: '/student/account' },
    { label: 'My Orders', path: '/student/orders' }
  ];

  @Input() studentName = "X"
  @Input() studentSurname = "Y"

  cartItemCount: number = 0;

  constructor(
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cartService.cart$.subscribe(items => {
      this.cartItemCount = items.reduce((acc, item) => acc + item.quantity, 0);
    });
  }

  goToCart(): void {
    const restaurantId = this.cartService.getRestaurantId();
    if (restaurantId) {
      this.router.navigate(['/student/restaurant', restaurantId, 'menu']);
    } else {
      alert('Your cart is empty. Please select a restaurant to start ordering.');
    }
  }
}
