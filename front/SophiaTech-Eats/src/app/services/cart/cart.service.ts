import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Dish, Topping } from '../restaurant/restaurant.service';

export interface CartItem {
  dish: Dish;
  selectedToppings: Topping[];
  quantity: number;
  totalPrice: number; // (Base Price + Toppings) * Quantity
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItems: CartItem[] = [];
  private restaurantId: string | null = null;

  private cartSubject = new BehaviorSubject<CartItem[]>([]);
  public cart$ = this.cartSubject.asObservable();

  private totalSubject = new BehaviorSubject<number>(0);
  public total$ = this.totalSubject.asObservable();

  constructor() {}

  addToCart(dish: Dish, toppings: Topping[], restaurantId: string): boolean {
    if (this.restaurantId && this.restaurantId !== restaurantId) {
      return false;
    }

    this.restaurantId = restaurantId;

    const toppingsCost = toppings.reduce((sum, t) => sum + t.price, 0);
    const unitPrice = dish.price + toppingsCost;

    const newItem: CartItem = {
      dish: dish,
      selectedToppings: toppings,
      quantity: 1,
      totalPrice: unitPrice
    };

    this.cartItems.push(newItem);
    this.updateState();
    return true;
  }

  removeFromCart(index: number): void {
    this.cartItems.splice(index, 1);
    if (this.cartItems.length === 0) {
      this.restaurantId = null;
    }
    this.updateState();
  }

  clearCart(): void {
    this.cartItems = [];
    this.restaurantId = null;
    this.updateState();
  }

  getCartItems(): CartItem[] {
    return this.cartItems;
  }
  getRestaurantId(): string | null {
    return this.restaurantId;
  }

  private updateState(): void {
    this.cartSubject.next([...this.cartItems]);
    const total = this.cartItems.reduce((sum, item) => sum + item.totalPrice, 0);
    this.totalSubject.next(total);
  }
}
