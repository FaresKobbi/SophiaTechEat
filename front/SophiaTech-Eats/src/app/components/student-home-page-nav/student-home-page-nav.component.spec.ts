import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentHomePageNavComponent } from './student-home-page-nav.component';

describe('StudentHomePageNavComponent', () => {
  let component: StudentHomePageNavComponent;
  let fixture: ComponentFixture<StudentHomePageNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentHomePageNavComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentHomePageNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
