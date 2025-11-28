
import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { CreateStudentPageComponent } from './pages/create-student-page/create-student-page.component';
import {CreateRestaurantPageComponent} from './pages/create-restaurant-page/create-restaurant-page.component';
import {RestaurantDashboardComponent} from './pages/restaurant-dashboard/restaurant-dashboard.component';
import {ManageDishComponent} from './pages/manage-dish/manage-dish.component';
import {CreateDishPageComponent} from './pages/create-dish-page/create-dish-page.component';
import {UpdateDishPageComponent} from './pages/update-dish-page/update-dish-page.component';
import { OpeningHoursComponent } from './pages/opening-hours-component/opening-hours-component.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: 'Home Page'
  },
  { path: 'create-student-page', component: CreateStudentPageComponent },
  { path: 'create-restaurant-page', component: CreateRestaurantPageComponent },
  { path: 'manager/dashboard/:restaurantId', component: RestaurantDashboardComponent },
  { path: 'manager/dish/:restaurantId', component: ManageDishComponent },
  { path: 'manager/dish/edit/:restaurantId/:dishId', component: ManageDishComponent },
  { path: 'manager/dish/create/:restaurantId', component: CreateDishPageComponent },
  { path: 'manager/dish/update/:restaurantId/:dishId', component: UpdateDishPageComponent },
  { path: 'manager/opening-hours/:restaurantId', component: OpeningHoursComponent },
];
