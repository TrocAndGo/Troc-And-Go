import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dropdown-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dropdown-button.component.html',
  styleUrl: './dropdown-button.component.css'
})
export class DropdownButtonComponent {
  @Input() text: string = 'Click me';
  @Input() color: string = 'primary';
  @Input() items: string[] = [];
  @Input() disabled: boolean = false;

  dropdownOpen: boolean = false;

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  selectItem(item: string) {
    console.log('Item selected:', item);
    this.dropdownOpen = false;
  }

}
