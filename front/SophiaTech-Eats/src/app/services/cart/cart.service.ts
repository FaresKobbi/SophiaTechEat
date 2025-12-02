import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Dish, Topping } from '../restaurant/restaurant.service';

export interface CartItem {
  dish: Dish;
  selectedToppings: Topping[];
  quantity: number;
  totalPrice: number;
}


interface UserCartState {
  items: CartItem[];
  restaurantId: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private carts = new Map<string, UserCartState>();
  private currentUserId: string | null = null;

  private cartSubject = new BehaviorSubject<CartItem[]>([]);
  public cart$ = this.cartSubject.asObservable();

  private totalSubject = new BehaviorSubject<number>(0);
  public total$ = this.totalSubject.asObservable();

  constructor() {}


  setCurrentUser(userId: string): void {
    this.currentUserId = userId;

    if (!this.carts.has(userId)) {
      this.carts.set(userId, { items: [], restaurantId: null });
    }

    this.updateState();
  }

  addToCart(dish: Dish, toppings: Topping[], restaurantId: string): boolean {
    if (!this.currentUserId) {
      console.error("No student logged in!");
      return false;
    }

    const userCart = this.carts.get(this.currentUserId)!;

    
    if (userCart.restaurantId && userCart.restaurantId !== restaurantId) {
      return false;
    }

    userCart.restaurantId = restaurantId;

    const toppingsCost = toppings.reduce((sum, t) => sum + t.price, 0);
    const unitPrice = dish.price + toppingsCost;

    const newItem: CartItem = {
      dish: dish,
      selectedToppings: toppings,
      quantity: 1,
      totalPrice: unitPrice
    };

    userCart.items.push(newItem);
    this.updateState();
    return true;
  }

  removeFromCart(index: number): void {
    if (!this.currentUserId) return;
    const userCart = this.carts.get(this.currentUserId)!;

    userCart.items.splice(index, 1);
    if (userCart.items.length === 0) {
      userCart.restaurantId = null;
    }
    this.updateState();
  }

  clearCart(): void {
    if (!this.currentUserId) return;
    const userCart = this.carts.get(this.currentUserId)!;

    userCart.items = [];
    userCart.restaurantId = null;
    this.updateState();
  }

  getRestaurantId(): string | null {
    if (!this.currentUserId) return null;
    return this.carts.get(this.currentUserId)?.restaurantId || null;
  }

  private updateState(): void {
    if (!this.currentUserId) {
      this.cartSubject.next([]);
      this.totalSubject.next(0);
      return;
    }

    const userCart = this.carts.get(this.currentUserId)!;
    this.cartSubject.next([...userCart.items]);
    const total = userCart.items.reduce((sum, item) => sum + item.totalPrice, 0);
    this.totalSubject.next(total);
  }
}
