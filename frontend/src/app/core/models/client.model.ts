export interface Client {
  id?: string;
  clientNumber?: string;
  consultantId?: string;
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
