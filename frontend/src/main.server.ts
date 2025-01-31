import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { config } from './app/app.config.server';

if (process.env['NODE_ENV'] === 'development') {
  process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = '0'; // This is a workaround for the issue with self-signed certificates
}

const bootstrap = () => bootstrapApplication(AppComponent, config);

export default bootstrap;
