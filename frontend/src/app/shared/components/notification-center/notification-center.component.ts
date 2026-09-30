import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationModel } from '../../../core/models/property.model';

@Component({
  selector: 'app-notification-center',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notification-wrapper">
      <button class="icon-btn" (click)="togglePopover()" title="Notifications">
        🔔
        <span class="badge" *ngIf="unreadCount > 0">{{ unreadCount }}</span>
      </button>

      <div class="popover-card" *ngIf="showPopover">
        <div class="popover-header">
          <h3>Notifications Center</h3>
          <button class="mark-all-btn" (click)="markAllRead()">Mark All Read</button>
        </div>

        <div class="popover-body">
          <div *ngIf="notifications.length === 0" class="empty-notif">
            <p>No notifications right now.</p>
          </div>

          <div *ngFor="let n of notifications" class="notif-item" [class.unread]="!n.read" (click)="markRead(n)">
            <div class="notif-type-icon" [ngClass]="n.type.toLowerCase()">
              {{ getIcon(n.type) }}
            </div>
            <div class="notif-content">
              <span class="notif-title">{{ n.title }}</span>
              <p class="notif-msg">{{ n.message }}</p>
              <span class="notif-time">{{ n.createdAt | date:'shortTime' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notification-wrapper {
      position: relative;
      display: inline-block;
    }
    .icon-btn {
      background: rgba(255,255,255,0.1);
      border: 1px solid rgba(255,255,255,0.15);
      border-radius: 50%;
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      cursor: pointer;
      position: relative;
      color: white;
    }
    .badge {
      position: absolute;
      top: -4px;
      right: -4px;
      background: #ef4444;
      color: white;
      font-size: 11px;
      font-weight: 800;
      width: 18px;
      height: 18px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .popover-card {
      position: absolute;
      right: 0;
      top: 50px;
      width: 340px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 10px 25px -5px rgba(0,0,0,0.2);
      border: 1px solid #e2e8f0;
      z-index: 1050;
      overflow: hidden;
      color: #1e293b;
    }
    .popover-header {
      padding: 12px 16px;
      background: #0f172a;
      color: white;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .popover-header h3 {
      margin: 0;
      font-size: 14px;
      font-weight: 700;
    }
    .mark-all-btn {
      background: transparent;
      border: none;
      color: #38bdf8;
      font-size: 11px;
      font-weight: 600;
      cursor: pointer;
    }
    .popover-body {
      max-height: 360px;
      overflow-y: auto;
    }
    .empty-notif {
      padding: 24px;
      text-align: center;
      color: #94a3b8;
      font-size: 13px;
    }
    .notif-item {
      padding: 12px 16px;
      border-bottom: 1px solid #f1f5f9;
      display: flex;
      gap: 12px;
      cursor: pointer;
      transition: background 0.2s ease;
    }
    .notif-item:hover { background: #f8fafc; }
    .notif-item.unread { background: #f0f9ff; }
    .notif-type-icon {
      width: 32px;
      height: 32px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
      flex-shrink: 0;
    }
    .inquiry_alert { background: #fef3c7; }
    .site_visit_reminder { background: #e0f2fe; }
    .approval_update { background: #dcfce7; }
    .message_alert { background: #f3e8ff; }
    .notif-content { flex: 1; }
    .notif-title { font-weight: 700; font-size: 13px; display: block; color: #0f172a; }
    .notif-msg { font-size: 12px; color: #475569; margin: 2px 0 4px 0; }
    .notif-time { font-size: 10px; color: #94a3b8; }
  `]
})
export class NotificationCenterComponent implements OnInit {
  private notifService = inject(NotificationService);

  notifications: NotificationModel[] = [];
  unreadCount = 0;
  showPopover = false;

  ngOnInit(): void {
    this.loadData();
    setInterval(() => this.loadData(), 5000);
  }

  loadData(): void {
    this.notifService.getUnreadCount().subscribe({
      next: (res) => this.unreadCount = res.data || 0
    });
  }

  togglePopover(): void {
    this.showPopover = !this.showPopover;
    if (this.showPopover) {
      this.notifService.getUserNotifications().subscribe({
        next: (res) => this.notifications = res.data || []
      });
    }
  }

  markRead(n: NotificationModel): void {
    if (n.read) return;
    this.notifService.markAsRead(n.id).subscribe({
      next: () => {
        n.read = true;
        this.loadData();
      }
    });
  }

  markAllRead(): void {
    this.notifService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.read = true);
        this.unreadCount = 0;
      }
    });
  }

  getIcon(type: string): string {
    switch (type) {
      case 'INQUIRY_ALERT': return '📩';
      case 'SITE_VISIT_REMINDER': return '📅';
      case 'APPROVAL_UPDATE': return '✓';
      case 'MESSAGE_ALERT': return '💬';
      default: return '🔔';
    }
  }
}
