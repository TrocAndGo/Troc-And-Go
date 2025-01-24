import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AdService } from '../../services/ad.service';
import { ImageManagementService } from '../../services/image-management.service';
import { SearchResult } from '../../services/search.service';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';
import { Page } from '../../utils/PageableResponse';

@Component({
  selector: 'app-my-ads',
  standalone: true,
  imports: [ServiceCardComponent, CommonModule],
  templateUrl: './my-ads.component.html',
  styleUrl: './my-ads.component.css'
})
export class MyAdsComponent implements OnInit {
  services: SearchResult[] = [];
  page: Page | null = null;
  profilePictureUrl: string = 'icone.jpg';

  constructor(private adService: AdService, private imageService: ImageManagementService) {}

  ngOnInit(): void {
    this.getMyServices();
    this.getProfilePicture();
  }

  onServiceDeleted(): void {
    this.getMyServices();
  }

  getMyServices(): void {
    this.adService.getMyServices().subscribe({
      next: (results) => {
        this.services = results.content;
        this.page = results.page;
      },
      error: (error) => {
        console.error('Erreur lors de la récupération du service', error);
      }
    });
  }

  getProfilePicture() {
    this.profilePictureUrl = this.imageService.avatarUrlSubject.getValue();
    this.imageService.avatarUrl$.subscribe((url) => {
      this.profilePictureUrl = url;
    });
  }
}
