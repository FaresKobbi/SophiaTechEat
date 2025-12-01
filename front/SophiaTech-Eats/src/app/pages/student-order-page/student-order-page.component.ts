import { Component, OnInit } from '@angular/core';
import { StudentAccount, StudentAccountService } from '../../services/student/student-account-service.service';
import { StudentHomePageNavComponent } from '../../components/student-home-page-nav/student-home-page-nav.component';
import { ListComponent } from '../../components/item-list/item-list.component';
import { Order, OrderService } from '../../services/order/order.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-student-order-page',
  standalone: true,
  imports: [
    StudentHomePageNavComponent,
    ListComponent,
    CommonModule
  ],
  templateUrl: './student-order-page.component.html',
  styleUrl: './student-order-page.component.css'
})
export class StudentOrderPageComponent implements OnInit {
  private selectedStudent: StudentAccount | null = null;
  private studentId: string | null = null;
  studentName: string = "X";
  studentSurname: string = "Y";

  orders: Order[] = [];
  displayOrders: any[] = [];
  selectedOrder: Order | null = null;

  constructor(
    private studentService: StudentAccountService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.selectedStudent = this.studentService.getSelectedStudent();
    if (this.selectedStudent) {
      this.studentId = this.selectedStudent.studentID;
      this.studentName = this.selectedStudent.name;
      this.studentSurname = this.selectedStudent.surname;
      this.loadOrders();
    }
  }

  loadOrders(): void {
    if (!this.studentId) return;

    this.orderService.getOrdersByStudent(this.studentId).subscribe({
      next: (data) => {
        this.orders = data;
        this.displayOrders = data.map(o => ({
          ...o,
          // UPDATED LABEL: "Pizza Palace - VALIDATED - €12.50"
          displayLabel: `${o.restaurantName || 'Restaurant'} - ${o.orderStatus} - ${o.amount.toFixed(2)}€`
        }));
      },
      error: (err) => console.error("Error loading orders", err)
    });
  }

  onOrderSelect(item: any): void {
    this.selectedOrder = item as Order;
  }
}