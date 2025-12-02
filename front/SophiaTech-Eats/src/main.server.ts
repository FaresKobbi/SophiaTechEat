// src/main.server.ts (The REAL Fix)

import {
  bootstrapApplication,
  BootstrapContext // 1. Import BootstrapContext
} from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { config } from './app/app.config.server';

// 2. Change the signature to accept 'context' directly
const bootstrap = (context: BootstrapContext) => // <-- THIS IS THE FIX
  bootstrapApplication(AppComponent, config, context); // <-- Pass context as 3rd arg

export default bootstrap;
