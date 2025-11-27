import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ListComponent} from '../../components/item-list/item-list.component';
import {RestaurantService, Dish} from '../../services/restaurant/restaurant.service';

@Component({
  selector: 'app-manage-dish',
  standalone: true,
  imports: [CommonModule, RouterLink, ListComponent, FormsModule],
  templateUrl: './manage-dish.component.html',
  styleUrl: './manage-dish.component.css'
})
export class ManageDishComponent implements OnInit {

  restaurantId: string = '';
  dishes: Dish[] = [];
  selectedDish: Dish | null = null;
  isLoading: boolean = true;
  errorMessage: string | null = null;

  constructor(
    private router: Router,
    private restaurantService: RestaurantService
  ) {
  }

  ngOnInit(): void {
    const id = this.restaurantService.getSelectedRestaurant()?.restaurantId;
    if (id) {
      this.restaurantId = id;
      this.loadDishes();
    } else {
      this.errorMessage = 'Restaurant ID is missing in the URL.';
      this.isLoading = false;
    }
  }

  loadDishes(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.restaurantService.getDishesByRestaurantId(this.restaurantId).subscribe({
      next: (data) => {
        this.dishes = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = `Failed to load dishes. Status: ${err.statusText || err.message}`;
        this.isLoading = false;
        console.error('Error fetching dishes:', err);
      }
    });
  }

  onDishSelect(dish: Dish): void {
    console.log("Dish selected:", dish);
    this.selectedDish = dish;
  }

  navigateToCreateDish(): void {
    this.router.navigate(['/manager/dish/create', this.restaurantId]);
  }

  navigateToUpdateDish(): void {
    if (!this.selectedDish) return;

    const dishIdentifier = this.selectedDish.id || this.selectedDish.name;

    this.router.navigate(['/manager/dish/update', this.restaurantId, dishIdentifier]);
  }
}
