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
  { path: '',
    component: HomeComponent,
    title: 'Troc and Go - Accueil'
  }, // Page d'accueil
  { path: 'about',
    component: AboutComponent,
    title: 'Troc and Go - A propos'
  },
  { path: 'contact',
    component: ContactComponent,
    title: 'Troc and Go - Contact'
  },
  { path: 'profil',
    component: ProfilComponent,
    canActivate: [AuthGuardService],
    title: 'Troc and Go - Mon profil'
  },
  { path: 'annonce',
    component: CreateAdComponent,
    canActivate: [AuthGuardService],
    title: 'Troc and Go - Créer une annonce'
  },
  { path: 'my-ads',
    component: MyAdsComponent,
    canActivate: [AuthGuardService],
    title: 'Troc and Go - Mes annonces'
  },
  { path: 'recherche',
    component: SearchComponent,
    title: 'Troc and Go - Recherche'
  },
  { path: 'favorites',
    component: FavoritesComponent,
    canActivate: [AuthGuardService],
    title: 'Troc and Go - Favoris'
  },
  { path: '**', redirectTo: '' },         // Redirection vers l'accueil pour les URLs invalides
];
