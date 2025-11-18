import { Component } from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-create-restaurant-form',
  imports: [
    ReactiveFormsModule,
  ],
  templateUrl: './create-restaurant-form.component.html',
  styleUrl: './create-restaurant-form.component.css'
})
export class CreateRestaurantFormComponent {

  form = new FormGroup({
    name: new FormControl('')
  });

  onSubmit() {
    const formValue = this.form.value;

    console.log({
      name: formValue.name
    });
  }

}
