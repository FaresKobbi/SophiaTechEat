import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Order, OrderService } from '../../services/order/order.service';
import {RestaurantService} from '../../services/restaurant/restaurant.service';
import {ListComponent} from '../../components/item-list/item-list.component';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-order-list-page',
  standalone: true,
  imports: [CommonModule, ListComponent, RouterLink],
  templateUrl: './order-list-page.component.html',
  styleUrl: './order-list-page.component.css'
})
export class OrderListPageComponent implements OnInit {
  restaurantId: string = '';
  orders: Order[] = [];
  selectedOrder: Order | null = null;

  listItems: any[] = [];
  isLoading: boolean = true;
  errorMessage: string | null = null;

  constructor(
    private restaurantService:RestaurantService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.restaurantId = this.restaurantService.getSelectedRestaurant()?.restaurantId || '';
    if (this.restaurantId) {
      this.loadOrders();
    } else {
      this.errorMessage = 'No Restaurant ID provided.';
      this.isLoading = false;
    }
  }

  loadOrders(): void {
    this.orderService.getOrdersByRestaurant(this.restaurantId).subscribe({
      next: (data) => {
        this.orders = data;
        this.listItems = data.map(order => ({
          ...order,
          displayLabel: `${order.studentName} - €${order.amount.toFixed(2)}`
        }));
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching orders', err);
        this.errorMessage = 'Failed to load validated orders.';
        this.isLoading = false;
      }
    });
  }

  onOrderSelect(item: any): void {
    this.selectedOrder = item as Order;
  }
}
