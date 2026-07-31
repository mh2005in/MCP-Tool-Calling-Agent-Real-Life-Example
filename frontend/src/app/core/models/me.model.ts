export type AppRole = 'PLATFORM_ADMIN' | 'CONSULTANT_OWNER' | 'CONSULTANT_STAFF' | 'CLIENT';
export type AppUserStatus = 'ACTIVE' | 'PENDING' | 'DISABLED';

export interface Me {
  id: string;
  email: string;
  displayName: string;
  role: AppRole;
  status: AppUserStatus;
  consultantId: string | null;
  consultantAdmin: boolean;
  consultantName: string | null;
  companyName: string | null;
}
