import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { CredencialesComponent } from './shared/credenciales/credenciales.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CredencialesComponent, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {}
