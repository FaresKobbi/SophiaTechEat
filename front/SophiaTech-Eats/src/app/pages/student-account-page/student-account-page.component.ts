import {Component, OnInit} from '@angular/core';
import {ListComponent} from '../../components/item-list/item-list.component';
import {RestaurantFilterComponent} from '../../components/restaurant-filter/restaurant-filter.component';
import {StudentHomePageNavComponent} from '../../components/student-home-page-nav/student-home-page-nav.component';
import {StudentAccount, StudentAccountService} from '../../services/student/student-account-service.service';
import {RestaurantService} from '../../services/restaurant/restaurant.service';
import {StudentInfoBoxComponent} from '../../components/student-info-box/student-info-box.component';

@Component({
  selector: 'app-student-account-page',
  imports: [
    StudentHomePageNavComponent,
    StudentInfoBoxComponent
  ],
  templateUrl: './student-account-page.component.html',
  styleUrl: './student-account-page.component.css'
})
export class StudentAccountPageComponent implements OnInit{
  private selectedStudent: StudentAccount | null = null;
  private studentId : string | null = null
  studentName : string = "X"
  studentSurname : string = "Y"

  constructor(private studentService: StudentAccountService){

  }


  ngOnInit(): void {
    this.selectedStudent = this.studentService.getSelectedStudent();
    this.studentId = this.selectedStudent ? this.selectedStudent.studentID : this.studentId
    this.studentName = this.selectedStudent ? this.selectedStudent.name : this.studentName
    this.studentSurname = this.selectedStudent ? this.selectedStudent.surname : this.studentSurname
  }
}
