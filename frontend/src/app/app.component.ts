import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ButtonComponent } from './shared/button/button.component';
import { DropdownButtonComponent } from './shared/dropdown-button/dropdown-button.component';
import { ServiceCardComponent } from './shared/service-card/service-card.component';
import { HeaderComponent } from './shared/header/header.component'

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ButtonComponent, DropdownButtonComponent, ServiceCardComponent, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend';
  date = new Date();

  handleClick() {
    alert('Button clicked!');
  }
}
