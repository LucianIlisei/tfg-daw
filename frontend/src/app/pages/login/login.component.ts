// Importaciones necesarias para formularios reactivos, HTTP, navegación y alertas
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';

// Decorador del componente con configuración standalone
@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, RouterModule]
})
export class LoginComponent {
  // Formulario de login y variable para mensaje de error
  loginForm: FormGroup;
  error: string = '';

  // Constructor: se inyectan servicios necesarios y se construye el formulario
  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.loginForm = this.fb.group({
      userName: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  // Método que se ejecuta al enviar el formulario
  onSubmit() {
    if (this.loginForm.valid) {
      // Llamada al backend para intentar iniciar sesión
      this.http.post<any>('http://localhost:8080/login', this.loginForm.value).subscribe({
        next: (response) => {
          // Login exitoso
          console.log('Login exitoso', response);

          // Guardamos el token y el username en localStorage
          localStorage.setItem('token', response.token);
          this.error = '';
          this.loginForm.reset();

          // Mostramos alerta con SweetAlert2 y navegamos al dashboard
          Swal.fire({
            icon: 'success',
            title: `¡Hola, ${response.username}!`,
            text: 'Has iniciado sesión con éxito.',
            confirmButtonText: 'Ir al panel',
            confirmButtonColor: '#3085d6'
          }).then(() => {
            this.router.navigate(['/dashboard']);
          });
        },
        error: (err) => {
          // Login fallido
          console.error('Error al iniciar sesión', err);
          this.error = 'Usuario o contraseña incorrectos.';
        }
      });
    }
  }
}
