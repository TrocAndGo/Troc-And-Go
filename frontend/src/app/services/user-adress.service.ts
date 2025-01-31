import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ProfileService } from './profile.service';

@Injectable({
  providedIn: 'root'
})
export class UserAdressService {
  private userAdressSubject = new BehaviorSubject<string | null>(null);
  userAdress$ = this.userAdressSubject.asObservable();

  constructor(private profileService: ProfileService) {
  }

  loadUserAdress(): void {
    console.log('Chargement de l\'adresse utilisateur...');
    this.profileService.getUserProfile().subscribe({
      next: (data) => {
        console.log('Données utilisateur récupérées :', data);
        let adress: string | null = null;
        if (data.address && data.zipCode && data.city){
          adress = `${data.address} ${data.zipCode} ${data.city}`;
        }
        this.userAdressSubject.next(adress);
        console.log('Adresse émise :', adress);
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des données utilisateur', err);
        this.userAdressSubject.next(null);
      },
    });
  }
    getUserAdress(): string | null {
      return this.userAdressSubject.getValue();
    }
  }

