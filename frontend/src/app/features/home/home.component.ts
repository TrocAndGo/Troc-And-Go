import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ServiceCardComponent } from '../../shared/service-card/service-card.component';
import { SearchBarComponent } from '../search-bar/search-bar.component'

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ServiceCardComponent, SearchBarComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  date = new Date();

  handleClick() {
    alert('Button clicked!');
  }
}
