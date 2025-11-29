import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, map, Observable, of, tap } from 'rxjs';
import { StudentAccount } from '../student/student-account-service.service';

export interface Restaurant {
  restaurantName: string;
  restaurantId: string;

}

export interface Topping {
  name: string;
  price: number;
}

export interface Dish {
  id?: string;
  name: string;
  description: string;
  price: number;
  category?: string;
  dishType?: string;
  dietaryLabels?: string[];
  toppings?: Topping[];
}


export interface TimeSlot {
  startTime: string;
  endTime: string;
  capacity: number;
}

export interface OpeningHoursDTO {
  id: string;
  day: string;
  openingTime: string;
  closingTime: string;
  slots: { [key: string]: number };
}

export interface OpeningHours {
  id: string;
  day: string;
  openingTime: string;
  closingTime: string;
  slots: TimeSlot[];
}

export interface OpeningHourCreation {
  day: string;
  openingTime: string;
  closingTime: string;
}

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private apiUrl = 'http://localhost:8080/api/restaurants';
  private cache: Restaurant[] | null = null;
  private selectedRestaurant: Restaurant | null = null;

  private restaurantsSubject = new BehaviorSubject<Restaurant[]>([])
  public restaurants$ = this.restaurantsSubject.asObservable();

  private dietaryLabelsSubject = new BehaviorSubject<string[]>([]);
  public dietaryLabels$ = this.dietaryLabelsSubject.asObservable();
  private dietaryLoaded = false

  private cuisineTypeSubject = new BehaviorSubject<string[]>([]);
  public cuisineType$ = this.cuisineTypeSubject.asObservable();
  private cuisineTypeLoaded = false

  constructor(private http: HttpClient) {
  }

  getCuisineTypes(): Observable<string[]> {
    if (this.cuisineTypeLoaded) {
      return of(this.cuisineTypeSubject.value);
    }

    // 2. Sinon, on fait l'appel HTTP
    return this.http.get<string[]>(`${this.apiUrl}/dishes/cuisinetypes`).pipe(
      map(data => data.map(label => this.formatLabel(label))),

      tap(formattedData => {
        console.log('Cuisines chargées:', formattedData);
        this.cuisineTypeSubject.next(formattedData);
        this.cuisineTypeLoaded = true;
      })
    );
  }

  getDietaryLabels(): Observable<string[]> {
    if (this.dietaryLoaded) {
      return of(this.dietaryLabelsSubject.value);
    }

    return this.http.get<string[]>(`${this.apiUrl}/dishes/dietarylabels`).pipe(
      map(data => data.map(label => this.formatLabel(label))),
      tap(formattedData => {
        console.log('Labels chargées:', formattedData);
        this.dietaryLabelsSubject.next(formattedData);
        this.dietaryLoaded = true;
      })
    );
  }

  getRestaurants(cuisine?: string, labels: string[] = []): Observable<Restaurant[]> {
    let params = new HttpParams();

    if (cuisine) {
      params = params.set('cuisine', cuisine.toUpperCase());
    }

    if (labels.length > 0) {
      labels.forEach(label => {
        const formattedLabel = label.toUpperCase().replace(/ /g, '_');

        params = params.append('label', formattedLabel);
      });
    }

    return this.http.get<Restaurant[]>(this.apiUrl, { params }).pipe(
      tap((data) => this.restaurantsSubject.next(data))
    );
  }

  setSelectedRestaurant(restaurant: Restaurant): void {
    this.selectedRestaurant = restaurant;
    console.log('Restaurant selected:', restaurant.restaurantName);
  }

  getDishesByRestaurantId(restaurantId: string): Observable<Dish[]> {
    const url = `${this.apiUrl}/${restaurantId}/dishes`;
    console.log(`Fetching dishes from: ${url}`);
    return this.http.get<Dish[]>(url);
  }

  getSelectedRestaurant(): Restaurant | null {
    return this.selectedRestaurant;
  }


  createRestaurant(data: { restaurantName: string }) {
    return this.http.post<Restaurant>(this.apiUrl, data).pipe(
      tap(() => {
        this.getRestaurants();
      })
    );
  }
  getDish(restaurantId: string, dishIdOrName: string): Observable<Dish | undefined> {
    return this.getDishesByRestaurantId(restaurantId).pipe(
      map(dishes => dishes.find(d => d.id === dishIdOrName || d.name === dishIdOrName))
    );
  }

  createDish(restaurantId: string, dish: Dish): Observable<Dish> {
    const url = `${this.apiUrl}/${restaurantId}/dishes`;
    return this.http.post<Dish>(url, dish);
  }

  updateDish(restaurantId: string, dish: Dish): Observable<Dish> {
    const identifier = dish.id || dish.name;
    const url = `${this.apiUrl}/${restaurantId}/dishes/${identifier}`;
    return this.http.put<Dish>(url, dish);
  }

  private formatLabel(label: string): string {
    if (!label) return '';
    const text = label.replace(/_/g, ' ').toLowerCase();
    return text.charAt(0).toUpperCase() + text.slice(1);
  }




  getOpeningHours(restaurantId: string): Observable<OpeningHours[]> {
    const url = `${this.apiUrl}/${restaurantId}/opening-hours`;
    return this.http.get<any[]>(url).pipe(
      map(dtos => dtos.map(dto => {
        const slotsArray: TimeSlot[] = [];

        if (dto.slots) {
          Object.keys(dto.slots).forEach(key => {
            const parts = key.split(' - ');
            if (parts.length === 2) {
              slotsArray.push({
                startTime: parts[0].trim(),
                endTime: parts[1].trim(),
                capacity: dto.slots[key]
              });
            }
          });
          slotsArray.sort((a, b) => a.startTime.localeCompare(b.startTime));
        }

        return {
          id: dto.id,
          day: dto.day,
          openingTime: dto.openingTime,
          closingTime: dto.closingTime,
          slots: slotsArray
        };
      }))
    );
  }

  addOpeningHour(restaurantId: string, data: OpeningHourCreation): Observable<OpeningHours> {
    const url = `${this.apiUrl}/${restaurantId}/opening-hours`;
    return this.http.post<any>(url, data);
  }

  deleteOpeningHour(restaurantId: string, openingHourId: string): Observable<void> {
    const url = `${this.apiUrl}/${restaurantId}/opening-hours/${openingHourId}`;
    return this.http.delete<void>(url);
  }

  updateSlotCapacity(restaurantId: string, day: string, start: string, end: string, capacity: number): Observable<any> {
    const url = `${this.apiUrl}/${restaurantId}/capacities`;
    const payload = {
      day: day,
      start: start,
      end: end,
      capacity: capacity
    };
    return this.http.put(url, payload);
  }




  getSuggestions(keyword: string): Observable<Dish[]> {
    const url = `http://localhost:8080/api/suggestions`;
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<Dish[]>(url, { params });
  }
}
