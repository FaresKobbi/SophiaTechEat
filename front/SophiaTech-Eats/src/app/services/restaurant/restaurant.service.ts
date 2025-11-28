import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, map, Observable, of, tap} from 'rxjs';
import {StudentAccount} from '../student/student-account-service.service';

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
  day: string;
  openingTime: string;
  closingTime: string;
  slots: { [key: string]: number };
}

export interface OpeningHours {
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
  private restaurantsSubject = new BehaviorSubject<Restaurant[]>([])
  public restaurants$ = this.restaurantsSubject.asObservable();
  private selectedRestaurant: Restaurant | null = null;


  constructor(private http: HttpClient) {
  }

  getRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(this.apiUrl).pipe(
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




  getOpeningHours(restaurantId: string): Observable<OpeningHours[]> {
    const url = `${this.apiUrl}/${restaurantId}/opening-hours`;
    return this.http.get<OpeningHoursDTO[]>(url).pipe(
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

  deleteOpeningHour(restaurantId: string, day: string): Observable<void> {
    const url = `${this.apiUrl}/${restaurantId}/opening-hours/${day}`; // ou ?day=MONDAY
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



}
