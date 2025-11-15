import { Component } from '@angular/core';
import {FormArray, FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-create-restaurant-form',
  imports: [
    ReactiveFormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './create-restaurant-form.component.html',
  styleUrl: './create-restaurant-form.component.css'
})
export class CreateRestaurantFormComponent {
  isEstablishmentOpen = false;
  isCuisineOpen = false;

  ESTABLISHMENT_TYPES = ['CROUS', 'RESTAURANT', 'FOOD_TRUCK'];

  CUISINE_TYPES = [
  'GENERAL', 'ITALIAN', 'FRENCH', 'JAPANESE',
  'INDIAN', 'AMERICAN', 'CHINESE', 'MEXICAN', 'VEGETARIAN'];

  form = new FormGroup({
    name: new FormControl(''),

    // Choix unique → radio
    establishmentType: new FormControl(''),

    // Choix multiple → FormArray de booleans
    cuisines: new FormArray(
      this.CUISINE_TYPES.map(() => new FormControl(false))
    )
  });

  onSubmit() {
    const formValue = this.form.value;

    // Transformer le tableau de booleans en liste de cuisines sélectionnées
    const selectedCuisines = this.CUISINE_TYPES.filter((_, i) => formValue.cuisines?.[i]);

    console.log({
      name: formValue.name,
      establishmentType: formValue.establishmentType,
      cuisines: selectedCuisines
    });
  }

}
