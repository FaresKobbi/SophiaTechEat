import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentInfoBoxComponent } from './student-info-box.component';

describe('StudentInfoBoxComponent', () => {
  let component: StudentInfoBoxComponent;
  let fixture: ComponentFixture<StudentInfoBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentInfoBoxComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentInfoBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
