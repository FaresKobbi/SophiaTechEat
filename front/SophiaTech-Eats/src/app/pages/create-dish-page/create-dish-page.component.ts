import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DishFormComponent } from '../../components/dish-form/dish-form.component';
import { RestaurantService, Dish } from '../../services/restaurant/restaurant.service';

@Component({
  selector: 'app-create-dish-page',
  standalone: true,
  imports: [CommonModule, DishFormComponent, RouterLink],
  templateUrl: './create-dish-page.component.html',
  styleUrl: './create-dish-page.component.css'
})
export class CreateDishPageComponent implements OnInit {
  restaurantId: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restaurantService: RestaurantService
  ) {}

  ngOnInit(): void {
    this.restaurantId = this.route.snapshot.paramMap.get('restaurantId') || '';
  }

  handleCreate(dish: Dish): void {
    this.restaurantService.createDish(this.restaurantId, dish).subscribe({
      next: () => {
        console.log('Dish created successfully');
        this.router.navigate(['/manager/dish', this.restaurantId]);
      },
      error: (err) => console.error('Error creating dish', err)
    });
  }
}
