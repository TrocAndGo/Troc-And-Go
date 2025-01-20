import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SearchResponse, SearchService } from '../../services/search.service';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';
import { SearchBarComponent } from '../search-bar/search-bar.component';

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

  constructor(private route: ActivatedRoute, private router: Router, private searchService: SearchService) {}

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

  get resultCount(): number {
    return this.results ? this.results.page.totalElements : 0;
  }

  private loadSearchResults() {
    this.searchService.search({
      region: this.region,
      department: this.department,
      city: this.city,
      category: this.category,
      sort: `${this.sort},${this.sortDir}`,
      size: 20
    }).subscribe(results => this.results = results);
  }

}
