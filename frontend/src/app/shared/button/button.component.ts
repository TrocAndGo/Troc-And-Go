import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './button.component.html',
  styleUrl: './button.component.css'
})
export class ButtonComponent {
  // Définir des entrées pour les propriétés du bouton
  @Input() text: string = 'Button';  // Texte du bouton, par défaut "Button"
  @Input() type: 'button' | 'submit' | 'reset' = 'button'; // Type du bouton, par défaut 'button'
  @Input() color: string = 'primary'; // Couleur du bouton, par défaut 'primary'
  @Input() disabled: boolean = false; // Indicateur si le bouton est désactivé

  // Définir un événement de clic
  @Output() clicked = new EventEmitter<void>();

  // Méthode pour émettre l'événement de clic
  onClick() {
    this.clicked.emit();
  }
}
