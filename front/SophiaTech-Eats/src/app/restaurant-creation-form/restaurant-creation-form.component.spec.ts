import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantCreationFormComponent } from './restaurant-creation-form.component';

describe('RestaurantCreationFormComponent', () => {
  let component: RestaurantCreationFormComponent;
  let fixture: ComponentFixture<RestaurantCreationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantCreationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantCreationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
