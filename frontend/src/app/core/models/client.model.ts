export interface Client {
  id?: number;
  clientNumber?: string;
  consultantId?: number;
  fullName: string;
  email: string;
  phone?: string;
  whatsapp?: string;
  dateOfBirth?: string;
  countryOfCitizenship?: string;
  currentLocation?: string;
  currentStatus?: string;
  passportNumber?: string;
  maritalStatus?: string;
  preferredLanguage?: string;
  notes?: string;
}
