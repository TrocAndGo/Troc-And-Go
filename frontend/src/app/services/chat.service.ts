// src/app/services/chat.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { distinctUntilChanged, EMPTY, Observable, Subject, Subscription, switchMap } from 'rxjs';
import SockJS from 'sockjs-client';
import { LocalStorageService } from '../services/local-storage.service';
import { AuthService } from './auth.service';
import { ProfileService } from './profile.service';

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
  private stompClient: Client | null = null;
  private storage = inject(LocalStorageService); // Injection du service de stockage local
  private http = inject(HttpClient); // Injection du client HTTP
  private profile = inject(ProfileService);
  private auth = inject(AuthService);

  private messageSubject: Subject<ChatMessage> = new Subject<ChatMessage>();
  private subscriptions = new Subscription();

  constructor() {
    // INITIALISER uniquement quand l'état de connexion change
    this.subscriptions.add(
      this.auth.loggedIn$.pipe(
        distinctUntilChanged(),
        switchMap(loggedIn => {
          if (loggedIn) {
            console.log("🔄 Utilisateur connecté, récupération du profil...");
            return this.profile.getUserProfile();
          } else {
            console.log("🔴 Utilisateur déconnecté, fermeture du WebSocket.");
            this.disconnect();
            return EMPTY;
          }
        })
      ).subscribe(profile => {
        if (profile && profile.username) {
          console.log("✅ Connexion WebSocket pour :", profile.username);
          this.initConnection(profile.username);
        }
      })
    );
  }
/*
  public getProfileAndInitConnection(): void {
    if (this.auth.isLoggedIn() == false) {
      this.disconnect();
      return;
    }

    this.profile.getUserProfile().subscribe((profile) =>{
      this.initConnection(profile.username);
    });
  }
*/
  private initConnection(username: string): void {
    if (this.stompClient?.connected) return;

    const authToken = this.storage.getItem('authToken'); // Récupération du token JWT
    const socketUrl = authToken
      ? `https://localhost:8443/ws-chat?token=${authToken}` // Ajout du token dans l'URL
      : 'https://localhost:8443/ws-chat';

    const socket = new SockJS(socketUrl);

    this.stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str), // Pour le débogage
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('✅ WebSocket connected successfully!');
        // S'abonner à la destination personnelle pour recevoir les messages
        this.stompClient?.subscribe(`/user/${username}/queue/messages`, (message: IMessage) => {
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
    const url = `/chat/conversation?user1=${currentUser}&user2=${targetUser}`;
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
    if (this.stompClient) {
      console.log('🔴 Déconnexion du WebSocket...');
      this.stompClient.deactivate();
      this.stompClient = null;
      console.log('✅ WebSocket déconnecté avec succès !');
    }
  }

  ngOnDestroy() {
    //this.subscriptions.unsubscribe();
    //this.disconnect();
  }
}
