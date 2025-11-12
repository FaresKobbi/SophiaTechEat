// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component'; // <-- 1. IMPORT
import { CreateStudentPageComponent } from './create-student-page/create-student-page.component';

export const routes: Routes = [
  // 2. ADD THIS ROUTE
  {
    path: '', // <-- Empty path means this is the default route
    component: HomeComponent,
    title: 'Home Page' // Optional: Sets the browser tab title
  },
  { path: 'create-student-page', component: CreateStudentPageComponent },
];
