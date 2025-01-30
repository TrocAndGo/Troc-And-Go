import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, output, Output } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { AdService } from '../../services/ad.service';
import { AuthService } from '../../services/auth.service';
import { FavoritesService } from '../../services/favorites.service';
import { ButtonComponent } from '../button/button.component';

export type Coords = {
  user: string;
  mail: string;
  phoneNumber: string;
};

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
  @Input() owner: boolean = false;
  @Input() isFavorite: boolean = false;

  @Output() onShowCoords = new EventEmitter<Coords>();

  get formattedDate(): string {
    const dateObj = typeof this.date === 'string' ? new Date(this.date) : this.date;
    return dateObj.toLocaleDateString(); // Exemple : "17/12/1995"
  }

  isLoggedIn = false;
  isCoordinatesVisible = false;

  toggleCoordinates() {
    this.isCoordinatesVisible = !this.isCoordinatesVisible;
    this.onShowCoords.emit({user: this.user, mail: this.mail, phoneNumber: this.phoneNumber});
  }

  constructor(
    public authService: AuthService,
    private favoritesService: FavoritesService,
    private adService: AdService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    // S'abonner à l'état de connexion
    this.authService.loggedIn$.subscribe((status) => {
      this.isLoggedIn = status;
    });
  }

   // Définir un événement de clic
    @Output() clicked = new EventEmitter<void>();
    @Output() deleted = new EventEmitter<void>();
    @Output() favorited = new EventEmitter<void>();
    @Output() unfavorited = new EventEmitter<void>();

    // Méthode pour émettre l'événement de clic
    onClick() {
      this.clicked.emit();
    }

  addToFavorites(serviceId: string): void {
    this.favoritesService.addFavorite(serviceId).subscribe({
      next: (response) => {
        console.log('Favorite added successfully:', response);
        this.isFavorite = true;
        this.favorited.emit();
      },
      error: (error) => {
        console.error('Error adding favorite:', error.message);
        this.toastr.error('Une erreur est survenue.');
      }
    });
  }

  @Output() favoriteRemoved = new EventEmitter<string>();

  removeFromFavorites(serviceId: string): void {
    this.favoritesService.removeFavorite(serviceId).subscribe({
      next: (response) => {
        console.log('Favorite removed successfully:', response);
        this.isFavorite = false;
        this.favoriteRemoved.emit(serviceId);
      },
      error: (error) => {
        console.error('Error removing favorite:', error.message);
        this.toastr.error('Une erreur est survenue.');
      }
    });
  }

  deleteService(serviceId: string): void {
    if(!confirm('Are you sure you want to delete this service?')) return;

    this.adService.deleteService(serviceId).subscribe({
      next: (_) => {
        this.toastr.success('Service supprimé avec succès.');
        this.deleted.emit();
      },
      error: (error) => {
        console.error('Error deleting service:', error.message);
        this.toastr.error('Une erreur est survenue.');
      }
    });
  }
}
