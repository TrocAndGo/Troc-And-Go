// src/app/services/chat.service.ts
import { Injectable, inject } from '@angular/core';
import SockJS from 'sockjs-client';
import { Client, IMessage } from '@stomp/stompjs';
import { LocalStorageService } from '../services/local-storage.service';
import { Observable, Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

// Définition de ChatMessage
export interface ChatMessage {
  sender: string;
  receiver: string;
  content: string;
  timestamp?: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private stompClient!: Client;
  private storage = inject(LocalStorageService); // Injection du service de stockage local
  private http = inject(HttpClient); // Injection du client HTTP

  private messageSubject: Subject<ChatMessage> = new Subject<ChatMessage>();

  // URL de base pour les endpoints REST (à adapter si besoin)
  private baseApiUrl = 'https://localhost:8443/api/v1/chat';

  constructor() {
    this.initConnection();
  }

  private initConnection(): void {
    const authToken = this.storage.getItem('authToken'); // Récupération du token JWT
    const socketUrl = authToken
      ? `https://localhost:8443/ws-chat?token=${authToken}` // Ajout du token dans l'URL
      : 'https://localhost:8443/ws-chat';

    const socket = new SockJS(socketUrl);

    this.stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str), // Pour le débogage
      onConnect: () => {
        console.log('✅ WebSocket connected successfully!');
        // S'abonner à la destination personnelle pour recevoir les messages
        this.stompClient.subscribe('/user/queue/messages', (message: IMessage) => {
          if (message.body) {
            const chatMessage: ChatMessage = JSON.parse(message.body);
            this.messageSubject.next(chatMessage);
          }
        });
      },
      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame);
      },
      onWebSocketError: (error) => {
        console.error('❌ WebSocket error:', error);
      }
    });

    this.stompClient.activate();
  }

  // Méthode pour afficher une connexion (peut être utilisée pour des logs ou initialiser des abonnements)
  connect(username: string): void {
    console.log(`${username} connected to WebSocket`);
  }

  // Observable pour écouter les messages entrants
  onMessageReceived(): Observable<ChatMessage> {
    return this.messageSubject.asObservable();
  }

  // Récupération de l'historique des messages entre deux utilisateurs via l'API REST
  getConversation(currentUser: string, targetUser: string): Observable<ChatMessage[]> {
    const url = `${this.baseApiUrl}/conversation?user1=${currentUser}&user2=${targetUser}`;
    return this.http.get<ChatMessage[]>(url);
  }

  // Envoi d'un message via WebSocket
  sendMessage(message: ChatMessage): void {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.publish({
        destination: '/app/sendMessage', // Correspond au mapping @MessageMapping("/sendMessage") du backend
        body: JSON.stringify(message)
      });
    } else {
      console.error('Le client STOMP n\'est pas connecté');
    }
  }

  // Déconnexion du WebSocket
  disconnect(): void {
  if (this.stompClient?.connected) {
    this.stompClient.deactivate();
  }
}
}
