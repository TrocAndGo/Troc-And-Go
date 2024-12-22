import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-service-card',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './service-card.component.html',
  styleUrl: './service-card.component.css'
})
export class ServiceCardComponent {
  @Input() cheminImage:any = '/quote.png';
  @Input() user: string = 'User';
  @Input() localisation: string = 'Localisation';
  @Input() date: Date = new Date();
  @Input() search: string = 'Service recherché';
  @Input() propose: string = 'Service proposé';

  get formattedDate(): string {
    return this.date.toLocaleDateString(); // Exemple : "17/12/1995"
  }

   // Définir un événement de clic
    @Output() clicked = new EventEmitter<void>();

    // Méthode pour émettre l'événement de clic
    onClick() {
      this.clicked.emit();
    }

}
