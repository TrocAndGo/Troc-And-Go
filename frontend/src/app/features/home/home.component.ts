import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { SearchBarComponent } from '../../shared/search-bar/search-bar.component';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ServiceCardComponent, SearchBarComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  date = new Date();

  constructor(private router: Router) {}

  handleClick() {
    alert('Button clicked!');
  }

  onSearch(form: FormGroup) {
    console.log(form, typeof form);

    this.router.navigate(['/recherche'], {
      queryParams: {
        area: form.value.area,
        category: form.value.category
      }
    });
  }
}
