import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SuggestionBoxComponent } from './suggestion-box.component';
import { RestaurantService } from '../../services/restaurant/restaurant.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('SuggestionBoxComponent', () => {
    let component: SuggestionBoxComponent;
    let fixture: ComponentFixture<SuggestionBoxComponent>;
    let mockRestaurantService: any;

    beforeEach(async () => {
        mockRestaurantService = {
            getSuggestions: jasmine.createSpy('getSuggestions').and.returnValue(of([]))
        };

        await TestBed.configureTestingModule({
            imports: [SuggestionBoxComponent, FormsModule],
            providers: [
                { provide: RestaurantService, useValue: mockRestaurantService }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(SuggestionBoxComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
