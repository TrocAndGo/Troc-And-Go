import { Routes } from '@angular/router';
import { AboutComponent } from './features/about/about.component';
import { ContactComponent } from './features/contact/contact.component';
import { CreateAdComponent } from './features/create-ad/create-ad.component';
import { HomeComponent } from './features/home/home.component';
import { MyAdsComponent } from './features/my-ads/my-ads.component';
import { ProfilComponent } from './features/profil/profil.component';
import { SearchComponent } from './features/search/search.component';
import { FavoritesComponent } from './features/favorites/favorites.component';

export const routes: Routes = [
  { path: '', component: HomeComponent }, // Page d'accueil
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'profil', component: ProfilComponent },
  { path: 'annonce', component: CreateAdComponent},
  { path: 'my-ads', component: MyAdsComponent},
  { path: 'recherche', component: SearchComponent },
  { path: 'my-favorites', component: FavoritesComponent},
  { path: '**', redirectTo: '' },         // Redirection vers l'accueil pour les URLs invalides
];
