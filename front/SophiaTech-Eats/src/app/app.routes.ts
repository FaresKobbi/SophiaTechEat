
import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { CreateStudentPageComponent } from './pages/create-student-page/create-student-page.component';
import {CreateRestaurantPageComponent} from './pages/create-restaurant-page/create-restaurant-page.component';
import {RestaurantDashboardComponent} from './pages/restaurant-dashboard/restaurant-dashboard.component';
import {ManageDishComponent} from './pages/manage-dish/manage-dish.component';
import {StudentHomePageComponent} from './pages/student-home-page/student-home-page.component';
import {StudentAccountPageComponent} from './pages/student-account-page/student-account-page.component';
import {StudentOrderPageComponent} from './pages/student-order-page/student-order-page.component';
import {CreateDishPageComponent} from './pages/create-dish-page/create-dish-page.component';
import {UpdateDishPageComponent} from './pages/update-dish-page/update-dish-page.component';
import {OrderListPageComponent} from './pages/order-list-page/order-list-page.component';
import {RestaurantMenuPageComponent} from './pages/restaurant-menu-page/restaurant-menu-page.component';
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
  { path: 'student/homepage', component: StudentHomePageComponent },
  { path: 'student/account', component: StudentAccountPageComponent },
  { path: 'student/orders', component: StudentOrderPageComponent },
  { path: 'manager/dish/create/:restaurantId', component: CreateDishPageComponent },
  { path: 'manager/dish/update/:restaurantId/:dishId', component: UpdateDishPageComponent },
  { path: 'manager/orders/:restaurantId', component: OrderListPageComponent },
  { path: 'student/restaurant/:restaurantId/menu', component: RestaurantMenuPageComponent },
  { path: 'manager/opening-hours/:restaurantId', component: OpeningHoursComponent },
];
