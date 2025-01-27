import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FavoritesService } from '../../services/favorites.service';
import { SearchResult, SearchService } from '../../services/search.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';
import { Page } from '../../utils/PageableResponse';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule, ServiceCardComponent, PaginationComponent],
  templateUrl: './favorites.component.html',
  styleUrl: './favorites.component.css'
})
export class FavoritesComponent implements OnInit {
  services: SearchResult[] = [];
  page: Page | null = null;
  currentPage: number = 0;

  constructor(private favoritesService: FavoritesService, private searchService: SearchService) { }

  ngOnInit(): void {
    this.getFavorites();
  }

  handleClick() {
    alert('Button clicked!');
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.getFavorites();
  }

  getFavorites(): void {
    this.favoritesService.getFavorites(this.currentPage).subscribe({
      next: (data) => {
        this.services = data.content;
        this.page = data.page;

        // Télécharge les images pour chaque résultat
        this.services.forEach((result) => {
          if (result.creatorProfilePicture) {
            this.downloadImage(result);
          }
        });
      },
      error: (error) => {
        console.error('Erreur lors de la récupération du service', error);
      }
    });
  }

  downloadImage(result: SearchResult) {
    this.searchService.getImageBlob(result.creatorProfilePicture!).subscribe((blob) => {
      const reader = new FileReader();

      // Converti le blob en URL local pour l'affichage
      reader.onload = () => {
        result.creatorProfilePicture = reader.result as string;
      };

      reader.readAsDataURL(blob);
    });
  }
}
