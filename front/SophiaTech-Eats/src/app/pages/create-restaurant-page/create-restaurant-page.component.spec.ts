import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRestaurantPageComponent } from './create-restaurant-page.component';

describe('CreateRestaurantPageComponent', () => {
  let component: CreateRestaurantPageComponent;
  let fixture: ComponentFixture<CreateRestaurantPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateRestaurantPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateRestaurantPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
