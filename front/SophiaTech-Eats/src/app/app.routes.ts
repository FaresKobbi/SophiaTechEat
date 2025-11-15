// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component'; // <-- 1. IMPORT
import { CreateStudentPageComponent } from './pages/create-student-page/create-student-page.component';
import {CreateRestaurantPageComponent} from './pages/create-restaurant-page/create-restaurant-page.component';

export const routes: Routes = [
  // 2. ADD THIS ROUTE
  {
    path: '', // <-- Empty path means this is the default route
    component: HomeComponent,
    title: 'Home Page' // Optional: Sets the browser tab title
  },
  { path: 'create-student-page', component: CreateStudentPageComponent },
  { path: 'create-restaurant-page', component: CreateRestaurantPageComponent },
];
