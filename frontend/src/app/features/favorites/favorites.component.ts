import { Component, OnInit } from '@angular/core';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';
import { AdService } from '../../services/ad.service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule, ServiceCardComponent],
  templateUrl: './favorites.component.html',
  styleUrl: './favorites.component.css'
})
export class FavoritesComponent implements OnInit{
  services: any[] = [];

    constructor(private adService: AdService) {}

    ngOnInit(): void {
      this.getAllServices();
    }

    handleClick() {
      alert('Button clicked!');
    }

    getAllServices(): void {
      this.adService.getServices().subscribe(
        (data) => {
          this.services = data.content || data;
        },
        (error) => {
          console.error('Erreur lors de la récupération du service', error);
        }
      );
    }
  }
