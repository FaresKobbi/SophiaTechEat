import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentOrderPageComponent } from './student-order-page.component';

describe('StudentOrderPageComponent', () => {
  let component: StudentOrderPageComponent;
  let fixture: ComponentFixture<StudentOrderPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentOrderPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentOrderPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
