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
  @Input() multiple: boolean = true;

  @Output() selectionChange = new EventEmitter<string[]>();

  isOpen = false;
  selectedOptions: string[] = [];

  constructor(private eRef: ElementRef) {}

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  toggleOption(option: string, event: Event) {
    event.stopPropagation();

    const isAlreadySelected = this.isSelected(option);

    if (this.multiple) {
      if (isAlreadySelected) {
        this.selectedOptions = this.selectedOptions.filter(o => o !== option);
      } else {
        this.selectedOptions.push(option);
      }
    } else {
      if (isAlreadySelected) {
        this.selectedOptions = [];
      } else {
        this.selectedOptions = [option];
      }
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
    if (!this.multiple && this.selectedOptions.length === 1) {
      return `${this.label}: ${this.selectedOptions[0]}`;
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
