import {Component, OnInit} from '@angular/core';
import {ListComponent} from '../../components/item-list/item-list.component';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {Restaurant, RestaurantService} from '../../services/restaurant/restaurant.service';
import {Subscription} from 'rxjs';

import {StudentAccount, StudentAccountService} from '../../services/student/student-account-service.service';


@Component({
  selector: 'app-home',
  imports: [CommonModule, ListComponent, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  restaurants: string[] = [];
  private restaurantSub?: Subscription;

  students: string[] = [];
  private studentSub?: Subscription;

  constructor(private restaurantService: RestaurantService, private studentService: StudentAccountService) {
  }


  ngOnInit(): void {
    this.restaurantSub = this.restaurantService.getRestaurants().subscribe({
      next: (data) => {
        this.restaurants = data
          .map(r => r.restaurantName)
          .filter(name => !!name);
      },
      error: (err) => console.error('Erreur de récupération des restaurants', err)
    });
    this.studentSub = this.studentService.students$.subscribe({
      next: (data: StudentAccount[]) => {
        this.students = data.map(student => `${student.name} ${student.surname}`);
      },
      error: (err) => console.error('Erreur de récupération des étudiants', err)
    });



  }

  ngOnDestroy(): void {
    this.restaurantSub?.unsubscribe();
    this.studentSub?.unsubscribe();
  }

}
