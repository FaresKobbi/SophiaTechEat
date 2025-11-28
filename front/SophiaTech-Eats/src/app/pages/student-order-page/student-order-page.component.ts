import {Component, OnInit} from '@angular/core';
import {StudentAccount, StudentAccountService} from '../../services/student/student-account-service.service';
import {StudentHomePageNavComponent} from '../../components/student-home-page-nav/student-home-page-nav.component';
import {ListComponent} from '../../components/item-list/item-list.component';

@Component({
  selector: 'app-student-order-page',
  imports: [
    StudentHomePageNavComponent,
    ListComponent
  ],
  templateUrl: './student-order-page.component.html',
  styleUrl: './student-order-page.component.css'
})
export class StudentOrderPageComponent implements OnInit{
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
