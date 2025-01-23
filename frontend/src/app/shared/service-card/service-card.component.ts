import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { ButtonComponent } from '../button/button.component';
import { FavoritesService } from '../../services/favorites.service';

@Component({
  selector: 'app-service-card',
  standalone: true,
  imports: [ButtonComponent, CommonModule,],
  templateUrl: './service-card.component.html',
  styleUrl: './service-card.component.css',
})
export class ServiceCardComponent {
  @Input() id!: string;
  @Input() profilePicture: any = '/quote.png';
  @Input() user: string = 'User';
  @Input() localisation: string = 'Localisation';
  @Input() date: Date = new Date();
  @Input() propose: string = 'Service proposé';
  @Input() title: string = 'Title';
  @Input() categorie: string = 'Categorie';
  @Input() mail: string = 'Mail';
  @Input() phoneNumber: string = 'phoneNumber';

  get formattedDate(): string {
    const dateObj = typeof this.date === 'string' ? new Date(this.date) : this.date;
    return dateObj.toLocaleDateString(); // Exemple : "17/12/1995"
  }

  isLoggedIn = false;
  isCoordinatesVisible = false;
  isFavorite = false;

  toggleCoordinates() {
    this.isCoordinatesVisible = !this.isCoordinatesVisible;
  }

  creationDate: Date; // Déclaration explicite de la propriété
  constructor(public authService: AuthService, private favoritesService: FavoritesService) {
    this.creationDate = new Date(); // Enregistre la date actuelle
  }

  ngOnInit() {
    // S'abonner à l'état de connexion
    this.authService.loggedIn$.subscribe((status) => {
      this.isLoggedIn = status;
    });
  }

   // Définir un événement de clic
    @Output() clicked = new EventEmitter<void>();

    // Méthode pour émettre l'événement de clic
    onClick() {
      this.clicked.emit();
    }

  addToFavorites(serviceId: string): void {
    this.favoritesService.addFavorite(serviceId).subscribe({
      next: (response) => {
        console.log('Favorite added successfully:', response);
        this.isFavorite = true;
      },
      error: (error) => {
        console.error('Error adding favorite:', error.message);
      }
    });
  }

  removeFromFavorites(serviceId: string): void {
    this.favoritesService.removeFavorite(serviceId).subscribe({
      next: (response) => {
        console.log('Favorite removed successfully:', response);
        this.isFavorite = false;
      },
      error: (error) => {
        console.error('Error removing favorite:', error.message);
      }
    });
  }

}
