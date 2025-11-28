import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DishDetailModalComponent } from './dish-detail-modal.component';

describe('DishDetailModalComponent', () => {
  let component: DishDetailModalComponent;
  let fixture: ComponentFixture<DishDetailModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DishDetailModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DishDetailModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
