import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRestaurantFormComponent } from './create-restaurant-form.component';

describe('CreateRestaurantFormComponent', () => {
  let component: CreateRestaurantFormComponent;
  let fixture: ComponentFixture<CreateRestaurantFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateRestaurantFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateRestaurantFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
