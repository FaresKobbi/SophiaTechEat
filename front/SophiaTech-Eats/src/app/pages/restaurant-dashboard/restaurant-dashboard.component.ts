import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Restaurant, RestaurantService } from '../../services/restaurant/restaurant.service';

@Component({
  selector: 'app-restaurant-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './restaurant-dashboard.component.html',
  styleUrl: './restaurant-dashboard.component.css'
})
export class RestaurantDashboardComponent implements OnInit {

  restaurantId: string | null = null;
  restaurantName: string = 'RESTAURANT X';
  selectedRestaurant: Restaurant | null = null;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService
  ) {}

  ngOnInit(): void {
    this.selectedRestaurant = this.restaurantService.getSelectedRestaurant();
    this.restaurantId = this.selectedRestaurant ? this.selectedRestaurant.restaurantId : this.restaurantId;

    if (this.selectedRestaurant && this.selectedRestaurant.restaurantName) {
      this.restaurantName = this.selectedRestaurant.restaurantName;
    } else if (this.restaurantId) {
      this.restaurantName = `Restaurant ID: ${this.restaurantId}`;
    }
  }

}
