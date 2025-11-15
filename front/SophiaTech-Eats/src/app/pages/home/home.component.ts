import {Component, OnInit} from '@angular/core';
import {ListComponent} from '../../components/item-list/item-list.component';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {Restaurant, RestaurantService} from '../../services/restaurant/restaurant.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [CommonModule, ListComponent, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  students = ['STUDENT 1', 'STUDENT 2', 'STUDENT 3','STUDENT 4','STUDENT 5'];
  restaurants: string[] = [];
  private sub?: Subscription;

  constructor(private restaurantService: RestaurantService) {
  }


  ngOnInit(): void {
    this.sub = this.restaurantService.getRestaurants().subscribe({
      next: (data) => {
        this.restaurants = data
          .map(r => r.restaurantName)
          .filter(name => !!name);
      },
      error: (err) => console.error('Erreur de récupération des restaurants', err)
    });
  }

}
