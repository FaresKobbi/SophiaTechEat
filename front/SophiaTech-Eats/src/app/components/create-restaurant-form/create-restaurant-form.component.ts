import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant/restaurant.service';

@Component({
  selector: 'app-create-restaurant-form',
  imports: [
    ReactiveFormsModule,
  ],
  templateUrl: './create-restaurant-form.component.html',
  styleUrl: './create-restaurant-form.component.css'
})
export class CreateRestaurantFormComponent {
  cuisineTypes: string[] = [];

  constructor(private restaurantService: RestaurantService) { }

  form = new FormGroup({
    name: new FormControl('', Validators.required),
    cuisine: new FormControl('', Validators.required)
  });

  ngOnInit() {
    this.restaurantService.getCuisineTypes().subscribe({
      next: (types) => this.cuisineTypes = types,
      error: (err) => console.error('Error fetching cuisine types', err)
    });
  }

  onSubmit() {
    if (this.form.invalid) return;

    const restaurantName = this.form.value.name as string;
    const cuisineType = this.form.value.cuisine as string;

    const restaurantData = {
      restaurantName: restaurantName,
      cuisineType: cuisineType
    };
    this.restaurantService.createRestaurant(restaurantData).subscribe({
      next: (newRestaurant) => {
        console.log('Restaurant créé avec succès:', newRestaurant);
        this.form.reset();
      },
      error: (err) => console.error('Erreur de création:', err)
    });

    console.log('Soumission réussie:', restaurantData);
  }

}
