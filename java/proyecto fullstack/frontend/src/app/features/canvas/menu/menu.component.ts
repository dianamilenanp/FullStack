import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { MenubarModule } from 'primeng/menubar';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, MenubarModule, ButtonModule  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent {

  items = [
    {
      label: 'Opciones',
      items: [
        { label: 'Inicio', icon: 'pi pi-home' },
        { label: 'Cursos', icon: 'pi pi-book' },
        { label: 'Salir', icon: 'pi pi-sign-out' }
      ]
    }
  ];

}
