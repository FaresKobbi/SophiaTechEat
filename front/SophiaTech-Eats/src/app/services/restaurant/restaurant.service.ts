import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {BehaviorSubject, Observable, of, tap} from 'rxjs';
import {StudentAccount} from '../student/student-account-service.service';

export interface Restaurant {
  restaurantName: string;
}

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private apiUrl = 'http://localhost:8080/api/restaurants';
  private cache: Restaurant[] | null = null;
  private restaurantsSubject = new BehaviorSubject<Restaurant[]>([])
  public restaurants$ = this.restaurantsSubject.asObservable();
  constructor(private http: HttpClient) {}

  getRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(this.apiUrl).pipe(
        tap((data) => this.restaurantsSubject.next(data))
      );
    }


  createRestaurant(data: {restaurantName : string}){
    return this.http.post<Restaurant>(this.apiUrl, data).pipe(
      tap(() => {
        this.getRestaurants();
      })
    );
  }
}
