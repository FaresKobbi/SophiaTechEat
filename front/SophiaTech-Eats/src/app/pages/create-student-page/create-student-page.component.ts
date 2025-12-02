import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {StudentAccountService} from '../../services/student/student-account-service.service';

@Component({
  selector: 'app-create-student-page',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    CommonModule
  ],
  templateUrl: './create-student-page.component.html',
  styleUrl: './create-student-page.component.css'
})
export class CreateStudentPageComponent {

  name: string = '';
  surname: string = '';
  email: string = '';
  errorMessage: string | null = null;
  isLoading: boolean = false;

  constructor(
    private studentService: StudentAccountService,
    private router: Router
  ) {}

  createStudent(): void {
    if (!this.name || !this.surname || !this.email) {
      this.errorMessage = 'All fields are required.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    const studentData = {
      name: this.name,
      surname: this.surname,
      email: this.email
    };

    this.studentService.createStudent(studentData).subscribe({
      next: (newStudent) => {
        this.isLoading = false;
        console.log('Successfully created student:', newStudent);
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to create student. Please try again.';
        console.error('Error creating student', err);
      }
    });
  }
}
