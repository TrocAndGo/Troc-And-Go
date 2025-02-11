import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ConversationsService } from '../../services/conversations.service';
import { CommonModule } from '@angular/common';
import { ChatStateService } from '../../services/chat-state.service';

@Component({
  selector: 'app-conversations',
  standalone: true,
  imports: [CommonModule],  // Ajouter CommonModule ici
  templateUrl: './conversations.component.html',
  styleUrls: ['./conversations.component.css']
})
export class ConversationsComponent implements OnInit {
  conversations: string[] = [];

  constructor(
    private router: Router,
    private conversationsService: ConversationsService,
    private chatStateService: ChatStateService
  ) {}

  ngOnInit(): void {
    this.conversationsService.getConversations().subscribe({
      next: (data) => this.conversations = data,
      error: (err) => console.error('Erreur lors du chargement des conversations', err)
    });
  }

  openChat(user: string): void {
    this.chatStateService.setTargetUser(user);  // Sauvegarder le targetUser dans le ChatStateService
    this.router.navigate(['/chat']);  // Naviguer vers la page de chat sans passer d'arguments
  }
}
