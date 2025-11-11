// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component'; // <-- 1. IMPORT

export const routes: Routes = [
  // 2. ADD THIS ROUTE
  {
    path: '', // <-- Empty path means this is the default route
    component: HomeComponent,
    title: 'Home Page' // Optional: Sets the browser tab title
  },

  // You can add other routes here later, e.g.:
  // { path: 'about', component: AboutComponent },
  // { path: 'contact', component: ContactComponent },
];
