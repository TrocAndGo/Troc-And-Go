import { Routes } from '@angular/router';
import { AboutComponent } from './features/about/about.component';
import { ContactComponent } from './features/contact/contact.component';
import { CreateAdComponent } from './features/create-ad/create-ad.component';
import { FavoritesComponent } from './features/favorites/favorites.component';
import { HomeComponent } from './features/home/home.component';
import { MyAdsComponent } from './features/my-ads/my-ads.component';
import { ProfilComponent } from './features/profil/profil.component';
import { SearchComponent } from './features/search/search.component';
import { AuthGuardService } from './services/auth-guard.service';

export const routes: Routes = [
  { path: '', component: HomeComponent }, // Page d'accueil
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'profil', component: ProfilComponent, canActivate: [AuthGuardService] },
  { path: 'annonce', component: CreateAdComponent, canActivate: [AuthGuardService] },
  { path: 'my-ads', component: MyAdsComponent, canActivate: [AuthGuardService] },
  { path: 'recherche', component: SearchComponent },
  { path: 'favorites', component: FavoritesComponent, canActivate: [AuthGuardService] },
  { path: '**', redirectTo: '' },         // Redirection vers l'accueil pour les URLs invalides
];
