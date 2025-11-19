import { Component } from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {RestaurantService} from '../../services/restaurant/restaurant.service';

@Component({
  selector: 'app-create-restaurant-form',
  imports: [
    ReactiveFormsModule,
  ],
  templateUrl: './create-restaurant-form.component.html',
  styleUrl: './create-restaurant-form.component.css'
})
export class CreateRestaurantFormComponent {

  constructor(private restaurantService: RestaurantService) {}

  form = new FormGroup({
    name: new FormControl('')
  });

  onSubmit() {
    const restaurantName = this.form.value.name as string;
    const restaurantData = {
      restaurantName: restaurantName,
    };
    this.restaurantService.createRestaurant(restaurantData).subscribe({
      next: (newRestaurant) => console.log('Restaurant créé avec succès:', newRestaurant),
      error: (err) => console.error('Erreur de création:', err)
    });

    console.log('Soumission réussie:', restaurantData);

    this.form.reset();
  }

}
