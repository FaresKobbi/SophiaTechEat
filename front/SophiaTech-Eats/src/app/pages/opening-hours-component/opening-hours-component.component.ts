import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { RestaurantService, OpeningHours, TimeSlot } from '../../services/restaurant/restaurant.service';
import { ListComponent } from '../../components/item-list/item-list.component';

@Component({
  selector: 'app-opening-hours',
  standalone: true,
  imports: [CommonModule, FormsModule, ListComponent, RouterModule],
  templateUrl: './opening-hours-component.component.html',
  styleUrls: ['./opening-hours-component.component.css']
})
export class OpeningHoursComponent implements OnInit {

  openingHours: OpeningHours[] = [];
  formattedHoursList: any[] = [];
  selectedHour: any | null = null;

  restaurantId: string = '';

  newHour = {
    day: 'MONDAY',
    openingTime: '12:00',
    closingTime: '14:00'
  };

  daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  constructor(private restaurantService: RestaurantService) {}

  ngOnInit(): void {
    this.restaurantId = this.restaurantService.getSelectedRestaurant()?.restaurantId || '';
    this.loadData();
  }

  private formatTime12H(time24: any): string {
    if (!time24) return '';

    let hours: number;
    let minutesStr: string;

    if (Array.isArray(time24)) {
      hours = time24[0];
      const m = time24[1];
      minutesStr = m < 10 ? '0' + m : '' + m;
    }
    else if (typeof time24 === 'string') {
      const parts = time24.split(':');
      hours = parseInt(parts[0], 10);
      minutesStr = parts[1] || '00';
    }
    else {
      return '';
    }

    const suffix = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12 || 12;

    return `${hours}:${minutesStr} ${suffix}`;
  }

  loadData() {
    this.restaurantService.getOpeningHours(this.restaurantId).subscribe({
      next: (data) => {
        this.openingHours = data;

        this.formattedHoursList = this.openingHours.map(h => {
          const startAMPM = this.formatTime12H(h.openingTime);
          const endAMPM = this.formatTime12H(h.closingTime);

          return {
            ...h,
            startDisplay: startAMPM,
            endDisplay: endAMPM,
            displayLabel: `${h.day} : ${startAMPM} - ${endAMPM}`
          };
        });

        if (this.selectedHour) {
          this.selectedHour = this.formattedHoursList.find(h => h.id === this.selectedHour?.id) || null;
        }
      },
      error: (err) => console.error('Error loading hours', err)
    });
  }

  onHourSelect(item: any) {
    this.selectedHour = item;
  }

  addHour() {
    const restaurantId = this.restaurantService.getSelectedRestaurant()?.restaurantId || '';

    const payload = {
      day: this.newHour.day,
      openingTime: this.newHour.openingTime,
      closingTime: this.newHour.closingTime
    };

    this.restaurantService.addOpeningHour(restaurantId, payload).subscribe({
      next: () => {
        this.loadData();
      },
      error: (err) => alert("Error adding schedule. Please check if it already exists.")
    });
  }

  deleteSelectedHour() {
    if (!this.selectedHour) return;

    if(confirm(`Delete schedule for ${this.selectedHour.day}?`)) {
      const restaurantId = this.restaurantService.getSelectedRestaurant()?.restaurantId || '';

      this.restaurantService.deleteOpeningHour(restaurantId, this.selectedHour.id).subscribe({
        next: () => {
          this.selectedHour = null;
          this.loadData();
        },
        error: (err) => console.error("Deletion error", err)
      });
    }
  }

  updateCapacity(slot: TimeSlot) {
    if (!this.selectedHour) return;
    const restaurantId = this.restaurantService.getSelectedRestaurant()?.restaurantId || '';

    this.restaurantService.updateSlotCapacity(
      restaurantId,
      this.selectedHour.day,
      slot.startTime,
      slot.endTime,
      slot.capacity
    ).subscribe({
      next: () => console.log('Capacity updated!'),
      error: (err) => console.error('Error updating capacity', err)
    });
  }

  getSlotDisplay(time: string): string {
    return this.formatTime12H(time);
  }
}
