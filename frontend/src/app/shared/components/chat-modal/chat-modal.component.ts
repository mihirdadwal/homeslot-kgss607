import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../../../core/services/chat.service';
import { TokenService } from '../../../core/services/token.service';
import { ChatMessageModel } from '../../../core/models/property.model';

@Component({
  selector: 'app-chat-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="chat-backdrop" *ngIf="showModal">
      <div class="chat-modal">
        <div class="chat-header">
          <div class="header-info">
            <span class="online-indicator"></span>
            <div>
              <h3>Chat with {{ recipientName }}</h3>
              <p class="property-tag">Property: {{ propertyTitle }}</p>
            </div>
          </div>
          <button class="close-btn" (click)="close()">&times;</button>
        </div>

        <div class="chat-body" #chatBody>
          <div *ngIf="messages.length === 0" class="empty-chat">
            <p>No messages yet. Send a message to start the conversation!</p>
          </div>

          <div *ngFor="let msg of messages" class="message-row" [class.outgoing]="msg.senderId === currentUserId" [class.incoming]="msg.senderId !== currentUserId">
            <div class="message-bubble">
              <span class="sender-name" *ngIf="msg.senderId !== currentUserId">{{ msg.senderName }}</span>
              <p class="message-text">{{ msg.message }}</p>
              <span class="message-time">{{ msg.createdAt | date:'shortTime' }}</span>
            </div>
          </div>
        </div>

        <div class="chat-footer">
          <form (ngSubmit)="send()">
            <input type="text" [(ngModel)]="messageText" name="msg" placeholder="Type your message..." class="chat-input" required>
            <button type="submit" class="btn-send">Send ✈️</button>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .chat-backdrop {
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      height: 100vh;
      background: rgba(15, 23, 42, 0.5);
      backdrop-filter: blur(4px);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1100;
      padding: 20px;
    }
    .chat-modal {
      background: white;
      border-radius: 16px;
      width: 100%;
      max-width: 500px;
      height: 600px;
      display: flex;
      flex-direction: column;
      box-shadow: 0 20px 25px -5px rgba(0,0,0,0.2);
      overflow: hidden;
    }
    .chat-header {
      background: #0f172a;
      color: white;
      padding: 16px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .header-info {
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .online-indicator {
      width: 10px;
      height: 10px;
      background: #22c55e;
      border-radius: 50%;
    }
    .header-info h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 700;
    }
    .property-tag {
      margin: 0;
      font-size: 12px;
      color: #38bdf8;
    }
    .close-btn {
      background: transparent;
      border: none;
      color: white;
      font-size: 24px;
      cursor: pointer;
    }
    .chat-body {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      background: #f8fafc;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    .empty-chat {
      text-align: center;
      color: #94a3b8;
      margin: auto;
      font-size: 14px;
    }
    .message-row {
      display: flex;
      margin-bottom: 4px;
    }
    .message-row.outgoing {
      justify-content: flex-end;
    }
    .message-row.incoming {
      justify-content: flex-start;
    }
    .message-bubble {
      max-width: 75%;
      padding: 10px 14px;
      border-radius: 14px;
      font-size: 14px;
      position: relative;
    }
    .outgoing .message-bubble {
      background: #7c3aed;
      color: white;
      border-bottom-right-radius: 2px;
    }
    .incoming .message-bubble {
      background: white;
      color: #1e293b;
      border: 1px solid #e2e8f0;
      border-bottom-left-radius: 2px;
    }
    .sender-name {
      display: block;
      font-size: 11px;
      font-weight: 700;
      color: #7c3aed;
      margin-bottom: 2px;
    }
    .message-text {
      margin: 0;
      word-break: break-word;
    }
    .message-time {
      display: block;
      font-size: 10px;
      opacity: 0.75;
      text-align: right;
      margin-top: 4px;
    }
    .chat-footer {
      padding: 16px;
      border-top: 1px solid #e2e8f0;
      background: white;
    }
    .chat-footer form {
      display: flex;
      gap: 10px;
    }
    .chat-input {
      flex: 1;
      padding: 10px 14px;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      font-size: 14px;
      outline: none;
    }
    .btn-send {
      background: #7c3aed;
      color: white;
      border: none;
      padding: 10px 18px;
      border-radius: 8px;
      font-weight: 700;
      cursor: pointer;
    }
  `]
})
export class ChatModalComponent implements OnInit, OnDestroy {
  @Input() showModal = false;
  @Input() propertyId!: number;
  @Input() propertyTitle!: string;
  @Input() recipientId!: number;
  @Input() recipientName!: string;
  @Output() closeModal = new EventEmitter<void>();

  private chatService = inject(ChatService);
  private tokenService = inject(TokenService);

  messages: ChatMessageModel[] = [];
  messageText = '';
  currentUserId: number | null = null;
  private pollTimer: any;

  ngOnInit(): void {
    const u = this.tokenService.getUser();
    if (u) this.currentUserId = u.id;
    if (this.showModal) {
      this.loadConversation();
      this.pollTimer = setInterval(() => this.loadConversation(), 3000);
    }
  }

  ngOnDestroy(): void {
    if (this.pollTimer) clearInterval(this.pollTimer);
  }

  loadConversation(): void {
    if (!this.propertyId || !this.recipientId) return;
    this.chatService.getConversation(this.propertyId, this.recipientId).subscribe({
      next: (res) => {
        this.messages = res.data || [];
      },
      error: (err) => console.error(err)
    });
  }

  send(): void {
    if (!this.messageText.trim()) return;
    this.chatService.sendMessage({
      propertyId: this.propertyId,
      recipientId: this.recipientId,
      message: this.messageText.trim()
    }).subscribe({
      next: (res) => {
        if (res.data) this.messages.push(res.data);
        this.messageText = '';
      },
      error: (err) => console.error(err)
    });
  }

  close(): void {
    if (this.pollTimer) clearInterval(this.pollTimer);
    this.closeModal.emit();
  }
}
