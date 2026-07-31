export interface PartyProfile {
  id: string;
  caseId: string;
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
