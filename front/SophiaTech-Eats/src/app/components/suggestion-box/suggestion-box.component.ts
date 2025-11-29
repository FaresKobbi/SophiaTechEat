import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestaurantService, Dish } from '../../services/restaurant/restaurant.service';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of, catchError, tap } from 'rxjs';

@Component({
  selector: 'app-suggestion-box',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './suggestion-box.component.html',
  styleUrls: ['./suggestion-box.component.css']
})
export class SuggestionBoxComponent {
  @Output() dishSelected = new EventEmitter<Dish>();

  keyword: string = '';
  suggestions: Dish[] = [];
  private searchSubject = new Subject<string>();

  constructor(private restaurantService: RestaurantService) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if (!term || term.length < 2) return of([]);
        return this.restaurantService.getSuggestions(term).pipe(
          tap(results => console.log('Suggestions found:', results)),
          catchError(err => {
            console.error('Error fetching suggestions:', err);
            return of([]);
          })
        );
      })
    ).subscribe(results => {
      this.suggestions = results;
    });
  }

  onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  selectDish(dish: Dish): void {
    this.dishSelected.emit(dish);
    this.keyword = '';
    this.suggestions = [];
  }
}
