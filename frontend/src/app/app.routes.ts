import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AboutComponent } from './features/about/about.component';
import { ContactComponent } from './features/contact/contact.component';
import { ProfilComponent } from './features/profil/profil.component';
import { CreateAdComponent } from './features/create-ad/create-ad.component';

export const routes: Routes = [
  { path: '', component: HomeComponent }, // Page d'accueil
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'profil', component: ProfilComponent },
  { path: 'annonce', component: CreateAdComponent},
  { path: '**', redirectTo: '' },         // Redirection vers l'accueil pour les URLs invalides
];
