import { Component, OnInit } from '@angular/core';
import { ListComponent } from '../../components/item-list/item-list.component';
import { StudentHomePageNavComponent } from '../../components/student-home-page-nav/student-home-page-nav.component';
import { DeliveryLocation, StudentAccount, StudentAccountService } from '../../services/student/student-account-service.service';
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
  private selectedStudent: StudentAccount | null = null;
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

  constructor(private studentService: StudentAccountService) {

  }


  ngOnInit(): void {
    this.selectedStudent = this.studentService.getSelectedStudent();
    this.studentId = this.selectedStudent ? this.selectedStudent.studentID : this.studentId
    this.studentName = this.selectedStudent ? this.selectedStudent.name : this.studentName
    this.studentSurname = this.selectedStudent ? this.selectedStudent.surname : this.studentSurname

    if (this.studentId) {
      this.loadLocations();
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

  // Ouvre le popup
  openAddLocationModal() {
    this.isModalOpen = true;
  }

  // Ferme le popup et réinitialise le formulaire
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
    const newLoc: DeliveryLocation = {name: name, address: address, city: city, zipCode: zipCode };

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

}
