import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDishPageComponent } from './update-dish-page.component';

describe('UpdateDishPageComponent', () => {
  let component: UpdateDishPageComponent;
  let fixture: ComponentFixture<UpdateDishPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateDishPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateDishPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
