import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, of, tap} from 'rxjs';

export interface Restaurant {
  restaurantName: string;
}

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private apiUrl = 'http://localhost:8080/api/restaurants';
  private cache: Restaurant[] | null = null;

  constructor(private http: HttpClient) {}

  getRestaurants(): Observable<Restaurant[]> {
    if (this.cache) {
      return of(this.cache);
    } else {
      return this.http.get<Restaurant[]>(this.apiUrl).pipe(
        tap((data) => this.cache = data)
      );
    }
  }

  clearCache(): void {
    this.cache = null;
  }
}
