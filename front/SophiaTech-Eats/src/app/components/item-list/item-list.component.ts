import {Component, Input, Output,EventEmitter} from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-item-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css']
})
export class ListComponent {
  @Input() title: string = '';
  @Input() items: any[] = [];
  @Input() displayKey: string = 'name';
  @Output() itemClicked = new EventEmitter<any>();

  onItemClick(item: any): void {
      this.itemClicked.emit(item);
  }

  getDisplayValue(item: any): string {
    return item[this.displayKey] || 'N/A';
  }}
