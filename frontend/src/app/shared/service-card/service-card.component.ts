import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-service-card',
  standalone: true,
  imports: [ButtonComponent, CommonModule],
  templateUrl: './service-card.component.html',
  styleUrl: './service-card.component.css'
})
export class ServiceCardComponent {
  @Input() profilePicture: any = '/quote.png';
  @Input() user: string = 'User';
  @Input() localisation: string = 'Localisation';
  @Input() date: Date = new Date();
  @Input() search: string | null = null;
  @Input() propose: string | null = null;
  @Input() description: string | null = null;

  get formattedDate(): string {
    const dateObj = typeof this.date === 'string' ? new Date(this.date) : this.date;
    return dateObj.toLocaleDateString(); // Exemple : "17/12/1995"
  }

   // Définir un événement de clic
    @Output() clicked = new EventEmitter<void>();

    // Méthode pour émettre l'événement de clic
    onClick() {
      this.clicked.emit();
    }

}
