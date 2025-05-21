import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, RouterModule]
})
export class LoginComponent {
  loginForm: FormGroup;
  error: string = '';

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.loginForm = this.fb.group({
      userName: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.http.post<any>('http://localhost:8080/login', this.loginForm.value).subscribe({
        next: (response) => {
          console.log('Login exitoso', response);
          localStorage.setItem('token', response.token);
          this.error = '';
          this.loginForm.reset();

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
          console.error('Error al iniciar sesión', err);
          this.error = 'Usuario o contraseña incorrectos.';
        }
      });
    }
  }
}
