import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatMessage } from '../../services/chat.service';
import { ProfileService } from '../../services/profile.service';
import { ChatStateService } from '../../services/chat-state.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer: ElementRef | undefined; // Référence au conteneur des messages
  currentUser: string = ''; // Utilisateur connecté
  targetUser = ''; // Destinataire à saisir
  messageContent = '';
  messages: ChatMessage[] = [];
  lastMessageDate: Date | null = null;

  constructor(
    private chatService: ChatService,
    private profileService: ProfileService,
    private chatState: ChatStateService
  ) {}

  ngOnInit(): void {
    // Récupérer le username connecté
    this.profileService.getUserProfile().subscribe({
      next: (data) => {
        this.currentUser = data.username || '';
        console.log('Utilisateur connecté :', this.currentUser);

        // Récupération du targetUser
        this.targetUser = this.chatState.getTargetUser();
        console.log('Chat avec :', this.targetUser);

        // Charger la conversation SEULEMENT si currentUser ET targetUser sont définis
        if (this.currentUser && this.targetUser) {
          this.loadConversation();
        }
      },
      error: (err) => {
        console.error('Erreur lors de la récupération du profil', err);
      }
    });

    // Écouter les nouveaux messages en temps réel
    this.chatService.onMessageReceived().subscribe((msg: ChatMessage) => {
      if (this.shouldDisplayMessage(msg)) {
        this.messages.push(msg);
        this.scrollToBottom(); // Faire défiler vers le bas quand un nouveau message est reçu
      }
    });
  }

  ngOnDestroy(): void {
    // Si tu as une logique de déconnexion, tu pourrais l'ajouter ici
    // this.chatService.disconnect();
  }

  ngAfterViewChecked(): void {
    // Faire défiler vers le bas après chaque vérification de la vue, surtout après l'ajout de nouveaux messages
    this.scrollToBottom();
  }

  loadConversation(): void {
    if (!this.targetUser) return;

    this.chatService.getConversation(this.currentUser, this.targetUser).subscribe({
      next: (msgs) => {
        this.messages = msgs;
        this.scrollToBottom(); // Faire défiler vers le bas après avoir chargé la conversation
      },
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
      this.scrollToBottom(); // Faire défiler vers le bas après l'envoi d'un message
    }
  }

  private shouldDisplayMessage(msg: ChatMessage): boolean {
    return (msg.sender === this.currentUser && msg.receiver === this.targetUser) ||
           (msg.sender === this.targetUser && msg.receiver === this.currentUser);
  }

  private scrollToBottom(): void {
    if (this.messagesContainer) {
      const container = this.messagesContainer.nativeElement;
      container.scrollTop = container.scrollHeight; // Faire défiler vers le bas
    }
  }
}
