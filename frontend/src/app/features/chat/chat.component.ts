import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatMessage } from '../../services/chat.service';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, OnDestroy {
  currentUser: string = '';
   // Utilisateur connecté
  targetUser = ''; // Destinataire à saisir
  messageContent = '';
  messages: ChatMessage[] = [];

  constructor(
    private chatService: ChatService,
    private profileService: ProfileService  // Injection du ProfileService
  ) {
    // Récupérer le profil de l'utilisateur et assigner le nom d'utilisateur
    this.profileService.getUserProfile().subscribe({
      next: (data) => {
        this.currentUser = data.username || '';
        console.log('Utilisateur connecté :', this.currentUser);
      },
      error: (err) => {
        console.error('Erreur lors de la récupération du profil', err);
      }
    });
  }

  ngOnInit(): void {
    this.chatService.onMessageReceived().subscribe((msg: ChatMessage) => {
      if (this.shouldDisplayMessage(msg)) {
        this.messages.push(msg);
      }
    });
  }

  ngOnDestroy(): void {
    this.chatService.disconnect();
  }

  loadConversation(): void {
    if (!this.targetUser) return;

    this.chatService.getConversation(this.currentUser, this.targetUser).subscribe({
      next: (msgs) => this.messages = msgs,
      error: (err) => console.error('Erreur:', err)
    });
  }

  sendMessage(): void {
    if (this.messageContent.trim() && this.targetUser) {
      const message: ChatMessage = {
        sender: this.currentUser,
        receiver: this.targetUser,
        content: this.messageContent,
        timestamp: new Date()
      };

      this.chatService.sendMessage(message);
      this.messageContent = '';
    }
  }

  private shouldDisplayMessage(msg: ChatMessage): boolean {
    return (msg.sender === this.currentUser && msg.receiver === this.targetUser) ||
           (msg.sender === this.targetUser && msg.receiver === this.currentUser);
  }
}
