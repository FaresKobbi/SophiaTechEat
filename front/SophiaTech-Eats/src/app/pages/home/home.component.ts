import {Component, OnInit} from '@angular/core';
import {ListComponent} from '../../components/item-list/item-list.component';
import {CommonModule} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
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

  allRestaurants: Restaurant[] = [];
  restaurantsForList: Restaurant[] = [];

  private restaurantSub?: Subscription;

  students: StudentAccount[] = [];
  private studentSub?: Subscription;

  constructor(
    private restaurantService: RestaurantService,
    private studentService: StudentAccountService,
    private router: Router
  ) {
  }



  ngOnInit(): void {
    this.restaurantSub = this.restaurantService.restaurants$.subscribe({
      next: (data) => {
        this.allRestaurants = data;
        this.restaurantsForList = data;
      },
      error: (err) => console.error('Erreur de récupération des restaurants', err)
    });

    this.restaurantService.getRestaurants().subscribe();

    this.studentSub = this.studentService.students$.subscribe({
      next: (data: StudentAccount[]) => {
        this.students = data;
      },
      error: (err) => console.error('Erreur de récupération des étudiants', err)
    });


  }

  ngOnDestroy(): void {
    this.restaurantSub?.unsubscribe();
    this.studentSub?.unsubscribe();
  }

  onStudentSelect(student: StudentAccount): void {
    console.log(student)
    if (!student || !student.studentID) {
      console.error('Invalid student data received.');
      return;
    }

    this.studentService.setSelectedStudent(student);

    this.router.navigate(['/student/homepage']);
  }


  onRestaurantSelect(restaurant: Restaurant): void {
    if (!restaurant || !restaurant.restaurantId) {
      console.error('Invalid restaurant data received.');
      return;
    }

    this.restaurantService.setSelectedRestaurant(restaurant);

    this.router.navigate(['/manager/dashboard', restaurant.restaurantId]);
  }

}
