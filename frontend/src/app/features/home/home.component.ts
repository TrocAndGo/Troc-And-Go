import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SearchResult, SearchService } from '../../services/search.service';
import { SearchBarComponent } from '../../shared/search-bar/search-bar.component';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';

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

  constructor(
    private router: Router,
    private searchService: SearchService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.getHomePageServices();

    this.isLoggedIn = this.authService.isLoggedIn();
    this.authService.loggedIn$.subscribe((newLoggedInValue) => {
      if (!this.isLoggedIn && newLoggedInValue)
        this.getHomePageServices();
      this.isLoggedIn = newLoggedInValue;
    });
  }

  getHomePageServices() {
    this.searchService.search({
      size: 3
    }).subscribe((resultPageable) => {
      this.services = resultPageable.content;
      this.count = resultPageable.page.totalElements;
      // Télécharge les images pour chaque résultat
      this.services.forEach((service) => {
        if (service.creatorProfilePicture) {
          this.downloadImage(service);
        }
      });
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

  onSearch(form: FormGroup) {
    console.log(form, typeof form);

    this.router.navigate(['/recherche'], {
      queryParams: {
        category: form.value.category
      }
    });
  }
}
