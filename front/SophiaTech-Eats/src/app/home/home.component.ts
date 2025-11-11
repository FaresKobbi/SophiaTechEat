import { Component } from '@angular/core';
import {ListComponent} from '../item-list/item-list.component';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CommonModule, ListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  students = ['STUDENT 1', 'STUDENT 2', 'STUDENT 3','STUDENT 4','STUDENT 5'];
  restaurants = ['RESTAURANT 1', 'RESTAURANT 2', 'RESTAURANT 3'];
}
