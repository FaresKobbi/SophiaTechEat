import { Component, OnInit } from '@angular/core';
import { ListComponent } from '../../components/item-list/item-list.component';
import { StudentHomePageNavComponent } from '../../components/student-home-page-nav/student-home-page-nav.component';
import { BankInfo, DeliveryLocation, StudentAccount, StudentAccountService } from '../../services/student/student-account-service.service';
import { StudentInfoBoxComponent } from '../../components/student-info-box/student-info-box.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-student-account-page',
  imports: [
    ListComponent,
    FormsModule,
    StudentHomePageNavComponent,
    StudentInfoBoxComponent,
    CommonModule
  ],
  templateUrl: './student-account-page.component.html',
  styleUrl: './student-account-page.component.css'
})
export class StudentAccountPageComponent implements OnInit {
  currentYear: number = new Date().getFullYear();
  public selectedStudent: StudentAccount | null = null;
  private studentId: string | null = null
  studentName: string = "X"
  studentSurname: string = "Y"
  locations: DeliveryLocation[] = []
  locationDisplayKey: string[] = ['name', 'address', 'city', 'zipCode']; // Or use a single key or getter in ListComponent logic

  selectedLocation: DeliveryLocation | null = null

  isModalOpen: boolean = false;

  newLocationData = {
    name: '',
    address: '',
    city: '',
    zipCode: ''
  };

  bankInfo: BankInfo | null = null;
  isBankModalOpen: boolean = false;

  bankFormData = {
    cardNumber: '',
    cvv: '',
    month: '',
    year: ''
  };

  isPersonalInfoModalOpen: boolean = false;
  personalInfoFormData = {
    name: '',
    surname: '',
    email: ''
  };

  constructor(private studentService: StudentAccountService) {

  }


  ngOnInit(): void {
    this.selectedStudent = this.studentService.getSelectedStudent();
    this.studentId = this.selectedStudent ? this.selectedStudent.studentID : this.studentId
    this.studentName = this.selectedStudent ? this.selectedStudent.name : this.studentName
    this.studentSurname = this.selectedStudent ? this.selectedStudent.surname : this.studentSurname

    if (this.studentId) {
      this.loadLocations();
      this.loadBankInfo();
      this.studentService.refreshStudent(this.studentId);
      this.selectedStudent = this.studentService.getSelectedStudent();
      this.studentId = this.selectedStudent ? this.selectedStudent.studentID : this.studentId
      this.studentName = this.selectedStudent ? this.selectedStudent.name : this.studentName
      this.studentSurname = this.selectedStudent ? this.selectedStudent.surname : this.studentSurname
    }
  }

  loadLocations() {
    if (this.studentId) {
      this.studentService.getDeliveryLocations(this.studentId).subscribe({
        next: (data) => {
          console.log("Locations loaded:", data);
          this.locations = data;
        },
        error: (err) => console.error("Error loading locations", err)
      });
    }
  }


  onLocationSelect(location: any) {
    console.log("Selected location:", location);
    this.selectedLocation = location;
  }

  openAddLocationModal() {
    this.isModalOpen = true;
  }

  closeAddLocationModal() {
    this.isModalOpen = false;
    this.resetForm();
  }

  resetForm() {
    this.newLocationData = { name: '', address: '', city: '', zipCode: '' };
  }


  submitNewLocation() {

    if (!this.newLocationData.name || !this.newLocationData.address || !this.newLocationData.city || !this.newLocationData.zipCode) {
      alert("Please fill in Name, Address, City and Zip Code");
      return;
    }

    this.addLocation(
      this.newLocationData.name,
      this.newLocationData.address,
      this.newLocationData.city,
      this.newLocationData.zipCode
    );
  }

  addLocation(name: string, address: string, city: string, zipCode: string) {
    const newLoc: DeliveryLocation = { name: name, address: address, city: city, zipCode: zipCode };

    this.studentService.addDeliveryLocation(this.studentId!, newLoc).subscribe({
      next: () => {
        this.loadLocations();
        this.closeAddLocationModal();
      },
      error: (err) => console.error("Error adding location", err)
    });
  }

  deleteSelectedLocation() {
    if (!this.selectedLocation || !this.selectedLocation.id) {
      console.warn("No location selected or location has no ID");
      return;
    }

    if (this.studentId) {
      this.studentService.removeDeliveryLocation(this.studentId, this.selectedLocation.id)
        .subscribe({
          next: () => {
            this.loadLocations();
            this.selectedLocation = null;
          },
          error: (err) => console.error("Error deleting", err)
        });
    }
  }

  loadBankInfo() {
    if (this.studentId) {
      this.studentService.getBankInfo(this.studentId).subscribe({
        next: (data) => {
          this.bankInfo = data;
        },
        error: (err) => console.error("Error loading bank info", err)
      });
    }
  }

  openBankModal() {
    if (this.bankInfo) {
      this.bankFormData = {
        cardNumber: this.bankInfo.cardNumber,
        cvv: this.bankInfo.cvv.toString(),
        month: this.bankInfo.month.toString(),
        year: this.bankInfo.year.toString()
      };
    } else {
      this.bankFormData = { cardNumber: '', cvv: '', month: '', year: '' };
    }
    this.isBankModalOpen = true;
  }

  closeBankModal() {
    this.isBankModalOpen = false;
  }

  submitBankInfo() {
    if (!this.studentId) return;

    // 1. Validation de présence
    if (!this.bankFormData.cardNumber || !this.bankFormData.cvv || !this.bankFormData.month || !this.bankFormData.year) {
      alert("Please fill all bank fields");
      return;
    }

    // 2. Validation Card (16 chiffres)
    const cardRegex = /^\d{16}$/;
    if (!cardRegex.test(this.bankFormData.cardNumber)) {
      alert("Card number must contain exactly 16 digits.");
      return;
    }

    // 3. Validation CVV (3 chiffres)
    const cvvString = this.bankFormData.cvv.toString();
    const cvvRegex = /^\d{3}$/;
    if (!cvvRegex.test(cvvString)) {
      alert("CVV must contain exactly 3 digits.");
      return;
    }

    // --- 4. Validation MONTH (1 ou 2 chiffres, valeur 1-12) ---
    const monthVal = parseInt(this.bankFormData.month);
    // Vérifie si c'est un nombre, entre 1 et 12
    if (isNaN(monthVal) || monthVal < 1 || monthVal > 12) {
      alert("Month must be between 1 and 12.");
      return;
    }

    // --- 5. Validation YEAR (4 chiffres, >= année actuelle) ---
    const yearVal = parseInt(this.bankFormData.year);
    const yearString = this.bankFormData.year.toString();

    // Regex pour s'assurer que c'est bien 4 chiffres (ex: évite "24" pour "2024")
    const yearRegex = /^\d{4}$/;

    if (!yearRegex.test(yearString)) {
      alert("Year must be exactly 4 digits (e.g., 2025).");
      return;
    }

    if (yearVal < this.currentYear) {
      alert(`Year cannot be in the past (minimum ${this.currentYear}).`);
      return;
    }

    const currentMonth = new Date().getMonth() + 1;
    if (yearVal === this.currentYear && monthVal < currentMonth) {
      alert("Card has already expired.");
      return;
    }

    const newInfo: BankInfo = {
      cardNumber: this.bankFormData.cardNumber,
      cvv: parseInt(this.bankFormData.cvv),
      month: monthVal,
      year: yearVal
    };

    this.studentService.updateBankInfo(this.studentId, newInfo).subscribe({
      next: () => {
        this.loadBankInfo();
        this.closeBankModal();
      },
      error: (err) => console.error("Error updating bank info", err)
    });
  }

  openPersonalInfoModal() {
    if (this.selectedStudent) {
      this.personalInfoFormData = {
        name: this.selectedStudent.name,
        surname: this.selectedStudent.surname,
        email: this.selectedStudent.email || ''
      };
    }
    this.isPersonalInfoModalOpen = true;
  }

  closePersonalInfoModal() {
    this.isPersonalInfoModalOpen = false;
  }

  submitPersonalInfo() {
    if (!this.studentId) return;

    this.studentService.updateStudentPersonalInfo(this.studentId, this.personalInfoFormData).subscribe({
      next: (updated) => {
        if (this.selectedStudent) {
          this.selectedStudent.name = this.personalInfoFormData.name;
          this.selectedStudent.surname = this.personalInfoFormData.surname;
          this.selectedStudent.email = this.personalInfoFormData.email;

          this.studentName = this.selectedStudent.name;
          this.studentSurname = this.selectedStudent.surname;
        }
        this.closePersonalInfoModal();
      },
      error: (err) => console.error("Error updating personal info", err)
    });
  }
}
