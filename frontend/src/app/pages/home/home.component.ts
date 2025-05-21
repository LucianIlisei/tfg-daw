import { Component } from '@angular/core';
import { RouterModule } from '@angular/router'; // <-- IMPORTAR ESTO

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule], // <-- IMPORTAR ESTO
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent { }
