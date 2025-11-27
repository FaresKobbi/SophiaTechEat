import {Component, OnInit} from '@angular/core';
import {RestaurantFilterComponent} from '../../components/restaurant-filter/restaurant-filter.component';
import {StudentAccount, StudentAccountService} from '../../services/student/student-account-service.service';
import {RouterLink} from '@angular/router';
import {ListComponent} from '../../components/item-list/item-list.component';
import {Restaurant, RestaurantService} from '../../services/restaurant/restaurant.service';
import {StudentHomePageNavComponent} from '../../components/student-home-page-nav/student-home-page-nav.component';

@Component({
  selector: 'app-student-home-page',
  imports: [
    RestaurantFilterComponent,
    ListComponent,
    StudentHomePageNavComponent,
  ],
  templateUrl: './student-home-page.component.html',
  styleUrl: './student-home-page.component.css'
})
export class StudentHomePageComponent implements OnInit{
  private selectedStudent: StudentAccount | null = null;
  private studentId : string | null = null
  studentName : string = "X"
  studentSurname : string = "Y"

  restaurantList : Restaurant[] = []

  constructor(private studentService: StudentAccountService, private restaurantService: RestaurantService){

  }


  ngOnInit(): void {

    this.restaurantService.restaurants$.subscribe({
      next: (data)=>{
        this.restaurantList = data;
    }
    })

    this.selectedStudent = this.studentService.getSelectedStudent();
    this.studentId = this.selectedStudent ? this.selectedStudent.studentID : this.studentId
    this.studentName = this.selectedStudent ? this.selectedStudent.name : this.studentName
    this.studentSurname = this.selectedStudent ? this.selectedStudent.surname : this.studentSurname
  }

  onItemChange(selectedItems: string[]) {
    console.log('Item chosen:', selectedItems);
  }
}
