import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/auth.model';
import { ChatMessageModel, SendChatMessageRequest } from '../models/property.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/chat';

  sendMessage(req: SendChatMessageRequest): Observable<ApiResponse<ChatMessageModel>> {
    return this.http.post<ApiResponse<ChatMessageModel>>(`${this.apiUrl}/send`, req);
  }

  getConversation(propertyId: number, otherUserId: number): Observable<ApiResponse<ChatMessageModel[]>> {
    return this.http.get<ApiResponse<ChatMessageModel[]>>(`${this.apiUrl}/conversation?propertyId=${propertyId}&otherUserId=${otherUserId}`);
  }

  getUserConversations(): Observable<ApiResponse<ChatMessageModel[]>> {
    return this.http.get<ApiResponse<ChatMessageModel[]>>(`${this.apiUrl}/user-messages`);
  }
}
