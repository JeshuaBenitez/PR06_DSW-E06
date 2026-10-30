import { Routes } from '@angular/router';

import { InicioComponent } from './features/inicio/inicio.component';
import { RegistrarComponent } from './features/registrar/registrar.component';
import { RegistrosComponent } from './features/registros/registros.component';
import { SesionesComponent } from './features/sesiones/sesiones.component';

export const routes: Routes = [
  { path: '', component: InicioComponent },
  { path: 'sesiones', component: SesionesComponent },
  { path: 'registrar', component: RegistrarComponent },
  { path: 'registros', component: RegistrosComponent },
  { path: '**', redirectTo: '' }
];
