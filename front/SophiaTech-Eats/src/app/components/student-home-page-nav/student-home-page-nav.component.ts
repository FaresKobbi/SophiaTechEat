import {Component, Input} from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-student-home-page-nav',
  imports: [
    RouterLink,
    RouterLinkActive,
    NgForOf
  ],
  templateUrl: './student-home-page-nav.component.html',
  styleUrl: './student-home-page-nav.component.css'
})
export class StudentHomePageNavComponent {
  navLinks = [
    { label: 'Home', path: '/student/homepage' },
    { label: 'My Account', path: '/student/account' },
    { label: 'My Orders', path: '/student/orders' }
  ];

  @Input() studentName = "X"
  @Input() studentSurname = "Y"

}
