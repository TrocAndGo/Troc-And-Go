import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ChatStateService {
  private targetUser: string = '';

  setTargetUser(user: string) {
    this.targetUser = user;
  }

  getTargetUser(): string {
    return this.targetUser;
  }
}
