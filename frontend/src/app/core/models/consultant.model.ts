export interface Consultant {
  id: string;
  consultantNumber?: string;
  fullName: string;
  email: string;
  phone?: string;
  licenseNumber?: string;
  companyName?: string;
  admin: boolean;
  active: boolean;
  activeCaseCount: number;
  createdAt?: string;
}
