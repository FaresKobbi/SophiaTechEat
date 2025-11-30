import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Dish } from '../restaurant/restaurant.service';

//TODO REMOVE THIS INTERFACE WHEN THE STUDENT-SERVICE IS DONE
export interface DeliveryLocation {
  name: string;
  address: string;
  city: string;
  zipCode: string;
}



export interface Order {
  orderId: string;
  studentName: string;
  amount: number;
  orderStatus: string;
  dishes: Dish[];
  deliveryLocation: DeliveryLocation;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) { }

  createOrder(orderPayload: any): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, orderPayload);
  }

  getOrdersByRestaurant(restaurantId: string): Observable<Order[]> {
    const url = `${this.apiUrl}/restaurant/${restaurantId}`;
    console.log(`Fetching orders from: ${url}`);
    return this.http.get<Order[]>(url);
  }
}
