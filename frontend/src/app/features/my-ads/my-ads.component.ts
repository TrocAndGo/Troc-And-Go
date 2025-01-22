import { Component, OnInit } from '@angular/core';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';
import { AdService } from '../../services/ad.service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-my-ads',
  standalone: true,
  imports: [ServiceCardComponent, CommonModule],
  templateUrl: './my-ads.component.html',
  styleUrl: './my-ads.component.css'
})
export class MyAdsComponent implements OnInit {
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
