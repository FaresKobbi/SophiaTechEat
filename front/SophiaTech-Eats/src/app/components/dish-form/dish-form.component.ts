import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Dish, Topping } from '../../services/restaurant/restaurant.service';

@Component({
  selector: 'app-dish-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dish-form.component.html',
  styleUrl: './dish-form.component.css'
})
export class DishFormComponent implements OnInit, OnChanges {
  @Input() dishData: Dish | null = null;
  @Input() submitLabel: string = 'SAVE';
  @Output() formSubmit = new EventEmitter<Dish>();

  dishForm: FormGroup;

  
  categories = ['STARTER', 'MAIN_COURSE', 'DESSERT', 'DRINK'];
  dishTypes = ['Pasta','Meat','Pizza','Burger','Sushi','Ice_Cream','Cake','Salad','Drink','General'];
  availableDietaryLabels = ['Vegan','Vegetarian', 'Gluten_Free', 'Frozen_Products', 'Halal'];

  constructor(private fb: FormBuilder) {
    this.dishForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      category: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      dishType: [''],
      toppings: this.fb.array([]),
      dietaryLabels: this.fb.array([])
    });
    this.addDietaryLabelControls();
  }

  ngOnInit(): void {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['dishData'] && this.dishData) {
      this.populateForm(this.dishData);
    }
  }

  
  get toppings(): FormArray {
    return this.dishForm.get('toppings') as FormArray;
  }

  addTopping(name: string = '', price: number = 0): void {
    this.toppings.push(this.fb.group({
      name: [name, Validators.required],
      price: [price, [Validators.required, Validators.min(0)]]
    }));
  }

  removeTopping(index: number): void {
    this.toppings.removeAt(index);
  }

  get dietaryLabelsFormArray(): FormArray {
    return this.dishForm.get('dietaryLabels') as FormArray;
  }

  private addDietaryLabelControls() {
    this.availableDietaryLabels.forEach(() => {
      this.dietaryLabelsFormArray.push(this.fb.control(false));
    });
  }

  populateForm(dish: Dish): void {
    const matchingType = this.dishTypes.find(
      type => type.toUpperCase() === dish.dishType?.toUpperCase()
    );
    this.dishForm.patchValue({
      name: dish.name,
      description: dish.description,
      category: dish.category,
      price: dish.price,
      dishType: matchingType
    });

    this.toppings.clear();
    if (dish.toppings) {
      dish.toppings.forEach(t => this.addTopping(t.name, t.price));
    }

    if (dish.dietaryLabels) {
      this.availableDietaryLabels.forEach((label, index) => {
        if (dish.dietaryLabels!.includes(label.toUpperCase())) {
          this.dietaryLabelsFormArray.at(index).setValue(true);
        }
      });
    }
  }
  onSubmit(): void {
    if (this.dishForm.valid) {
      const formValue = this.dishForm.value;

      const selectedLabels = this.dishForm.value.dietaryLabels
        .map((checked: boolean, i: number) => checked ? this.availableDietaryLabels[i] : null)
        .filter((v: string | null) => v !== null);

      const dishPayload: Dish = {
        ...this.dishData,
        name: formValue.name,
        description: formValue.description,
        category: formValue.category,
        price: formValue.price,
        dishType: formValue.dishType ? formValue.dishType.toUpperCase() : null,
        toppings: formValue.toppings,
        dietaryLabels: selectedLabels
      };

      this.formSubmit.emit(dishPayload);
    } else {
      this.dishForm.markAllAsTouched();
    }
  }
}
