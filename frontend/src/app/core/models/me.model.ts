export type AppRole = 'PLATFORM_ADMIN' | 'CONSULTANT_OWNER' | 'CONSULTANT_STAFF' | 'CLIENT';
export type AppUserStatus = 'ACTIVE' | 'PENDING' | 'DISABLED';

export interface Me {
  id: number;
  email: string;
  displayName: string;
  role: AppRole;
  status: AppUserStatus;
  consultantId: number | null;
  consultantAdmin: boolean;
  consultantName: string | null;
  companyName: string | null;
}
