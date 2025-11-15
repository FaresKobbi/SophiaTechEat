import { Component } from '@angular/core';
import {CreateRestaurantFormComponent} from '../../components/create-restaurant-form/create-restaurant-form.component';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-create-restaurant-page',
  imports: [
    CreateRestaurantFormComponent,
    RouterLink
  ],
  templateUrl: './create-restaurant-page.component.html',
  styleUrl: './create-restaurant-page.component.css'
})
export class CreateRestaurantPageComponent {

}
