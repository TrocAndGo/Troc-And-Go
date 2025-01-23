import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SearchResponse, SearchService, SearchResult } from '../../services/search.service';
import { SearchBarComponent } from '../../shared/search-bar/search-bar.component';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [ServiceCardComponent, SearchBarComponent, CommonModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
  region!: string;
  department!: string;
  city!: string;
  category!: string;
  sort!: string;
  sortDir!: string;
  results!: SearchResponse;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.region = params.get('region') || '';
      this.department = params.get('departement') || '';
      this.city = params.get('ville') || '';
      this.category = params.get('category') || '';
      this.sort = params.get('sort') || 'creationDate';
      this.sortDir = params.get('sortDir') || 'desc';
      this.loadSearchResults();
    });
  }

  onSearch(form: FormGroup): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        region: form.value.region,
        departement: form.value.departement,
        ville: form.value.ville,
        category: form.value.category
      },
      queryParamsHandling: 'merge',
    });
  }

  get resultCount(): number {
    return this.results ? this.results.page.totalElements : 0;
  }

  private loadSearchResults() {
    this.searchService
      .search({
        region: this.region,
        department: this.department,
        city: this.city,
        category: this.category,
        sort: `${this.sort},${this.sortDir}`,
        size: 20,
      })
      .subscribe((results) => {
        this.results = results;

        // Télécharge les images pour chaque résultat
        this.results.content.forEach((result) => {
          if (result.creatorProfilePicture) {
            this.downloadImage(result);
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

}
