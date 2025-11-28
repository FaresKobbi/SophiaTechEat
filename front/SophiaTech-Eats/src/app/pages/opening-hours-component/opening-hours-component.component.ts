import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestaurantService, OpeningHours, TimeSlot } from '../../services/restaurant/restaurant.service';
import { ListComponent } from '../../components/item-list/item-list.component';

@Component({
  selector: 'app-opening-hours',
  standalone: true,
  imports: [CommonModule, FormsModule, ListComponent],
  templateUrl: './opening-hours-component.component.html',
  styleUrls: ['./opening-hours-component.component.css']
})
export class OpeningHoursComponent implements OnInit {

  openingHours: OpeningHours[] = [];

  formattedHoursList: any[] = [];

  selectedHour: OpeningHours | null = null;

  newHour = {
    day: 'MONDAY',
    openingTime: '12:00',
    closingTime: '14:00'
  };

  daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  constructor(private restaurantService: RestaurantService) {}

  ngOnInit(): void {
    this.loadData();
  }



  loadData() {
    const restaurantId = this.restaurantService.getSelectedRestaurant()?.restaurantId || '';

    this.restaurantService.getOpeningHours(restaurantId).subscribe({
      next: (data) => {
        this.openingHours = data;

        this.formattedHoursList = this.openingHours.map(h => ({
          ...h,
          displayLabel: `${h.day} : ${h.openingTime} - ${h.closingTime}`
        }));

        if (this.selectedHour) {
          this.selectedHour = this.formattedHoursList.find(h => h.day === this.selectedHour?.day) || null;
        }
      },
      error: (err) => console.error('Erreur chargement horaires', err)
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
      error: (err) => alert("Erreur lors de l'ajout (Vérifiez que le créneau n'existe pas déjà)")
    });
  }

  deleteSelectedHour() {
    if (!this.selectedHour) return;

    if(confirm(`Supprimer les horaires de ${this.selectedHour.day} ?`)) {
      const restaurantId = this.restaurantService.getSelectedRestaurant()?.restaurantId || '';

      this.restaurantService.deleteOpeningHour(restaurantId, this.selectedHour.day).subscribe({
        next: () => {
          this.selectedHour = null;
          this.loadData();         },
        error: (err) => console.error("Erreur suppression", err)
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
      next: () => console.log('Capacité sauvegardée !'),
      error: (err) => console.error('Erreur sauvegarde capacité', err)
    });
  }
}
