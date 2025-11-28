import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dish, Topping } from '../../services/restaurant/restaurant.service';

@Component({
  selector: 'app-dish-detail-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dish-detail-modal.component.html',
  styleUrls: ['./dish-detail-modal.component.css']
})
export class DishDetailModalComponent implements OnInit {
  @Input() dish!: Dish;
  @Output() close = new EventEmitter<void>();
  @Output() addToOrder = new EventEmitter<{ dish: Dish, toppings: Topping[] }>();

  selectedToppings: Topping[] = [];
  currentTotal: number = 0;

  ngOnInit(): void {
    this.currentTotal = this.dish.price;
  }

  toggleTopping(topping: Topping): void {
    const index = this.selectedToppings.indexOf(topping);
    if (index === -1) {
      this.selectedToppings.push(topping);
    } else {
      this.selectedToppings.splice(index, 1);
    }
    this.calculateTotal();
  }

  isSelected(topping: Topping): boolean {
    return this.selectedToppings.includes(topping);
  }

  calculateTotal(): void {
    const toppingsCost = this.selectedToppings.reduce((sum, t) => sum + t.price, 0);
    this.currentTotal = this.dish.price + toppingsCost;
  }

  onAdd(): void {
    this.addToOrder.emit({ dish: this.dish, toppings: this.selectedToppings });
  }

  onCancel(): void {
    this.close.emit();
  }
}
