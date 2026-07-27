import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

// Keycloak is initialized via APP_INITIALIZER (see app.config.ts) before the app renders.
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
