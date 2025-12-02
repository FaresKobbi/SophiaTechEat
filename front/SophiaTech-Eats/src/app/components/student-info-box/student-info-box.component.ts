import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-student-info-box',
  imports: [],
  templateUrl: './student-info-box.component.html',
  styleUrl: './student-info-box.component.css'
})
export class StudentInfoBoxComponent {
  @Input() title: string = 'Information';
}
