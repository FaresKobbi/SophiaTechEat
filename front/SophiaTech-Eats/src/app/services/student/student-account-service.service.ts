import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, catchError, of } from 'rxjs';


export interface StudentAccount {
  studentID: string;
  name: string;
  surname: string;
  email: string;
  balance: number;
}

export interface DeliveryLocation {
  id?: string;
  name: string;
  address: string;
  city: string;
  zipCode: string;
}

export interface BankInfo {
  cardNumber: string;
  cvv: number;
  month: number;
  year: number;
}

@Injectable({
  providedIn: 'root'
})
export class StudentAccountService {
  private apiUrl = 'http://localhost:8080/api/accounts';
  private studentsSubject = new BehaviorSubject<StudentAccount[]>([]);
  public students$: Observable<StudentAccount[]> = this.studentsSubject.asObservable();
  private selectedStudent: StudentAccount | null = null

  constructor(private http: HttpClient) {
    this.loadStudents();
  }



  getSelectedStudent () : StudentAccount | null{
    return this.selectedStudent
  }
  public setSelectedStudent (student : StudentAccount){
    this.selectedStudent = student
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

  createStudent(data: {name: string, surname: string, email: string}): Observable<StudentAccount> {
    return this.http.post<StudentAccount>(this.apiUrl, data).pipe(
      tap(() => {
        this.refreshStudents();
      })
    );
  }

  updateStudentPersonalInfo(studentId: string, info: { name: string, surname: string, email: string}): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${studentId}/personal-info`, info);
  }

  public refreshStudents(): void {
    this.loadStudents();
  }

  // --- GESTION DELIVERY LOCATIONS ---

  getDeliveryLocations(studentId: string): Observable<DeliveryLocation[]> {
    return this.http.get<DeliveryLocation[]>(`${this.apiUrl}/${studentId}/locations`);
  }

  addDeliveryLocation(studentId: string, location: DeliveryLocation): Observable<DeliveryLocation> {
    return this.http.post<DeliveryLocation>(`${this.apiUrl}/${studentId}/locations`, location);
  }

  removeDeliveryLocation(studentId: string, locationId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${studentId}/locations/${locationId}`);
  }

  getBankInfo(studentId: string): Observable<BankInfo | null> {
    return this.http.get<BankInfo | null>(`${this.apiUrl}/${studentId}/bankinfo`);
  }

  updateBankInfo(studentId: string, info: BankInfo): Observable<BankInfo> {
    return this.http.put<BankInfo>(`${this.apiUrl}/${studentId}/bankinfo`, info);
  }
}
