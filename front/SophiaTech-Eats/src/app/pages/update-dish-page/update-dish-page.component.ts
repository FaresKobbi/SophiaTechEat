import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DishFormComponent } from '../../components/dish-form/dish-form.component';
import { RestaurantService, Dish } from '../../services/restaurant/restaurant.service';

@Component({
  selector: 'app-update-dish-page',
  standalone: true,
  imports: [CommonModule, DishFormComponent, RouterLink],
  templateUrl: './update-dish-page.component.html',
  styleUrl: './update-dish-page.component.css'
})
export class UpdateDishPageComponent implements OnInit {
  restaurantId: string = '';
  dishId: string = '';
  dishData: Dish | null = null;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restaurantService: RestaurantService
  ) {}

  ngOnInit(): void {
    this.restaurantId = this.route.snapshot.paramMap.get('restaurantId') || '';
    this.dishId = this.route.snapshot.paramMap.get('dishId') || '';

    if (this.restaurantId && this.dishId) {
      this.loadDish();
    }
  }

  loadDish(): void {
    this.restaurantService.getDish(this.restaurantId, this.dishId).subscribe({
      next: (dish) => {
        if (dish) {
          this.dishData = dish;
        } else {
          console.error('Dish not found');
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  handleUpdate(dish: Dish): void {
    this.restaurantService.updateDish(this.restaurantId, dish).subscribe({
      next: () => {
        console.log('Dish updated successfully');
        this.router.navigate(['/manager/dish', this.restaurantId]);
      },
      error: (err) => console.error('Error updating dish', err)
    });
  }
}
