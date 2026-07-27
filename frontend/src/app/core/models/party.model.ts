export interface PartyProfile {
  id: number;
  caseId: number;
  partyType: 'HOST' | 'SPONSOR' | 'EMPLOYER';
  fullName: string;
  email?: string;
  phone?: string;
  relationship?: string;
  organization?: string;
  portalEnabled: boolean;
  accessToken?: string;
  notes?: string;
}
