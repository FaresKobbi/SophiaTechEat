import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantMenuPageComponent } from './restaurant-menu-page.component';

describe('RestaurantMenuPageComponent', () => {
  let component: RestaurantMenuPageComponent;
  let fixture: ComponentFixture<RestaurantMenuPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantMenuPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestaurantMenuPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
