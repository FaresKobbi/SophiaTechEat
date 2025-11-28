import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentAccountPageComponent } from './student-account-page.component';

describe('StudentAccountPageComponent', () => {
  let component: StudentAccountPageComponent;
  let fixture: ComponentFixture<StudentAccountPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentAccountPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentAccountPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
