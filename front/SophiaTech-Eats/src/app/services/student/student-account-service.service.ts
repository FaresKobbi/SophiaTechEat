import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, catchError, of } from 'rxjs';


export interface StudentAccount {
  id: string;
  firstName: string;
  lastName: string;
}

@Injectable({
  providedIn: 'root'
})
export class StudentAccountService {
  private apiUrl = 'http://localhost:8080/api/accounts';

  private studentsSubject = new BehaviorSubject<StudentAccount[]>([]);

  public students$: Observable<StudentAccount[]> = this.studentsSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadStudents();
  }


  private loadStudents(): void {
    this.http.get<StudentAccount[]>(this.apiUrl).pipe(
      tap((data) => {
        this.studentsSubject.next(data);
      }),
      catchError((error) => {
        console.error('Failed to load student accounts:', error);
        this.studentsSubject.next([]);
        return of([]);
      })
    ).subscribe();
  }


  public refreshStudents(): void {
    this.loadStudents();
  }
}
