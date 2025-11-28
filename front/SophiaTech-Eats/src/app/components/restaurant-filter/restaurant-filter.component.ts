import {Component, ElementRef, EventEmitter, HostListener, Input, Output} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-restaurant-filter',
  imports: [
    NgIf,
    NgForOf
  ],
  templateUrl: './restaurant-filter.component.html',
  styleUrl: './restaurant-filter.component.css'
})
export class RestaurantFilterComponent {
  @Input() label: string = 'Filtre';
  @Input() options: string[] = [];

  // On renvoie la liste au parent quand ça change
  @Output() selectionChange = new EventEmitter<string[]>();

  isOpen = false;
  selectedOptions: string[] = []; // Tableau pour stocker plusieurs choix

  constructor(private eRef: ElementRef) {}

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  toggleOption(option: string, event: Event) {
    // IMPORTANT : Empêche le menu de se fermer quand on clique sur une case
    event.stopPropagation();

    const index = this.selectedOptions.indexOf(option);

    if (index === -1) {
      this.selectedOptions.push(option);
    } else {
      this.selectedOptions.splice(index, 1);
    }

    this.selectionChange.emit(this.selectedOptions);
  }

  isSelected(option: string): boolean {
    return this.selectedOptions.includes(option);
  }

  get buttonLabel(): string {
    if (this.selectedOptions.length === 0) {
      return this.label;
    }
    return `${this.label} (${this.selectedOptions.length})`;
  }

  @HostListener('document:click', ['$event'])
  clickout(event: any) {
    if(!this.eRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
    }
  }
}
