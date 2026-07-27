export type ReminderStatus = 'DRAFT' | 'APPROVED' | 'SENT' | 'CANCELLED';

export interface Reminder {
  id?: number;
  caseId?: number;
  subject: string;
  messageBody: string;
  channel: string;
  status: ReminderStatus;
  scheduledAt?: string;
  sentAt?: string;
  approvedAt?: string;
  approvedBy?: string;
  attemptCount?: number;
}
