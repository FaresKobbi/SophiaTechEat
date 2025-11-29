import {Component, OnInit} from '@angular/core';
import {RestaurantFilterComponent} from '../../components/restaurant-filter/restaurant-filter.component';
import {StudentAccount, StudentAccountService} from '../../services/student/student-account-service.service';
import {Router, RouterLink} from '@angular/router';
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

  dietaryLabels: string[] = [];
  cuisineTypes: string[] = []
  restaurantList : Restaurant[] = []

  selectedCuisine: string | undefined;
  selectedLabels: string[] = [];

  constructor(private studentService: StudentAccountService, private restaurantService: RestaurantService, private router: Router) {

  }


  ngOnInit(): void {
    this.loadRestaurants()


    this.restaurantService.restaurants$.subscribe({
      next: (data)=>{
        this.restaurantList = data;
      }
    })

    this.restaurantService.getDietaryLabels().subscribe(data => {
      this.dietaryLabels = data;
      console.log("Labels reçus dans le component :", this.dietaryLabels);
    });

    this.restaurantService.getCuisineTypes().subscribe(data => {
      this.cuisineTypes = data;
    });

    this.selectedStudent = this.studentService.getSelectedStudent();
    this.studentId = this.selectedStudent ? this.selectedStudent.studentID : this.studentId
    this.studentName = this.selectedStudent ? this.selectedStudent.name : this.studentName
    this.studentSurname = this.selectedStudent ? this.selectedStudent.surname : this.studentSurname
  }

  loadRestaurants() {
    this.restaurantService.getRestaurants(this.selectedCuisine, this.selectedLabels).subscribe();
  }

  onCuisineChange(selection: string[]) {
    this.selectedCuisine = selection.length > 0 ? selection[0] : undefined;

    console.log("Cuisine sélectionnée:", this.selectedCuisine);
    this.loadRestaurants();
  }

  onDietaryChange(selection: string[]) {
    this.selectedLabels = selection;

    console.log("Labels sélectionnés:", this.selectedLabels);
    this.loadRestaurants();
  }

  onRestaurantClick(restaurant: any): void {
    if (restaurant && restaurant.restaurantId) {
      this.router.navigate(['/student/restaurant', restaurant.restaurantId, 'menu']);
    }
  }
}
