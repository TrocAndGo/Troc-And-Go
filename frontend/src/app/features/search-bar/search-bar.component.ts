import { Component } from '@angular/core';
import { DropdownButtonComponent } from '../../shared/dropdown-button/dropdown-button.component'

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [DropdownButtonComponent],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent {

}
