import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SearchResult, SearchService } from '../../services/search.service';
import { SearchBarComponent } from '../../shared/search-bar/search-bar.component';
import { Coords, ServiceCardComponent } from '../../shared/service-card/service-card.component';

import { isPlatformBrowser } from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ServiceCardComponent, SearchBarComponent, CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  services: SearchResult[] = [];
  count: number = 0;
  isLoggedIn: boolean = false;
    showCoordsModal: boolean = false;
    coords: Coords | null = null;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private router: Router,
    private searchService: SearchService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {  // ✅ Empêche l'exécution côté serveur
      this.getHomePageServices();
      this.isLoggedIn = this.authService.isLoggedIn();
      this.authService.loggedIn$.subscribe({
        next: (newLoggedInValue) => {
          if (!this.isLoggedIn && newLoggedInValue) {
            this.getHomePageServices();
          }
          this.isLoggedIn = newLoggedInValue;
        },
        error: (err) => {
          console.error('Erreur lors de la vérification de l’état de connexion :', err);
        }
      });
    }
  }


  onShowCoords(coords: Coords) {
    this.coords = coords;
    this.showCoordsModal = true;
  }

  closeCoordsModal() {
    this.showCoordsModal = false;
  }

  getHomePageServices() {
    this.searchService.search({ size: 3 }).subscribe({
      next: (resultPageable) => {
        if (resultPageable && resultPageable.content) {
          this.services = resultPageable.content;
          this.count = resultPageable.page?.totalElements || 0;

          this.services.forEach((service) => {
            if (service.creatorProfilePicture) {
              this.downloadImage(service);
            }
          });
        }
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des services :', err);
      }
    });
  }

  downloadImage(result: SearchResult) {
    if (!result.creatorProfilePicture || result.creatorProfilePicture.startsWith('data:image/')) {
      return; // ✅ Déjà chargée ou pas d'image
    }

    this.searchService.getImageBlob(result.creatorProfilePicture).subscribe({
      next: (blob) => {
        const reader = new FileReader();
        reader.onload = () => {
          result.creatorProfilePicture = reader.result as string;
        };
        reader.onerror = (e) => {
          console.error('Erreur lors de la lecture du blob :', e);
        };
        reader.readAsDataURL(blob);
      },
      error: (err) => {
        console.error(`Erreur lors du téléchargement de l'image pour ${result.creatorProfilePicture}:`, err);
      }
    });
  }


  onSearch(form: FormGroup) {
    console.log(form, typeof form);

    this.router.navigate(['/recherche'], {
      queryParams: {
        category: form.value.category
      }
    });
  }
}
